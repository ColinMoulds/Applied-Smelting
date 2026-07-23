package dev.excal1bur.appliedsmelting.block;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import dev.excal1bur.appliedsmelting.blockentity.MESmelterBlockEntity;
import dev.excal1bur.appliedsmelting.core.ModBlocks;
import dev.excal1bur.appliedsmelting.core.ModItems;
import dev.excal1bur.appliedsmelting.core.ModMenus;
import dev.excal1bur.appliedsmelting.service.SmelterTier;
import dev.excal1bur.appliedsmelting.service.SmeltingService;

public final class MESmelterBlock extends AbstractCookingFurnaceBlock<MESmelterBlockEntity> {
    private final SmelterTier tier;

    public MESmelterBlock(Properties properties, SmelterTier tier) {
        super(properties);
        this.tier = tier;
    }

    public SmelterTier getTier() {
        return tier;
    }

    @Override
    protected ResourceKey<RecipePropertySet> recipePropertySet() {
        return RecipePropertySet.FURNACE_INPUT;
    }

    @Override
    protected MenuType<?> menuType() {
        return ModMenus.ME_SMELTER.get();
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
        if (player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof MESmelterBlockEntity smelter) {
            return tryApplyUpgradeKit(level, pos, smelter, stack, player);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    /** Applies a tier-upgrade kit if it matches this block's current tier; rejects with a message otherwise. */
    private InteractionResult tryApplyUpgradeKit(
            Level level, BlockPos pos, MESmelterBlockEntity smelter, ItemStack stack, Player player) {
        var targetTier = SmelterTier.fromKitLevel(ModItems.upgradeKitLevel(stack));
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
        var processingTicksRequired = smelter.getProcessingTicksRequired();
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

        // Everything worth keeping is already captured above - empty the old block entity's
        // inventories before the swap so vanilla's block-changed drop handling (BaseEntityBlock#onRemove,
        // triggered because setBlock below changes to a different Block instance) has nothing left to
        // dump on the ground.
        smelter.getInternalInventory().setItemDirect(0, ItemStack.EMPTY);
        smelter.getUpgrades().clear();

        var oldState = level.getBlockState(pos);
        var newBlock = ModBlocks.blockForTier(targetTier).get();
        var newState = copyProperties(oldState, newBlock.defaultBlockState());
        level.setBlock(pos, newState, 3);

        if (level.getBlockEntity(pos) instanceof MESmelterBlockEntity newSmelter) {
            newSmelter.restoreProcessingState(
                    bufferedInput,
                    progress,
                    processingTicksRequired,
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
}
