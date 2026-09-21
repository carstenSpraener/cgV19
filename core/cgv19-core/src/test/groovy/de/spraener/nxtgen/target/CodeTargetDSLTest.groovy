package de.spraener.nxtgen.target

import de.spraener.nxtgen.target.dsl.CodeTargetDSL
import org.junit.jupiter.api.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

/**
 * End-to-end tests for the <strong>production</strong> Groovy build DSL
 * ({@link de.spraener.nxtgen.target.dsl.CodeTargetDSL}).
 * <p>
 * This replaces the former CodeTargetBuildTest, which exercised a test-local
 * copy of the DSL instead of the production class.
 */
class CodeTargetDSLTest {

    // === build() basics ===

    @Test
    void buildWithoutRendererCreatesEmptyTarget() {
        CodeTarget target = CodeTargetDSL.build { }

        assertThat(target).isNotNull()
        assertThat(target.getRenderer()).isNull()
        assertThat(target.getSectionsOrdered()).isEmpty()
    }

    @Test
    void buildWithRendererWiresItToTheTarget() {
        JavaRenderer renderer = new JavaRenderer()

        CodeTarget target = CodeTargetDSL.build(renderer) { }

        assertThat(target.getRenderer()).isSameAs(renderer)
    }

    // === tableOfContents / section declarations ===

    @Test
    void tableOfContentsDeclaresSectionsInOrder() {
        CodeTarget target = CodeTargetDSL.build { CodeTargetDSL._BuildDSL ctx ->
            ctx.tableOfContents { CodeTargetDSL._BuildDSL toc ->
                toc.section('header')
                toc.section('imports', SectionType.UNIQUE_LINES)
                toc.section('body')
            }
        }

        List<CodeSection> sections = target.getSectionsOrdered() as List
        assertThat(sections).hasSize(3)
        assertThat(sections[0].getId()).isEqualTo('header')
        assertThat(sections[1].getId()).isEqualTo('imports')
        assertThat(sections[2].getId()).isEqualTo('body')
    }

    @Test
    void tocClosureReceivesTheBuildContext() {
        CodeTargetDSL._BuildDSL outerCtx = null
        CodeTargetDSL._BuildDSL tocCtx = null

        CodeTargetDSL.build { CodeTargetDSL._BuildDSL ctx ->
            outerCtx = ctx
            ctx.tableOfContents { CodeTargetDSL._BuildDSL inner ->
                tocCtx = inner
            }
        }

        assertThat(outerCtx).isNotNull()
        assertThat(tocCtx).isSameAs(outerCtx)
    }

    @Test
    void sectionOutsideTableOfContentsBlockAlsoWorks() {
        CodeTarget target = CodeTargetDSL.build { CodeTargetDSL._BuildDSL ctx ->
            ctx.section('x')
        }

        List<CodeSection> sections = target.getSectionsOrdered() as List
        assertThat(sections).hasSize(1)
        assertThat(sections[0].getId()).isEqualTo('x')
    }

    @Test
    void defaultSectionTypeIsSimple() {
        CodeTarget target = CodeTargetDSL.build { CodeTargetDSL._BuildDSL ctx ->
            ctx.section('s')
        }

        assertThat(target.getSection('s')).isInstanceOf(SimpleCodeSection)
        assertThat(target.getSection('s').getId()).isEqualTo('s')
    }

    @Test
    void explicitSectionTypesMaterializeTheRightClasses() {
        CodeTarget target = CodeTargetDSL.build { CodeTargetDSL._BuildDSL ctx ->
            ctx.tableOfContents { CodeTargetDSL._BuildDSL toc ->
                toc.section('simple', SectionType.SIMPLE)
                toc.section('unique', SectionType.UNIQUE_LINES)
                toc.section('prefixed', SectionType.PREFIXED_LIST)
            }
        }

        assertThat(target.getSection('simple')).isInstanceOf(SimpleCodeSection)
        assertThat(target.getSection('unique')).isInstanceOf(UniqueLineSection)
        assertThat(target.getSection('prefixed')).isInstanceOf(NonEmptyPrefixedListSection)
    }

    @Test
    void duplicateSectionIdIsRejected() {
        assertThatThrownBy({
            CodeTargetDSL.build { CodeTargetDSL._BuildDSL ctx ->
                ctx.section('a')
                ctx.section('a')
            }
        }).isInstanceOf(IllegalArgumentException)
         .hasMessageContaining('a')
    }

    // === section semantics through the DSL ===

    @Test
    void uniqueLinesSectionDeduplicatesSnippets() {
        CodeTarget target = CodeTargetDSL.build { CodeTargetDSL._BuildDSL ctx ->
            ctx.tableOfContents { CodeTargetDSL._BuildDSL toc ->
                toc.section('imports', SectionType.UNIQUE_LINES)
            }
        }

        target.append('imports', 'import java.util.List;')
        target.append('imports', 'import java.util.List;')

        assertThat(target.getSection('imports').getSnippetsOrdered()).hasSize(1)
    }

    @Test
    void prefixedListWithConfigAppliesPrefixAndSeparator() {
        CodeTarget target = CodeTargetDSL.build { CodeTargetDSL._BuildDSL ctx ->
            ctx.tableOfContents { CodeTargetDSL._BuildDSL toc ->
                toc.section('impl', SectionType.PREFIXED_LIST, [prefix: 'implements ', sep: '; '])
            }
        }

        NonEmptyPrefixedListSection impl = target.getSection('impl') as NonEmptyPrefixedListSection
        impl.add(new SingleLineSnippet('x', 'Serializable'))
        impl.add(new SingleLineSnippet('y', 'Cloneable'))

        // prefix + snippet1 + separator + snippet2 (SingleLineSnippet appends a newline)
        assertThat(renderSection(impl)).isEqualTo('implements Serializable\n; Cloneable\n')
    }

    @Test
    void prefixedListWithoutConfigUsesDefaults() {
        CodeTarget target = CodeTargetDSL.build { CodeTargetDSL._BuildDSL ctx ->
            ctx.tableOfContents { CodeTargetDSL._BuildDSL toc ->
                toc.section('impl', SectionType.PREFIXED_LIST)
            }
        }

        NonEmptyPrefixedListSection impl = target.getSection('impl') as NonEmptyPrefixedListSection
        impl.add(new SingleLineSnippet('x', 'A'))
        impl.add(new SingleLineSnippet('y', 'B'))

        // default: empty prefix, separator ", " (SingleLineSnippet appends a newline)
        assertThat(renderSection(impl)).isEqualTo('A\n, B\n')
    }

    @Test
    void emptyPrefixedListRendersNothing() {
        CodeTarget target = CodeTargetDSL.build { CodeTargetDSL._BuildDSL ctx ->
            ctx.tableOfContents { CodeTargetDSL._BuildDSL toc ->
                toc.section('impl', SectionType.PREFIXED_LIST, [prefix: 'implements ', sep: '; '])
            }
        }

        assertThat(renderSection(target.getSection('impl'))).isEmpty()
    }

    // === full render through JavaRenderer ===

    @Test
    void buildResultRendersThroughJavaRendererInSectionOrder() {
        JavaRenderer renderer = new JavaRenderer()

        CodeTarget target = CodeTargetDSL.build(renderer) { CodeTargetDSL._BuildDSL ctx ->
            ctx.tableOfContents { CodeTargetDSL._BuildDSL toc ->
                toc.section('header')
                toc.section('imports', SectionType.UNIQUE_LINES)
                toc.section('body')
            }
        }

        target.forAspect('java') { to 'header', 'package demo;' }
        target.forAspect('logging') { to 'imports', 'import java.util.List;' }
        target.forAspect('java') { to 'body', 'class Demo {}' }

        String source = renderer.render(target)

        assertThat(source).contains('package demo;')
                          .contains('import java.util.List;')
                          .contains('class Demo {}')

        // sections must appear in table-of-contents order
        int iHeader = source.indexOf('package demo;')
        int iImport = source.indexOf('import java.util.List;')
        int iBody = source.indexOf('class Demo {}')
        assertThat(iHeader).isLessThan(iImport)
        assertThat(iImport).isLessThan(iBody)
    }

    @Test
    void forAspectOnBuildResultIsChainable() {
        CodeTarget target = CodeTargetDSL.build { CodeTargetDSL._BuildDSL ctx ->
            ctx.section('s')
        }

        CodeTarget result = target.forAspect('a') { to 's', 'line1' }
                                 .forAspect('b') { to 's', 'line2' }

        assertThat(result).isSameAs(target)
        assertThat(target.getSection('s').getSnippetsOrdered()).hasSize(2)
    }

    // === helpers ===

    private static String renderSection(CodeSection section) {
        StringBuilder sb = new StringBuilder()
        for (CodeSnippet snippet : section.getSnippetsOrdered()) {
            snippet.evaluate(sb)
        }
        return sb.toString()
    }
}
