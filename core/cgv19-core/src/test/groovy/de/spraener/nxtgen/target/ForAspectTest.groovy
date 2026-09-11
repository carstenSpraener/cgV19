package de.spraener.nxtgen.target

import de.spraener.nxtgen.model.impl.ModelElementImpl
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

public class ForAspectTest {

    static class TestModel extends ModelElementImpl {
        TestModel(String n) { setName(n); }
    }

    @Test
    void forAspectAddsSnippetWithCorrectAspect() {
        def mc = new TestModel("User")
        CodeTarget target = buildWithSection('attrs')

        target.forAspect('logging', mc) {
            to 'attrs', 'LOGGER field'
        }

        def snippets = target.getSection('attrs').getSnippetsForAspect('logging')
        assertEquals(1, snippets.size())

        StringBuilder sb = new StringBuilder()
        snippets[0].snippet.evaluate(sb)
        assertEquals('LOGGER field\n', sb.toString())
    }

    @Test
    void forAspectAddsSnippetWithCorrectElement() {
        def mc = new TestModel("User")
        CodeTarget target = buildWithSection('attrs')

        target.forAspect('logging', mc) {
            to 'attrs', 'LOGGER field'
        }

        def snippets = target.getSection('attrs').getSnippetsForAspectAndModelElement('logging', mc)
        assertEquals(1, snippets.size())
    }

    @Test
    void toAppendsToEnd() {
        def mc = new TestModel("User")
        CodeTarget target = buildWithSection('s')

        target.getSection('s').add(new SingleLineSnippet('base', 'original'))

        target.forAspect('a', mc) {
            to 's', 'added'
        }

        def ordered = target.getSection('s').getSnippetsOrdered() as List
        assertEquals(2, ordered.size())
        assertEquals('original', evaluateSnippet(ordered[0]))
        assertEquals('added', evaluateSnippet(ordered[1]))
    }

    @Test
    void firstInsertsAtStart() {
        def mc = new TestModel("User")
        CodeTarget target = buildWithSection('s')

        target.getSection('s').add(new SingleLineSnippet('base', 'original'))

        target.forAspect('a', mc) {
            first 's', 'first'
        }

        def ordered = target.getSection('s').getSnippetsOrdered() as List
        assertEquals(2, ordered.size())
        assertEquals('first', evaluateSnippet(ordered[0]))
        assertEquals('original', evaluateSnippet(ordered[1]))
    }

    @Test
    void multipleToCallsPreserveOrder() {
        def mc = new TestModel("User")
        CodeTarget target = buildWithSection('s')

        target.forAspect('a', mc) {
            to 's', 'one'
            to 's', 'two'
            to 's', 'three'
        }

        def ordered = target.getSection('s').getSnippetsOrdered() as List
        assertEquals(3, ordered.size())
        assertEquals('one', evaluateSnippet(ordered[0]))
        assertEquals('two', evaluateSnippet(ordered[1]))
        assertEquals('three', evaluateSnippet(ordered[2]))
    }

    @Test
    void contextIsCleanedAfterForAspect() {
        def mc = new TestModel("User")
        CodeTarget target = buildWithSection('s')

        target.forAspect('a', mc) {
            to 's', 'snippet'
        }

        // getActiveContext() returns EMPTY_CONTEXT (not null) when stack is empty
        def ctx = CodeTargetContext.getActiveContext()
        assertNull(ctx.getAspect(), "Aspect should be null after forAspect")
    }

    @Test
    void nestedForAspectWorks() {
        def mc = new TestModel("User")
        CodeTarget target = buildWithSection('s')

        target.forAspect('outer', mc) {
            to 's', 'outer'
            target.forAspect('inner', mc) {
                to 's', 'inner'
            }
        }

        def ordered = target.getSection('s').getSnippetsOrdered() as List
        assertEquals(2, ordered.size())
    }

    // --- Helpers ---

    private CodeTarget buildWithSection(String sectionId) {
        CodeTarget target = new CodeTarget()
        target.addCodeSection(sectionId, SectionType.SIMPLE.create(sectionId))
        return target
    }

    private String evaluateSnippet(CodeSnippet snippet) {
        StringBuilder sb = new StringBuilder()
        snippet.evaluate(sb)
        return sb.toString().trim()
    }
}
