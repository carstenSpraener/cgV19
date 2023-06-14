package de.spraener.nxtgen.target;

import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CodeSnippetTest {

    @Test
    void testMatchesAspectAndModelElement() {
        ModelElement me = new ModelElementImpl();

        CodeSnippet snippet = new CodeSnippet("aspect", me) {
            @Override
            public void evaluate(StringBuilder sb) {}
        };

        assertTrue(snippet.matches("aspect", me));
        assertFalse(snippet.matches("aspect", null));
    }

    @Test
    void testSnippetWithNullModelElementMatchesAspectAndNull() {
        CodeSnippet snippet = new CodeSnippet("aspect") {
            public void evaluate(StringBuilder sb) {}
        };
        assertTrue(snippet.matches("aspect", null));
        assertFalse(snippet.matches("aspect", new ModelElementImpl()));
    }
}

