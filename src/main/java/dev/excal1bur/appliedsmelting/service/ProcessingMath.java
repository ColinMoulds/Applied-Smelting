package dev.excal1bur.appliedsmelting.service;

/** Pure arithmetic shared by machine processing and its unit tests. */
public final class ProcessingMath {
    public static final long LAVA_UNITS_PER_MB = 1_000_000L;

    public record LavaConsumption(long wholeMb, long remainderUnits) {
        public LavaConsumption {
            if (wholeMb < 0) {
                throw new IllegalArgumentException("Whole lava consumption cannot be negative");
            }
            if (remainderUnits < 0 || remainderUnits >= LAVA_UNITS_PER_MB) {
                throw new IllegalArgumentException("Lava remainder must be less than one millibucket");
            }
        }
    }

    public static LavaConsumption accumulateLava(double mbPerWorkTick, int workTicks, long remainderUnits) {
        if (!Double.isFinite(mbPerWorkTick) || mbPerWorkTick < 0) {
            throw new IllegalArgumentException("Lava rate must be finite and non-negative");
        }
        if (workTicks < 0) {
            throw new IllegalArgumentException("Work ticks cannot be negative");
        }
        if (remainderUnits < 0 || remainderUnits >= LAVA_UNITS_PER_MB) {
            throw new IllegalArgumentException("Lava remainder must be less than one millibucket");
        }

        var workUnits = Math.max(0, Math.round(mbPerWorkTick * LAVA_UNITS_PER_MB * workTicks));
        var accumulatedUnits = saturatedAddNonNegative(remainderUnits, workUnits);
        return new LavaConsumption(
                accumulatedUnits / LAVA_UNITS_PER_MB,
                accumulatedUnits % LAVA_UNITS_PER_MB);
    }

    public static boolean canStartTargetJob(long target, long stored, long pending, long output) {
        if (target < 0 || stored < 0 || pending < 0 || output < 0) {
            throw new IllegalArgumentException("Target amounts cannot be negative");
        }
        if (target == 0) {
            return true;
        }
        var total = saturatedAddNonNegative(stored, pending);
        total = saturatedAddNonNegative(total, output);
        return total <= target;
    }

    public static long saturatedAddNonNegative(long left, long right) {
        if (left < 0 || right < 0) {
            throw new IllegalArgumentException("Saturated operands cannot be negative");
        }
        return left > Long.MAX_VALUE - right ? Long.MAX_VALUE : left + right;
    }

    private ProcessingMath() {
    }
}
