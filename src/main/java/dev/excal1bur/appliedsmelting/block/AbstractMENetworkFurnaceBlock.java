package dev.excal1bur.appliedsmelting.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.api.stacks.AEItemKey;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;

import dev.excal1bur.appliedsmelting.blockentity.AbstractMENetworkFurnaceBlockEntity;

/** Shared right-click interactions (recipe pin, toggle, open menu) for every ME network furnace-style block. */
public abstract class AbstractMENetworkFurnaceBlock<T extends AbstractMENetworkFurnaceBlockEntity>
        extends AEBaseEntityBlock<T> {
    protected AbstractMENetworkFurnaceBlock(Properties properties) {
        super(properties);
    }

    /** Whether {@code stack} is a valid recipe input for this machine, for the pin-by-right-click check. */
    protected abstract boolean isValidPinInput(Level level, ItemStack stack);

    protected abstract MenuType<?> menuType();

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
                // Must return a definitive result - falling through to super() would cascade into
                // useWithoutItem()'s toggle-enabled branch via vanilla's TRY_WITH_EMPTY_HAND.
                return InteractionResult.PASS;
            }
            if (isValidPinInput(level, stack)) {
                if (!level.isClientSide()) {
                    var input = AEItemKey.of(stack);
                    var clear = input != null && input.equals(machine.getPinnedInput());
                    machine.setPinnedInput(clear ? null : input);
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
}
