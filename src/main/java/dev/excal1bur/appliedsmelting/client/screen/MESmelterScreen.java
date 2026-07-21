package dev.excal1bur.appliedsmelting.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;

import dev.excal1bur.appliedsmelting.menu.MESmelterMenu;

public final class MESmelterScreen extends UpgradeableScreen<MESmelterMenu> {
    public MESmelterScreen(MESmelterMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        setTextContent(
                "cards",
                Component.translatable("gui.appliedsmelting.acceleration_cards", menu.accelerationCards));
        setTextContent(
                "speed",
                Component.translatable("gui.appliedsmelting.smelting_speed", menu.getSpeedMultiplier()));
    }

    @Override
    public void drawFG(
            GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        // The inherited ME Chest background contains a storage-cell slot that this screen does not use.
        guiGraphics.fill(79, 36, 99, 56, 0xffcbccd4);
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);
    }
}
