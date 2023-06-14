package de.spraener.nxtgen.target;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingleLineSnippetTest {

    @Test
    void testSingleLineSnippetAddsNewline() {
        SingleLineSnippet snippet = new SingleLineSnippet("aspect", "hello world");
        StringBuilder sb = new StringBuilder();
        snippet.evaluate(sb);

        assertEquals("hello world\n", sb.toString());
    }
}
