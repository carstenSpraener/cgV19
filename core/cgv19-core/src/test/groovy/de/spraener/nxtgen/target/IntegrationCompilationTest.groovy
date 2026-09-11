package de.spraener.nxtgen.target

import de.spraener.nxtgen.model.impl.ModelElementImpl

import groovy.lang.Closure
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import java.nio.file.Path
import javax.tools.JavaCompiler
import javax.tools.ToolProvider

import static org.junit.jupiter.api.Assertions.*

public class IntegrationCompilationTest {

    @TempDir
    Path tempDir

    static class TestAttr extends ModelElementImpl {
        String type
        TestAttr(String n, String t) { setName(n); this.type = t }
        String getType() { return type }
    }

    static class TestClass extends ModelElementImpl {
        List<TestAttr> attrs = []
        TestClass(String n) { setName(n); }
        void addAttr(TestAttr a) { attrs.add(a); addChilds(a) }
        List<TestAttr> getAttributes() { return attrs }
    }

    @Test
    void fullFlowCompiles() {
        // 1) Create model class with attributes
        def mc = new TestClass("MyEntity")
        mc.addAttr(new TestAttr("name", "String"))
        mc.addAttr(new TestAttr("count", "int"))

        // 2) Build CodeTarget via DSL
        def target = buildEntityTarget(mc)

        // 3) Apply aspects
        applyJavaAspect(target, mc)
        applyLoggingAspect(target, mc)

        // 4) Render to Java source
        def renderer = new JavaRenderer()
        def source = renderer.render(target)

        // 5) Write to temp file
        def pkgDir = tempDir.resolve("com/example")
        pkgDir.toFile().mkdirs()
        def javaFile = pkgDir.resolve("MyEntity.java").toFile()
        javaFile.text = source

        // 6) Compile with javac
        def compiler = ToolProvider.getSystemJavaCompiler()
        def result = compiler.run(
            null, System.out, System.err,
            "-d", tempDir.toFile().absolutePath,
            javaFile.absolutePath
        )

        if (result != 0) {
            println "=== Generated source ==="
            println source
            println "=== End source ==="
        }
        assertEquals(0, result, "Generated Java code must compile without errors")

        // 7) Verify .class file exists
        def classFile = tempDir.resolve("com/example/MyEntity.class")
        assertTrue(classFile.toFile().exists(), "Compiled class file must exist")
    }

    @Test
    void multiAspectInterleavingCompiles() {
        def mc = new TestClass("User")
        mc.addAttr(new TestAttr("email", "String"))

        def target = buildEntityTarget(mc)
        applyJavaAspect(target, mc)
        applyLoggingAspect(target, mc)

        def renderer = new JavaRenderer()
        def source = renderer.render(target)

        def pkgDir = tempDir.resolve("com/example")
        pkgDir.toFile().mkdirs()
        def javaFile = pkgDir.resolve("User.java").toFile()
        javaFile.text = source

        def compiler = ToolProvider.getSystemJavaCompiler()
        def result = compiler.run(
            null, System.out, System.err,
            "-d", tempDir.toFile().absolutePath,
            javaFile.absolutePath
        )

        assertEquals(0, result, "Multi-aspect code must compile")
    }

    @Test
    void uniqueLinesDeduplicatesImports() {
        def mc = new TestClass("Test")

        def target = buildEntityTarget(mc)
        applyJavaAspect(target, mc)
        applyLoggingAspect(target, mc)
        applyEntityAspect(target, mc)

        def renderer = new JavaRenderer()
        def source = renderer.render(target)

        // Count import java.util.logging.Logger occurrences
        def loggerImports = source.findAll(/import java\.util\.logging\.Logger/)
        assertEquals(1, loggerImports.size(), "Import should be deduplicated")
    }

    // --- DSL Builders ---

    private CodeTarget buildEntityTarget(TestClass mc) {
        CodeTarget target = new CodeTarget()

        // Table of Contents
        target.addCodeSection('header', SectionType.SIMPLE.create('header'))
        target.addCodeSection('imports', SectionType.UNIQUE_LINES.create('imports'))
        target.addCodeSection('classDeclaration', SectionType.SIMPLE.create('classDeclaration'))
        target.addCodeSection('bodyStart', SectionType.SIMPLE.create('bodyStart'))
        target.addCodeSection('attributes', SectionType.SIMPLE.create('attributes'))
        target.addCodeSection('constructors', SectionType.SIMPLE.create('constructors'))
        target.addCodeSection('methods', SectionType.SIMPLE.create('methods'))
        target.addCodeSection('bodyEnd', SectionType.SIMPLE.create('bodyEnd'))

        return target
    }

    private void applyJavaAspect(CodeTarget target, TestClass mc) {
        target.forAspect('java', mc) {
            to 'header', "package com.example;"
            to 'classDeclaration', "public class ${mc.name} "
            to 'bodyStart', "{"
            to 'bodyEnd', "}"

            // Default constructor
            target.forAspect('java-constructor', mc) {
                to 'constructors', "    public ${mc.name}() {\n        super();\n    }"
            }

            // Attributes and getters/setters
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
        }
    }

    private void applyEntityAspect(CodeTarget target, TestClass mc) {
        def tableName = mc.name.toLowerCase()
        target.forAspect('jpa', mc) {
            to 'imports', "import javax.persistence.Entity;"
            to 'imports', "import javax.persistence.Table;"
            beforeSnippet 'classDeclaration', [aspect: 'java'], """@Entity
@Table("${tableName}")"""
        }
    }
}
