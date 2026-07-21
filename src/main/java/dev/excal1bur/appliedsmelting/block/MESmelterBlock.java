package dev.excal1bur.appliedsmelting.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;

import dev.excal1bur.appliedsmelting.blockentity.MESmelterBlockEntity;
import dev.excal1bur.appliedsmelting.core.ModMenus;

public final class MESmelterBlock extends AEBaseEntityBlock<MESmelterBlockEntity> {
    public MESmelterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.horizontalFacing();
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
}
