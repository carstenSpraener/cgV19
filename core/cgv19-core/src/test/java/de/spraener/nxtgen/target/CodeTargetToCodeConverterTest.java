package de.spraener.nxtgen.target;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class CodeTargetToCodeConverterTest {

    @Test
    void flatRenderingIsUnchanged() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        section.add(new SingleLineSnippet("a", "line one"));
        section.add(new CodeBlockSnippet("b", null, "block line1\nblock line2"));
        target.addCodeSection("s1", section);

        String code = new CodeTargetToCodeConverter(target).toString();

        assertEquals("line one\nblock line1\nblock line2", code);
    }

    @Test
    void nestedScopeIsIndented() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        section.add(new SingleLineSnippet("a", "outer"));
        CodeSection child = section.getOrCreateScope("inner", SimpleCodeSection::new);
        child.add(new SingleLineSnippet("b", "inner line"));
        target.addCodeSection("s1", section);

        String code = new CodeTargetToCodeConverter(target).toString();

        assertEquals("outer\n    inner line\n", code);
    }

    @Test
    void multiLineSnippetIsIndentedOnEveryLine() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        CodeSection child = section.getOrCreateScope("inner", SimpleCodeSection::new);
        child.add(new CodeBlockSnippet("b", null, "line1\nline2"));
        target.addCodeSection("s1", section);

        String code = new CodeTargetToCodeConverter(target).toString();

        assertEquals("    line1\n    line2", code);
    }

    @Test
    void grandchildScopeIsIndentedTwice() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        CodeSection child = section.getOrCreateScope("c1", SimpleCodeSection::new);
        CodeSection grandchild = child.getOrCreateScope("c2", SimpleCodeSection::new);
        grandchild.add(new SingleLineSnippet("x", "deep"));
        target.addCodeSection("s1", section);

        String code = new CodeTargetToCodeConverter(target).toString();

        assertEquals("        deep\n", code);
    }

    @Test
    void snippetWithoutTrailingNewlineContinuesOnSameLine() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        CodeSection child = section.getOrCreateScope("inner", SimpleCodeSection::new);
        child.add(new CodeBlockSnippet("a", null, "no newline"));
        child.add(new SingleLineSnippet("b", "next line"));
        target.addCodeSection("s1", section);

        String code = new CodeTargetToCodeConverter(target).toString();

        assertEquals("    no newlinenext line\n", code);
    }

    @Test
    void inlineSectionRendersChildrenFlatWithoutExtraIndent() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection outer = new SimpleCodeSection();
        NonEmptyPrefixedListSection impl = (NonEmptyPrefixedListSection) outer.getOrCreateScope(
                "impl", () -> new NonEmptyPrefixedListSection("java", "implements ", ", "));
        CodeSection group = impl.getOrCreateScope("group1", SimpleCodeSection::new);
        impl.add(new CodeBlockSnippet("a", null, "Serializable"));
        group.add(new CodeBlockSnippet("b", null, "Cloneable"));
        target.addCodeSection("s1", outer);

        String code = new CodeTargetToCodeConverter(target).toString();

        assertEquals("    implements Serializable, Cloneable", code);
    }

    @Test
    void markersAreEmittedForNestedScopes() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        section.add(new SingleLineSnippet("a", "outer"));
        CodeSection child = section.getOrCreateScope("inner", SimpleCodeSection::new);
        child.add(new SingleLineSnippet("b", "inner line"));
        target.addCodeSection("s1", section);

        String code = new CodeTargetToCodeConverter(target).withMarkers(true).toString();

        assertTrue(code.contains("<<section id=s1>>"), code);
        assertTrue(code.contains("<<section id=inner>>"), code);
        assertTrue(code.contains("<</section:inner>>"), code);
    }

    @Test
    void emptyChildScopeRendersNothing() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        section.add(new SingleLineSnippet("a", "outer"));
        section.getOrCreateScope("empty", SimpleCodeSection::new);
        target.addCodeSection("s1", section);

        String code = new CodeTargetToCodeConverter(target).toString();

        assertEquals("outer\n", code);
    }
}
