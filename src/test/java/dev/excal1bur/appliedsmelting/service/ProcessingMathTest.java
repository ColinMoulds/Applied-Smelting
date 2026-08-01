package dev.excal1bur.appliedsmelting.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ProcessingMathTest {
    @Test
    void accumulatesHalfMillibucketAcrossTwoTicks() {
        var first = ProcessingMath.accumulateLava(0.5, 1, 0);
        assertEquals(0, first.wholeMb());
        assertEquals(500_000, first.remainderUnits());

        var second = ProcessingMath.accumulateLava(0.5, 1, first.remainderUnits());
        assertEquals(1, second.wholeMb());
        assertEquals(0, second.remainderUnits());
    }

    @Test
    void acceleratedBatchConsumesTheSameTotalAsIndividualTicks() {
        var batched = ProcessingMath.accumulateLava(0.625, 16, 0);

        long wholeMb = 0;
        long remainder = 0;
        for (int i = 0; i < 16; i++) {
            var tick = ProcessingMath.accumulateLava(0.625, 1, remainder);
            wholeMb += tick.wholeMb();
            remainder = tick.remainderUnits();
        }

        assertEquals(wholeMb, batched.wholeMb());
        assertEquals(remainder, batched.remainderUnits());
        assertEquals(10, batched.wholeMb());
    }

    @Test
    void rejectsInvalidFractionalInputs() {
        assertThrows(IllegalArgumentException.class, () -> ProcessingMath.accumulateLava(-0.1, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> ProcessingMath.accumulateLava(1, -1, 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> ProcessingMath.accumulateLava(1, 1, ProcessingMath.LAVA_UNITS_PER_MB));
    }

    @Test
    void targetBoundaryIncludesStoredPendingAndNewOutput() {
        assertTrue(ProcessingMath.canStartTargetJob(100, 60, 30, 10));
        assertFalse(ProcessingMath.canStartTargetJob(100, 60, 31, 10));
        assertTrue(ProcessingMath.canStartTargetJob(0, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE));
    }

    @Test
    void targetArithmeticCannotOverflowIntoAnAllowedResult() {
        assertFalse(ProcessingMath.canStartTargetJob(Long.MAX_VALUE - 1, Long.MAX_VALUE - 10, 20, 1));
        assertEquals(Long.MAX_VALUE, ProcessingMath.saturatedAddNonNegative(Long.MAX_VALUE - 1, 10));
    }
}
