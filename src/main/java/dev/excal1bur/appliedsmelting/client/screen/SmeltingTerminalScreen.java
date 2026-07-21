package dev.excal1bur.appliedsmelting.client.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;

import dev.excal1bur.appliedsmelting.menu.SmeltingTerminalMenu;

public final class SmeltingTerminalScreen extends AEBaseScreen<SmeltingTerminalMenu> {
    private Button toggleButton;

    public SmeltingTerminalScreen(
            SmeltingTerminalMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    protected void init() {
        super.init();
        toggleButton = addRenderableWidget(Button.builder(
                        Component.empty(), button -> menu.requestNetworkEnabled(!menu.enabled))
                .bounds(leftPos + 38, topPos + 55, 100, 20)
                .build());
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        setTextContent("smelters", Component.translatable("gui.ae2smelter.smelters", menu.smelterCount));
        setTextContent("working", Component.translatable("gui.ae2smelter.working", menu.workingCount));
        setTextContent(
                "network_state",
                Component.translatable(menu.enabled ? "gui.ae2smelter.running" : "gui.ae2smelter.paused"));
        if (toggleButton != null) {
            toggleButton.setMessage(Component.translatable(
                    menu.enabled ? "gui.ae2smelter.pause_all" : "gui.ae2smelter.resume_all"));
        }
    }
}
