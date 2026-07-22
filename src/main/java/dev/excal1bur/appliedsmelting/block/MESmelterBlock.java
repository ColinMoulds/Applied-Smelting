package dev.excal1bur.appliedsmelting.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.api.stacks.AEItemKey;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;

import dev.excal1bur.appliedsmelting.blockentity.MESmelterBlockEntity;
import dev.excal1bur.appliedsmelting.core.ModBlocks;
import dev.excal1bur.appliedsmelting.core.ModItems;
import dev.excal1bur.appliedsmelting.core.ModMenus;
import dev.excal1bur.appliedsmelting.service.SmelterTier;
import dev.excal1bur.appliedsmelting.service.SmeltingService;

public final class MESmelterBlock extends AEBaseEntityBlock<MESmelterBlockEntity> {
    private final SmelterTier tier;

    public MESmelterBlock(Properties properties, SmelterTier tier) {
        super(properties);
        this.tier = tier;
    }

    public SmelterTier getTier() {
        return tier;
    }

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
        if (level.getBlockEntity(pos) instanceof MESmelterBlockEntity smelter) {
            if (player.isShiftKeyDown()) {
                // Must return a definitive result - falling through to super() would cascade into
                // useWithoutItem()'s toggle-enabled branch via vanilla's TRY_WITH_EMPTY_HAND.
                return tryApplyUpgradeKit(level, pos, smelter, stack, player);
            } else if (level.recipeAccess().propertySet(RecipePropertySet.FURNACE_INPUT).test(stack)) {
                if (!level.isClientSide()) {
                    var input = AEItemKey.of(stack);
                    var clear = input != null && input.equals(smelter.getPinnedInput());
                    smelter.setPinnedInput(clear ? null : input);
                    player.sendOverlayMessage(clear
                            ? Component.translatable("message.appliedsmelting.recipe_unpinned")
                            : Component.translatable("message.appliedsmelting.recipe_pinned", stack.getHoverName()));
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof MESmelterBlockEntity smelter) {
            if (!level.isClientSide()) {
                if (player.isShiftKeyDown()) {
                    smelter.toggleEnabled();
                    player.sendOverlayMessage(smelter.getStatusMessage());
                } else {
                    MenuOpener.open(ModMenus.ME_SMELTER.get(), player, MenuLocators.forBlockEntity(smelter));
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    /** Applies a tier-upgrade kit if it matches this block's current tier; rejects with a message otherwise. */
    private InteractionResult tryApplyUpgradeKit(
            Level level, BlockPos pos, MESmelterBlockEntity smelter, ItemStack stack, Player player) {
        var targetTier = ModItems.tierForUpgradeKit(stack);
        if (targetTier == null) {
            return InteractionResult.PASS;
        }
        if (targetTier.previousTier() != tier) {
            if (!level.isClientSide()) {
                var requiredTier = targetTier.previousTier();
                var requiredName = requiredTier == null
                        ? Component.translatable("block.appliedsmelting.me_smelter")
                        : ModBlocks.blockForTier(requiredTier).get().asItem().getDefaultInstance().getHoverName();
                player.sendOverlayMessage(
                        Component.translatable("message.appliedsmelting.wrong_tier_kit", requiredName));
            }
            return InteractionResult.SUCCESS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        var grid = smelter.getMainNode().getGrid();
        var service = grid == null ? null : grid.getService(SmeltingService.class);
        var assignment = service == null ? null : service.getAssignment(smelter);
        var deferred = service == null ? null : service.getDeferredAssignment(smelter);

        var bufferedInput = smelter.getInternalInventory().getStackInSlot(0).copy();
        var progress = smelter.getProgress();
        var fuelTicksRemaining = smelter.getFuelTicksRemaining();
        var fuelTicksTotal = smelter.getFuelTicksTotal();
        var pendingOutputKey = smelter.getPendingOutputKey();
        var pendingOutputAmount = smelter.getPendingOutputAmount();
        var enabled = smelter.isEnabled();
        var powerMode = smelter.getPowerMode();
        var pinnedInput = smelter.getPinnedInput();
        var upgradeStacks = new ArrayList<ItemStack>();
        for (var upgrade : smelter.getUpgrades()) {
            upgradeStacks.add(upgrade.copy());
        }

        var oldState = level.getBlockState(pos);
        var newBlock = ModBlocks.blockForTier(targetTier).get();
        var newState = copyProperties(oldState, newBlock.defaultBlockState());
        level.setBlock(pos, newState, 3);

        if (level.getBlockEntity(pos) instanceof MESmelterBlockEntity newSmelter) {
            newSmelter.restoreProcessingState(
                    bufferedInput,
                    progress,
                    fuelTicksRemaining,
                    fuelTicksTotal,
                    pendingOutputKey,
                    pendingOutputAmount,
                    enabled,
                    powerMode,
                    pinnedInput);
            var newUpgrades = newSmelter.getUpgrades();
            for (int i = 0; i < upgradeStacks.size() && i < newUpgrades.size(); i++) {
                newUpgrades.setItemDirect(i, upgradeStacks.get(i));
            }
            var newGrid = newSmelter.getMainNode().getGrid();
            var newService = newGrid == null ? null : newGrid.getService(SmeltingService.class);
            if (newService != null && (assignment != null || deferred != null)) {
                newService.transferAssignment(newSmelter, assignment, deferred);
            }
        }

        stack.shrink(1);
        player.sendOverlayMessage(Component.translatable("message.appliedsmelting.tier_upgraded", targetTier.serializedName()));
        return InteractionResult.SUCCESS;
    }

    private static BlockState copyProperties(BlockState oldState, BlockState newState) {
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
