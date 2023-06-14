package de.spraener.nxtgen;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleStringCodeBlockTest {
    @Test
    public void testSimpleStringCodeBlock() throws Exception {
        SimpleStringCodeBlock scb = new SimpleStringCodeBlock("Hello World");
        assertNull(scb.getName());
        // FIXME: No newline out of thin air
        assertEquals("Hello World\n", scb.toCode());
        scb.println("What's going on?");
        assertEquals("""
                Hello World
                What's going on?
                """, scb.toCode());
    }

    @Test
    public void testCodeBlockAdding() throws Exception {
        SimpleStringCodeBlock scb = new SimpleStringCodeBlock("");
        SimpleStringCodeBlock scb2 = new SimpleStringCodeBlock("Embedded CodeBlock");
        scb.addCodeBlock(scb2);
        assertEquals("""
                                
                Embedded CodeBlock
                """, scb.toCode());
    }

    @Test
    public void testUnsupportedMethodWriteOutput() throws Exception {
        try {
            SimpleStringCodeBlock scb = new SimpleStringCodeBlock("");
            scb.writeOutput("targetDir");
            fail();
        } catch (UnsupportedOperationException xc) {
        }
    }

    @Test
    public void testUnsupportedMethodSetToFileStrategy() throws Exception {
        try {
            SimpleStringCodeBlock scb = new SimpleStringCodeBlock("");
            scb.setToFileStrategy(null);
            fail();
        } catch (UnsupportedOperationException xc) {
        }
    }
}
