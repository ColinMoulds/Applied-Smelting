package dev.excal1bur.appliedsmelting.client.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.excal1bur.appliedsmelting.AppliedSmelting;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;

/** Untextured position+color pipeline for the status LED, mirroring AE2's own ME Drive cell LEDs. */
public final class AppliedSmeltingRenderTypes {
    public static final RenderPipeline STATUS_LED_PIPELINE = RenderPipeline.builder(
                    RenderPipelines.MATRICES_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
            .withLocation(AppliedSmelting.id("pipeline/smelter_status_led"))
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withCull(false)
            .build();

    public static final RenderType SMELTER_STATUS_LED = RenderType.create(
            "appliedsmelting_smelter_status_led",
            RenderSetup.builder(STATUS_LED_PIPELINE).bufferSize(256).createRenderSetup());

    /**
     * Textured, unlit pipeline for the front-face fire glow: samples the block atlas so the soft
     * glow mask keeps its shape, tinted per-vertex for the status-reactive rainbow color, and
     * ignores world lighting (no lightmap in this vertex format) so it stays visible in the dark.
     */
    public static final RenderPipeline FIRE_GLOW_PIPELINE = RenderPipeline.builder(
                    RenderPipelines.MATRICES_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
            .withLocation(AppliedSmelting.id("pipeline/smelter_fire_glow"))
            .withVertexShader("core/position_tex_color")
            .withFragmentShader("core/position_tex_color")
            .withSampler("Sampler0")
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build();

    public static final RenderType SMELTER_FIRE_GLOW = RenderType.create(
            "appliedsmelting_smelter_fire_glow",
            RenderSetup.builder(FIRE_GLOW_PIPELINE)
                    .withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS)
                    .bufferSize(256)
                    .createRenderSetup());

    private AppliedSmeltingRenderTypes() {}
}
