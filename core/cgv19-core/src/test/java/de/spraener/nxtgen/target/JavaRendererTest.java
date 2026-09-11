package de.spraener.nxtgen.target;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JavaRendererTest {

    @Test
    void simpleSectionRendersSnippet() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        section.add(new SingleLineSnippet("test", "public class Foo {}"));
        target.addCodeSection("s1", section);

        JavaRenderer renderer = new JavaRenderer();
        String result = renderer.render(target);

        assertTrue(result.contains("public class Foo {}"));
    }

    @Test
    void multipleSectionsRenderInOrder() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection sectionA = new SimpleCodeSection();
        sectionA.add(new SingleLineSnippet("test", "section A"));
        SimpleCodeSection sectionB = new SimpleCodeSection();
        sectionB.add(new SingleLineSnippet("test", "section B"));
        target.addCodeSection("a", sectionA);
        target.addCodeSection("b", sectionB);

        JavaRenderer renderer = new JavaRenderer();
        String result = renderer.render(target);

        int aIdx = result.indexOf("section A");
        int bIdx = result.indexOf("section B");
        assertTrue(aIdx >= 0);
        assertTrue(bIdx >= 0);
        assertTrue(aIdx < bIdx, "A should appear before B");
    }

    @Test
    void emptySectionProducesNoOutput() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        target.addCodeSection("empty", section);

        JavaRenderer renderer = new JavaRenderer();
        String result = renderer.render(target);

        assertEquals("", result.trim());
    }

    @Test
    void withMarkersAddsCommentMarkers() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        section.add(new SingleLineSnippet("test", "code"));
        target.addCodeSection("s1", section);

        JavaRenderer renderer = new JavaRenderer().withMarkers();
        String result = renderer.render(target);

        assertTrue(result.contains("<<section id=s1>>"));
        assertTrue(result.contains("<</section:s1>>"));
    }

    @Test
    void nestedScopeIsIndented() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        section.add(new SingleLineSnippet("test", "outer"));
        CodeSection child = section.getOrCreateScope("inner", SimpleCodeSection::new);
        child.add(new SingleLineSnippet("test", "inner"));
        target.addCodeSection("s1", section);

        String result = new JavaRenderer().render(target);

        assertEquals("outer\n    inner\n", result);
    }

    @Test
    void inlineSectionRendersChildrenWithoutExtraIndent() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection outer = new SimpleCodeSection();
        NonEmptyPrefixedListSection impl = (NonEmptyPrefixedListSection) outer.getOrCreateScope(
                "impl", () -> new NonEmptyPrefixedListSection("java", "implements ", ", "));
        CodeSection group = impl.getOrCreateScope("group1", SimpleCodeSection::new);
        impl.add(new CodeBlockSnippet("a", null, "Serializable"));
        group.add(new CodeBlockSnippet("b", null, "Cloneable"));
        target.addCodeSection("s1", outer);

        String result = new JavaRenderer().render(target);

        // JavaRenderer renders one trimmed line per snippet; children must stay at the
        // section's indent level (4 spaces), not get an extra level.
        assertEquals("    implements\n    Serializable\n    ,\n    Cloneable\n", result);
    }

    @Test
    void markersAreIndentedForNestedScopes() {
        CodeTarget target = new CodeTarget();
        SimpleCodeSection section = new SimpleCodeSection();
        CodeSection child = section.getOrCreateScope("inner", SimpleCodeSection::new);
        child.add(new SingleLineSnippet("test", "code"));
        target.addCodeSection("s1", section);

        String result = new JavaRenderer().withMarkers().render(target);

        assertTrue(result.contains("    <<section id=inner>>"), result);
        assertTrue(result.contains("    <</section:inner>>"), result);
    }
}
