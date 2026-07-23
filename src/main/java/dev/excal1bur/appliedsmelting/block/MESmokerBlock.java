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

import dev.excal1bur.appliedsmelting.blockentity.MESmokerBlockEntity;
import dev.excal1bur.appliedsmelting.core.ModBlocks;
import dev.excal1bur.appliedsmelting.core.ModItems;
import dev.excal1bur.appliedsmelting.core.ModMenus;
import dev.excal1bur.appliedsmelting.service.SmokerTier;
import dev.excal1bur.appliedsmelting.service.SmokingService;

public final class MESmokerBlock extends AbstractCookingFurnaceBlock<MESmokerBlockEntity> {
    private final SmokerTier tier;

    public MESmokerBlock(Properties properties, SmokerTier tier) {
        super(properties);
        this.tier = tier;
    }

    public SmokerTier getTier() {
        return tier;
    }

    @Override
    protected ResourceKey<RecipePropertySet> recipePropertySet() {
        return RecipePropertySet.SMOKER_INPUT;
    }

    @Override
    protected MenuType<?> menuType() {
        return ModMenus.ME_SMOKER.get();
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
        if (player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof MESmokerBlockEntity machine) {
            return tryApplyUpgradeKit(level, pos, machine, stack, player);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    /** Applies a tier-upgrade kit if it matches this block's current tier; rejects with a message otherwise. */
    private InteractionResult tryApplyUpgradeKit(
            Level level, BlockPos pos, MESmokerBlockEntity machine, ItemStack stack, Player player) {
        var targetTier = SmokerTier.fromKitLevel(ModItems.upgradeKitLevel(stack));
        if (targetTier == null) {
            return InteractionResult.PASS;
        }
        if (targetTier.previousTier() != tier) {
            if (!level.isClientSide()) {
                var requiredTier = targetTier.previousTier();
                var requiredName = requiredTier == null
                        ? Component.translatable("block.appliedsmelting.me_smoker")
                        : ModBlocks.blockForSmokerTier(requiredTier).get().asItem().getDefaultInstance().getHoverName();
                player.sendOverlayMessage(
                        Component.translatable("message.appliedsmelting.wrong_tier_kit", requiredName));
            }
            return InteractionResult.SUCCESS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        var grid = machine.getMainNode().getGrid();
        var service = grid == null ? null : grid.getService(SmokingService.class);
        var assignment = service == null ? null : service.getAssignment(machine);
        var deferred = service == null ? null : service.getDeferredAssignment(machine);

        var bufferedInput = machine.getInternalInventory().getStackInSlot(0).copy();
        var progress = machine.getProgress();
        var processingTicksRequired = machine.getProcessingTicksRequired();
        var fuelTicksRemaining = machine.getFuelTicksRemaining();
        var fuelTicksTotal = machine.getFuelTicksTotal();
        var pendingOutputKey = machine.getPendingOutputKey();
        var pendingOutputAmount = machine.getPendingOutputAmount();
        var enabled = machine.isEnabled();
        var powerMode = machine.getPowerMode();
        var pinnedInput = machine.getPinnedInput();
        var upgradeStacks = new ArrayList<ItemStack>();
        for (var upgrade : machine.getUpgrades()) {
            upgradeStacks.add(upgrade.copy());
        }

        machine.getInternalInventory().setItemDirect(0, ItemStack.EMPTY);
        machine.getUpgrades().clear();

        var oldState = level.getBlockState(pos);
        var newBlock = ModBlocks.blockForSmokerTier(targetTier).get();
        var newState = copyProperties(oldState, newBlock.defaultBlockState());
        level.setBlock(pos, newState, 3);

        if (level.getBlockEntity(pos) instanceof MESmokerBlockEntity newMachine) {
            newMachine.restoreProcessingState(
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
            var newUpgrades = newMachine.getUpgrades();
            for (int i = 0; i < upgradeStacks.size() && i < newUpgrades.size(); i++) {
                newUpgrades.setItemDirect(i, upgradeStacks.get(i));
            }
            var newGrid = newMachine.getMainNode().getGrid();
            var newService = newGrid == null ? null : newGrid.getService(SmokingService.class);
            if (newService != null && (assignment != null || deferred != null)) {
                newService.transferAssignment(newMachine, assignment, deferred);
            }
        }

        stack.shrink(1);
        player.sendOverlayMessage(Component.translatable("message.appliedsmelting.tier_upgraded", targetTier.serializedName()));
        return InteractionResult.SUCCESS;
    }
}
