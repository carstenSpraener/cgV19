package de.spraener.nxtgen.target

import de.spraener.nxtgen.model.impl.ModelElementImpl
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

public class DynamicSectionTest {

    static class TestModel extends ModelElementImpl {
        TestModel(String n) { setName(n); }
    }

    @Test
    void addSectionAppendsAtEnd() {
        def mc = new TestModel("User")
        CodeTarget target = new CodeTarget()
        target.addCodeSection('header', SectionType.SIMPLE.create('header'))

        target.forAspect('a', mc) {
            addSection 'footer'
        }

        def sections = target.getSectionsOrdered() as List
        assertEquals(2, sections.size())
        assertEquals('footer', sections[1].getId())
    }

    @Test
    void addSectionWithType() {
        def mc = new TestModel("User")
        CodeTarget target = new CodeTarget()
        target.addCodeSection('header', SectionType.SIMPLE.create('header'))

        target.forAspect('a', mc) {
            addSection 'imports', SectionType.UNIQUE_LINES
        }

        def section = target.getSection('imports')
        assertNotNull(section)
        assertTrue(section instanceof UniqueLineSection)
    }

    @Test
    void addSectionAfterExisting() {
        def mc = new TestModel("User")
        CodeTarget target = new CodeTarget()
        target.addCodeSection('a', SectionType.SIMPLE.create('a'))
        target.addCodeSection('b', SectionType.SIMPLE.create('b'))
        target.addCodeSection('c', SectionType.SIMPLE.create('c'))

        target.forAspect('x', mc) {
            addSection('new', 'b')  // after: 'b'
        }

        def sections = target.getSectionsOrdered() as List
        assertEquals(4, sections.size())
        // 'new' should be after 'b', before 'c'
        assertEquals('a', sections[0].getId())
        assertEquals('b', sections[1].getId())
        assertEquals('new', sections[2].getId())
        assertEquals('c', sections[3].getId())
    }

    @Test
    void addSectionIdempotent() {
        def mc = new TestModel("User")
        CodeTarget target = new CodeTarget()
        target.addCodeSection('s', SectionType.SIMPLE.create('s'))

        target.forAspect('a', mc) {
            addSection 'new'
            addSection 'new'  // Should not duplicate
        }

        def sections = target.getSectionsOrdered() as List
        assertEquals(2, sections.size())
    }

    @Test
    void addSectionWithPrefixedListConfig() {
        def mc = new TestModel("User")
        CodeTarget target = new CodeTarget()
        target.addCodeSection('header', SectionType.SIMPLE.create('header'))

        target.forAspect('a', mc) {
            addSection 'impl', SectionType.PREFIXED_LIST, [prefix: 'implements ', sep: ', ']
        }

        def section = target.getSection('impl')
        assertTrue(section instanceof NonEmptyPrefixedListSection)

        // Verify it renders with prefix
        section.add(new SingleLineSnippet('x', 'Serializable'))
        StringBuilder sb = new StringBuilder()
        for (CodeSnippet s : section.getSnippetsOrdered()) {
            s.evaluate(sb)
        }
        assertTrue(sb.toString().contains('implements '), 'Should contain prefix')
    }
}
