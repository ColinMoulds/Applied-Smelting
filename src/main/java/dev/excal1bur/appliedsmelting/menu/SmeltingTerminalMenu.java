package dev.excal1bur.appliedsmelting.menu;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

import org.jetbrains.annotations.Nullable;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.helpers.InventoryAction;
import appeng.menu.guisync.ClientActionKey;
import appeng.menu.guisync.GuiSync;
import appeng.menu.me.common.MEStorageMenu;

import dev.excal1bur.appliedsmelting.core.ModMenus;

public final class SmeltingTerminalMenu extends MEStorageMenu {
    private static final ClientActionKey<Boolean> SET_ENABLED = new ClientActionKey<>("setEnabled");
    private static final ClientActionKey<Long> SET_TARGET_AMOUNT = new ClientActionKey<>("setTargetAmount");

    private final SmeltingTerminalHost terminal;

    @GuiSync(1)
    public int smelterCount;

    @GuiSync(2)
    public int workingCount;

    @GuiSync(3)
    public boolean enabled;

    @GuiSync(4)
    public GenericStack selectedInput;

    @GuiSync(5)
    public GenericStack selectedFuel;

    @GuiSync(6)
    public GenericStack outputPreview;

    @GuiSync(7)
    public long targetAmount;

    @GuiSync(8)
    public int statusId;

    @GuiSync(9)
    public int progressPercent;

    @GuiSync(10)
    public int fuelPercent;

    public SmeltingTerminalMenu(int id, Inventory playerInventory, SmeltingTerminalHost terminal) {
        super(ModMenus.SMELTING_TERMINAL.get(), id, playerInventory, terminal);
        this.terminal = terminal;
        registerClientAction(SET_ENABLED, ByteBufCodecs.BOOL, this::setNetworkEnabled);
        registerClientAction(SET_TARGET_AMOUNT, ByteBufCodecs.VAR_LONG, this::setTargetAmount);
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            var service = terminal.getSmeltingService();
            smelterCount = service == null ? 0 : service.getSmelterCount();
            workingCount = service == null ? 0 : service.getWorkingCount();
            enabled = service != null && service.isEnabled();
            var input = service == null ? null : service.getSelectedInput();
            var fuel = service == null ? null : service.getSelectedFuel();
            selectedInput = input == null ? null : new GenericStack(input, 1);
            selectedFuel = fuel == null ? null : new GenericStack(fuel, 1);
            outputPreview = getOutputPreview(input);
            targetAmount = service == null ? 0 : service.getTargetAmount();
            statusId = service == null ? 0 : service.getOverallStatus().id();
            progressPercent = service == null ? 0 : service.getAverageProgressPercent();
            fuelPercent = service == null ? 0 : service.getAverageFuelPercent();
        }
        super.broadcastChanges();
    }

    @Override
    protected boolean hideViewCells() {
        return true;
    }

    @Override
    protected boolean showsCraftables() {
        return false;
    }

    @Override
    public boolean isKeyVisible(AEKey key) {
        if (!(key instanceof AEItemKey itemKey)) {
            return false;
        }
        var stack = itemKey.toStack();
        return getPlayer().level().fuelValues().isFuel(stack) || isSmeltable(itemKey);
    }

    @Override
    protected void handleNetworkInteraction(
            ServerPlayer player, @Nullable AEKey clickedKey, InventoryAction action) {
        if (clickedKey instanceof AEItemKey itemKey) {
            var service = terminal.getSmeltingService();
            if (service != null && action == InventoryAction.PICKUP_OR_SET_DOWN && isSmeltable(itemKey)) {
                service.setSelectedInput(itemKey.equals(service.getSelectedInput()) ? null : itemKey);
                terminal.setSelections(service.getSelectedInput(), service.getSelectedFuel());
                return;
            }
            if (service != null
                    && action == InventoryAction.SPLIT_OR_PLACE_SINGLE
                    && player.level().fuelValues().isFuel(itemKey.toStack())) {
                service.setSelectedFuel(itemKey.equals(service.getSelectedFuel()) ? null : itemKey);
                terminal.setSelections(service.getSelectedInput(), service.getSelectedFuel());
                return;
            }
        }
        super.handleNetworkInteraction(player, clickedKey, action);
    }

    private boolean isSmeltable(AEItemKey itemKey) {
        var level = getPlayer().level();
        return level.recipeAccess().propertySet(RecipePropertySet.FURNACE_INPUT).test(itemKey.toStack());
    }

    private GenericStack getOutputPreview(AEItemKey input) {
        if (input == null) {
            return null;
        }
        if (!(getPlayer().level() instanceof ServerLevel level)) {
            return null;
        }
        var recipeInput = new SingleRecipeInput(input.toStack());
        var recipe = level.recipeAccess().getRecipeFor(RecipeType.SMELTING, recipeInput, level);
        if (recipe.isEmpty()) {
            return null;
        }
        return GenericStack.fromItemStack(recipe.get().value().assemble(recipeInput));
    }

    public void requestNetworkEnabled(boolean enabled) {
        sendClientAction(SET_ENABLED, enabled);
    }

    public void requestTargetAmount(long targetAmount) {
        sendClientAction(SET_TARGET_AMOUNT, Math.max(0, targetAmount));
    }

    private void setTargetAmount(long targetAmount) {
        var amount = Math.max(0, targetAmount);
        var service = terminal.getSmeltingService();
        if (service != null) {
            service.setTargetAmount(amount);
            terminal.setTargetAmount(amount);
        }
    }

    private void setNetworkEnabled(boolean enabled) {
        var service = terminal.getSmeltingService();
        if (service != null) {
            service.setEnabled(enabled);
        }
    }
}
