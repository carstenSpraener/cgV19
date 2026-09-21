package de.spraener.nxtgen.target;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CompositeCodeSectionTest {

    private SimpleCodeSection uut = new SimpleCodeSection();
    private CodeSnippet snippetA = new SingleLineSnippet("A", "line a");
    private CodeSnippet snippetB = new SingleLineSnippet("B", "line b");

    @Test
    void getOrCreateScopeCreatesChildWithIdAndParent() {
        CodeSection child = uut.getOrCreateScope("scope1", SimpleCodeSection::new);

        assertNotNull(child);
        assertEquals("scope1", child.getId());
        assertSame(uut, child.getParent());
        assertSame(child, uut.getScope("scope1"));
    }

    @Test
    void supplierIsInvokedOnlyOnce() {
        AtomicInteger invocations = new AtomicInteger();
        Supplier<CodeSection> supplier = () -> {
            invocations.incrementAndGet();
            return new SimpleCodeSection();
        };

        CodeSection first = uut.getOrCreateScope("scope1", supplier);
        CodeSection second = uut.getOrCreateScope("scope1", () -> new SimpleCodeSection());

        assertSame(first, second);
        assertEquals(1, invocations.get());
    }

    @Test
    void getScopeReturnsNullForUnknownScope() {
        assertNull(uut.getScope("unknown"));
    }

    @Test
    void getChildrenReturnsInsertionOrder() {
        uut.getOrCreateScope("s1", SimpleCodeSection::new);
        uut.getOrCreateScope("s2", SimpleCodeSection::new);

        List<CodeSection> children = new ArrayList<>(uut.getChildren());
        assertEquals(2, children.size());
        assertEquals("s1", children.get(0).getId());
        assertEquals("s2", children.get(1).getId());
    }

    @Test
    void rootSectionHasNoParent() {
        assertNull(uut.getParent());
    }

    @Test
    void getSnippetsOrderedIsDepthFirstPreOrder() {
        CodeSnippet child1a = new SingleLineSnippet("C1", "child 1 a");
        CodeSnippet child1b = new SingleLineSnippet("C1", "child 1 b");
        CodeSnippet child2a = new SingleLineSnippet("C2", "child 2 a");

        CodeSection child1 = uut.getOrCreateScope("c1", SimpleCodeSection::new);
        CodeSection child2 = uut.getOrCreateScope("c2", SimpleCodeSection::new);
        uut.add(snippetA);
        child1.add(child1a);
        child1.add(child1b);
        child2.add(child2a);

        List<CodeSnippet> ordered = new ArrayList<>(uut.getSnippetsOrdered());
        assertThat(ordered).containsExactly(snippetA, child1a, child1b, child2a);
    }

    @Test
    void getOwnSnippetsExcludesChildren() {
        CodeSection child = uut.getOrCreateScope("c1", SimpleCodeSection::new);
        uut.add(snippetA);
        child.add(snippetB);

        assertThat(uut.getOwnSnippets()).containsExactly(snippetA);
    }

    @Test
    void rendersChildrenInlineDefaultsToFalse() {
        assertFalse(uut.rendersChildrenInline());
    }

    @Test
    void concurrentGetOrCreateScopeInvokesSupplierOnce() throws InterruptedException {
        int threads = 8;
        AtomicInteger invocations = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        List<CodeSection> results = java.util.Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                try {
                    start.await();
                    results.add(uut.getOrCreateScope("shared", () -> {
                        invocations.incrementAndGet();
                        return new SimpleCodeSection();
                    }));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }).start();
        }

        start.countDown();
        assertTrue(done.await(10, TimeUnit.SECONDS), "all threads should finish");

        assertEquals(threads, results.size());
        assertEquals(1, invocations.get());
        for (CodeSection result : results) {
            assertSame(results.get(0), result);
        }
    }

    @Test
    void insertBeforeDelegatesToChildScope() {
        CodeSection child = uut.getOrCreateScope("c1", SimpleCodeSection::new);
        child.add(snippetA);

        uut.insertBefore(snippetA, snippetB);

        assertThat(child.getOwnSnippets()).containsExactly(snippetB, snippetA);
    }

    @Test
    void insertAfterDelegatesToChildScope() {
        CodeSection child = uut.getOrCreateScope("c1", SimpleCodeSection::new);
        child.add(snippetA);

        uut.insertAfter(snippetA, snippetB);

        assertThat(child.getOwnSnippets()).containsExactly(snippetA, snippetB);
    }

    @Test
    void replaceDelegatesToChildScope() {
        CodeSection child = uut.getOrCreateScope("c1", SimpleCodeSection::new);
        child.add(snippetA);

        uut.replace(snippetA, snippetB);

        assertThat(child.getOwnSnippets()).containsExactly(snippetB);
    }

    @Test
    void insertBeforeUnknownSnippetStillThrows() {
        CodeSection child = uut.getOrCreateScope("c1", SimpleCodeSection::new);
        child.add(snippetA);

        assertThrows(IllegalArgumentException.class, () -> uut.insertBefore(new SingleLineSnippet("X", "x"), snippetB));
    }

    @Test
    void ownerResolutionFindsSnippetInGrandchildScope() {
        CodeSection child = uut.getOrCreateScope("c1", SimpleCodeSection::new);
        CodeSection grandchild = child.getOrCreateScope("gc1", SimpleCodeSection::new);
        grandchild.add(snippetA);

        uut.insertBefore(snippetA, snippetB);

        assertThat(grandchild.getOwnSnippets()).containsExactly(snippetB, snippetA);
    }
}
