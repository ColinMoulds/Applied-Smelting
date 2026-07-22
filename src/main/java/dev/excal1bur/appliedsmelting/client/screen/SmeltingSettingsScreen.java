package dev.excal1bur.appliedsmelting.client.screen;

import java.util.List;

import net.minecraft.network.chat.Component;

import appeng.client.gui.AESubScreen;
import appeng.client.gui.NumberEntryType;
import appeng.client.gui.widgets.NumberEntryWidget;
import appeng.client.gui.widgets.TabButton;
import appeng.client.gui.widgets.ToggleButton;
import appeng.util.Icon;

import dev.excal1bur.appliedsmelting.menu.SmeltingTerminalMenu;

/** AE-style configuration page for controls that do not belong in the storage view. */
public final class SmeltingSettingsScreen
        extends AESubScreen<SmeltingTerminalMenu, SmeltingTerminalScreen> {
    private final ToggleButton powerButton;
    private final NumberEntryWidget targetAmount;

    public SmeltingSettingsScreen(SmeltingTerminalScreen parent) {
        super(parent, "/screens/appliedsmelting/smelting_settings.json");

        var backButton = new TabButton(
                Icon.BACK,
                Component.translatable("gui.appliedsmelting.back_to_terminal"),
                button -> closeSettings());
        widgets.add("back", backButton);

        powerButton = new ToggleButton(
                Icon.REDSTONE_ON,
                Icon.REDSTONE_OFF,
                enabled -> menu.requestNetworkEnabled(enabled));
        powerButton.setTooltipOn(List.of(Component.translatable("gui.appliedsmelting.turn_off")));
        powerButton.setTooltipOff(List.of(Component.translatable("gui.appliedsmelting.turn_on")));
        widgets.add("power", powerButton);

        targetAmount = widgets.addNumberEntryWidget("target", NumberEntryType.UNITLESS);
        targetAmount.setTextFieldStyle(getStyle().getWidget("targetInput"));
        targetAmount.setMinValue(0);
        targetAmount.setMaxValue(Long.MAX_VALUE);
        targetAmount.setLongValue(menu.targetAmount);
        targetAmount.setOnChange(this::saveTargetAmount);
        targetAmount.setOnConfirm(this::saveTargetAmount);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        powerButton.setState(menu.enabled);
        powerButton.active = menu.smelterCount > 0;
        targetAmount.setActive(menu.outputPreview != null);
        setTextContent(
                "power_status",
                Component.translatable(menu.enabled ? "gui.appliedsmelting.on" : "gui.appliedsmelting.off")
                        .withColor(menu.enabled ? 0x38c947 : 0xd84747));
        setTextContent(
                "network_idle",
                Component.translatable(
                        "gui.appliedsmelting.network_idle_ae", menu.combinedIdleAeTimes100 / 100.0));
        setTextContent(
                "network_ae_fuel",
                Component.translatable(
                        "gui.appliedsmelting.network_ae_fuel", menu.combinedAeFuelTimes100 / 100.0));
    }

    private void saveTargetAmount() {
        targetAmount.getLongValue().ifPresent(menu::requestTargetAmount);
    }

    private void closeSettings() {
        saveTargetAmount();
        returnToParent();
    }
}
