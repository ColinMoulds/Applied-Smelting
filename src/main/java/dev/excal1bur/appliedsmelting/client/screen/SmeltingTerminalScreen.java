package dev.excal1bur.appliedsmelting.client.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import appeng.client.api.AEKeyRendering;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.me.common.RepoSlot;
import appeng.client.gui.style.ScreenStyle;
import appeng.helpers.InventoryAction;

import dev.excal1bur.appliedsmelting.menu.SmeltingTerminalMenu;

public final class SmeltingTerminalScreen extends MEStorageScreen<SmeltingTerminalMenu> {
    private static final int INPUT_X = 42;
    private static final int FUEL_X = 42;
    private static final int OUTPUT_X = 130;

    private Button toggleButton;
    private EditBox targetAmountField;
    private boolean updatingTargetField;
    private long draggedSerial = -1;
    private appeng.api.stacks.AEItemKey draggedKey;
    private double dragStartX;
    private double dragStartY;

    public SmeltingTerminalScreen(
            SmeltingTerminalMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    public void init() {
        super.init();
        toggleButton = addRenderableWidget(Button.builder(
                        Component.empty(), button -> menu.requestNetworkEnabled(!menu.enabled))
                .bounds(leftPos + 10, topPos + panelTop() + 48, 72, 16)
                .build());

        targetAmountField = new EditBox(
                font,
                leftPos + 126,
                topPos + panelTop() + 48,
                34,
                16,
                Component.translatable("gui.appliedsmelting.target_amount"));
        targetAmountField.setMaxLength(12);
        targetAmountField.setFilter(value -> value.isEmpty() || value.chars().allMatch(Character::isDigit));
        targetAmountField.setValue(Long.toString(menu.targetAmount));
        targetAmountField.setResponder(this::targetAmountChanged);
        addRenderableWidget(targetAmountField);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        setTextContent("smelters", Component.translatable(
                "gui.appliedsmelting.smelters_compact", menu.workingCount, menu.smelterCount));
        if (toggleButton != null) {
            toggleButton.setMessage(Component.translatable(
                    menu.enabled ? "gui.appliedsmelting.pause_all" : "gui.appliedsmelting.resume_all"));
        }
        if (targetAmountField != null && !targetAmountField.isFocused()) {
            var targetText = Long.toString(menu.targetAmount);
            if (!targetText.equals(targetAmountField.getValue())) {
                updatingTargetField = true;
                targetAmountField.setValue(targetText);
                updatingTargetField = false;
            }
        }
    }

    @Override
    public void drawFG(
            GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);

        int top = panelTop();
        // Cover the crafting grid inherited from AE2's terminal texture and replace it with a furnace layout.
        guiGraphics.fill(9, top, 168, top + 66, 0xffa9adc2);
        drawInset(guiGraphics, 9, top, 159, 66);
        drawSlot(guiGraphics, INPUT_X, top + 8);
        drawSlot(guiGraphics, FUEL_X, top + 38);
        drawSlot(guiGraphics, OUTPUT_X, top + 23);

        // Furnace flame and processing arrow.
        guiGraphics.fill(51, top + 30, 57, top + 35, 0xff5f6378);
        guiGraphics.fill(49, top + 32, 59, top + 37, 0xff5f6378);
        guiGraphics.fill(78, top + 31, 119, top + 34, 0xff62667b);
        guiGraphics.fill(116, top + 27, 122, top + 38, 0xff62667b);
        guiGraphics.fill(119, top + 29, 124, top + 36, 0xff62667b);

        renderSelection(guiGraphics, menu.selectedInput, INPUT_X + 1, top + 9);
        renderSelection(guiGraphics, menu.selectedFuel, FUEL_X + 1, top + 39);
        renderSelection(guiGraphics, menu.outputPreview, OUTPUT_X + 1, top + 24);
        guiGraphics.text(
                font,
                Component.translatable("gui.appliedsmelting.target"),
                86,
                top + 52,
                0xff30323d,
                false);

        if (draggedKey != null) {
            AEKeyRendering.drawInGui(minecraft, guiGraphics, mouseX - leftPos - 8, mouseY - topPos - 8, draggedKey);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
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
            if (isHovering(FUEL_X, top + 38, 18, 18, event.x(), event.y())) {
                menu.handleInteraction(draggedSerial, InventoryAction.SPLIT_OR_PLACE_SINGLE);
            } else if (isHovering(INPUT_X, top + 8, 18, 18, event.x(), event.y())
                    || Math.hypot(event.x() - dragStartX, event.y() - dragStartY) < 4.0) {
                // Dropping on the input slot, or a normal left-click, selects the input.
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
        if (showSelectionTooltip(guiGraphics, x, y, INPUT_X, top + 8, menu.selectedInput,
                "gui.appliedsmelting.selected_input")) {
            return;
        }
        if (showSelectionTooltip(guiGraphics, x, y, FUEL_X, top + 38, menu.selectedFuel,
                "gui.appliedsmelting.selected_fuel")) {
            return;
        }
        if (showSelectionTooltip(guiGraphics, x, y, OUTPUT_X, top + 23, menu.outputPreview,
                "gui.appliedsmelting.output_preview")) {
            return;
        }
        super.extractTooltip(guiGraphics, x, y);
    }

    private int panelTop() {
        return imageHeight - 161;
    }

    private void targetAmountChanged(String value) {
        if (updatingTargetField) {
            return;
        }
        try {
            menu.requestTargetAmount(value.isEmpty() ? 0 : Long.parseLong(value));
        } catch (NumberFormatException ignored) {
            // The length filter keeps normal input in range; ignore pasted values that exceed a long.
        }
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

    private void renderSelection(GuiGraphicsExtractor graphics, appeng.api.stacks.GenericStack stack, int x, int y) {
        if (stack != null) {
            AEKeyRendering.drawInGui(minecraft, graphics, x, y, stack.what());
        }
    }

    private boolean showSelectionTooltip(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            int slotX,
            int slotY,
            appeng.api.stacks.GenericStack stack,
            String labelKey) {
        if (!isHovering(slotX, slotY, 18, 18, mouseX, mouseY)) {
            return false;
        }
        if (stack == null) {
            graphics.setComponentTooltipForNextFrame(
                    font, java.util.List.of(Component.translatable(labelKey), Component.translatable("gui.appliedsmelting.not_selected")), mouseX, mouseY);
        } else {
            var tooltip = new java.util.ArrayList<Component>();
            tooltip.add(Component.translatable(labelKey));
            tooltip.addAll(AEKeyRendering.getTooltip(stack.what()));
            graphics.setComponentTooltipForNextFrame(font, tooltip, mouseX, mouseY);
        }
        return true;
    }
}
