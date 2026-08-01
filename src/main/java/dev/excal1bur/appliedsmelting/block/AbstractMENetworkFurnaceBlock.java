package dev.excal1bur.appliedsmelting.block;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.api.stacks.AEItemKey;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import dev.excal1bur.appliedsmelting.blockentity.AbstractMENetworkFurnaceBlockEntity;
import dev.excal1bur.appliedsmelting.core.ModItems;
import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

/** Shared right-click interactions (recipe pin, toggle, open menu) for every ME network furnace-style block. */
public abstract class AbstractMENetworkFurnaceBlock<T extends AbstractMENetworkFurnaceBlockEntity>
        extends AEBaseEntityBlock<T> {
    protected AbstractMENetworkFurnaceBlock(Properties properties) {
        super(properties);
    }

    /** Whether {@code stack} is a valid recipe input for this machine, for the pin-by-right-click check. */
    protected abstract boolean isValidPinInput(Level level, ItemStack stack);

    protected abstract MenuType<?> menuType();

    protected abstract int tierLevel();

    protected abstract Block blockForTierLevel(int tierLevel);

    protected abstract Class<? extends AbstractFurnaceNetworkService> serviceClass();

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.horizontalFacing();
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof AbstractMENetworkFurnaceBlockEntity machine) {
            if (player.isShiftKeyDown()) {
                return tryApplyUpgradeKit(level, pos, machine, stack, player);
            }
            if (isValidPinInput(level, stack)) {
                if (!level.isClientSide()) {
                    var input = AEItemKey.of(stack);
                    var clear = input != null && input.equals(machine.getPinnedInput());
                    machine.setPinnedInput(clear ? null : input);
                    player.sendOverlayMessage(
                            clear
                                    ? Component.translatable("message.appliedsmelting.recipe_unpinned")
                                    : Component.translatable(
                                            "message.appliedsmelting.recipe_pinned", stack.getHoverName()));
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    private InteractionResult tryApplyUpgradeKit(
            Level level, BlockPos pos, AbstractMENetworkFurnaceBlockEntity machine, ItemStack stack, Player player) {
        var targetTierLevel = ModItems.upgradeKitLevel(stack);
        if (targetTierLevel < 1 || targetTierLevel > 3) {
            // Must return a definitive result for upgrade kits, while unrelated held items pass
            // through without cascading into the empty-hand sneak interaction.
            return InteractionResult.PASS;
        }
        if (targetTierLevel != tierLevel() + 1) {
            if (!level.isClientSide()) {
                var requiredName = blockForTierLevel(targetTierLevel - 1)
                        .asItem()
                        .getDefaultInstance()
                        .getHoverName();
                player.sendOverlayMessage(
                        Component.translatable("message.appliedsmelting.wrong_tier_kit", requiredName));
            }
            return InteractionResult.SUCCESS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        var grid = machine.getMainNode().getGrid();
        var service = grid == null ? null : grid.getService(serviceClass());
        var assignment = service == null ? null : service.getAssignment(machine);
        var deferred = service == null ? null : service.getDeferredAssignment(machine);
        var processingState = machine.captureProcessingState();
        var upgradeStacks = new ArrayList<ItemStack>();
        for (var upgrade : machine.getUpgrades()) {
            upgradeStacks.add(upgrade.copy());
        }

        // Empty the old inventories after capturing them so the block replacement cannot drop
        // duplicate contents through BaseEntityBlock#onRemove.
        machine.getInternalInventory().setItemDirect(0, ItemStack.EMPTY);
        machine.getUpgrades().clear();

        var oldState = level.getBlockState(pos);
        var newBlock = blockForTierLevel(targetTierLevel);
        var newState = copyProperties(oldState, newBlock.defaultBlockState());
        level.setBlock(pos, newState, 3);

        if (level.getBlockEntity(pos) instanceof AbstractMENetworkFurnaceBlockEntity newMachine) {
            newMachine.restoreProcessingState(processingState);
            var newUpgrades = newMachine.getUpgrades();
            for (int i = 0; i < upgradeStacks.size() && i < newUpgrades.size(); i++) {
                newUpgrades.setItemDirect(i, upgradeStacks.get(i));
            }
            var newGrid = newMachine.getMainNode().getGrid();
            var newService = newGrid == null ? null : newGrid.getService(serviceClass());
            if (newService != null && (assignment != null || deferred != null)) {
                newService.transferAssignment(newMachine, assignment, deferred);
            }
        }

        stack.shrink(1);
        player.sendOverlayMessage(
                Component.translatable("message.appliedsmelting.tier_upgraded", "mk" + targetTierLevel));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof AbstractMENetworkFurnaceBlockEntity machine) {
            if (!level.isClientSide()) {
                if (player.isShiftKeyDown()) {
                    machine.toggleEnabled();
                    player.sendOverlayMessage(machine.getStatusMessage());
                } else {
                    MenuOpener.open(menuType(), player, MenuLocators.forBlockEntity(machine));
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    /** Copies every property {@code newState} also has from {@code oldState} - used when swapping tiers in place. */
    protected static BlockState copyProperties(BlockState oldState, BlockState newState) {
        var result = newState;
        for (var property : oldState.getProperties()) {
            if (result.hasProperty(property)) {
                result = copyProperty(oldState, result, property);
            }
        }
        return result;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(
            BlockState oldState, BlockState newState, Property<T> property) {
        return newState.setValue(property, oldState.getValue(property));
    }
}
