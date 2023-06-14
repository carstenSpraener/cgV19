package de.spraener.nxtgen.target;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CodeTargetTests {

    CodeTarget uut = new CodeTarget();
    SimpleCodeSection cbSectionA = new SimpleCodeSection();
    SimpleCodeSection cbSectionB = new SimpleCodeSection();

    @Test
    void testCodeSectionAdded() throws Exception {
        uut.addCodeSection("A",cbSectionA);
        assertNotNull(uut.getSection("A"));
        assertEquals(cbSectionA, uut.getSection("A"));
    }


    @Test
    void testCodeSectionRetrieveOrder() throws Exception {
        uut.withCodeSection("A",cbSectionA)
                .withCodeSection("B",cbSectionB)
                .withCodeSection("C",cbSectionA)
                .withCodeSection("D",cbSectionB)
        ;

        assertNotNull(uut.getSection("A"));
        assertEquals(cbSectionA, uut.getSection("A"));

        assertNotNull(uut.getSection("B"));
        assertEquals(cbSectionB, uut.getSection("B"));

        assertNotNull(uut.getSection("C"));
        assertEquals(cbSectionA, uut.getSection("C"));

        assertNotNull(uut.getSection("D"));
        assertEquals(cbSectionB, uut.getSection("D"));

        Assertions.assertThat(uut.getSectionsOrdered())
                .contains(
                        cbSectionA, cbSectionB, cbSectionA, cbSectionB
                );
    }

    @Test
    void testAddingSameKeyThrowsException() {
        uut.addCodeSection("A", cbSectionA);
        try {
            uut.addCodeSection("A", cbSectionB);
            fail();
        } catch( IllegalArgumentException xc) {
        } catch( Exception e ) {
            fail("Unexpected Exception", e);
        }
    }
}
