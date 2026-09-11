package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.impl.ModelElementImpl;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SectionTypeTest {

    @Test
    void simpleCreatesSimpleCodeSection() {
        CodeSection section = SectionType.SIMPLE.create("test");
        assertNotNull(section);
        assertTrue(section instanceof SimpleCodeSection);
        assertEquals("test", section.getId());
    }

    @Test
    void uniqueLinesCreatesUniqueLineSection() {
        CodeSection section = SectionType.UNIQUE_LINES.create("imports");
        assertNotNull(section);
        assertTrue(section instanceof UniqueLineSection);
        assertEquals("imports", section.getId());
    }

    @Test
    void prefixedListCreatesNonEmptyPrefixedListSection() {
        CodeSection section = SectionType.PREFIXED_LIST.create("impl", ["prefix": "implements ", "sep": ", "]);
        assertNotNull(section);
        assertTrue(section instanceof NonEmptyPrefixedListSection);
        assertEquals("impl", section.getId());
    }

    @Test
    void simpleDeduplicationOff() {
        CodeSection section = SectionType.SIMPLE.create("s");
        section.add(new SingleLineSnippet("a", "line1"));
        section.add(new SingleLineSnippet("b", "line1"));
        assertEquals(2, section.getSnippetsOrdered().size());
    }

    @Test
    void uniqueLinesDeduplicates() {
        CodeSection section = SectionType.UNIQUE_LINES.create("imports");
        section.add(new SingleLineSnippet("a", "import foo;"));
        section.add(new SingleLineSnippet("b", "import foo;"));
        assertEquals(1, section.getSnippetsOrdered().size());
    }

    @Test
    void prefixedListRendersPrefixOnlyWhenNotEmpty() {
        CodeSection section = SectionType.PREFIXED_LIST.create("impl", ["prefix": "implements ", "sep": ", "]);
        assertEquals(0, section.getSnippetsOrdered().size());

        section.add(new SingleLineSnippet("x", "Serializable"));
        def snippets = section.getSnippetsOrdered() as java.util.List;
        assertEquals(2, snippets.size()); // prefix + snippet
    }

    @Test
    void prefixedListJoinsWithSeparator() {
        CodeSection section = SectionType.PREFIXED_LIST.create("impl", ["prefix": "implements ", "sep": ", "]);
        section.add(new SingleLineSnippet("x", "A"));
        section.add(new SingleLineSnippet("y", "B"));

        StringBuilder sb = new StringBuilder();
        for (CodeSnippet s : section.getSnippetsOrdered()) {
            s.evaluate(sb);
        }
        String result = sb.toString();
        assertTrue(result.contains("implements "), "Should contain prefix");
        assertTrue(result.contains(", "), "Should contain separator");
    }
}
