package dev.excal1bur.appliedsmelting.client.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

import dev.excal1bur.appliedsmelting.service.SmelterStatus;

/** Data captured on the main thread for {@link MENetworkFurnaceBlockEntityRenderer#submit}. */
public final class SmelterRenderState extends BlockEntityRenderState {
    public float glowIntensity = 1.0F;
    public Direction facing = Direction.NORTH;
    public SmelterStatus status = SmelterStatus.OFFLINE;
}
