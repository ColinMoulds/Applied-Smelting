package dev.excal1bur.appliedsmelting.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.api.AEKeyRendering;

import dev.excal1bur.appliedsmelting.client.widget.PowerModeButton;
import dev.excal1bur.appliedsmelting.menu.MECrucibleMenu;

public final class MECrucibleScreen extends UpgradeableScreen<MECrucibleMenu> {
    private final PowerModeButton powerModeButton;

    public MECrucibleScreen(MECrucibleMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        powerModeButton = new PowerModeButton(menu::requestPowerMode);
        widgets.add("powerMode", powerModeButton);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        setTextContent(
                "cards",
                Component.translatable(
                        "gui.appliedsmelting.upgrade_summary",
                        menu.accelerationCards,
                        menu.energyCards,
                        menu.fuelEfficiencyCards,
                        menu.capacityCards,
                        menu.redstoneCard ? 1 : 0));
        setTextContent(
                "speed",
                Component.translatable("gui.appliedsmelting.smelting_speed", menu.getSpeedMultiplier()));
        setTextContent(
                "power_mode",
                Component.translatable(switch (menu.getPowerMode()) {
                    case AE_POWER -> "gui.appliedsmelting.mode_ae_power";
                    case LAVA_FUEL -> "gui.appliedsmelting.mode_lava_fuel";
                    case ITEM_FUEL -> "gui.appliedsmelting.mode_item_fuel";
                }));
        setTextContent(
                "recipe",
                Component.translatable(menu.pinnedInput == null
                        ? "gui.appliedsmelting.recipe_network_queue"
                        : "gui.appliedsmelting.recipe_pinned"));
        setTextContent(
                "power_usage",
                Component.translatable(
                        "gui.appliedsmelting.power_usage",
                        menu.idleAeTimes100 / 100.0,
                        menu.aeFuelTimes100 / 100.0,
                        menu.lavaFuelTimes100 / 100.0));
        setTextContent(
                "fuel_efficiency",
                Component.translatable("gui.appliedsmelting.fuel_efficiency", menu.fuelEfficiencyPercent));
        setTextContent(
                "redstone",
                Component.translatable(menu.redstoneCard
                        ? "gui.appliedsmelting.redstone_signal_required"
                        : "gui.appliedsmelting.redstone_ignored"));
        powerModeButton.setMode(menu.getPowerMode());
    }

    @Override
    public void drawFG(
            GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
        if (menu.pinnedInput != null) {
            AEKeyRendering.drawInGui(minecraft, guiGraphics, 145, 45, menu.pinnedInput.what());
        }
    }

    @Override
    public void drawBG(
            GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTick) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTick);
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(offsetX, offsetY);

        drawInset(guiGraphics, 8, 22, 160, 108);
        drawInset(guiGraphics, 7, 145, 162, 56);
        drawInset(guiGraphics, 7, 203, 162, 20);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlot(guiGraphics, 8 + column * 18, 146 + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            drawSlot(guiGraphics, 8 + column * 18, 204);
        }

        guiGraphics.pose().popMatrix();
    }

    private static void drawInset(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + 1, 0xff6d7186);
        graphics.fill(x, y, x + 1, y + height, 0xff6d7186);
        graphics.fill(x, y + height - 1, x + width, y + height, 0xffd6daec);
        graphics.fill(x + width - 1, y, x + width, y + height, 0xffd6daec);
    }

    private static void drawSlot(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, 0xff686c81);
        graphics.fill(x + 1, y + 1, x + 18, y + 18, 0xffd4d8ea);
        graphics.fill(x + 2, y + 2, x + 17, y + 17, 0xffaeb2c8);
    }
}
