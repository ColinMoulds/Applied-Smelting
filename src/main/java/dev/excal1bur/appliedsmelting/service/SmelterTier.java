package dev.excal1bur.appliedsmelting.service;

import java.util.Locale;

import dev.excal1bur.appliedsmelting.core.AppliedSmeltingConfig;

/** ME Smelter tier. Ordinal order also defines upgrade-kit progression order. */
public enum SmelterTier {
    DEFAULT(AppliedSmeltingConfig.DEFAULT),
    MK1(AppliedSmeltingConfig.MK1),
    MK2(AppliedSmeltingConfig.MK2),
    MK3(AppliedSmeltingConfig.MK3);

    private final AppliedSmeltingConfig.TierValues values;

    SmelterTier(AppliedSmeltingConfig.TierValues values) {
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
    public SmelterTier previousTier() {
        var all = values();
        var index = ordinal();
        return index == 0 ? null : all[index - 1];
    }

    public String serializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static SmelterTier fromSerializedName(String name) {
        for (var tier : values()) {
            if (tier.serializedName().equals(name)) {
                return tier;
            }
        }
        return DEFAULT;
    }
}
