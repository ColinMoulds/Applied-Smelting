package dev.excal1bur.appliedsmelting.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

import appeng.api.orientation.IOrientationStrategy;

import dev.excal1bur.appliedsmelting.blockentity.MESmelterBlockEntity;
import dev.excal1bur.appliedsmelting.service.SmelterStatus;
import dev.excal1bur.appliedsmelting.service.SmelterTier;

/**
 * Recolors the ME Smelter's front-face status dot (bottom-right texel, the same spot every tier's
 * texture already reserves for it) based on live machine status, using the same technique AE2 uses
 * for its own ME Drive cell status LEDs: a small untextured quad on {@link AppliedSmeltingRenderTypes#SMELTER_STATUS_LED}
 * (no sampler, no lightmap - see appeng.client.renderer.blockentity.CellLedRenderer), colored purely
 * via per-vertex color rather than a tinted texture.
 */
public final class MESmelterBlockEntityRenderer implements BlockEntityRenderer<MESmelterBlockEntity, SmelterRenderState> {
    private static final int COLOR_RUNNING = 0xFF3CD94A;
    private static final int COLOR_IDLE = 0xFF3C8CD9;
    private static final int COLOR_BLOCKED = 0xFFD9433C;
    private static final int COLOR_DISCONNECTED = 0xFF1A1A1A;

    // Bottom-right texel (13,13) of the 16x16 front-face texture. Vanilla's cube model bakes the
    // north face with u = 1 - localX (see CuboidFace.UVs / FaceInfo.NORTH), so a texture column near
    // u=1 (texel 13/16) lands at LOW local X - which is screen-RIGHT once viewed head-on, since a
    // camera facing +Z (looking at the north face) has "right" pointing toward -X.
    private static final float LED_MIN_X = 2.0F / 16.0F;
    private static final float LED_MAX_X = 3.0F / 16.0F;
    private static final float LED_MIN_Y = 2.0F / 16.0F;
    private static final float LED_MAX_Y = 3.0F / 16.0F;
    private static final float LED_Z = -5.0E-4F;

    public MESmelterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public SmelterRenderState createRenderState() {
        return new SmelterRenderState();
    }

    @Override
    public void extractRenderState(
            MESmelterBlockEntity blockEntity,
            SmelterRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.tier = blockEntity.getTier();
        state.status = blockEntity.getRawStatus();
        var blockState = blockEntity.getBlockState();
        state.facing = IOrientationStrategy.get(blockState).getFacing(blockState);
    }

    @Override
    public void submit(
            SmelterRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        var color = applyIntensity(statusColor(state.status), intensityFor(state.tier));

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRotationFor(state.facing)));
        poseStack.translate(-0.5, -0.5, -0.5);

        submitNodeCollector.submitCustomGeometry(
                poseStack, AppliedSmeltingRenderTypes.SMELTER_STATUS_LED, (pose, buffer) -> renderLed(pose, buffer, color));

        poseStack.popPose();
    }

    private static float yRotationFor(Direction facing) {
        return switch (facing) {
            case EAST -> 90.0F;
            case SOUTH -> 180.0F;
            case WEST -> 270.0F;
            default -> 0.0F;
        };
    }

    private static int statusColor(SmelterStatus status) {
        return switch (status) {
            case SMELTING -> COLOR_RUNNING;
            case WAITING_FOR_SELECTION, TARGET_REACHED, PAUSED -> COLOR_IDLE;
            // Not connected/powered to the ME network at all (IGridNode#isActive() == false) - keep this
            // visually distinct from a connected-but-blocked smelter (red) rather than lumping them together.
            case OFFLINE, NO_SMELTERS -> COLOR_DISCONNECTED;
            case MISSING_INPUT, MISSING_FUEL, MISSING_POWER, OUTPUT_FULL,
                    INVALID_RECIPE, REDSTONE_PAUSED -> COLOR_BLOCKED;
        };
    }

    private static float intensityFor(SmelterTier tier) {
        return switch (tier) {
            case DEFAULT, MK1 -> 1.0F;
            case MK2 -> 1.15F;
            case MK3 -> 1.35F;
        };
    }

    private static int applyIntensity(int color, float intensity) {
        int a = color >>> 24 & 0xFF;
        int r = Math.min(255, Math.round((color >> 16 & 0xFF) * intensity));
        int g = Math.min(255, Math.round((color >> 8 & 0xFF) * intensity));
        int b = Math.min(255, Math.round((color & 0xFF) * intensity));
        return a << 24 | r << 16 | g << 8 | b;
    }

    private static void renderLed(PoseStack.Pose pose, VertexConsumer buffer, int color) {
        buffer.addVertex(pose, LED_MIN_X, LED_MIN_Y, LED_Z).setColor(color);
        buffer.addVertex(pose, LED_MAX_X, LED_MIN_Y, LED_Z).setColor(color);
        buffer.addVertex(pose, LED_MAX_X, LED_MAX_Y, LED_Z).setColor(color);
        buffer.addVertex(pose, LED_MIN_X, LED_MAX_Y, LED_Z).setColor(color);
    }
}
