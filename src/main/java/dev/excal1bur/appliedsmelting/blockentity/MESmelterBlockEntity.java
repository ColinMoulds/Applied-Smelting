package dev.excal1bur.appliedsmelting.blockentity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.InternalInventory;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.blockentity.grid.AENetworkedInvBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.me.helpers.MachineSource;
import appeng.util.inv.AppEngInternalInventory;

import dev.excal1bur.appliedsmelting.service.SmeltingService;
import dev.excal1bur.appliedsmelting.service.SmeltingPowerMode;
import dev.excal1bur.appliedsmelting.service.SmelterStatus;
import dev.excal1bur.appliedsmelting.service.SmelterTier;
import dev.excal1bur.appliedsmelting.block.MESmelterBlock;
import dev.excal1bur.appliedsmelting.core.ModItems;

public final class MESmelterBlockEntity extends AENetworkedInvBlockEntity
        implements IGridTickable, IUpgradeableObject {
    private static final int PROCESSING_TICKS = 200;
    private static final double BASE_IDLE_AE_PER_TICK = 2.0;
    private static final double BASE_AE_FUEL_PER_WORK_TICK = 8.0;
    private static final double ENERGY_CARD_REDUCTION = 0.15;
    private static final double MINIMUM_ENERGY_MULTIPLIER = 0.4;
    private static final int FUEL_EFFICIENCY_PER_CARD_PERCENT = 25;

    private final AppEngInternalInventory inventory = new AppEngInternalInventory(this, 1);
    private final IUpgradeInventory upgrades;
    private final IActionSource actionSource = new MachineSource(this);
    private final SmelterTier tier;
    private int progress;
    private int fuelTicksRemaining;
    private int fuelTicksTotal;
    private boolean enabled = true;
    private SmeltingPowerMode powerMode = SmeltingPowerMode.ITEM_FUEL;
    private AEItemKey pinnedInput;
    private SmelterStatus status = SmelterStatus.WAITING_FOR_SELECTION;
    private AEItemKey pendingOutputKey;
    private int pendingOutputAmount;

    public MESmelterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        tier = state.getBlock() instanceof MESmelterBlock smelterBlock ? smelterBlock.getTier() : SmelterTier.DEFAULT;
        getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .addService(IGridTickable.class, this);
        upgrades = UpgradeInventories.forMachine(state.getBlock().asItem(), tier.upgradeSlots(), this::onUpgradesChanged);
        updateIdlePowerUsage();
    }

    public SmelterTier getTier() {
        return tier;
    }

    @Override
    protected net.minecraft.world.item.Item getItemFromBlockEntity() {
        return getBlockState().getBlock().asItem();
    }

    /** Restores in-flight processing state captured from a smelter this one just replaced (a tier upgrade). */
    public void restoreProcessingState(
            ItemStack bufferedInput,
            int progress,
            int fuelTicksRemaining,
            int fuelTicksTotal,
            AEItemKey pendingOutputKey,
            int pendingOutputAmount,
            boolean enabled,
            SmeltingPowerMode powerMode,
            AEItemKey pinnedInput) {
        inventory.setItemDirect(0, bufferedInput);
        this.progress = progress;
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
        fuelTicksRemaining = input.getIntOr("fuelTicksRemaining", 0);
        fuelTicksTotal = input.getIntOr("fuelTicksTotal", fuelTicksRemaining);
        enabled = input.getBooleanOr("enabled", true);
        powerMode = SmeltingPowerMode.fromSerializedName(input.getStringOr("powerMode", "item_fuel"));
        status = SmelterStatus.fromId(input.getIntOr("status", SmelterStatus.WAITING_FOR_SELECTION.id()));
        pinnedInput = readItemKey(input.childOrEmpty("pinnedInput"));
        upgrades.readFromNBT(input, "upgrades");
        updateIdlePowerUsage();
    }

    // saveAdditional/loadTag only cover disk persistence - AE2 syncs live block entity state to nearby
    // clients through this separate binary stream instead (see AEBaseBlockEntity#getUpdateTag, which
    // wraps writeToStream()). Without this override, status changes (smelting/idle/blocked) would never
    // reach an already-loaded client, only the value present at the moment the chunk was first sent.
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
        var service = grid.getService(SmeltingService.class);
        var selectedInput = service.assignInput(this);
        var selectedFuel = service.getSelectedFuel();
        var storage = grid.getStorageService().getInventory();

        if (selectedInput == null
                || powerMode == SmeltingPowerMode.ITEM_FUEL && selectedFuel == null) {
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
        var recipeInput = new SingleRecipeInput(input);
        var recipe = level.recipeAccess().getRecipeFor(RecipeType.SMELTING, recipeInput, level);
        if (recipe.isEmpty()) {
            returnInputToNetwork(grid.getStorageService().getInventory());
            setStatus(SmelterStatus.INVALID_RECIPE);
            service.deferAssignment(this, selectedInput);
            return TickRateModulation.SLOWER;
        }

        var pendingResult = recipe.get().value().assemble(recipeInput);
        pendingOutputKey = AEItemKey.of(pendingResult);
        pendingOutputAmount = pendingResult.getCount();

        if (progress >= PROCESSING_TICKS) {
            var result = pendingResult;
            var resultKey = AEItemKey.of(result);
            if (resultKey != null) {
                var amount = result.getCount();
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

        var workTicks = Math.min(ticksSinceLastCall * getSpeedMultiplier(), PROCESSING_TICKS - progress);
        if (powerMode == SmeltingPowerMode.ITEM_FUEL) {
            if (fuelTicksRemaining <= 0 && !consumeFuel(level, storage, selectedFuel)) {
                return TickRateModulation.SLOWER;
            }
            workTicks = Math.min(workTicks, fuelTicksRemaining);
            fuelTicksRemaining -= workTicks;
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
            SmeltingService service) {
        var stack = itemKey.toStack();
        var recipeInput = new SingleRecipeInput(stack);
        var recipe = level.recipeAccess().getRecipeFor(RecipeType.SMELTING, recipeInput, level);
        if (recipe.isEmpty()) {
            setStatus(SmelterStatus.INVALID_RECIPE);
            service.deferAssignment(this, itemKey);
            return false;
        }

        var result = recipe.get().value().assemble(recipeInput);
        var resultKey = AEItemKey.of(result);
        if (resultKey == null
                || storage.insert(resultKey, result.getCount(), Actionable.SIMULATE, actionSource)
                        != result.getCount()) {
            setStatus(SmelterStatus.OUTPUT_FULL);
            return false;
        }

        var storedAmount = storage.getAvailableStacks().get(resultKey);
        if (!service.canStartJob(this, resultKey, result.getCount(), storedAmount)) {
            setStatus(SmelterStatus.TARGET_REACHED);
            service.deferAssignment(this, itemKey);
            return false;
        }

        if (storage.extract(itemKey, 1, Actionable.MODULATE, actionSource) == 1) {
            inventory.setItemDirect(0, itemKey.toStack());
            progress = 0;
            pendingOutputKey = resultKey;
            pendingOutputAmount = result.getCount();
            setStatus(SmelterStatus.SMELTING);
            saveChanges();
            return true;
        }
        setStatus(SmelterStatus.MISSING_INPUT);
        service.deferAssignment(this, itemKey);
        return false;
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

    public long getPendingOutputAmount(AEItemKey output) {
        return output.equals(pendingOutputKey) ? pendingOutputAmount : 0;
    }

    public int getProgress() {
        return progress;
    }

    public int getFuelTicksRemaining() {
        return fuelTicksRemaining;
    }

    public int getFuelTicksTotal() {
        return fuelTicksTotal;
    }

    public AEItemKey getPendingOutputKey() {
        return pendingOutputKey;
    }

    public int getPendingOutputAmount() {
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
        var raw = tier.baseSpeedMultiplier() * (1 << cards);
        return (int) Math.round(Math.min(tier.accelerationCap(), raw));
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
        return BASE_IDLE_AE_PER_TICK * tier.idleDrawMultiplier() * getEnergyMultiplier();
    }

    public double getAeFuelPerWorkTick() {
        return BASE_AE_FUEL_PER_WORK_TICK * tier.aeFuelDrawMultiplier() * getEnergyMultiplier();
    }

    public double getMaximumAeFuelPerTick() {
        return powerMode == SmeltingPowerMode.AE_POWER ? getAeFuelPerWorkTick() * getSpeedMultiplier() : 0;
    }

    public int getFuelEfficiencyPercent() {
        return (int) Math.round(100 * tier.fuelEfficiencyMultiplier())
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
        getMainNode().ifPresent((grid, node) -> grid.getService(SmeltingService.class).releaseAssignment(this));
        saveChanges();
        wakeForSelectionChange();
    }

    private double getEnergyMultiplier() {
        return Math.max(
                MINIMUM_ENERGY_MULTIPLIER,
                1.0 - getEnergyCardCount() * ENERGY_CARD_REDUCTION);
    }

    private void updateIdlePowerUsage() {
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
        return Math.max(0, Math.min(100, progress * 100 / PROCESSING_TICKS));
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

    /**
     * The raw, tick-computed status, synced from the server via saveAdditional/loadTag. Unlike
     * {@link #getMachineStatus()}, this doesn't re-derive enabled/redstone/grid-active state
     * client-side (those are already folded into this value server-side via setStatus calls in
     * tickingRequest), which makes it the safe choice for client-only code like rendering.
     */
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
            return Component.translatable("message.appliedsmelting.progress", progress, PROCESSING_TICKS);
        }
        return Component.translatable(getMachineStatus().translationKey());
    }
}
