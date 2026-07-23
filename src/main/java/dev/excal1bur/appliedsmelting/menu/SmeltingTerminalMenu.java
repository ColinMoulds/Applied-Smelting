package dev.excal1bur.appliedsmelting.menu;

import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import dev.excal1bur.appliedsmelting.service.FurnaceType;

public final class SmeltingTerminalMenu extends MEStorageMenu {
    private static final ClientActionKey<Boolean> SET_ENABLED = new ClientActionKey<>("setEnabled");
    private static final ClientActionKey<Long> SET_TARGET_AMOUNT = new ClientActionKey<>("setTargetAmount");
    private static final ClientActionKey<Integer> REMOVE_QUEUED_INPUT = new ClientActionKey<>("removeQueuedInput");
    private static final ClientActionKey<Holder<Item>> SET_FUEL_FROM_ITEM = new ClientActionKey<>("setFuelFromItem");
    private static final ClientActionKey<Integer> SET_ACTIVE_TYPE = new ClientActionKey<>("setActiveType");

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

    @GuiSync(11)
    public long storedOutputAmount;

    @GuiSync(12)
    public int queueSize;

    @GuiSync(13)
    public int queueCapacity;

    @GuiSync(14)
    public int combinedSpeedMultiplier;

    @GuiSync(15)
    public int combinedIdleAeTimes100;

    @GuiSync(16)
    public int combinedAeFuelTimes100;

    @GuiSync(17)
    public GenericStack queuePreview0;
    @GuiSync(18)
    public GenericStack queuePreview1;
    @GuiSync(19)
    public GenericStack queuePreview2;
    @GuiSync(20)
    public GenericStack queuePreview3;
    @GuiSync(21)
    public GenericStack queuePreview4;
    @GuiSync(22)
    public GenericStack queuePreview5;
    @GuiSync(23)
    public GenericStack queuePreview6;
    @GuiSync(24)
    public GenericStack queuePreview7;
    @GuiSync(25)
    public GenericStack queuePreview8;

    @GuiSync(26)
    public boolean fuelInUse;

    @GuiSync(27)
    public int activeQueueMask;

    @GuiSync(28)
    public GenericStack queueOutput0;
    @GuiSync(29)
    public GenericStack queueOutput1;
    @GuiSync(30)
    public GenericStack queueOutput2;
    @GuiSync(31)
    public GenericStack queueOutput3;
    @GuiSync(32)
    public GenericStack queueOutput4;
    @GuiSync(33)
    public GenericStack queueOutput5;
    @GuiSync(34)
    public GenericStack queueOutput6;
    @GuiSync(35)
    public GenericStack queueOutput7;
    @GuiSync(36)
    public GenericStack queueOutput8;

    @GuiSync(37)
    public int activeTypeOrdinal;

    public SmeltingTerminalMenu(int id, Inventory playerInventory, SmeltingTerminalHost terminal) {
        super(ModMenus.SMELTING_TERMINAL.get(), id, playerInventory, terminal);
        this.terminal = terminal;
        registerClientAction(SET_ENABLED, ByteBufCodecs.BOOL, this::setNetworkEnabled);
        registerClientAction(SET_TARGET_AMOUNT, ByteBufCodecs.VAR_LONG, this::setTargetAmount);
        registerClientAction(REMOVE_QUEUED_INPUT, ByteBufCodecs.VAR_INT, this::removeQueuedInput);
        registerClientAction(SET_FUEL_FROM_ITEM, Item.STREAM_CODEC, this::setFuelFromItem);
        registerClientAction(SET_ACTIVE_TYPE, ByteBufCodecs.VAR_INT, this::setActiveTypeOrdinal);
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            var type = getActiveType();
            var service = terminal.getService(type);
            smelterCount = service == null ? 0 : service.getSmelterCount();
            workingCount = service == null ? 0 : service.getWorkingCount();
            enabled = service != null && service.isEnabled();
            var input = service == null ? null : service.getDisplayInput();
            var fuel = service == null ? null : service.getSelectedFuel();
            selectedInput = input == null ? null : new GenericStack(input, 1);
            selectedFuel = fuel == null ? null : new GenericStack(fuel, 1);
            outputPreview = getOutputPreview(type, input);
            storedOutputAmount = outputPreview == null || storage == null
                    ? 0
                    : storage.getAvailableStacks().get(outputPreview.what());
            targetAmount = service == null ? 0 : service.getTargetAmount();
            statusId = service == null ? 0 : service.getOverallStatus().id();
            progressPercent = service == null ? 0 : service.getAverageProgressPercent();
            fuelPercent = service == null ? 0 : service.getAverageFuelPercent();
            fuelInUse = service != null && service.getItemFuelSmelterCount() > 0;
            queueSize = service == null ? 0 : service.getQueuedInputs().size();
            queueCapacity = service == null ? 1 : service.getQueueCapacity();
            combinedSpeedMultiplier = service == null ? 0 : service.getCombinedSpeedMultiplier();
            combinedIdleAeTimes100 = service == null ? 0 : (int) Math.round(service.getCombinedIdleAePerTick() * 100);
            combinedAeFuelTimes100 =
                    service == null ? 0 : (int) Math.round(service.getCombinedMaximumAeFuelPerTick() * 100);
            var queue = service == null ? java.util.List.<AEItemKey>of() : service.getQueuedInputs();
            queuePreview0 = queuePreview(queue, 0);
            queuePreview1 = queuePreview(queue, 1);
            queuePreview2 = queuePreview(queue, 2);
            queuePreview3 = queuePreview(queue, 3);
            queuePreview4 = queuePreview(queue, 4);
            queuePreview5 = queuePreview(queue, 5);
            queuePreview6 = queuePreview(queue, 6);
            queuePreview7 = queuePreview(queue, 7);
            queuePreview8 = queuePreview(queue, 8);
            var activeInputs = service == null ? java.util.Set.<AEItemKey>of() : service.getActiveInputs();
            int mask = 0;
            for (int i = 0; i < queue.size() && i < 9; i++) {
                if (activeInputs.contains(queue.get(i))) {
                    mask |= 1 << i;
                }
            }
            activeQueueMask = mask;
            queueOutput0 = queueOutputPreview(type, queue, 0);
            queueOutput1 = queueOutputPreview(type, queue, 1);
            queueOutput2 = queueOutputPreview(type, queue, 2);
            queueOutput3 = queueOutputPreview(type, queue, 3);
            queueOutput4 = queueOutputPreview(type, queue, 4);
            queueOutput5 = queueOutputPreview(type, queue, 5);
            queueOutput6 = queueOutputPreview(type, queue, 6);
            queueOutput7 = queueOutputPreview(type, queue, 7);
            queueOutput8 = queueOutputPreview(type, queue, 8);
            activeTypeOrdinal = type.ordinal();
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
        return getPlayer().level().fuelValues().isFuel(stack) || isSmeltable(getActiveType(), itemKey);
    }

    @Override
    protected void handleNetworkInteraction(
            ServerPlayer player, @Nullable AEKey clickedKey, InventoryAction action) {
        if (clickedKey instanceof AEItemKey itemKey) {
            var type = getActiveType();
            var service = terminal.getService(type);
            if (service != null && action == InventoryAction.PICKUP_OR_SET_DOWN && isSmeltable(type, itemKey)) {
                if (service.toggleQueuedInput(itemKey)) {
                    terminal.setQueuedInputs(type, service.getQueuedInputs());
                } else {
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable(
                            "message.appliedsmelting.queue_full", service.getQueueCapacity()));
                }
                return;
            }
            if (service != null
                    && action == InventoryAction.SPLIT_OR_PLACE_SINGLE
                    && player.level().fuelValues().isFuel(itemKey.toStack())) {
                applyFuelSelection(itemKey);
                return;
            }
        }
        super.handleNetworkInteraction(player, clickedKey, action);
    }

    private void setFuelFromItem(Holder<Item> itemHolder) {
        var stack = new ItemStack(itemHolder);
        if (!getPlayer().level().fuelValues().isFuel(stack)) {
            return;
        }
        applyFuelSelection(AEItemKey.of(stack));
    }

    private void applyFuelSelection(AEItemKey itemKey) {
        var type = getActiveType();
        var service = terminal.getService(type);
        if (service == null) {
            return;
        }
        service.setSelectedFuel(itemKey.equals(service.getSelectedFuel()) ? null : itemKey);
        terminal.setSelections(type, service.getSelectedInput(), service.getSelectedFuel());
    }

    private boolean isSmeltable(FurnaceType type, AEItemKey itemKey) {
        var level = getPlayer().level();
        return level.recipeAccess().propertySet(type.recipePropertySet()).test(itemKey.toStack());
    }

    private GenericStack getOutputPreview(FurnaceType type, AEItemKey input) {
        if (input == null) {
            return null;
        }
        if (!(getPlayer().level() instanceof ServerLevel level)) {
            return null;
        }
        var recipeInput = new SingleRecipeInput(input.toStack());
        var recipe = level.recipeAccess().getRecipeFor(type.recipeType(), recipeInput, level);
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

    public void requestRemoveQueuedInput(int index) {
        sendClientAction(REMOVE_QUEUED_INPUT, index);
    }

    public void requestFuelFromItem(Item item) {
        sendClientAction(SET_FUEL_FROM_ITEM, item.builtInRegistryHolder());
    }

    public void requestActiveType(FurnaceType type) {
        sendClientAction(SET_ACTIVE_TYPE, type.ordinal());
    }

    public FurnaceType getActiveType() {
        var types = FurnaceType.values();
        return types[Math.max(0, Math.min(types.length - 1, activeTypeOrdinal))];
    }

    public GenericStack getQueuePreview(int index) {
        return switch (index) {
            case 0 -> queuePreview0;
            case 1 -> queuePreview1;
            case 2 -> queuePreview2;
            case 3 -> queuePreview3;
            case 4 -> queuePreview4;
            case 5 -> queuePreview5;
            case 6 -> queuePreview6;
            case 7 -> queuePreview7;
            case 8 -> queuePreview8;
            default -> null;
        };
    }

    public GenericStack getQueueOutputPreview(int index) {
        return switch (index) {
            case 0 -> queueOutput0;
            case 1 -> queueOutput1;
            case 2 -> queueOutput2;
            case 3 -> queueOutput3;
            case 4 -> queueOutput4;
            case 5 -> queueOutput5;
            case 6 -> queueOutput6;
            case 7 -> queueOutput7;
            case 8 -> queueOutput8;
            default -> null;
        };
    }

    private void setTargetAmount(long targetAmount) {
        var amount = Math.max(0, targetAmount);
        var type = getActiveType();
        var service = terminal.getService(type);
        if (service != null) {
            service.setTargetAmount(amount);
            terminal.setTargetAmount(type, amount);
        }
    }

    private void removeQueuedInput(int index) {
        var type = getActiveType();
        var service = terminal.getService(type);
        if (service != null && service.removeQueuedInput(index)) {
            terminal.setQueuedInputs(type, service.getQueuedInputs());
        }
    }

    private void setActiveTypeOrdinal(int ordinal) {
        var types = FurnaceType.values();
        activeTypeOrdinal = Math.max(0, Math.min(types.length - 1, ordinal));
    }

    private static GenericStack queuePreview(java.util.List<AEItemKey> queue, int index) {
        return index < queue.size() ? new GenericStack(queue.get(index), 1) : null;
    }

    private GenericStack queueOutputPreview(FurnaceType type, java.util.List<AEItemKey> queue, int index) {
        return index < queue.size() ? getOutputPreview(type, queue.get(index)) : null;
    }

    private void setNetworkEnabled(boolean enabled) {
        var service = terminal.getService(getActiveType());
        if (service != null) {
            service.setEnabled(enabled);
        }
    }
}
