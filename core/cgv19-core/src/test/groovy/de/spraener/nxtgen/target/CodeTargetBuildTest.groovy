package de.spraener.nxtgen.target

import groovy.lang.Binding
import groovy.lang.Closure
import groovy.lang.GroovyShell
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

public class CodeTargetBuildTest {

    @Test
    void buildCreatesCodeTarget() {
        Binding binding = new Binding()
        binding.setVariable("SectionType", SectionType.class)

        // Create the DSL body as an explicit closure
        Closure buildBody = { DslImpl ctx ->
            ctx.tableOfContents({ DslImpl tocCtx ->
                tocCtx.section('header')
                tocCtx.section('body')
            })
        }

        CodeTarget target = runBuild(buildBody)
        assertNotNull(target)
    }

    @Test
    void buildCreatesSectionsInOrder() {
        Closure buildBody = { DslImpl ctx ->
            ctx.tableOfContents({ DslImpl tocCtx ->
                tocCtx.section('a')
                tocCtx.section('b')
                tocCtx.section('c')
            })
        }

        CodeTarget target = runBuild(buildBody)
        java.util.List<CodeSection> sections = new java.util.ArrayList<>(target.getSectionsOrdered())
        assertEquals(3, sections.size())
        assertEquals("a", sections.get(0).getId())
        assertEquals("b", sections.get(1).getId())
        assertEquals("c", sections.get(2).getId())
    }

    @Test
    void buildWithSectionTypeSimple() {
        Closure buildBody = { DslImpl ctx ->
            ctx.tableOfContents({ DslImpl tocCtx ->
                tocCtx.section('s', SectionType.SIMPLE)
            })
        }

        CodeTarget target = runBuild(buildBody)
        assertTrue(target.getSection("s") instanceof SimpleCodeSection)
    }

    @Test
    void buildWithUniqueLines() {
        Closure buildBody = { DslImpl ctx ->
            ctx.tableOfContents({ DslImpl tocCtx ->
                tocCtx.section('imports', SectionType.UNIQUE_LINES)
            })
        }

        CodeTarget target = runBuild(buildBody)
        assertTrue(target.getSection("imports") instanceof UniqueLineSection)
    }

    @Test
    void buildWithPrefixedListAndConfig() {
        Closure buildBody = { DslImpl ctx ->
            ctx.tableOfContents({ DslImpl tocCtx ->
                tocCtx.section('impl', SectionType.PREFIXED_LIST, [prefix: 'implements ', sep: ', '])
            })
        }

        CodeTarget target = runBuild(buildBody)
        assertTrue(target.getSection("impl") instanceof NonEmptyPrefixedListSection)

        target.getSection("impl").add(new SingleLineSnippet("x", "Serializable"))
        StringBuilder sb = new StringBuilder()
        for (CodeSnippet s : target.getSection("impl").getSnippetsOrdered()) {
            s.evaluate(sb)
        }
        assertTrue(sb.toString().contains("implements "), "Should contain prefix")
    }

    @Test
    void buildWithRenderer() {
        Closure buildBody = { DslImpl ctx ->
            ctx.tableOfContents({ DslImpl tocCtx ->
                tocCtx.section('s')
            })
        }

        CodeTarget target = runBuild(new JavaRenderer(), buildBody)
        assertNotNull(target.getRenderer())
    }

    @Test
    void defaultSectionTypeIsSimple() {
        Closure buildBody = { DslImpl ctx ->
            ctx.tableOfContents({ DslImpl tocCtx ->
                tocCtx.section('default')
            })
        }

        CodeTarget target = runBuild(buildBody)
        assertTrue(target.getSection("default") instanceof SimpleCodeSection)
    }

    private CodeTarget runBuild(Closure buildBody) {
        return runBuild(null, buildBody)
    }

    private CodeTarget runBuild(CodeTargetRenderer renderer, Closure buildBody) {
        CodeTarget target = new CodeTarget()
        if (renderer != null) target.setRenderer(renderer)
        DslImpl dsl = new DslImpl(target)
        buildBody.call(dsl)
        return target
    }

    static class DslImpl {
        private final CodeTarget target

        DslImpl(CodeTarget target) { this.target = target }

        void tableOfContents(Closure toc) {
            toc.call(this)
        }

        void section(String id) {
            target.addCodeSection(id, SectionType.SIMPLE.create(id))
        }

        void section(String id, SectionType type) {
            target.addCodeSection(id, type.create(id))
        }

        void section(String id, SectionType type, Map<String, Object> config) {
            target.addCodeSection(id, type.create(id, config))
        }
    }
}
