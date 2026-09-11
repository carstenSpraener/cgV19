package de.spraener.nxtgen.target;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CodeTargetRendererIntegrationTest {

    @Test
    void rendererDefaultIsNull() {
        CodeTarget target = new CodeTarget();
        assertNull(target.getRenderer());
    }

    @Test
    void setAndGetRenderer() {
        CodeTarget target = new CodeTarget();
        CodeTargetRenderer renderer = t -> "rendered";

        target.setRenderer(renderer);
        assertEquals(renderer, target.getRenderer());
    }
}
