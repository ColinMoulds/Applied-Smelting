package dev.excal1bur.appliedsmelting.client.render;

import appeng.api.orientation.IOrientationStrategy;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.excal1bur.appliedsmelting.blockentity.AbstractMENetworkFurnaceBlockEntity;
import dev.excal1bur.appliedsmelting.service.SmelterStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** Recolors an ME network furnace machine's front-face status dot and fire glow to reflect live machine status. */
public final class MENetworkFurnaceBlockEntityRenderer
        implements BlockEntityRenderer<AbstractMENetworkFurnaceBlockEntity, SmelterRenderState> {
    private static final int COLOR_RUNNING = 0xFF3CD94A;
    private static final int COLOR_IDLE = 0xFF3C8CD9;
    private static final int COLOR_BLOCKED = 0xFFD9433C;
    private static final int COLOR_DISCONNECTED = 0xFF1A1A1A;

    // Bottom-right texel of the 16x16 front-face texture; the cube model's north face bakes u = 1 - localX.
    private static final float LED_MIN_X = 2.0F / 16.0F;
    private static final float LED_MAX_X = 3.0F / 16.0F;
    private static final float LED_MIN_Y = 2.0F / 16.0F;
    private static final float LED_MAX_Y = 3.0F / 16.0F;
    private static final float LED_Z = -5.0E-4F;

    // The fire-glow mask covers the whole front face at the mask texture's own resolution (mostly
    // transparent outside the glowing shape), so it's rendered as a full corner-to-corner quad -
    // same as the base cube model's own north face - rather than a hand-picked sub-rectangle. That
    // keeps this generic across every machine's own glow mask instead of hardcoding one shape/position.
    private static final float FIRE_Z = -5.0E-4F;

    private static final long RAINBOW_CYCLE_MS = 6000L;

    public MENetworkFurnaceBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public SmelterRenderState createRenderState() {
        return new SmelterRenderState();
    }

    @Override
    public void extractRenderState(
            AbstractMENetworkFurnaceBlockEntity blockEntity,
            SmelterRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.glowIntensity = blockEntity.getGlowIntensity();
        state.status = blockEntity.getRawStatus();
        state.fireGlowTexture = blockEntity.getFireGlowTexture();
        var blockState = blockEntity.getBlockState();
        state.facing = IOrientationStrategy.get(blockState).getFacing(blockState);
    }

    @Override
    public void submit(
            SmelterRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera) {
        var color = applyIntensity(statusColor(state.status), state.glowIntensity);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-yRotationFor(state.facing)));
        poseStack.translate(-0.5, -0.5, -0.5);

        submitNodeCollector.submitCustomGeometry(
                poseStack,
                AppliedSmeltingRenderTypes.SMELTER_STATUS_LED,
                (pose, buffer) -> renderLed(pose, buffer, color));

        if (state.fireGlowTexture != null) {
            var fireColor = rainbowColor(state.glowIntensity);
            var fireSprite = fireGlowSprite(state.fireGlowTexture);
            submitNodeCollector.submitCustomGeometry(
                    poseStack,
                    AppliedSmeltingRenderTypes.SMELTER_FIRE_GLOW,
                    (pose, buffer) -> renderFireGlow(pose, buffer, fireSprite, fireColor));
        }

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
            case OFFLINE, NO_SMELTERS -> COLOR_DISCONNECTED;
            case MISSING_INPUT, MISSING_FUEL, MISSING_POWER, OUTPUT_FULL, INVALID_RECIPE, REDSTONE_PAUSED ->
                COLOR_BLOCKED;
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

    private static TextureAtlasSprite fireGlowSprite(Identifier texture) {
        var atlas = (TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
        return atlas.getSprite(texture);
    }

    /** AE2 Controller-style cycling rainbow hue, faded by the same glowIntensity as the status LED. */
    private static int rainbowColor(float intensity) {
        float hue = (System.currentTimeMillis() % RAINBOW_CYCLE_MS) / (float) RAINBOW_CYCLE_MS;
        int alpha = Math.round(255 * Math.max(0.0F, Math.min(1.0F, intensity)));
        return alpha << 24 | hsvToRgb(hue, 1.0F, 1.0F);
    }

    private static int hsvToRgb(float hue, float saturation, float value) {
        float h = hue * 6.0F;
        int sector = (int) h;
        float f = h - sector;
        float p = value * (1.0F - saturation);
        float q = value * (1.0F - saturation * f);
        float t = value * (1.0F - saturation * (1.0F - f));
        float r;
        float g;
        float b;
        switch (sector % 6) {
            case 0 -> {
                r = value;
                g = t;
                b = p;
            }
            case 1 -> {
                r = q;
                g = value;
                b = p;
            }
            case 2 -> {
                r = p;
                g = value;
                b = t;
            }
            case 3 -> {
                r = p;
                g = q;
                b = value;
            }
            case 4 -> {
                r = t;
                g = p;
                b = value;
            }
            default -> {
                r = value;
                g = p;
                b = q;
            }
        }
        return Math.round(r * 255) << 16 | Math.round(g * 255) << 8 | Math.round(b * 255);
    }

    private static void renderFireGlow(
            PoseStack.Pose pose, VertexConsumer buffer, TextureAtlasSprite sprite, int color) {
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        // Full corner-to-corner face quad; the north face bakes u = 1 - localX (same as the LED above).
        buffer.addVertex(pose, 0.0F, 0.0F, FIRE_Z).setUv(u1, v1).setColor(color);
        buffer.addVertex(pose, 1.0F, 0.0F, FIRE_Z).setUv(u0, v1).setColor(color);
        buffer.addVertex(pose, 1.0F, 1.0F, FIRE_Z).setUv(u0, v0).setColor(color);
        buffer.addVertex(pose, 0.0F, 1.0F, FIRE_Z).setUv(u1, v0).setColor(color);
    }
}
