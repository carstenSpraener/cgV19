package de.spraener.nxtgen.visitor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link EBeforeOrAfter}.
 */
class EBeforeOrAfterTest {

    @Test
    void declaresExactlyThreeConstants() {
        assertArrayEquals(new EBeforeOrAfter[] {EBeforeOrAfter.BEFORE, EBeforeOrAfter.AFTER, EBeforeOrAfter.BOTH},
                EBeforeOrAfter.values());
    }

    @Test
    void valueOfResolvesEachConstant() {
        assertNotNull(EBeforeOrAfter.valueOf("BEFORE"));
        assertNotNull(EBeforeOrAfter.valueOf("AFTER"));
        assertNotNull(EBeforeOrAfter.valueOf("BOTH"));
    }

    @Test
    void ordinalIsStable() {
        assertEquals(0, EBeforeOrAfter.BEFORE.ordinal());
        assertEquals(1, EBeforeOrAfter.AFTER.ordinal());
        assertEquals(2, EBeforeOrAfter.BOTH.ordinal());
    }
}
