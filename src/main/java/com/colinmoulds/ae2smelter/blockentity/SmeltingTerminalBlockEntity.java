package com.colinmoulds.ae2smelter.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.networking.GridFlags;
import appeng.blockentity.grid.AENetworkedBlockEntity;

import com.colinmoulds.ae2smelter.service.SmeltingService;

public final class SmeltingTerminalBlockEntity extends AENetworkedBlockEntity {
    public SmeltingTerminalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL).setIdlePowerUsage(1.0);
    }

    public SmeltingService getSmeltingService() {
        var grid = getMainNode().getGrid();
        return grid == null ? null : grid.getService(SmeltingService.class);
    }
}
