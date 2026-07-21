package dev.excal1bur.appliedsmelting.client.screen;

import java.util.Locale;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import appeng.client.api.AEKeyRendering;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.me.common.RepoSlot;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.TabButton;
import appeng.helpers.InventoryAction;
import appeng.util.Icon;

import dev.excal1bur.appliedsmelting.menu.SmeltingTerminalMenu;
import dev.excal1bur.appliedsmelting.service.SmelterStatus;

public final class SmeltingTerminalScreen extends MEStorageScreen<SmeltingTerminalMenu> {
    private static final int INPUT_X = 42;
    private static final int INPUT_Y = 3;
    private static final int FUEL_X = 42;
    private static final int FUEL_Y = 45;
    private static final int OUTPUT_X = 130;
    private static final int OUTPUT_Y = 23;
    private static final int FLAME_X = INPUT_X + 2;
    private static final int FLAME_Y = 25;
    private static final Identifier FURNACE_FLAME_SPRITE =
            Identifier.withDefaultNamespace("container/furnace/lit_progress");
    private TabButton settingsButton;
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
        settingsButton = new TabButton(
                Icon.COG,
                Component.translatable("gui.appliedsmelting.open_settings"),
                button -> switchToScreen(new SmeltingSettingsScreen(this)));
        settingsButton.setX(leftPos + imageWidth - 24);
        settingsButton.setY(topPos + panelTop() + 4);
        addRenderableWidget(settingsButton);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        setTextContent("smelters", Component.translatable(
                SmelterStatus.fromId(menu.statusId).translationKey()));
        setTextContent("active", Component.translatable(
                "gui.appliedsmelting.smelters_compact", menu.workingCount, menu.smelterCount));
        setTextContent("stored", Component.translatable(
                "gui.appliedsmelting.stored_target",
                formatCompactAmount(menu.storedOutputAmount),
                menu.targetAmount == 0
                        ? Component.translatable("gui.appliedsmelting.unlimited")
                        : Component.literal(formatCompactAmount(menu.targetAmount))));
        settingsButton.active = menu.smelterCount > 0;
    }

    @Override
    public void drawBG(
            GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTick) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTick);
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(offsetX, offsetY);
        int top = panelTop();
        // Cover the crafting grid inherited from AE2's terminal texture and replace it with a furnace layout.
        guiGraphics.fill(9, top, 168, top + 66, 0xffa9adc2);
        drawInset(guiGraphics, 9, top, 159, 66);
        drawSlot(guiGraphics, INPUT_X, top + INPUT_Y);
        drawSlot(guiGraphics, FUEL_X, top + FUEL_Y);
        drawSlot(guiGraphics, OUTPUT_X, top + OUTPUT_Y);

        // Furnace flame and processing progress.
        drawFlame(guiGraphics, top);
        drawProgress(guiGraphics, top);

        guiGraphics.pose().popMatrix();
    }

    @Override
    public void drawFG(
            GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        int top = panelTop();
        super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);

        renderSelection(guiGraphics, menu.selectedInput, INPUT_X + 1, top + INPUT_Y + 1);
        renderSelection(guiGraphics, menu.selectedFuel, FUEL_X + 1, top + FUEL_Y + 1);
        renderSelection(guiGraphics, menu.outputPreview, OUTPUT_X + 1, top + OUTPUT_Y + 1);

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
            if (isHovering(FUEL_X, top + FUEL_Y, 18, 18, event.x(), event.y())) {
                menu.handleInteraction(draggedSerial, InventoryAction.SPLIT_OR_PLACE_SINGLE);
            } else if (isHovering(INPUT_X, top + INPUT_Y, 18, 18, event.x(), event.y())
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
        if (showSelectionTooltip(guiGraphics, x, y, INPUT_X, top + INPUT_Y, menu.selectedInput,
                "gui.appliedsmelting.selected_input")) {
            return;
        }
        if (showSelectionTooltip(guiGraphics, x, y, FUEL_X, top + FUEL_Y, menu.selectedFuel,
                "gui.appliedsmelting.selected_fuel")) {
            return;
        }
        if (showSelectionTooltip(guiGraphics, x, y, OUTPUT_X, top + OUTPUT_Y, menu.outputPreview,
                "gui.appliedsmelting.output_preview")) {
            return;
        }
        super.extractTooltip(guiGraphics, x, y);
    }

    private int panelTop() {
        return imageHeight - 161;
    }

    private static String formatCompactAmount(long amount) {
        if (amount >= 1_000_000_000) {
            return String.format(Locale.ROOT, "%.1fB", amount / 1_000_000_000.0);
        }
        if (amount >= 1_000_000) {
            return String.format(Locale.ROOT, "%.1fM", amount / 1_000_000.0);
        }
        if (amount >= 1_000) {
            return String.format(Locale.ROOT, "%.1fk", amount / 1_000.0);
        }
        return Long.toString(amount);
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

    private void drawProgress(GuiGraphicsExtractor graphics, int top) {
        int progress = Math.max(0, Math.min(100, menu.progressPercent));
        graphics.fill(78, top + 28, 124, top + 37, 0xff686c81);
        graphics.fill(79, top + 29, 123, top + 36, 0xff969aaf);
        int fillWidth = 42 * progress / 100;
        if (fillWidth > 0) {
            graphics.fill(80, top + 30, 80 + fillWidth, top + 35, 0xffe6a728);
        }

        var progressText = Component.literal(progress + "%");
        graphics.text(font, progressText, 101 - font.width(progressText) / 2, top + 39, 0xff30323d, false);
    }

    private void drawFlame(GuiGraphicsExtractor graphics, int top) {
        int fuel = Math.max(0, Math.min(100, menu.fuelPercent));
        if (fuel > 0) {
            int litHeight = Math.min(14, (int) Math.ceil(fuel * 13 / 100.0) + 1);
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    FURNACE_FLAME_SPRITE,
                    14,
                    14,
                    0,
                    14 - litHeight,
                    FLAME_X,
                    top + FLAME_Y + 14 - litHeight,
                    14,
                    litHeight);
        }
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
