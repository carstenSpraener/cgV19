package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleCodeSectionTest {
    private SimpleCodeSection uut = new SimpleCodeSection();
    private CodeSnippet snippetA = new SingleLineSnippet("A","line a");
    private CodeSnippet snippetB = new SingleLineSnippet("B","line b");
    private CodeSnippet snippetC = new SingleLineSnippet("C","line c");

    @Test
    void testAddSnippet() {
        uut.withSnippet("A", snippetA);
        assertNotNull(uut.getSnippetsForAspect("A").get(0));
        assertEquals(snippetA, uut.getSnippetsForAspect("A").get(0).get());
        assertThat(uut.getSnippetsOrdered())
                .contains(snippetA);
    }

    @Test
    void testInsertBefore() {
        uut.withSnippet("A", snippetA)
                .insertBefore(snippetA, snippetB);
        ;
        assertThat(uut.getSnippetsOrdered())
                .contains(snippetB, snippetA);
    }

    @Test
    void testInserAfter() {
        // Given: A CodeSection with two snippets A and B
        uut.withSnippet("A", snippetA)
                .withSnippet("B", snippetB)
                // When: Inserting snippet C after A
                .insertAfter(snippetA, snippetC);
        ;
        // Then: the order should be A, C, B
        assertThat(uut.getSnippetsOrdered())
                .contains(snippetA, snippetC, snippetB);
    }

    @Test
    void testInsertBeforeNotContainedSnippet() {
        try {
            uut.insertBefore(snippetA, snippetB);
            fail();
        } catch( IllegalArgumentException iaXC ) {}
    }

    @Test
    void testReplaceSnippet() {
        // Given: A CodeSection with two Snippets A and B
        uut.withSnippet("A", snippetA)
                .withSnippet("B", snippetB);
        // when: replacing B with C
        uut.replace(snippetB, snippetC);
        // then: The order should be A, C
        assertThat(uut.getSnippetsOrdered())
                .contains(snippetA, snippetC);
        // and Key B shall deliver snippetC
        assertEquals(snippetC, uut.getSnippetsForAspect("B").get(0).get());
    }

    @Test
    void testAddSnippetByCodeLine() {
        uut.withSnippet("X", "code line X");
        assertNotNull(uut.getSnippetsForAspect("X").get(0));
        assertTrue(uut.getSnippetsForAspect("X").get(0).get() instanceof SingleLineSnippet);
    }

    @Test
    void testAddSnippetWithKeyAndModelElement() {
        final ModelElement me = new ModelElementImpl();
        CodeSnippet cs = new CodeSnippet("aspect", me) {
            @Override
            public void evaluate(StringBuilder sb) {
                sb.append("Code needed to implement aspect A for ModelElement me" );
            }
        };
        uut.withSnippet("A", snippetA)
                .withSnippet("A", cs);
        List<CodeSnippetRef> snippetRefList = uut.getSnippetsForAspectAndModelElement("aspect", me);
        assertEquals(1, snippetRefList.size());
        assertEquals(cs, snippetRefList.get(0).get());
    }

    @Test
    void testInsertBeforeNotExistentSnippetThrowsIllegalArgument() {
        uut.withSnippet("A", snippetA);
        try {
            uut.insertBefore(snippetC, snippetB);
            fail();
        } catch( IllegalArgumentException iaXC ) {}
    }

    @Test
    void testInsertAfterNotExistentSnippetThrowsIllegalArgument() {
        uut.withSnippet("A", snippetA);
        try {
            uut.insertAfter(snippetC, snippetB);
            fail();
        } catch( IllegalArgumentException iaXC ) {}
    }

    @Test
    void testGetLastAndFirstForAspect() {
        SingleLineSnippet aspectA1=new SingleLineSnippet("A", null, "line1");
        SingleLineSnippet aspectA2=new SingleLineSnippet("A", null, "line2");
        uut.withSnippet("A", aspectA1)
                .withSnippet("A", aspectA2)
        ;
        assertEquals(aspectA2, uut.getLastSnippetForAspect("A").get());
        assertEquals(aspectA1, uut.getFirstSnippetForAspect("A").get());
    }

    @Test
    void testGetLastAndFirstForAspectAndModelElement() {
        ModelElement me = new ModelElementImpl();

        SingleLineSnippet aspectA1=new SingleLineSnippet("A", null, "line1");
        SingleLineSnippet aspectA2=new SingleLineSnippet("A", null, "line2");
        SingleLineSnippet aspectA3=new SingleLineSnippet("A", me, "line3");
        SingleLineSnippet aspectA4=new SingleLineSnippet("A", me, "line4");

        uut.withSnippet("A", aspectA1)
                .withSnippet("A", aspectA2)
                .withSnippet("A", aspectA3)
                .add("A", me, "line between")
                .withSnippet("A", aspectA4)
        ;
        assertEquals(aspectA4, uut.getLastSnippetForAspectAndModelElement("A", me).get());
        assertEquals(aspectA3, uut.getFirstSnippetForAspectAndModelElement("A", me).get());
        assertNull(uut.getLastSnippetForAspect("B"));
        assertNull(uut.getLastSnippetForAspectAndModelElement("A", new ModelElementImpl()));
    }

}
