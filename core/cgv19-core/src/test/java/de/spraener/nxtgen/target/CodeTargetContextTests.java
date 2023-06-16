package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.target.java.JavaSections;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CodeTargetContextTests {

    public static final String TEST_ASPECT = "test-aspect";

    @Test
    void testContextSet() {
        CodeTarget target = JavaSections.createJavaCodeTarget();
        ModelElement me = new ModelElementImpl();
        try( CodeTargetContext ctxt = new CodeTargetContext(TEST_ASPECT, me)) {
            target.getSection(JavaSections.HEADER)
                    .add(new SingleLineSnippet("Hello World"));
        }
        CodeSnippet snippet = target.getSection(JavaSections.HEADER).getLastSnippetForAspect(TEST_ASPECT).get();
        assertNotNull(snippet);
        assertTrue(snippet instanceof SingleLineSnippet);
        assertEquals(TEST_ASPECT, snippet.getAspect());
        assertEquals(me, snippet.getModelElement());
    }

    /**
     * Ensure that CodeTargetContext can be nested and that the aspect and ModelElement
     * are set correctly.
     *
     */
    @Test
    void testNestedContext() {
        // Given: A CodeTarget with Sections
        CodeTarget target = JavaSections.createJavaCodeTarget();
        // ... and two ModelElements to refer to
        ModelElement meParent = new ModelElementImpl();
        ModelElement meChild = new ModelElementImpl();
        // when: Adding a snippet in a context of aspect and ModelElement parent...
        try( CodeTargetContext cOut = new CodeTargetContext(TEST_ASPECT, meParent)) {
            target.getSection(JavaSections.HEADER)
                    .add(new SingleLineSnippet("Hello Parent"));
            // ... and in a nested context a snippet for the same aspect but ModelElement child.
            try( CodeTargetContext cInner = new CodeTargetContext(TEST_ASPECT, meChild)) {
                target.getSection(JavaSections.HEADER)
                        .add(new SingleLineSnippet(TEST_ASPECT, "Hello Child"));
            }
        }
        // then: The snippets can be correctly addressed by the aspect and the ModelElement
        CodeSnippet outer = target.getSection(JavaSections.HEADER)
                .getFirstSnippetForAspectAndModelElement(TEST_ASPECT, meParent).get();
        CodeSnippet inner = target.getSection(JavaSections.HEADER)
                .getFirstSnippetForAspectAndModelElement(TEST_ASPECT, meChild).get();
        assertEquals("Hello Parent\n", readText(outer));
        assertEquals("Hello Child\n", readText(inner));
    }

    private String readText(CodeSnippet snippet) {
        StringBuilder sb = new StringBuilder();
        snippet.evaluate(sb);
        return sb.toString();
    }
}
