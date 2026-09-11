package de.spraener.nxtgen.target;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CodeTargetGetOrCreateTest {

    @Test
    void singleSegmentCreatesSectionWithId() {
        CodeTarget target = new CodeTarget();
        AtomicInteger calls = new AtomicInteger();

        CodeSection section = target.getOrCreate("OPERATIONS", () -> {
            calls.incrementAndGet();
            return new SimpleCodeSection();
        });

        assertEquals("OPERATIONS", section.getId());
        assertEquals(1, calls.get());
        assertSame(section, target.getSection("OPERATIONS"));
    }

    @Test
    void singleSegmentReturnsExistingByKeyWithoutCallingSupplier() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection existing = new SimpleCodeSection();
        target.addCodeSection("OPERATIONS", existing);
        AtomicInteger calls = new AtomicInteger();

        CodeSection section = target.getOrCreate("OPERATIONS", () -> {
            calls.incrementAndGet();
            return new SimpleCodeSection();
        });

        assertSame(existing, section);
        assertEquals(0, calls.get());
    }

    @Test
    void singleSegmentFallsBackToSectionId() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection existing = new SimpleCodeSection();
        Object key = new Object() {
            @Override
            public String toString() {
                return "OPERATIONS";
            }
        };
        target.addCodeSection(key, existing);
        AtomicInteger calls = new AtomicInteger();

        CodeSection section = target.getOrCreate("OPERATIONS", () -> {
            calls.incrementAndGet();
            return new SimpleCodeSection();
        });

        assertSame(existing, section);
        assertEquals(0, calls.get());
    }

    @Test
    void multiSegmentCreatesLastOnExistingParent() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection operations = new SimpleCodeSection();
        target.addCodeSection("OPERATIONS", operations);
        AtomicInteger calls = new AtomicInteger();

        CodeSection op = target.getOrCreate("OPERATIONS/login", () -> {
            calls.incrementAndGet();
            return StandardSections.operation().get();
        });

        assertEquals("login", op.getId());
        assertSame(operations, op.getParent());
        assertEquals(1, calls.get());

        CodeSection again = target.getOrCreate("OPERATIONS/login", StandardSections.operation());
        assertSame(op, again);
        assertEquals(1, calls.get(), "supplier must run exactly once");
    }

    @Test
    void multiSegmentThrowsWhenParentMissing() {
        CodeTarget target = new CodeTarget();

        assertThrows(IllegalArgumentException.class, () -> target.getOrCreate("NOPE/login", StandardSections.plain()));
    }

    @Test
    void deepPathOnlyCreatesLastSegment() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection operations = new SimpleCodeSection();
        CodeSection login = operations.getOrCreateScope("login", StandardSections.plain());
        target.addCodeSection("OPERATIONS", operations);

        CodeSection in = target.getOrCreate("OPERATIONS/login/IN_OPERATION", StandardSections.plain());

        assertEquals("IN_OPERATION", in.getId());
        assertSame(login, in.getParent());
    }

    @Test
    void deepPathThrowsWhenIntermediateMissing() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection operations = new SimpleCodeSection();
        target.addCodeSection("OPERATIONS", operations);

        assertThrows(IllegalArgumentException.class, () -> target.getOrCreate("OPERATIONS/missing/IN_OPERATION", StandardSections.plain()));
    }

    @Test
    void leadingSlashIsTolerated() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection operations = new SimpleCodeSection();
        target.addCodeSection("OPERATIONS", operations);

        CodeSection op = target.getOrCreate("/OPERATIONS/login", StandardSections.plain());

        assertEquals("login", op.getId());
        assertSame(operations, op.getParent());
    }

    @Test
    void getSectionToleratesLeadingSlash() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection operations = new SimpleCodeSection();
        CodeSection login = operations.getOrCreateScope("login", StandardSections.plain());
        target.addCodeSection("OPERATIONS", operations);

        assertSame(login, target.getSection("/OPERATIONS/login"));
    }

    @Test
    void concurrentGetOrCreateInvokesSupplierOnce() throws InterruptedException {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection operations = new SimpleCodeSection();
        target.addCodeSection("OPERATIONS", operations);

        int threads = 8;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger calls = new AtomicInteger();
        CodeSection[] results = new CodeSection[threads];

        for (int i = 0; i < threads; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    start.await();
                    results[index] = target.getOrCreate("OPERATIONS/login", () -> {
                        calls.incrementAndGet();
                        return new SimpleCodeSection();
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }).start();
        }

        start.countDown();
        assertTrue(done.await(10, TimeUnit.SECONDS), "all threads should finish");

        assertEquals(1, calls.get());
        for (CodeSection result : results) {
            assertSame(results[0], result);
        }
    }

    @Test
    void operationSectionRendersWithInheritedIndentation() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection operations = new SimpleCodeSection();
        target.addCodeSection("OPERATIONS", operations);

        CodeSection op = target.getOrCreate("OPERATIONS/login", StandardSections.operation());
        op.add(new SingleLineSnippet("sig", "public void login() {"));
        op.getScope("BEFORE_OPERATION").add(new SingleLineSnippet("b", "checkAuth();"));
        op.getScope("IN_OPERATION").add(new SingleLineSnippet("i", "doLogin();"));
        op.getScope("AFTER_OPERATION").add(new SingleLineSnippet("a", "}"));

        String out = new CodeTargetToCodeConverter(target).toString();

        assertThat(out)
                .contains("    public void login() {")
                .contains("        checkAuth();")
                .contains("        doLogin();")
                .contains("        }");
        assertThat(out.indexOf("checkAuth()")).isLessThan(out.indexOf("doLogin();"));
        assertThat(out.indexOf("doLogin();")).isLessThan(out.lastIndexOf("}"));
    }
}
