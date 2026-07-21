package dev.excal1bur.appliedsmelting.service;

public enum SmelterStatus {
    NO_SMELTERS(0, "gui.appliedsmelting.status.no_smelters"),
    OFFLINE(1, "gui.appliedsmelting.status.offline"),
    PAUSED(2, "gui.appliedsmelting.status.paused"),
    WAITING_FOR_SELECTION(3, "gui.appliedsmelting.status.waiting_for_selection"),
    MISSING_INPUT(4, "gui.appliedsmelting.status.missing_input"),
    MISSING_FUEL(5, "gui.appliedsmelting.status.missing_fuel"),
    MISSING_POWER(6, "gui.appliedsmelting.status.missing_power"),
    OUTPUT_FULL(7, "gui.appliedsmelting.status.output_full"),
    TARGET_REACHED(8, "gui.appliedsmelting.status.target_reached"),
    INVALID_RECIPE(9, "gui.appliedsmelting.status.invalid_recipe"),
    SMELTING(10, "gui.appliedsmelting.status.smelting");

    private final int id;
    private final String translationKey;

    SmelterStatus(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public int id() {
        return id;
    }

    public String translationKey() {
        return translationKey;
    }

    public static SmelterStatus fromId(int id) {
        for (var status : values()) {
            if (status.id == id) {
                return status;
            }
        }
        return OFFLINE;
    }
}
