package de.spraener.nxtgen;

import org.junit.Test;

import static org.junit.Assert.*;

public class IndentationCodeBlockTest {

    @Test
    public void println() {
        IndentationCodeBlock icb = new IndentationCodeBlock("// ");
        icb.println("Hello World");
        // FIXME: Newline out of thin air
        assertEquals("\n// Hello World\n", icb.toCode());
    }

    @Test
    public void getLinePrefix() {
        IndentationCodeBlock icb = new IndentationCodeBlock("// ");
        assertEquals("// ", icb.getLinePrefix());
    }
}
