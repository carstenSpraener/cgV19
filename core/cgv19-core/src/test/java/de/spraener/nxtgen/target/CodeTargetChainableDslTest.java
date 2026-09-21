package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import groovy.lang.Closure;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the new chainable DSL API: setDefaultModelElement, 2-arg forAspect, evaluate.
 * <p>
 * Renamed from CodeTargetDSLTest to free the name for the Groovy test class
 * {@code de.spraener.nxtgen.target.CodeTargetDSLTest} (src/test/groovy), which tests
 * the production Groovy build DSL ({@code de.spraener.nxtgen.target.dsl.CodeTargetDSL}).
 */
public class CodeTargetChainableDslTest {

    // === setDefaultModelElement Tests ===

    @Test
    void testSetDefaultModelElementReturnsThis() {
        CodeTarget ct = new CodeTarget();
        ModelElement me = createMockModelElement("test");

        // Should return this for chaining
        CodeTarget result = ct.setDefaultModelElement(me);
        assertSame(ct, result);
    }

    @Test
    void testGetDefaultModelElement() {
        CodeTarget ct = new CodeTarget();
        ModelElement me = createMockModelElement("test");

        ct.setDefaultModelElement(me);
        assertSame(me, ct.getDefaultModelElement());
    }

    @Test
    void testDefaultModelElementInitiallyNull() {
        CodeTarget ct = new CodeTarget();
        assertNull(ct.getDefaultModelElement());
    }

    // === 2-arg forAspect Tests ===

    @Test
    void testTwoArgForAspectUsesDefaultModelElement() {
        CodeTarget ct = new CodeTarget();
        ModelElement me = createMockModelElement("test");

        SimpleCodeSection section = new SimpleCodeSection();
        ct.addCodeSection("test", section);

        // 2-arg forAspect: the model element is taken from defaultModelElement
        ct.setDefaultModelElement(me)
           .forAspect("logging", (Closure) createToTestClosure());

        // Verify snippet was added with correct aspect and model element
        Collection<CodeSnippet> snippets = section.getSnippetsOrdered();
        assertEquals(1, snippets.size());

        CodeSnippet snippet = snippets.iterator().next();
        assertEquals("logging", snippet.getAspect());
        assertSame(me, snippet.getModelElement());
    }

    @Test
    void testTwoArgForAspectReturnsThis() {
        CodeTarget ct = new CodeTarget();
        ModelElement me = createMockModelElement("test");

        SimpleCodeSection section = new SimpleCodeSection();
        ct.addCodeSection("test", section);

        CodeTarget result = ct.setDefaultModelElement(me)
                              .forAspect("logging", (Closure) createToTestClosure());

        assertSame(ct, result);
    }

    @Test
    void testTwoArgForAspectChaining() {
        CodeTarget ct = new CodeTarget();
        ModelElement me = createMockModelElement("test");

        SimpleCodeSection section = new SimpleCodeSection();
        ct.addCodeSection("test", section);

        ct.setDefaultModelElement(me)
           .forAspect("logging", (Closure) createToTestClosure())
           .forAspect("entity", (Closure) createToTestClosure());

        // Verify both aspects added snippets
        Collection<CodeSnippet> snippets = section.getSnippetsOrdered();
        assertEquals(2, snippets.size());

        List<CodeSnippet> snippetList = new ArrayList<>(snippets);
        assertEquals("logging", snippetList.get(0).getAspect());
        assertEquals("entity", snippetList.get(1).getAspect());
    }

    @Test
    void testTwoArgForAspectWithNullDefaultModelElement() {
        CodeTarget ct = new CodeTarget();

        SimpleCodeSection section = new SimpleCodeSection();
        ct.addCodeSection("test", section);

        // No defaultModelElement set: the 2-arg forAspect must work with a null model element
        ct.forAspect("test", (Closure) createToTestClosure());

        Collection<CodeSnippet> snippets = section.getSnippetsOrdered();
        assertEquals(1, snippets.size());
        assertNull(snippets.iterator().next().getModelElement());
    }

    // === evaluate() Tests ===

    @Test
    void testEvaluateReturnsThis() {
        CodeTarget ct = new CodeTarget();
        ModelElement me = createMockModelElement("test");

        SimpleCodeSection section = new SimpleCodeSection();
        ct.addCodeSection("test", section);

        // Real script that adds a snippet (same pattern as testEvaluateChaining)
        String scriptContent = "ct.forAspect('eval-return', mClass) { to 'test', 'from script' }";
        String scriptPath = createTempScript("evaluate-returns-this.groovy", scriptContent);

        try {
            CodeTarget result = ct.setDefaultModelElement(me).evaluate(scriptPath);

            // evaluate() must return the same instance for fluent chaining
            assertSame(ct, result);

            // and it must have actually executed the script (evaluate() silently
            // returns 'this' when the script cannot be found)
            assertEquals(1, section.getSnippetsOrdered().size());
        } finally {
            cleanupTempScript(scriptPath);
        }
    }

    @Test
    void testEvaluateChaining() {
        CodeTarget ct = new CodeTarget();
        ModelElement me = createMockModelElement("test");

        SimpleCodeSection section = new SimpleCodeSection();
        ct.addCodeSection("test", section);

        // Create a test script that adds a snippet
        String scriptContent = "ct.forAspect('script-aspect', mClass) { to 'test', 'from script' }";
        String scriptPath = createTempScript("chaining-test.groovy", scriptContent);

        try {
            ct.setDefaultModelElement(me)
               .evaluate(scriptPath)
               .forAspect("after-eval", me, (Closure) createToTestClosure());

            // Verify both snippets exist
            Collection<CodeSnippet> snippets = section.getSnippetsOrdered();
            assertEquals(2, snippets.size());

            List<CodeSnippet> snippetList = new ArrayList<>(snippets);
            assertEquals("script-aspect", snippetList.get(0).getAspect());
            assertEquals("after-eval", snippetList.get(1).getAspect());
        } finally {
            cleanupTempScript(scriptPath);
        }
    }

    @Test
    void testEvaluateWithMClassBinding() {
        CodeTarget ct = new CodeTarget();
        ModelElement me = createMockModelElement("TestClass");

        SimpleCodeSection section = new SimpleCodeSection();
        ct.addCodeSection("test", section);

        // Script that uses mClass variable
        String scriptContent = "ct.forAspect('mclass-test', mClass) { to 'test', mClass.name }";
        String scriptPath = createTempScript("mclass-binding.groovy", scriptContent);

        try {
            ct.setDefaultModelElement(me)
               .evaluate(scriptPath);

            Collection<CodeSnippet> snippets = section.getSnippetsOrdered();
            assertEquals(1, snippets.size());
            
            // Verify the snippet text contains the class name
            StringBuilder sb = new StringBuilder();
            snippets.iterator().next().evaluate(sb);
            assertTrue(sb.toString().contains("TestClass"));
        } finally {
            cleanupTempScript(scriptPath);
        }
    }

    // === ForAspectDSL.mClass Tests ===

    @Test
    void testForAspectDSLMClassProperty() {
        CodeTarget ct = new CodeTarget();
        ModelElement me = createMockModelElement("test");

        SimpleCodeSection section = new SimpleCodeSection();
        ct.addCodeSection("test", section);

        // Use mClass property in the closure
        ct.setDefaultModelElement(me)
           .forAspect("mclass-prop", me, (Closure) createMClassPropertyTestClosure());

        Collection<CodeSnippet> snippets = section.getSnippetsOrdered();
        assertEquals(1, snippets.size());

        // Verify the snippet was added with correct model element
        assertSame(me, snippets.iterator().next().getModelElement());
    }

    // === Integration Test: Full DSL Workflow ===

    @Test
    void testFullDSLWorkflow() {
        CodeTarget ct = new CodeTarget();
        ModelElement me = createMockModelElement("test");

        // Setup sections
        SimpleCodeSection header = new SimpleCodeSection();
        UniqueLineSection imports = new UniqueLineSection();
        SimpleCodeSection body = new SimpleCodeSection();

        ct.addCodeSection("header", header);
        ct.addCodeSection("imports", imports);
        ct.addCodeSection("body", body);

        // Apply aspects using chainable DSL
        ct.setDefaultModelElement(me)
           .forAspect("java-frame", me, (Closure) createJavaFrameClosure())
           .forAspect("logging", me, (Closure) createLoggingClosure());

        // Verify results
        assertEquals(1, header.getSnippetsOrdered().size());
        assertEquals(1, imports.getSnippetsOrdered().size()); // only logging adds to imports
        assertEquals(1, body.getSnippetsOrdered().size());

        // Verify deduplication in imports (UniqueLineSection)
        List<String> importTexts = new ArrayList<>();
        for (CodeSnippet s : imports.getSnippetsOrdered()) {
            StringBuilder sb = new StringBuilder();
            s.evaluate(sb);
            importTexts.add(sb.toString());
        }

        assertTrue(importTexts.stream().anyMatch(t -> t.contains("java.util.logging")));
    }

    // === Helper Methods ===

    /**
     * Creates a simple mock ModelElement for testing.
     */
    private ModelElement createMockModelElement(String name) {
        return new ModelElementImpl() {{
            setName(name);
        }};
    }

    /**
     * Creates a closure that calls to() on the ForAspectDSL delegate.
     */
    private Closure createToTestClosure() {
        return new Closure<Object>(null) {
            @Override
            public Object call() {
                // Call to() on delegate (ForAspectDSL)
                ((de.spraener.nxtgen.target.dsl.ForAspectDSL)getDelegate()).to("test", "test content");
                return null;
            }
        };
    }

    private Closure createMClassPropertyTestClosure() {
        return new Closure<Object>(null) {
            @Override
            public Object call() {
                // Access mClass property from ForAspectDSL delegate and use it
                de.spraener.nxtgen.target.dsl.ForAspectDSL dsl = (de.spraener.nxtgen.target.dsl.ForAspectDSL)getDelegate();
                ModelElement mc = dsl.getMClass();
                dsl.to("test", "mclass: " + mc.getName());
                return null;
            }
        };
    }

    private Closure createJavaFrameClosure() {
        return new Closure<Object>(null) {
            @Override
            public Object call() {
                de.spraener.nxtgen.target.dsl.ForAspectDSL dsl = (de.spraener.nxtgen.target.dsl.ForAspectDSL)getDelegate();
                dsl.to("header", "package testpkg;");
                dsl.to("body", "{");
                return null;
            }
        };
    }

    private Closure createLoggingClosure() {
        return new Closure<Object>(null) {
            @Override
            public Object call() {
                ((de.spraener.nxtgen.target.dsl.ForAspectDSL)getDelegate()).to("imports", "import java.util.logging.Logger;");
                return null;
            }
        };
    }

    /**
     * Creates a temporary script file for testing evaluate().
     */
    private String createTempScript(String name, String content) {
        try {
            java.io.File f = java.nio.file.Files.createTempFile(name, ".groovy").toFile();
            java.nio.file.Files.writeString(f.toPath(), content);
            return f.getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create temp script", e);
        }
    }

    /**
     * Cleans up a temporary script file.
     */
    private void cleanupTempScript(String path) {
        try {
            java.io.File f = new java.io.File(path);
            if (f.exists()) f.delete();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
}
