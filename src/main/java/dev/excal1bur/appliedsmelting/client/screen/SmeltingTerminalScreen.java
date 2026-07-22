package dev.excal1bur.appliedsmelting.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import appeng.api.stacks.AEKey;
import appeng.client.api.AEKeyRendering;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.me.common.RepoSlot;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.TabButton;
import appeng.helpers.InventoryAction;
import appeng.util.Icon;
import appeng.util.prioritylist.IPartitionList;

import dev.excal1bur.appliedsmelting.menu.SmeltingTerminalMenu;

/**
 * The queue grid (input -> arrow -> output, per column) is the terminal's default view.
 * A separate "fuel" tab switches the same ME grid + player inventory to a fuel-only
 * picker so fuel can be selected from either the network or the player's own inventory.
 */
public final class SmeltingTerminalScreen extends MEStorageScreen<SmeltingTerminalMenu> {
    private static final int QUEUE_X = 11;
    private static final int QUEUE_INPUT_Y = 6;
    private static final int QUEUE_ARROW_Y = 25;
    private static final int QUEUE_OUTPUT_Y = 35;
    private static final int QUEUE_SLOT_SPACING = 17;
    private static final int FUEL_PANEL_X = 79;
    private static final int FUEL_PANEL_Y = 24;
    private TabButton settingsButton;
    private TabButton fuelButton;
    private TabButton fuelBackButton;
    private boolean fuelPickerView;
    private long draggedSerial = -1;
    private appeng.api.stacks.AEItemKey draggedKey;
    private double dragStartX;
    private double dragStartY;

    public SmeltingTerminalScreen(
            SmeltingTerminalMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        // The ME grid shows both furnace-smeltable inputs and furnace fuels by default; both
        // are already restricted server-side (SmeltingTerminalMenu#isKeyVisible). The fuel tab
        // narrows the client-side view further, to fuels only, via setFuelPickerView below.
    }

    @Override
    public void init() {
        super.init();
        settingsButton = new TabButton(
                Icon.COG,
                Component.translatable("gui.appliedsmelting.open_settings"),
                button -> switchToScreen(new SmeltingSettingsScreen(this)));
        settingsButton.setX(leftPos + imageWidth - 24);
        settingsButton.setY(topPos + panelTop() + 4);
        addRenderableWidget(settingsButton);

        fuelButton = new TabButton(
                Icon.BACKGROUND_FUEL,
                Component.translatable("gui.appliedsmelting.open_fuel_picker"),
                button -> setFuelPickerView(true));
        fuelButton.setX(leftPos + imageWidth - 24);
        fuelButton.setY(topPos + panelTop() + 26);
        addRenderableWidget(fuelButton);

        fuelBackButton = new TabButton(
                Icon.BACK,
                Component.translatable("gui.appliedsmelting.back_to_terminal"),
                button -> setFuelPickerView(false));
        fuelBackButton.setX(leftPos + imageWidth - 24);
        fuelBackButton.setY(topPos + panelTop() + 4);
        addRenderableWidget(fuelBackButton);
        updateFuelPickerWidgets();
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        settingsButton.active = menu.smelterCount > 0;
        fuelButton.active = menu.smelterCount > 0;
    }

    @Override
    public void drawBG(
            GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTick) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTick);
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(offsetX, offsetY);
        int top = panelTop();
        // Cover the crafting grid inherited from AE2's terminal texture and replace it with our active panel.
        guiGraphics.fill(9, top, 168, top + 66, 0xffa9adc2);
        drawInset(guiGraphics, 9, top, 159, 66);

        if (fuelPickerView) {
            drawSlot(guiGraphics, FUEL_PANEL_X, top + FUEL_PANEL_Y);
            guiGraphics.pose().popMatrix();
            return;
        }

        for (int i = 0; i < 9; i++) {
            int x = QUEUE_X + i * QUEUE_SLOT_SPACING;
            boolean active = (menu.activeQueueMask & (1 << i)) != 0;
            drawQueueSlot(guiGraphics, x, top + QUEUE_INPUT_Y, i < menu.queueCapacity, active);
            drawArrow(guiGraphics, x, top + QUEUE_ARROW_Y, active);
            drawQueueSlot(guiGraphics, x, top + QUEUE_OUTPUT_Y, i < menu.queueCapacity, false);
        }
        guiGraphics.pose().popMatrix();
    }

    @Override
    public void drawFG(
            GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        int top = panelTop();
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);

        if (fuelPickerView) {
            renderSelection(guiGraphics, menu.selectedFuel, FUEL_PANEL_X + 1, top + FUEL_PANEL_Y + 1);
            if (menu.selectedFuel != null && !menu.fuelInUse) {
                guiGraphics.fill(
                        FUEL_PANEL_X + 1, top + FUEL_PANEL_Y + 1, FUEL_PANEL_X + 17, top + FUEL_PANEL_Y + 17,
                        0x99000000);
            }
            return;
        }

        for (int i = 0; i < 9; i++) {
            int x = QUEUE_X + i * QUEUE_SLOT_SPACING;
            var input = menu.getQueuePreview(i);
            if (input != null) {
                AEKeyRendering.drawInGui(minecraft, guiGraphics, x + 1, top + QUEUE_INPUT_Y + 1, input.what());
            }
            var output = menu.getQueueOutputPreview(i);
            if (output != null) {
                AEKeyRendering.drawInGui(minecraft, guiGraphics, x + 1, top + QUEUE_OUTPUT_Y + 1, output.what());
            }
            if (i >= menu.queueCapacity) {
                // Dim the whole column (icons included) so locked slots read as clearly inactive,
                // even if a preserved-but-unscheduled item is still sitting in one.
                guiGraphics.fill(
                        x, top + QUEUE_INPUT_Y - 1, x + 18, top + QUEUE_OUTPUT_Y + 19, 0xb0202127);
            }
        }

        if (draggedKey != null) {
            AEKeyRendering.drawInGui(minecraft, guiGraphics, mouseX - leftPos - 8, mouseY - topPos - 8, draggedKey);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && fuelPickerView) {
            int top = panelTop();
            if (menu.selectedFuel != null
                    && isHovering(FUEL_PANEL_X, top + FUEL_PANEL_Y, 18, 18, event.x(), event.y())
                    && menu.selectedFuel.what() instanceof appeng.api.stacks.AEItemKey currentKey) {
                menu.requestFuelFromItem(currentKey.getItem());
                return true;
            }
            var item = resolveHoveredFuelItem();
            if (item != null) {
                menu.requestFuelFromItem(item);
                return true;
            }
            return super.mouseClicked(event, doubleClick);
        }
        if (event.button() == 0) {
            int top = panelTop();
            for (int i = 0; i < 9; i++) {
                if (menu.getQueuePreview(i) != null
                        && isHovering(
                                QUEUE_X + i * QUEUE_SLOT_SPACING, top + QUEUE_INPUT_Y, 18, 18, event.x(), event.y())) {
                    menu.requestRemoveQueuedInput(i);
                    return true;
                }
            }
            Slot hovered = getHoveredSlot();
            if (hovered instanceof RepoSlot repoSlot && repoSlot.getEntry() != null
                    && repoSlot.getEntry().getWhat() instanceof appeng.api.stacks.AEItemKey itemKey) {
                draggedSerial = repoSlot.getEntry().getSerial();
                draggedKey = itemKey;
                dragStartX = event.x();
                dragStartY = event.y();
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0 && draggedSerial != -1) {
            int top = panelTop();
            if (isHovering(QUEUE_X, top + QUEUE_INPUT_Y, 154, 18, event.x(), event.y())
                    || Math.hypot(event.x() - dragStartX, event.y() - dragStartY) < 4.0) {
                menu.handleInteraction(draggedSerial, InventoryAction.PICKUP_OR_SET_DOWN);
            }
            draggedSerial = -1;
            draggedKey = null;
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int x, int y) {
        int top = panelTop();
        if (fuelPickerView) {
            if (isHovering(FUEL_PANEL_X, top + FUEL_PANEL_Y, 18, 18, x, y) && menu.selectedFuel != null) {
                var tooltip = new java.util.ArrayList<Component>();
                tooltip.add(Component.translatable("gui.appliedsmelting.selected_fuel"));
                tooltip.addAll(AEKeyRendering.getTooltip(menu.selectedFuel.what()));
                if (!menu.fuelInUse) {
                    tooltip.add(Component.translatable("gui.appliedsmelting.fuel_not_in_use"));
                }
                tooltip.add(Component.translatable("gui.appliedsmelting.click_to_clear_fuel"));
                guiGraphics.setComponentTooltipForNextFrame(font, tooltip, x, y);
                return;
            }
            super.extractTooltip(guiGraphics, x, y);
            return;
        }
        for (int i = 0; i < 9; i++) {
            int slotX = QUEUE_X + i * QUEUE_SLOT_SPACING;
            boolean locked = i >= menu.queueCapacity;
            var input = menu.getQueuePreview(i);
            if (isHovering(slotX, top + QUEUE_INPUT_Y, 18, 18, x, y) && (input != null || locked)) {
                var tooltip = new java.util.ArrayList<Component>();
                if (input != null) {
                    tooltip.addAll(AEKeyRendering.getTooltip(input.what()));
                    if ((menu.activeQueueMask & (1 << i)) != 0) {
                        tooltip.add(Component.translatable("gui.appliedsmelting.queue_active"));
                    }
                }
                if (locked) {
                    tooltip.add(Component.translatable("gui.appliedsmelting.queue_locked"));
                }
                if (input != null) {
                    tooltip.add(Component.translatable("gui.appliedsmelting.remove_from_queue"));
                }
                guiGraphics.setComponentTooltipForNextFrame(font, tooltip, x, y);
                return;
            }
            var output = menu.getQueueOutputPreview(i);
            if (isHovering(slotX, top + QUEUE_OUTPUT_Y, 18, 18, x, y) && (output != null || locked)) {
                var tooltip = new java.util.ArrayList<Component>();
                if (output != null) {
                    tooltip.add(Component.translatable("gui.appliedsmelting.output_preview"));
                    tooltip.addAll(AEKeyRendering.getTooltip(output.what()));
                }
                if (locked) {
                    tooltip.add(Component.translatable("gui.appliedsmelting.queue_locked"));
                }
                guiGraphics.setComponentTooltipForNextFrame(font, tooltip, x, y);
                return;
            }
        }
        super.extractTooltip(guiGraphics, x, y);
    }

    private void setFuelPickerView(boolean fuelPickerView) {
        this.fuelPickerView = fuelPickerView;
        if (fuelPickerView) {
            repo.setPartitionList(new IPartitionList() {
                @Override
                public boolean isListed(AEKey key) {
                    return key instanceof appeng.api.stacks.AEItemKey itemKey
                            && minecraft != null
                            && minecraft.level != null
                            && minecraft.level.fuelValues().isFuel(itemKey.toStack());
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public Iterable<AEKey> getItems() {
                    return java.util.List.of();
                }
            });
        } else {
            repo.setPartitionList(IPartitionList.builder().build());
        }
        repo.updateView();
        updateFuelPickerWidgets();
    }

    private void updateFuelPickerWidgets() {
        if (settingsButton != null) {
            settingsButton.visible = !fuelPickerView;
        }
        if (fuelButton != null) {
            fuelButton.visible = !fuelPickerView;
        }
        if (fuelBackButton != null) {
            fuelBackButton.visible = fuelPickerView;
        }
    }

    private Item resolveHoveredFuelItem() {
        Slot hovered = getHoveredSlot();
        if (hovered == null) {
            return null;
        }
        ItemStack stack;
        if (hovered instanceof RepoSlot repoSlot) {
            if (repoSlot.getEntry() == null
                    || !(repoSlot.getEntry().getWhat() instanceof appeng.api.stacks.AEItemKey itemKey)) {
                return null;
            }
            stack = itemKey.toStack();
        } else {
            stack = hovered.getItem();
        }
        if (stack.isEmpty() || minecraft == null || minecraft.level == null
                || !minecraft.level.fuelValues().isFuel(stack)) {
            return null;
        }
        return stack.getItem();
    }

    private int panelTop() {
        return imageHeight - 161;
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

    private static void drawQueueSlot(
            GuiGraphicsExtractor graphics, int x, int y, boolean available, boolean active) {
        graphics.fill(x, y, x + 18, y + 18, active ? 0xffe6a728 : 0xff686c81);
        graphics.fill(x + 1, y + 1, x + 18, y + 18, 0xffd4d8ea);
        graphics.fill(x + 2, y + 2, x + 17, y + 17, available ? 0xffaeb2c8 : 0xff7d8195);
    }

    private void drawArrow(GuiGraphicsExtractor graphics, int slotX, int y, boolean active) {
        var arrow = Component.literal("↓");
        int color = active ? 0xffe6a728 : 0xff7d8195;
        graphics.text(font, arrow, slotX + 9 - font.width(arrow) / 2, y, color, false);
    }

    private void renderSelection(GuiGraphicsExtractor graphics, appeng.api.stacks.GenericStack stack, int x, int y) {
        if (stack != null) {
            AEKeyRendering.drawInGui(minecraft, graphics, x, y, stack.what());
        }
    }
}
