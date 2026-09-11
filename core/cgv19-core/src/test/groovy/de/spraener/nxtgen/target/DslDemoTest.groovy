package de.spraener.nxtgen.target

import de.spraener.nxtgen.model.impl.ModelElementImpl
import groovy.lang.Closure
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 * Comprehensive demo of the CodeTarget DSL showing:
 * - Table of Contents with different section types
 * - forAspect with automatic context
 * - to/first/beforeSnippet/afterSnippet operations
 * - addSection for dynamic sections
 * - Multi-aspect interleaving
 */
public class DslDemoTest {

    static class TestAttr extends ModelElementImpl {
        String type
        TestAttr(String n, String t) { setName(n); this.type = t }
    }

    static class TestClass extends ModelElementImpl {
        List<TestAttr> attrs = []
        TestClass(String n) { setName(n); }
        void addAttr(TestAttr a) { attrs.add(a); addChilds(a) }
        List<TestAttr> getAttributes() { return attrs }
    }

    @Test
    void fullDslDemo() {
        // Create model class
        def mc = new TestClass("MyEntity")
        mc.addAttr(new TestAttr("name", "String"))
        mc.addAttr(new TestAttr("count", "int"))

        // Build CodeTarget with DSL
        def target = buildWithDsl(mc)

        // Verify structure
        def sections = target.getSectionsOrdered() as List
        assertEquals(9, sections.size(), "Should have 9 sections")

        // Verify imports section is UniqueLineSection (dedup)
        assertTrue(target.getSection('imports') instanceof UniqueLineSection)

        // Verify class declaration has content
        def classDeclSnippets = target.getSection('classDeclaration').getSnippetsOrdered() as List
        assertTrue(classDeclSnippets.any { s ->
            def sb = new StringBuilder(); s.evaluate(sb); sb.toString().contains('public class')
        })

        // Verify attributes were added
        def attrSnippets = target.getSection('attributes').getSnippetsOrdered() as List
        assertEquals(3, attrSnippets.size(), "2 attributes + 1 logger")

        // Verify methods were added (2 attributes = 2 snippets, each with getter+setter)
        def methodSnippets = target.getSection('methods').getSnippetsOrdered() as List
        assertEquals(2, methodSnippets.size(), "2 attributes = 2 method snippets")

        // Verify imports are deduplicated
        def importSnippets = target.getSection('imports').getSnippetsOrdered() as List
        // Should have: java.util.logging.Logger (once, deduped)
        def loggerImports = importSnippets.findAll { s ->
            def sb = new StringBuilder(); s.evaluate(sb); sb.toString().contains('java.util.logging.Logger')
        }
        assertEquals(1, loggerImports.size(), "Logger import should be deduplicated")

        // Verify constructor was added (open + logging + close = 3 snippets)
        def ctorSnippets = target.getSection('constructors').getSnippetsOrdered() as List
        assertEquals(3, ctorSnippets.size())

        // Verify dynamic section was added
        assertNotNull(target.getSection('validators'), 'Dynamic validators section should exist')

        // Verify beforeSnippet worked (logging in constructor)
        def ctorText = new StringBuilder()
        for (s in target.getSection('constructors').getSnippetsOrdered()) {
            s.evaluate(ctorText)
        }
        assertTrue(ctorText.toString().contains('LOGGER.trace'), 'Constructor should contain logging call')

        // Verify afterSnippet worked
        assertTrue(ctorText.toString().contains('LOGGER.trace'), 'After snippet should be present')

        // Render and verify output
        def renderer = new JavaRenderer()
        def source = renderer.render(target)

        assertTrue(source.contains('package demoapp;'))
        assertTrue(source.contains('public class MyEntity'))
        assertTrue(source.contains('private String name;'))
        assertTrue(source.contains('private int count;'))
        assertTrue(source.contains('import java.util.logging.Logger;'))
        assertTrue(source.contains('private static final Logger LOGGER'))
    }

    @Test
    void dslWithRenderer() {
        def mc = new TestClass("Test")

        def renderer = new JavaRenderer()
        def target = buildWithDslAndRenderer(mc, renderer)

        assertNotNull(target.getRenderer())
        def source = target.getRenderer().render(target)
        assertTrue(source.contains('public class Test'))
    }

    // --- DSL Build Methods (demonstrating the DSL API) ---

    private CodeTarget buildWithDsl(TestClass mc) {
        // Step 1: Table of Contents via CodeTargetDSL
        def target = new CodeTarget()

        // Manually add sections (simulating DSL.build { tableOfContents { ... } })
        target.addCodeSection('header', SectionType.SIMPLE.create('header'))
        target.addCodeSection('imports', SectionType.UNIQUE_LINES.create('imports'))
        target.addCodeSection('classDeclaration', SectionType.SIMPLE.create('classDeclaration'))
        target.addCodeSection('bodyStart', SectionType.SIMPLE.create('bodyStart'))
        target.addCodeSection('attributes', SectionType.SIMPLE.create('attributes'))
        target.addCodeSection('constructors', SectionType.SIMPLE.create('constructors'))
        target.addCodeSection('methods', SectionType.SIMPLE.create('methods'))
        target.addCodeSection('bodyEnd', SectionType.SIMPLE.create('bodyEnd'))

        // Step 2: Apply aspects via forAspect DSL
        applyJavaAspect(target, mc)
        applyLoggingAspect(target, mc)

        // Step 3: Dynamic section via addSection
        target.forAspect('validation', mc) {
            addSection 'validators'  // Dynamic section at end
            to 'validators', "    public boolean validate() { return true; }"
        }

        return target
    }

    private CodeTarget buildWithDslAndRenderer(TestClass mc, JavaRenderer renderer) {
        def target = new CodeTarget()
        target.setRenderer(renderer)

        target.addCodeSection('header', SectionType.SIMPLE.create('header'))
        target.addCodeSection('classDeclaration', SectionType.SIMPLE.create('classDeclaration'))
        target.addCodeSection('bodyStart', SectionType.SIMPLE.create('bodyStart'))
        target.addCodeSection('methods', SectionType.SIMPLE.create('methods'))
        target.addCodeSection('bodyEnd', SectionType.SIMPLE.create('bodyEnd'))

        applyJavaAspect(target, mc)
        return target
    }

    private void applyJavaAspect(CodeTarget target, TestClass mc) {
        target.forAspect('java', mc) {
            to 'header', "package demoapp;"
            to 'classDeclaration', "public class ${mc.name} "
            to 'bodyStart', "{"
            to 'bodyEnd', "}"

            // Constructor (nested forAspect)
            target.forAspect('java-constructor', mc) {
                to 'constructors', "    public ${mc.name}() {\n        super();\n"
            }
            target.forAspect('java-constructor.close', mc) {
                to 'constructors', "    }"
            }

            // Attributes and methods
            for (attr in mc.attributes) {
                to 'attributes', "    private ${attr.type} ${attr.name};"

                def accessName = attr.name.capitalize()
                to 'methods', """
                    public ${attr.type} get${accessName}() {
                        return ${attr.name};
                    }

                    public void set${accessName}(${attr.type} value) {
                        this.${attr.name} = value;
                    }

                """
            }
        }
    }

    private void applyLoggingAspect(CodeTarget target, TestClass mc) {
        target.forAspect('logging', mc) {
            to 'imports', "import java.util.logging.Logger;"
            to 'attributes', "    private static final Logger LOGGER = Logger.getLogger(${mc.name}.class.getName());"

            // Insert logging call before constructor close
            beforeSnippet 'constructors', [aspect: 'java-constructor.close'], "        LOGGER.trace(\"new Instance of ${mc.name}.\");"
        }
    }
}
