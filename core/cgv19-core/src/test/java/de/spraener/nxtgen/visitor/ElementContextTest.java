package de.spraener.nxtgen.visitor;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.target.CodeSection;
import de.spraener.nxtgen.target.CodeSnippet;
import de.spraener.nxtgen.target.CodeTarget;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class ElementContextTest {

    @Test
    void getCodeTarget_returnsExactInstance() {
        CodeTarget ct = new CodeTarget();
        ModelElement el = new ModelElementImpl();
        ElementContext ctx = new ElementContext(ct, el);

        assertSame(ct, ctx.getCodeTarget());
    }

    @Test
    void getElement_returnsExactInstance() {
        CodeTarget ct = new CodeTarget();
        ModelElement el = new ModelElementImpl();
        ElementContext ctx = new ElementContext(ct, el);

        assertSame(el, ctx.getElement());
    }

    @Test
    void append_returnsFluentSameContext() {
        CodeTarget ct = new CodeTarget();
        ModelElement el = new ModelElementImpl();

        ElementContext ctx = new ElementContext(ct, el);
        Object result = ctx.append("FIELDS", "private String name;");

        assertSame(ctx, result);
    }

    @Test
    void append_verifiesCodeLandedInTarget() {
        CodeTarget ct = new CodeTarget();
        ModelElement el = new ModelElementImpl();

        ElementContext ctx = new ElementContext(ct, el);
        // append must self-create the FIELDS section on a fresh target (no manual pre-creation)
        ctx.append("FIELDS", "private String name;");

        // Read back via getSection → CodeSection → snippets → evaluate
        CodeSection section = ct.getSection("FIELDS");
        assertNotNull(section, "FIELDS section must exist after append");

        Collection<CodeSnippet> snippets = section.getSnippetsOrdered();
        assertFalse(snippets.isEmpty(), "Section must contain at least one snippet");

        StringBuilder sb = new StringBuilder();
        for (CodeSnippet snippet : snippets) {
            snippet.evaluate(sb);
        }

        String rendered = sb.toString();
        assertTrue(rendered.contains("private String name;"),
                "Rendered section content should contain the appended code. Got: " + rendered);
    }
}
