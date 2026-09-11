package de.spraener.nxtgen.target

import de.spraener.nxtgen.model.impl.ModelElementImpl
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

public class SnippetPositioningTest {

    static class TestModel extends ModelElementImpl {
        TestModel(String n) { setName(n); }
    }

    @Test
    void beforeSnippetInsertsBeforeTargetByAspect() {
        def mc = new TestModel("User")
        CodeTarget target = buildWithSection('s')

        // Add existing snippet with aspect 'base'
        target.getSection('s').add(new SingleLineSnippet('base', 'original'))

        target.forAspect('a', mc) {
            beforeSnippet 's', [aspect: 'base'], 'before'
        }

        def ordered = target.getSection('s').getSnippetsOrdered() as List
        assertEquals(2, ordered.size())
        assertEquals('before', evaluateSnippet(ordered[0]))
        assertEquals('original', evaluateSnippet(ordered[1]))
    }

    @Test
    void afterSnippetInsertsAfterTargetByAspect() {
        def mc = new TestModel("User")
        CodeTarget target = buildWithSection('s')

        target.getSection('s').add(new SingleLineSnippet('base', 'original'))

        target.forAspect('a', mc) {
            afterSnippet 's', [aspect: 'base'], 'after'
        }

        def ordered = target.getSection('s').getSnippetsOrdered() as List
        assertEquals(2, ordered.size())
        assertEquals('original', evaluateSnippet(ordered[0]))
        assertEquals('after', evaluateSnippet(ordered[1]))
    }

    @Test
    void beforeSnippetByElement() {
        def mc = new TestModel("User")
        CodeTarget target = buildWithSection('s')

        // Add snippet tied to mc
        target.forAspect('base', mc) {
            to 's', 'original'
        }

        target.forAspect('a', mc) {
            beforeSnippet 's', [element: mc], 'before'
        }

        def ordered = target.getSection('s').getSnippetsOrdered() as List
        assertEquals(2, ordered.size())
        assertEquals('before', evaluateSnippet(ordered[0]))
    }

    @Test
    void beforeSnippetByAspectAndElement() {
        def mc = new TestModel("User")
        CodeTarget target = buildWithSection('s')

        target.forAspect('base', mc) {
            to 's', 'original'
        }

        target.forAspect('a', mc) {
            beforeSnippet 's', [aspect: 'base', element: mc], 'before'
        }

        def ordered = target.getSection('s').getSnippetsOrdered() as List
        assertEquals(2, ordered.size())
        assertEquals('before', evaluateSnippet(ordered[0]))
    }

    @Test
    void beforeSnippetDoesNothingWhenTargetNotFound() {
        def mc = new TestModel("User")
        CodeTarget target = buildWithSection('s')

        target.getSection('s').add(new SingleLineSnippet('base', 'original'))

        target.forAspect('a', mc) {
            beforeSnippet 's', [aspect: 'nonexistent'], 'before'
        }

        def ordered = target.getSection('s').getSnippetsOrdered() as List
        assertEquals(1, ordered.size(), "Should not add snippet when target not found")
    }

    @Test
    void afterSnippetDoesNothingWhenTargetNotFound() {
        def mc = new TestModel("User")
        CodeTarget target = buildWithSection('s')

        target.getSection('s').add(new SingleLineSnippet('base', 'original'))

        target.forAspect('a', mc) {
            afterSnippet 's', [aspect: 'nonexistent'], 'after'
        }

        def ordered = target.getSection('s').getSnippetsOrdered() as List
        assertEquals(1, ordered.size(), "Should not add snippet when target not found")
    }

    @Test
    void newSnippetsInheritAspectFromContext() {
        def mc = new TestModel("User")
        CodeTarget target = buildWithSection('s')

        target.getSection('s').add(new SingleLineSnippet('base', 'original'))

        target.forAspect('logging', mc) {
            beforeSnippet 's', [aspect: 'base'], 'before'
        }

        def snippets = target.getSection('s').getSnippetsForAspect('logging')
        assertEquals(1, snippets.size(), "New snippet should have 'logging' aspect from context")
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
