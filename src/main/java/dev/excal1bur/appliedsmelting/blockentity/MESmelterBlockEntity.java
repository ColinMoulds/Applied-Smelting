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
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.blockentity.grid.AENetworkedInvBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.me.helpers.MachineSource;
import appeng.util.inv.AppEngInternalInventory;

import dev.excal1bur.appliedsmelting.service.SmeltingService;
import dev.excal1bur.appliedsmelting.service.SmelterStatus;
import dev.excal1bur.appliedsmelting.core.ModBlocks;

public final class MESmelterBlockEntity extends AENetworkedInvBlockEntity
        implements IGridTickable, IUpgradeableObject {
    private static final int PROCESSING_TICKS = 200;
    private static final double AE_PER_TICK = 2.0;

    private final AppEngInternalInventory inventory = new AppEngInternalInventory(this, 1);
    private final IUpgradeInventory upgrades;
    private final IActionSource actionSource = new MachineSource(this);
    private int progress;
    private int fuelTicksRemaining;
    private int fuelTicksTotal;
    private boolean enabled = true;
    private SmelterStatus status = SmelterStatus.WAITING_FOR_SELECTION;
    private AEItemKey pendingOutputKey;
    private int pendingOutputAmount;

    public MESmelterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .setIdlePowerUsage(2.0)
                .addService(IGridTickable.class, this);
        upgrades = UpgradeInventories.forMachine(ModBlocks.ME_SMELTER_ITEM.get(), 4, this::onUpgradesChanged);
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
        upgrades.writeToNBT(output, "upgrades");
    }

    @Override
    public void loadTag(ValueInput input) {
        super.loadTag(input);
        progress = input.getIntOr("progress", 0);
        fuelTicksRemaining = input.getIntOr("fuelTicksRemaining", 0);
        fuelTicksTotal = input.getIntOr("fuelTicksTotal", fuelTicksRemaining);
        enabled = input.getBooleanOr("enabled", true);
        upgrades.readFromNBT(input, "upgrades");
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
        if (!node.isActive()) {
            setStatus(SmelterStatus.OFFLINE);
            return TickRateModulation.SLOWER;
        }

        var level = node.getLevel();
        var grid = node.getGrid();
        var service = grid.getService(SmeltingService.class);
        var selectedInput = service.getSelectedInput();
        var selectedFuel = service.getSelectedFuel();
        var storage = grid.getStorageService().getInventory();

        if (selectedInput == null || selectedFuel == null) {
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
        if (fuelTicksRemaining <= 0 && !consumeFuel(level, storage, selectedFuel)) {
            return TickRateModulation.SLOWER;
        }
        workTicks = Math.min(workTicks, fuelTicksRemaining);
        var energyNeeded = AE_PER_TICK * workTicks;
        var energy = grid.getEnergyService();
        if (energy.extractAEPower(energyNeeded, Actionable.SIMULATE, PowerMultiplier.CONFIG) + 0.001
                < energyNeeded) {
            setStatus(SmelterStatus.MISSING_POWER);
            return TickRateModulation.SLOWER;
        }

        energy.extractAEPower(energyNeeded, Actionable.MODULATE, PowerMultiplier.CONFIG);
        progress += workTicks;
        fuelTicksRemaining -= workTicks;
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
        fuelTicksRemaining = burnDuration;
        fuelTicksTotal = burnDuration;
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
        saveChanges();
        wakeForSelectionChange();
    }

    public int getSpeedMultiplier() {
        var cards = Math.min(4, upgrades.getInstalledUpgrades(AEItems.SPEED_CARD));
        return 1 << cards;
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
        if (!getMainNode().isActive()) {
            return SmelterStatus.OFFLINE;
        }
        return status;
    }

    private void setStatus(SmelterStatus status) {
        this.status = status;
    }

    public Component getStatusMessage() {
        if (getMachineStatus() == SmelterStatus.SMELTING) {
            return Component.translatable("message.appliedsmelting.progress", progress, PROCESSING_TICKS);
        }
        return Component.translatable(getMachineStatus().translationKey());
    }
}
