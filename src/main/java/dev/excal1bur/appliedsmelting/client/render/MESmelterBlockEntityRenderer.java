package dev.excal1bur.appliedsmelting.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

import appeng.api.orientation.IOrientationStrategy;

import dev.excal1bur.appliedsmelting.AppliedSmelting;
import dev.excal1bur.appliedsmelting.blockentity.MESmelterBlockEntity;
import dev.excal1bur.appliedsmelting.service.SmelterStatus;
import dev.excal1bur.appliedsmelting.service.SmelterTier;

/**
 * Recolors the ME Smelter's existing front-face status dot (bottom-right, the same single texel
 * every tier's texture already reserves for it) at full brightness based on live machine status,
 * the same way AE2's ME Drive lights up its cell status lights: green while actively smelting,
 * blue while idle but fine, red when blocked (missing fuel/power/input, output full, etc.).
 */
public final class MESmelterBlockEntityRenderer implements BlockEntityRenderer<MESmelterBlockEntity, SmelterRenderState> {
    private static final int FULL_BRIGHT = 15728880;
    private static final int COLOR_RUNNING = 0xFF3CD94A;
    private static final int COLOR_IDLE = 0xFF3C8CD9;
    private static final int COLOR_BLOCKED = 0xFFD9433C;

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
        state.status = blockEntity.getMachineStatus();
        var blockState = blockEntity.getBlockState();
        state.facing = IOrientationStrategy.get(blockState).getFacing(blockState);
    }

    @Override
    public void submit(
            SmelterRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        var renderType = RenderTypes.entityTranslucentEmissive(emissiveTexture(state.tier));
        var color = statusColor(state.status);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRotationFor(state.facing)));
        poseStack.translate(-0.5, -0.5, -0.5);

        submitNodeCollector.submitCustomGeometry(
                poseStack, renderType, (pose, buffer) -> renderFrontFace(pose, buffer, color));

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
            case MISSING_INPUT, MISSING_FUEL, MISSING_POWER, OUTPUT_FULL,
                    INVALID_RECIPE, OFFLINE, REDSTONE_PAUSED, NO_SMELTERS -> COLOR_BLOCKED;
        };
    }

    private static Identifier emissiveTexture(SmelterTier tier) {
        var suffix = switch (tier) {
            case DEFAULT -> "me_smelter_emissive";
            case MK1 -> "me_smelter_mk1_emissive";
            case MK2 -> "me_smelter_mk2_emissive";
            case MK3 -> "me_smelter_mk3_emissive";
        };
        return AppliedSmelting.id("textures/block/" + suffix + ".png");
    }

    private static void renderFrontFace(PoseStack.Pose pose, VertexConsumer buffer, int color) {
        quad(pose, buffer, color, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, -1); // north (front)
    }

    private static void quad(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            int color,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float nx, float ny, float nz) {
        vertex(pose, buffer, color, x0, y0, z0, 0, 1, nx, ny, nz);
        vertex(pose, buffer, color, x1, y1, z1, 1, 1, nx, ny, nz);
        vertex(pose, buffer, color, x2, y2, z2, 1, 0, nx, ny, nz);
        vertex(pose, buffer, color, x3, y3, z3, 0, 0, nx, ny, nz);
    }

    private static void vertex(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            int color,
            float x, float y, float z,
            float u, float v,
            float nx, float ny, float nz) {
        buffer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(FULL_BRIGHT)
                .setNormal(pose, nx, ny, nz);
    }
}
