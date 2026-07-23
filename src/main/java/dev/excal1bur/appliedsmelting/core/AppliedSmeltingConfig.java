package dev.excal1bur.appliedsmelting.core;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Per-tier balance values for every machine type. Tier curve loosely follows Iron Furnaces/Mekanism progression conventions. */
public final class AppliedSmeltingConfig {
    public static final ModConfigSpec SPEC;

    public static final TierValues SMELTER_DEFAULT;
    public static final TierValues SMELTER_MK1;
    public static final TierValues SMELTER_MK2;
    public static final TierValues SMELTER_MK3;

    public static final TierValues BLAST_FURNACE_DEFAULT;
    public static final TierValues BLAST_FURNACE_MK1;
    public static final TierValues BLAST_FURNACE_MK2;
    public static final TierValues BLAST_FURNACE_MK3;

    public static final TierValues SMOKER_DEFAULT;
    public static final TierValues SMOKER_MK1;
    public static final TierValues SMOKER_MK2;
    public static final TierValues SMOKER_MK3;

    public static final TierValues CRUCIBLE_DEFAULT;
    public static final TierValues CRUCIBLE_MK1;
    public static final TierValues CRUCIBLE_MK2;
    public static final TierValues CRUCIBLE_MK3;

    static {
        var builder = new ModConfigSpec.Builder();
        SMELTER_DEFAULT = tier(builder, "smelter_default", 0);
        SMELTER_MK1 = tier(builder, "smelter_mk1", 1);
        SMELTER_MK2 = tier(builder, "smelter_mk2", 2);
        SMELTER_MK3 = tier(builder, "smelter_mk3", 3);

        BLAST_FURNACE_DEFAULT = tier(builder, "blast_furnace_default", 0);
        BLAST_FURNACE_MK1 = tier(builder, "blast_furnace_mk1", 1);
        BLAST_FURNACE_MK2 = tier(builder, "blast_furnace_mk2", 2);
        BLAST_FURNACE_MK3 = tier(builder, "blast_furnace_mk3", 3);

        SMOKER_DEFAULT = tier(builder, "smoker_default", 0);
        SMOKER_MK1 = tier(builder, "smoker_mk1", 1);
        SMOKER_MK2 = tier(builder, "smoker_mk2", 2);
        SMOKER_MK3 = tier(builder, "smoker_mk3", 3);

        CRUCIBLE_DEFAULT = tier(builder, "crucible_default", 0);
        CRUCIBLE_MK1 = tier(builder, "crucible_mk1", 1);
        CRUCIBLE_MK2 = tier(builder, "crucible_mk2", 2);
        CRUCIBLE_MK3 = tier(builder, "crucible_mk3", 3);
        SPEC = builder.build();
    }

    private AppliedSmeltingConfig() {
    }

    // Same relative curve for every machine type/tier 0-3, loosely modeled on Iron Furnaces'
    // per-tier cook-time steps and Mekanism's tiered power/speed scaling.
    private static TierValues tier(ModConfigSpec.Builder builder, String name, int mark) {
        return switch (mark) {
            case 1 -> TierValues.define(builder, name, 1.25, 8, 18.0, 0.95, 1.1, 1.1, 1.1, 2, 9);
            case 2 -> TierValues.define(builder, name, 1.75, 8, 24.0, 0.85, 1.25, 1.25, 1.25, 3, 14);
            case 3 -> TierValues.define(builder, name, 2.5, 8, 32.0, 0.7, 1.5, 1.5, 1.5, 5, 20);
            default -> TierValues.define(builder, name, 1.0, 8, 16.0, 1.0, 1.0, 1.0, 1.0, 1, 9);
        };
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
                    builder.comment("Base processing speed multiplier before Acceleration Cards")
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
                    builder.comment("Base network queue capacity contributed while a machine of this tier is connected")
                            .defineInRange("baseQueueCapacity", baseQueueCapacity, 1, 9),
                    builder.comment(
                                    "Maximum total network queue capacity contributed via Capacity Cards"
                                            + " while a machine of this tier is connected")
                            .defineInRange("capacityCardCap", capacityCardCap, 1, 64));
            builder.pop();
            return values;
        }
    }
}
