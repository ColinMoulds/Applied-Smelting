package dev.excal1bur.appliedsmelting.service;

public enum SmeltingPowerMode {
    ITEM_FUEL("item_fuel"),
    AE_POWER("ae_power"),
    LAVA_FUEL("lava_fuel");

    private final String serializedName;

    SmeltingPowerMode(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return serializedName;
    }

    public SmeltingPowerMode next() {
        var values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public static SmeltingPowerMode fromSerializedName(String name) {
        for (var mode : values()) {
            if (mode.serializedName.equals(name)) {
                return mode;
            }
        }
        return ITEM_FUEL;
    }
}
