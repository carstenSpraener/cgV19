package de.spraener.nxtgen.target;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeTargetRendererTest {

    @Test
    void renderReturnsExpectedString() {
        CodeTargetRenderer renderer = target -> "rendered";
        assertEquals("rendered", renderer.render(null));
    }
}
