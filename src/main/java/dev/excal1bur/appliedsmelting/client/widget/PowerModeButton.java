package dev.excal1bur.appliedsmelting.client.widget;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import appeng.client.gui.widgets.IconButton;
import appeng.util.Icon;

import dev.excal1bur.appliedsmelting.service.SmeltingPowerMode;

/** Cycles through {@link SmeltingPowerMode} on click, showing an icon/overlay/tooltip for the current mode. */
public final class PowerModeButton extends IconButton {
    private final Consumer<SmeltingPowerMode> onCycle;
    private SmeltingPowerMode mode = SmeltingPowerMode.ITEM_FUEL;

    public PowerModeButton(Consumer<SmeltingPowerMode> onCycle) {
        super(button -> {
        });
        this.onCycle = onCycle;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        onCycle.accept(mode.next());
    }

    public void setMode(SmeltingPowerMode mode) {
        this.mode = mode;
    }

    @Override
    protected Icon getIcon() {
        return mode == SmeltingPowerMode.AE_POWER ? Icon.POWER_UNIT_AE : Icon.BACKGROUND_FUEL;
    }

    @Override
    protected Item getItemOverlay() {
        return mode == SmeltingPowerMode.LAVA_FUEL ? Items.LAVA_BUCKET : null;
    }

    @Override
    public List<Component> getTooltipMessage() {
        return List.of(Component.translatable(switch (mode) {
            case ITEM_FUEL -> "gui.appliedsmelting.mode_item_fuel";
            case AE_POWER -> "gui.appliedsmelting.mode_ae_power";
            case LAVA_FUEL -> "gui.appliedsmelting.mode_lava_fuel";
        }));
    }
}
