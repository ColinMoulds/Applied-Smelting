package dev.excal1bur.appliedsmelting.block;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import dev.excal1bur.appliedsmelting.blockentity.MECrucibleBlockEntity;
import dev.excal1bur.appliedsmelting.core.ModBlocks;
import dev.excal1bur.appliedsmelting.core.ModItems;
import dev.excal1bur.appliedsmelting.core.ModMenus;
import dev.excal1bur.appliedsmelting.core.ModRecipes;
import dev.excal1bur.appliedsmelting.service.CrucibleService;
import dev.excal1bur.appliedsmelting.service.CrucibleTier;

public final class MECrucibleBlock extends AbstractMENetworkFurnaceBlock<MECrucibleBlockEntity> {
    private final CrucibleTier tier;

    public MECrucibleBlock(Properties properties, CrucibleTier tier) {
        super(properties);
        this.tier = tier;
    }

    public CrucibleTier getTier() {
        return tier;
    }

    @Override
    protected boolean isValidPinInput(Level level, ItemStack stack) {
        return level instanceof ServerLevel serverLevel
                && serverLevel.recipeAccess()
                        .getRecipeFor(ModRecipes.CRUCIBLE_MELTING.get(), new SingleRecipeInput(stack), serverLevel)
                        .isPresent();
    }

    @Override
    protected MenuType<?> menuType() {
        return ModMenus.ME_CRUCIBLE.get();
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
        if (player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof MECrucibleBlockEntity machine) {
            return tryApplyUpgradeKit(level, pos, machine, stack, player);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    /** Applies a tier-upgrade kit if it matches this block's current tier; rejects with a message otherwise. */
    private InteractionResult tryApplyUpgradeKit(
            Level level, BlockPos pos, MECrucibleBlockEntity machine, ItemStack stack, Player player) {
        var targetTier = CrucibleTier.fromKitLevel(ModItems.upgradeKitLevel(stack));
        if (targetTier == null) {
            return InteractionResult.PASS;
        }
        if (targetTier.previousTier() != tier) {
            if (!level.isClientSide()) {
                var requiredTier = targetTier.previousTier();
                var requiredName = requiredTier == null
                        ? Component.translatable("block.appliedsmelting.me_crucible")
                        : ModBlocks.blockForCrucibleTier(requiredTier).get().asItem().getDefaultInstance().getHoverName();
                player.sendOverlayMessage(
                        Component.translatable("message.appliedsmelting.wrong_tier_kit", requiredName));
            }
            return InteractionResult.SUCCESS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        var grid = machine.getMainNode().getGrid();
        var service = grid == null ? null : grid.getService(CrucibleService.class);
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
        var newBlock = ModBlocks.blockForCrucibleTier(targetTier).get();
        var newState = copyProperties(oldState, newBlock.defaultBlockState());
        level.setBlock(pos, newState, 3);

        if (level.getBlockEntity(pos) instanceof MECrucibleBlockEntity newMachine) {
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
            var newService = newGrid == null ? null : newGrid.getService(CrucibleService.class);
            if (newService != null && (assignment != null || deferred != null)) {
                newService.transferAssignment(newMachine, assignment, deferred);
            }
        }

        stack.shrink(1);
        player.sendOverlayMessage(Component.translatable("message.appliedsmelting.tier_upgraded", targetTier.serializedName()));
        return InteractionResult.SUCCESS;
    }
}
