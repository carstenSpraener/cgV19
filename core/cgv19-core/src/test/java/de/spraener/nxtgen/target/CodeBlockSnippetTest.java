package de.spraener.nxtgen.target;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeBlockSnippetTest {

    @Test
    void testEvaluateDoesNotAppendNewline() {
        CodeBlockSnippet cbs = new CodeBlockSnippet("aspect", null, "Hello World!\n");
        StringBuilder sb = new StringBuilder();
        cbs.evaluate(sb);
        assertEquals("Hello World!\n", sb.toString());
    }
}
