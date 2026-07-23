package dev.excal1bur.appliedsmelting.service;

import java.util.Locale;

import dev.excal1bur.appliedsmelting.core.AppliedSmeltingConfig;

/** ME Blast Furnace tier. Ordinal order also defines upgrade-kit progression order. */
public enum BlastFurnaceTier {
    DEFAULT(AppliedSmeltingConfig.BLAST_FURNACE_DEFAULT),
    MK1(AppliedSmeltingConfig.BLAST_FURNACE_MK1),
    MK2(AppliedSmeltingConfig.BLAST_FURNACE_MK2),
    MK3(AppliedSmeltingConfig.BLAST_FURNACE_MK3);

    private final AppliedSmeltingConfig.TierValues values;

    BlastFurnaceTier(AppliedSmeltingConfig.TierValues values) {
        this.values = values;
    }

    public double baseSpeedMultiplier() {
        return values.baseSpeedMultiplier().get();
    }

    public int upgradeSlots() {
        return values.upgradeSlots().get();
    }

    public double accelerationCap() {
        return values.accelerationCap().get();
    }

    public double idleDrawMultiplier() {
        return values.idleDrawMultiplier().get();
    }

    public double aeFuelDrawMultiplier() {
        return values.aeFuelDrawMultiplier().get();
    }

    public double lavaFuelDrawMultiplier() {
        return values.lavaFuelDrawMultiplier().get();
    }

    public double fuelEfficiencyMultiplier() {
        return values.fuelEfficiencyMultiplier().get();
    }

    public int baseQueueCapacity() {
        return values.baseQueueCapacity().get();
    }

    public int capacityCardCap() {
        return values.capacityCardCap().get();
    }

    /** The tier this one's upgrade kit is applied to (null for DEFAULT, which has no kit). */
    public BlastFurnaceTier previousTier() {
        var all = values();
        var index = ordinal();
        return index == 0 ? null : all[index - 1];
    }

    public String serializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static BlastFurnaceTier fromSerializedName(String name) {
        for (var tier : values()) {
            if (tier.serializedName().equals(name)) {
                return tier;
            }
        }
        return DEFAULT;
    }

    /** Maps an upgrade kit's mark level (1/2/3) to the tier it upgrades to, or null for 0/invalid. */
    public static BlastFurnaceTier fromKitLevel(int level) {
        return switch (level) {
            case 1 -> MK1;
            case 2 -> MK2;
            case 3 -> MK3;
            default -> null;
        };
    }
}
