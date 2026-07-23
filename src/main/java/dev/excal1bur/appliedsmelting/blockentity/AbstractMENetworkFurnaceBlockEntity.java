package dev.excal1bur.appliedsmelting.blockentity;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.InternalInventory;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.blockentity.grid.AENetworkedInvBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.me.helpers.MachineSource;
import appeng.util.inv.AppEngInternalInventory;

import dev.excal1bur.appliedsmelting.core.ModItems;
import dev.excal1bur.appliedsmelting.service.SmeltingPowerMode;
import dev.excal1bur.appliedsmelting.service.SmelterStatus;
import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;

/** Shared network/queue/upgrade-card/power logic for every ME network furnace-style machine. */
public abstract class AbstractMENetworkFurnaceBlockEntity extends AENetworkedInvBlockEntity
        implements IGridTickable, IUpgradeableObject {
    private static final int DEFAULT_PROCESSING_TICKS = 200;
    private static final double BASE_IDLE_AE_PER_TICK = 2.0;
    private static final double BASE_AE_FUEL_PER_WORK_TICK = 8.0;
    private static final double BASE_LAVA_MB_PER_WORK_TICK = 1.0;
    private static final double ENERGY_CARD_REDUCTION = 0.15;
    private static final double MINIMUM_ENERGY_MULTIPLIER = 0.4;
    private static final int FUEL_EFFICIENCY_PER_CARD_PERCENT = 25;

    private final AppEngInternalInventory inventory = new AppEngInternalInventory(this, 1);
    private final IUpgradeInventory upgrades;
    private final IActionSource actionSource = new MachineSource(this);
    private int progress;
    private int processingTicksRequired = DEFAULT_PROCESSING_TICKS;
    private int fuelTicksRemaining;
    private int fuelTicksTotal;
    private boolean enabled = true;
    private SmeltingPowerMode powerMode = SmeltingPowerMode.ITEM_FUEL;
    private AEItemKey pinnedInput;
    private SmelterStatus status = SmelterStatus.WAITING_FOR_SELECTION;
    private AEKey pendingOutputKey;
    private long pendingOutputAmount;

    protected AbstractMENetworkFurnaceBlockEntity(
            BlockEntityType<?> type, BlockPos pos, BlockState state, int upgradeSlots) {
        super(type, pos, state);
        getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .addService(IGridTickable.class, this);
        upgrades = UpgradeInventories.forMachine(state.getBlock().asItem(), upgradeSlots, this::onUpgradesChanged);
        // Not called here: idleDrawMultiplier() etc. are abstract and the subclass's own fields
        // (e.g. tier) aren't initialized until after this constructor returns. Subclasses must call
        // updateIdlePowerUsage() themselves once their fields are set.
    }

    /** Which grid service handles this machine's queue - must match the machine's own concrete type. */
    protected abstract Class<? extends AbstractFurnaceNetworkService> serviceClass();

    /** A resolved recipe: what network key/amount the buffered input will produce, and how long it takes. */
    protected record ResolvedRecipe(AEKey outputKey, long outputAmount, int processingTicks) {
    }

    /** Resolves what {@code input} produces for this machine type; empty if there's no matching recipe. */
    protected abstract Optional<ResolvedRecipe> resolveRecipe(ServerLevel level, ItemStack input);

    /** Status LED glow intensity multiplier; 1.0 for machines with no tier-based variation. */
    public float getGlowIntensity() {
        return 1.0F;
    }

    protected abstract double baseSpeedMultiplier();

    protected abstract double accelerationCap();

    protected abstract double idleDrawMultiplier();

    protected abstract double aeFuelDrawMultiplier();

    protected abstract double lavaFuelDrawMultiplier();

    protected abstract double fuelEfficiencyMultiplier();

    public abstract int baseQueueCapacity();

    public abstract int capacityCardCap();

    @Override
    protected net.minecraft.world.item.Item getItemFromBlockEntity() {
        return getBlockState().getBlock().asItem();
    }

    /** Restores in-flight processing state captured from a machine this one just replaced (e.g. a tier upgrade). */
    public void restoreProcessingState(
            ItemStack bufferedInput,
            int progress,
            int processingTicksRequired,
            int fuelTicksRemaining,
            int fuelTicksTotal,
            AEKey pendingOutputKey,
            long pendingOutputAmount,
            boolean enabled,
            SmeltingPowerMode powerMode,
            AEItemKey pinnedInput) {
        inventory.setItemDirect(0, bufferedInput);
        this.progress = progress;
        this.processingTicksRequired = processingTicksRequired;
        this.fuelTicksRemaining = fuelTicksRemaining;
        this.fuelTicksTotal = fuelTicksTotal;
        this.pendingOutputKey = pendingOutputKey;
        this.pendingOutputAmount = pendingOutputAmount;
        this.enabled = enabled;
        this.powerMode = powerMode;
        this.pinnedInput = pinnedInput;
        updateIdlePowerUsage();
        saveChanges();
    }

    @Override
    public InternalInventory getInternalInventory() {
        return inventory;
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return upgrades;
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var upgrade : upgrades) {
            drops.add(upgrade);
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        upgrades.clear();
    }

    @Override
    public InternalInventory getSubInventory(Identifier id) {
        if (id.equals(ISegmentedInventory.UPGRADES)) {
            return upgrades;
        }
        return super.getSubInventory(id);
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory changedInventory, int slot) {
        saveChanges();
        getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("progress", progress);
        output.putInt("processingTicksRequired", processingTicksRequired);
        output.putInt("fuelTicksRemaining", fuelTicksRemaining);
        output.putInt("fuelTicksTotal", fuelTicksTotal);
        output.putBoolean("enabled", enabled);
        output.putString("powerMode", powerMode.serializedName());
        output.putInt("status", status.id());
        writeItemKey(output.child("pinnedInput"), pinnedInput);
        upgrades.writeToNBT(output, "upgrades");
    }

    @Override
    public void loadTag(ValueInput input) {
        super.loadTag(input);
        progress = input.getIntOr("progress", 0);
        processingTicksRequired = input.getIntOr("processingTicksRequired", DEFAULT_PROCESSING_TICKS);
        fuelTicksRemaining = input.getIntOr("fuelTicksRemaining", 0);
        fuelTicksTotal = input.getIntOr("fuelTicksTotal", fuelTicksRemaining);
        enabled = input.getBooleanOr("enabled", true);
        powerMode = SmeltingPowerMode.fromSerializedName(input.getStringOr("powerMode", "item_fuel"));
        status = SmelterStatus.fromId(input.getIntOr("status", SmelterStatus.WAITING_FOR_SELECTION.id()));
        pinnedInput = readItemKey(input.childOrEmpty("pinnedInput"));
        upgrades.readFromNBT(input, "upgrades");
        updateIdlePowerUsage();
    }

    // AE2 syncs live block entity state via this stream, separate from saveAdditional/loadTag.
    @Override
    protected void writeToStream(net.minecraft.network.RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeVarInt(status.id());
    }

    @Override
    protected boolean readFromStream(net.minecraft.network.RegistryFriendlyByteBuf data) {
        var changed = super.readFromStream(data);
        var newStatus = SmelterStatus.fromId(data.readVarInt());
        if (newStatus != status) {
            status = newStatus;
            changed = true;
        }
        return changed;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(1, 20, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (!enabled) {
            setStatus(SmelterStatus.PAUSED);
            return TickRateModulation.SLOWER;
        }
        if (!isRedstoneAllowed()) {
            setStatus(SmelterStatus.REDSTONE_PAUSED);
            return TickRateModulation.SLOWER;
        }
        if (!node.isActive()) {
            setStatus(SmelterStatus.OFFLINE);
            return TickRateModulation.SLOWER;
        }

        var level = node.getLevel();
        var grid = node.getGrid();
        var service = grid.getService(serviceClass());
        var selectedInput = service.assignInput(this);
        var selectedFuel = service.getSelectedFuel();
        var storage = grid.getStorageService().getInventory();

        // Checked before selection so a player who queued an item but has no fuel/power sees
        // "missing fuel", not the more ambiguous "waiting for selection".
        if (!hasFuelForCurrentMode(grid, storage, selectedFuel)) {
            service.releaseAssignment(this);
            returnInputToNetwork(storage);
            setStatus(powerMode == SmeltingPowerMode.AE_POWER ? SmelterStatus.MISSING_POWER : SmelterStatus.MISSING_FUEL);
            return TickRateModulation.SLOWER;
        }

        if (selectedInput == null) {
            service.releaseAssignment(this);
            if (!returnInputToNetwork(storage)) {
                setStatus(SmelterStatus.OUTPUT_FULL);
                return TickRateModulation.SLOWER;
            }
            setStatus(SmelterStatus.WAITING_FOR_SELECTION);
            return TickRateModulation.SLOWER;
        }

        var bufferedInput = AEItemKey.of(inventory.getStackInSlot(0));
        if (bufferedInput != null && !bufferedInput.equals(selectedInput)) {
            if (!returnInputToNetwork(storage)) {
                setStatus(SmelterStatus.OUTPUT_FULL);
                return TickRateModulation.SLOWER;
            }
        }

        if (inventory.getStackInSlot(0).isEmpty()
                && !pullSelectedInput(level, storage, selectedInput, service)) {
            return TickRateModulation.SLOWER;
        }

        var input = inventory.getStackInSlot(0);
        var resolved = resolveRecipe(level, input);
        if (resolved.isEmpty()) {
            returnInputToNetwork(storage);
            setStatus(SmelterStatus.INVALID_RECIPE);
            service.deferAssignment(this, selectedInput);
            return TickRateModulation.SLOWER;
        }

        var pending = resolved.get();
        pendingOutputKey = pending.outputKey();
        pendingOutputAmount = pending.outputAmount();

        if (progress >= processingTicksRequired) {
            var resultKey = pending.outputKey();
            var amount = pending.outputAmount();
            if (resultKey != null) {
                if (storage.insert(resultKey, amount, Actionable.SIMULATE, actionSource) == amount) {
                    storage.insert(resultKey, amount, Actionable.MODULATE, actionSource);
                    inventory.setItemDirect(0, ItemStack.EMPTY);
                    progress = 0;
                    clearPendingOutput();
                    saveChanges();
                    setStatus(SmelterStatus.SMELTING);
                    return TickRateModulation.URGENT;
                }
            }
            setStatus(SmelterStatus.OUTPUT_FULL);
            return TickRateModulation.SLOWER;
        }

        var workTicks = Math.min(ticksSinceLastCall * getSpeedMultiplier(), processingTicksRequired - progress);
        if (powerMode == SmeltingPowerMode.ITEM_FUEL) {
            if (fuelTicksRemaining <= 0 && !consumeFuel(level, storage, selectedFuel)) {
                return TickRateModulation.SLOWER;
            }
            workTicks = Math.min(workTicks, fuelTicksRemaining);
            fuelTicksRemaining -= workTicks;
        } else if (powerMode == SmeltingPowerMode.LAVA_FUEL) {
            var lavaNeeded = (long) Math.ceil(getLavaMbPerWorkTick() * workTicks);
            var lavaKey = AEFluidKey.of(Fluids.LAVA);
            if (storage.extract(lavaKey, lavaNeeded, Actionable.SIMULATE, actionSource) < lavaNeeded) {
                setStatus(SmelterStatus.MISSING_FUEL);
                return TickRateModulation.SLOWER;
            }
            storage.extract(lavaKey, lavaNeeded, Actionable.MODULATE, actionSource);
        } else {
            var energyNeeded = getAeFuelPerWorkTick() * workTicks;
            var energy = grid.getEnergyService();
            if (energy.extractAEPower(energyNeeded, Actionable.SIMULATE, PowerMultiplier.CONFIG) + 0.001
                    < energyNeeded) {
                setStatus(SmelterStatus.MISSING_POWER);
                return TickRateModulation.SLOWER;
            }
            energy.extractAEPower(energyNeeded, Actionable.MODULATE, PowerMultiplier.CONFIG);
        }

        progress += workTicks;
        setStatus(SmelterStatus.SMELTING);
        saveChanges();
        return TickRateModulation.URGENT;
    }

    private boolean pullSelectedInput(
            ServerLevel level,
            appeng.api.storage.MEStorage storage,
            AEItemKey itemKey,
            AbstractFurnaceNetworkService service) {
        var stack = itemKey.toStack();
        var resolved = resolveRecipe(level, stack);
        if (resolved.isEmpty()) {
            setStatus(SmelterStatus.INVALID_RECIPE);
            service.deferAssignment(this, itemKey);
            return false;
        }

        var pending = resolved.get();
        var resultKey = pending.outputKey();
        var amount = pending.outputAmount();
        if (resultKey == null
                || storage.insert(resultKey, amount, Actionable.SIMULATE, actionSource) != amount) {
            setStatus(SmelterStatus.OUTPUT_FULL);
            return false;
        }

        var storedAmount = storage.getAvailableStacks().get(resultKey);
        if (!service.canStartJob(this, resultKey, amount, storedAmount)) {
            setStatus(SmelterStatus.TARGET_REACHED);
            service.deferAssignment(this, itemKey);
            return false;
        }

        if (storage.extract(itemKey, 1, Actionable.MODULATE, actionSource) == 1) {
            inventory.setItemDirect(0, itemKey.toStack());
            progress = 0;
            processingTicksRequired = pending.processingTicks();
            pendingOutputKey = resultKey;
            pendingOutputAmount = amount;
            setStatus(SmelterStatus.SMELTING);
            saveChanges();
            return true;
        }
        setStatus(SmelterStatus.MISSING_INPUT);
        service.deferAssignment(this, itemKey);
        return false;
    }

    /** Whether the network can currently supply this machine's power mode, checked before any item is selected. */
    private boolean hasFuelForCurrentMode(IGrid grid, MEStorage storage, AEItemKey selectedFuel) {
        return switch (powerMode) {
            case ITEM_FUEL -> fuelTicksRemaining > 0
                    || selectedFuel != null && storage.extract(selectedFuel, 1, Actionable.SIMULATE, actionSource) == 1;
            case AE_POWER -> {
                var needed = getAeFuelPerWorkTick();
                yield grid.getEnergyService().extractAEPower(needed, Actionable.SIMULATE, PowerMultiplier.CONFIG) + 0.001
                        >= needed;
            }
            case LAVA_FUEL -> {
                var needed = (long) Math.ceil(getLavaMbPerWorkTick());
                yield storage.extract(AEFluidKey.of(Fluids.LAVA), needed, Actionable.SIMULATE, actionSource) >= needed;
            }
        };
    }

    private boolean consumeFuel(ServerLevel level, appeng.api.storage.MEStorage storage, AEItemKey fuelKey) {
        var fuelStack = fuelKey.toStack();
        var burnDuration = level.fuelValues().burnDuration(fuelStack);
        if (burnDuration <= 0) {
            setStatus(SmelterStatus.MISSING_FUEL);
            return false;
        }

        var remainderTemplate = fuelStack.getItem().getCraftingRemainder();
        var remainder = remainderTemplate == null ? ItemStack.EMPTY : remainderTemplate.create();
        var remainderKey = AEItemKey.of(remainder);
        if (remainderKey != null
                && storage.insert(remainderKey, remainder.getCount(), Actionable.SIMULATE, actionSource)
                        != remainder.getCount()) {
            setStatus(SmelterStatus.OUTPUT_FULL);
            return false;
        }

        if (storage.extract(fuelKey, 1, Actionable.MODULATE, actionSource) != 1) {
            setStatus(SmelterStatus.MISSING_FUEL);
            return false;
        }
        if (remainderKey != null) {
            storage.insert(remainderKey, remainder.getCount(), Actionable.MODULATE, actionSource);
        }
        var adjustedBurnDuration = Math.max(1, (int) ((long) burnDuration * getFuelEfficiencyPercent() / 100));
        fuelTicksRemaining = adjustedBurnDuration;
        fuelTicksTotal = adjustedBurnDuration;
        saveChanges();
        return true;
    }

    private boolean returnInputToNetwork(appeng.api.storage.MEStorage storage) {
        var stack = inventory.getStackInSlot(0);
        var key = AEItemKey.of(stack);
        if (key == null) {
            clearPendingOutput();
            return true;
        }
        if (key != null && storage.insert(key, 1, Actionable.MODULATE, actionSource) == 1) {
            inventory.setItemDirect(0, ItemStack.EMPTY);
            progress = 0;
            clearPendingOutput();
            saveChanges();
            return true;
        }
        return false;
    }

    private void clearPendingOutput() {
        pendingOutputKey = null;
        pendingOutputAmount = 0;
    }

    public long getPendingOutputAmount(AEKey output) {
        return output.equals(pendingOutputKey) ? pendingOutputAmount : 0;
    }

    public int getProgress() {
        return progress;
    }

    public int getProcessingTicksRequired() {
        return processingTicksRequired;
    }

    public int getFuelTicksRemaining() {
        return fuelTicksRemaining;
    }

    public int getFuelTicksTotal() {
        return fuelTicksTotal;
    }

    public AEKey getPendingOutputKey() {
        return pendingOutputKey;
    }

    public long getPendingOutputAmount() {
        return pendingOutputAmount;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setStatus(enabled ? SmelterStatus.WAITING_FOR_SELECTION : SmelterStatus.PAUSED);
        saveChanges();
        getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    public void toggleEnabled() {
        setEnabled(!enabled);
    }

    public void wakeForSelectionChange() {
        getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    private void onUpgradesChanged() {
        updateIdlePowerUsage();
        saveChanges();
        wakeForSelectionChange();
    }

    public int getSpeedMultiplier() {
        var cards = Math.min(4, upgrades.getInstalledUpgrades(AEItems.SPEED_CARD));
        var raw = baseSpeedMultiplier() * (1 << cards);
        return (int) Math.round(Math.min(accelerationCap(), raw));
    }

    public int getEnergyCardCount() {
        return Math.min(4, upgrades.getInstalledUpgrades(AEItems.ENERGY_CARD));
    }

    public int getFuelEfficiencyCardCount() {
        return Math.min(4, upgrades.getInstalledUpgrades(ModItems.FUEL_EFFICIENCY_CARD));
    }

    public int getCapacityCardCount() {
        return Math.min(4, upgrades.getInstalledUpgrades(AEItems.CAPACITY_CARD));
    }

    public boolean hasRedstoneCard() {
        return upgrades.getInstalledUpgrades(AEItems.REDSTONE_CARD) > 0;
    }

    public double getIdleAePerTick() {
        return BASE_IDLE_AE_PER_TICK * idleDrawMultiplier() * getEnergyMultiplier();
    }

    public double getAeFuelPerWorkTick() {
        return BASE_AE_FUEL_PER_WORK_TICK * aeFuelDrawMultiplier() * getEnergyMultiplier();
    }

    public double getMaximumAeFuelPerTick() {
        return powerMode == SmeltingPowerMode.AE_POWER ? getAeFuelPerWorkTick() * getSpeedMultiplier() : 0;
    }

    /** Network-drawn lava consumption rate, in mB per work tick, before the Fuel Efficiency Card bonus. */
    public double getLavaMbPerWorkTick() {
        return BASE_LAVA_MB_PER_WORK_TICK * lavaFuelDrawMultiplier() * 100.0 / getFuelEfficiencyPercent();
    }

    public double getMaximumLavaMbPerTick() {
        return powerMode == SmeltingPowerMode.LAVA_FUEL ? getLavaMbPerWorkTick() * getSpeedMultiplier() : 0;
    }

    public int getFuelEfficiencyPercent() {
        return (int) Math.round(100 * fuelEfficiencyMultiplier())
                + getFuelEfficiencyCardCount() * FUEL_EFFICIENCY_PER_CARD_PERCENT;
    }

    public SmeltingPowerMode getPowerMode() {
        return powerMode;
    }

    public void setPowerMode(SmeltingPowerMode powerMode) {
        if (this.powerMode != powerMode) {
            this.powerMode = powerMode;
            saveChanges();
            wakeForSelectionChange();
        }
    }

    public AEItemKey getPinnedInput() {
        return pinnedInput;
    }

    public void setPinnedInput(AEItemKey pinnedInput) {
        if (java.util.Objects.equals(this.pinnedInput, pinnedInput)) {
            return;
        }
        this.pinnedInput = pinnedInput;
        getMainNode().ifPresent((grid, node) -> grid.getService(serviceClass()).releaseAssignment(this));
        saveChanges();
        wakeForSelectionChange();
    }

    private double getEnergyMultiplier() {
        return Math.max(
                MINIMUM_ENERGY_MULTIPLIER,
                1.0 - getEnergyCardCount() * ENERGY_CARD_REDUCTION);
    }

    protected void updateIdlePowerUsage() {
        getMainNode().setIdlePowerUsage(getIdleAePerTick());
    }

    private boolean isRedstoneAllowed() {
        var level = getLevel();
        return !hasRedstoneCard() || level == null || level.hasNeighborSignal(getBlockPos());
    }

    private static AEItemKey readItemKey(ValueInput input) {
        var stack = GenericStack.readTag(input);
        return stack != null && stack.what() instanceof AEItemKey itemKey ? itemKey : null;
    }

    private static void writeItemKey(ValueOutput output, AEItemKey key) {
        if (key != null) {
            GenericStack.writeTag(output, new GenericStack(key, 1));
        }
    }

    public boolean isWorking() {
        return !inventory.getStackInSlot(0).isEmpty();
    }

    public boolean isActivelySmelting() {
        return getMachineStatus() == SmelterStatus.SMELTING;
    }

    public int getProgressPercent() {
        return Math.max(0, Math.min(100, progress * 100 / processingTicksRequired));
    }

    public int getFuelPercent() {
        if (fuelTicksRemaining <= 0 || fuelTicksTotal <= 0) {
            return 0;
        }
        return Math.max(1, Math.min(100, (int) ((long) fuelTicksRemaining * 100 / fuelTicksTotal)));
    }

    public SmelterStatus getMachineStatus() {
        if (!enabled) {
            return SmelterStatus.PAUSED;
        }
        if (!isRedstoneAllowed()) {
            return SmelterStatus.REDSTONE_PAUSED;
        }
        if (!getMainNode().isActive()) {
            return SmelterStatus.OFFLINE;
        }
        return status;
    }

    /** Server-computed status as-is, without {@link #getMachineStatus()}'s client-side re-derivation. */
    public SmelterStatus getRawStatus() {
        return status;
    }

    private void setStatus(SmelterStatus status) {
        if (this.status != status) {
            this.status = status;
            markForUpdate();
        }
    }

    public Component getStatusMessage() {
        if (getMachineStatus() == SmelterStatus.SMELTING) {
            return Component.translatable("message.appliedsmelting.progress", progress, processingTicksRequired);
        }
        return Component.translatable(getMachineStatus().translationKey());
    }
}
