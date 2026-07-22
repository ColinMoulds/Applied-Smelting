package dev.excal1bur.appliedsmelting.client.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

import dev.excal1bur.appliedsmelting.service.SmelterStatus;
import dev.excal1bur.appliedsmelting.service.SmelterTier;

/** Data captured on the main thread for {@link MESmelterBlockEntityRenderer#submit}. */
public final class SmelterRenderState extends BlockEntityRenderState {
    public SmelterTier tier = SmelterTier.DEFAULT;
    public Direction facing = Direction.NORTH;
    public SmelterStatus status = SmelterStatus.OFFLINE;
}
