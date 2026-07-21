package com.colinmoulds.ae2smelter.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.BlockHitResult;

import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;

import com.colinmoulds.ae2smelter.blockentity.SmeltingTerminalBlockEntity;
import com.colinmoulds.ae2smelter.core.ModMenus;

public final class SmeltingTerminalBlock extends AEBaseEntityBlock<SmeltingTerminalBlockEntity> {
    private static final VoxelShape NORTH_SHAPE = Shapes.or(
            Block.box(2, 2, 0, 14, 14, 2),
            Block.box(4, 4, 2, 12, 12, 3));
    private static final VoxelShape SOUTH_SHAPE = Shapes.or(
            Block.box(2, 2, 14, 14, 14, 16),
            Block.box(4, 4, 13, 12, 12, 14));
    private static final VoxelShape WEST_SHAPE = Shapes.or(
            Block.box(0, 2, 2, 2, 14, 14),
            Block.box(2, 4, 4, 3, 12, 12));
    private static final VoxelShape EAST_SHAPE = Shapes.or(
            Block.box(14, 2, 2, 16, 14, 14),
            Block.box(13, 4, 4, 14, 12, 12));

    public SmeltingTerminalBlock(Properties properties) {
        super(properties.noOcclusion());
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.horizontalFacing();
    }

    @Override
    protected VoxelShape getShape(
            BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof SmeltingTerminalBlockEntity terminal) {
            if (!level.isClientSide()) {
                MenuOpener.open(ModMenus.SMELTING_TERMINAL.get(), player, MenuLocators.forBlockEntity(terminal));
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
