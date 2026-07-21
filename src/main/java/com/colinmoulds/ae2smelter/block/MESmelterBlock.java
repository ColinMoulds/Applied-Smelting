package com.colinmoulds.ae2smelter.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import appeng.block.AEBaseEntityBlock;

import com.colinmoulds.ae2smelter.blockentity.MESmelterBlockEntity;

public final class MESmelterBlock extends AEBaseEntityBlock<MESmelterBlockEntity> {
    public MESmelterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof MESmelterBlockEntity smelter) {
            if (!level.isClientSide()) {
                smelter.toggleEnabled();
                player.sendOverlayMessage(smelter.getStatusMessage());
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
