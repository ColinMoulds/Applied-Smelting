package dev.excal1bur.appliedsmelting.core;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Per-tier ME Smelter balance values. Starting points for playtesting, not final balance. */
public final class AppliedSmeltingConfig {
    public static final ModConfigSpec SPEC;
    public static final TierValues DEFAULT;
    public static final TierValues MK1;
    public static final TierValues MK2;
    public static final TierValues MK3;
    public static final TierValues BLAST_FURNACE;
    public static final TierValues SMOKER;

    public static final TierValues CRUCIBLE;

    static {
        var builder = new ModConfigSpec.Builder();
        DEFAULT = TierValues.define(builder, "default", 1.0, 8, 16.0, 1.0, 1.0, 1.0, 1.0, 1, 9);
        MK1 = TierValues.define(builder, "mk1", 1.0, 8, 16.0, 1.0, 1.0, 1.0, 1.0, 2, 9);
        MK2 = TierValues.define(builder, "mk2", 1.5, 8, 24.0, 0.85, 1.25, 1.25, 1.25, 3, 14);
        MK3 = TierValues.define(builder, "mk3", 2.25, 8, 32.0, 0.7, 1.5, 1.5, 1.5, 5, 20);
        // Blast Furnace/Smoker match the Default Smelter tier's multipliers - their inherent 2x speed
        // comes from vanilla's own recipe cooking time (100 ticks vs 200), not from baseSpeedMultiplier.
        BLAST_FURNACE = TierValues.define(builder, "blast_furnace", 1.0, 8, 16.0, 1.0, 1.0, 1.0, 1.0, 1, 9);
        SMOKER = TierValues.define(builder, "smoker", 1.0, 8, 16.0, 1.0, 1.0, 1.0, 1.0, 1, 9);
        CRUCIBLE = TierValues.define(builder, "crucible", 1.0, 8, 16.0, 1.0, 1.0, 1.0, 1.0, 1, 9);
        SPEC = builder.build();
    }

    private AppliedSmeltingConfig() {
    }

    public record TierValues(
            ModConfigSpec.DoubleValue baseSpeedMultiplier,
            ModConfigSpec.IntValue upgradeSlots,
            ModConfigSpec.DoubleValue accelerationCap,
            ModConfigSpec.DoubleValue idleDrawMultiplier,
            ModConfigSpec.DoubleValue aeFuelDrawMultiplier,
            ModConfigSpec.DoubleValue lavaFuelDrawMultiplier,
            ModConfigSpec.DoubleValue fuelEfficiencyMultiplier,
            ModConfigSpec.IntValue baseQueueCapacity,
            ModConfigSpec.IntValue capacityCardCap) {

        private static TierValues define(
                ModConfigSpec.Builder builder,
                String name,
                double baseSpeed,
                int upgradeSlots,
                double accelerationCap,
                double idleDrawMultiplier,
                double aeFuelDrawMultiplier,
                double lavaFuelDrawMultiplier,
                double fuelEfficiencyMultiplier,
                int baseQueueCapacity,
                int capacityCardCap) {
            builder.push(name);
            var values = new TierValues(
                    builder.comment("Base smelting speed multiplier before Acceleration Cards")
                            .defineInRange("baseSpeedMultiplier", baseSpeed, 0.1, 10.0),
                    builder.comment("Number of mixed upgrade card slots")
                            .defineInRange("upgradeSlots", upgradeSlots, 1, 64),
                    builder.comment("Maximum total speed multiplier achievable with Acceleration Cards")
                            .defineInRange("accelerationCap", accelerationCap, 1.0, 256.0),
                    builder.comment("Base idle AE draw multiplier before Energy Cards (1.0 = no change)")
                            .defineInRange("idleDrawMultiplier", idleDrawMultiplier, 0.1, 2.0),
                    builder.comment("Maximum AE-fuel draw rate multiplier before Energy Cards (1.0 = no change)")
                            .defineInRange("aeFuelDrawMultiplier", aeFuelDrawMultiplier, 0.1, 4.0),
                    builder.comment("Network-drawn lava draw rate multiplier (mB per work tick, before Fuel Efficiency Cards)")
                            .defineInRange("lavaFuelDrawMultiplier", lavaFuelDrawMultiplier, 0.1, 4.0),
                    builder.comment("Base fuel efficiency multiplier before Fuel Efficiency Cards")
                            .defineInRange("fuelEfficiencyMultiplier", fuelEfficiencyMultiplier, 0.1, 4.0),
                    builder.comment("Base network queue capacity contributed while a smelter of this tier is connected")
                            .defineInRange("baseQueueCapacity", baseQueueCapacity, 1, 9),
                    builder.comment(
                                    "Maximum total network queue capacity contributed via Capacity Cards"
                                            + " while a smelter of this tier is connected")
                            .defineInRange("capacityCardCap", capacityCardCap, 1, 64));
            builder.pop();
            return values;
        }
    }
}
