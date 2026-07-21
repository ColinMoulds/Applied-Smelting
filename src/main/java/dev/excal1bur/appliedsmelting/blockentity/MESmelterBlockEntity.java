package dev.excal1bur.appliedsmelting.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.AEItemKey;
import appeng.blockentity.grid.AENetworkedInvBlockEntity;
import appeng.me.helpers.MachineSource;
import appeng.util.inv.AppEngInternalInventory;

public final class MESmelterBlockEntity extends AENetworkedInvBlockEntity implements IGridTickable {
    private static final int PROCESSING_TICKS = 200;
    private static final double AE_PER_TICK = 2.0;

    private final AppEngInternalInventory inventory = new AppEngInternalInventory(this, 1);
    private final IActionSource actionSource = new MachineSource(this);
    private int progress;
    private boolean enabled = true;

    public MESmelterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        getMainNode()
                .setFlags(GridFlags.REQUIRE_CHANNEL)
                .setIdlePowerUsage(2.0)
                .addService(IGridTickable.class, this);
    }

    @Override
    public InternalInventory getInternalInventory() {
        return inventory;
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
        output.putBoolean("enabled", enabled);
    }

    @Override
    public void loadTag(ValueInput input) {
        super.loadTag(input);
        progress = input.getIntOr("progress", 0);
        enabled = input.getBooleanOr("enabled", true);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(1, 20, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (!enabled || !node.isActive()) {
            return TickRateModulation.SLOWER;
        }

        var level = node.getLevel();
        var grid = node.getGrid();
        if (inventory.getStackInSlot(0).isEmpty() && !pullNextInput(level, grid.getStorageService().getInventory())) {
            return TickRateModulation.SLOWER;
        }

        var input = inventory.getStackInSlot(0);
        var recipeInput = new SingleRecipeInput(input);
        var recipe = level.recipeAccess().getRecipeFor(RecipeType.SMELTING, recipeInput, level);
        if (recipe.isEmpty()) {
            returnInputToNetwork(grid.getStorageService().getInventory());
            return TickRateModulation.SLOWER;
        }

        if (progress >= PROCESSING_TICKS) {
            var result = recipe.get().value().assemble(recipeInput);
            var resultKey = AEItemKey.of(result);
            if (resultKey != null) {
                var storage = grid.getStorageService().getInventory();
                var amount = result.getCount();
                if (storage.insert(resultKey, amount, Actionable.SIMULATE, actionSource) == amount) {
                    storage.insert(resultKey, amount, Actionable.MODULATE, actionSource);
                    inventory.setItemDirect(0, ItemStack.EMPTY);
                    progress = 0;
                    saveChanges();
                    return TickRateModulation.URGENT;
                }
            }
            return TickRateModulation.SLOWER;
        }

        var workTicks = Math.min(ticksSinceLastCall, PROCESSING_TICKS - progress);
        var energyNeeded = AE_PER_TICK * workTicks;
        var energy = grid.getEnergyService();
        if (energy.extractAEPower(energyNeeded, Actionable.SIMULATE, PowerMultiplier.CONFIG) + 0.001
                < energyNeeded) {
            return TickRateModulation.SLOWER;
        }

        energy.extractAEPower(energyNeeded, Actionable.MODULATE, PowerMultiplier.CONFIG);
        progress += workTicks;
        saveChanges();
        return TickRateModulation.URGENT;
    }

    private boolean pullNextInput(ServerLevel level, appeng.api.storage.MEStorage storage) {
        for (var entry : getMainNode().getGrid().getStorageService().getCachedInventory()) {
            if (!(entry.getKey() instanceof AEItemKey itemKey) || entry.getLongValue() < 1) {
                continue;
            }

            var stack = itemKey.toStack();
            var recipeInput = new SingleRecipeInput(stack);
            var recipe = level.recipeAccess().getRecipeFor(RecipeType.SMELTING, recipeInput, level);
            if (recipe.isEmpty()) {
                continue;
            }

            var result = recipe.get().value().assemble(recipeInput);
            var resultKey = AEItemKey.of(result);
            if (resultKey == null
                    || storage.insert(resultKey, result.getCount(), Actionable.SIMULATE, actionSource)
                            != result.getCount()) {
                continue;
            }

            if (storage.extract(itemKey, 1, Actionable.MODULATE, actionSource) == 1) {
                inventory.setItemDirect(0, itemKey.toStack());
                progress = 0;
                saveChanges();
                return true;
            }
        }
        return false;
    }

    private void returnInputToNetwork(appeng.api.storage.MEStorage storage) {
        var stack = inventory.getStackInSlot(0);
        var key = AEItemKey.of(stack);
        if (key != null && storage.insert(key, 1, Actionable.MODULATE, actionSource) == 1) {
            inventory.setItemDirect(0, ItemStack.EMPTY);
            progress = 0;
            saveChanges();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        saveChanges();
        getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    public void toggleEnabled() {
        setEnabled(!enabled);
    }

    public boolean isWorking() {
        return !inventory.getStackInSlot(0).isEmpty();
    }

    public Component getStatusMessage() {
        if (!enabled) {
            return Component.translatable("message.ae2smelter.paused");
        }
        if (isWorking()) {
            return Component.translatable("message.ae2smelter.progress", progress, PROCESSING_TICKS);
        }
        return Component.translatable("message.ae2smelter.waiting");
    }
}
