package dev.excal1bur.appliedsmelting.client.render;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

import dev.excal1bur.appliedsmelting.AppliedSmelting;

/**
 * Mirrors AE2's own ME Drive cell status LEDs (appeng.client.render.AERenderTypes#STORAGE_CELL_LEDS):
 * a plain position+color pipeline with no texture sampler and no lightmap, so the status dot is a pure
 * vertex-colored quad rather than a tinted texture.
 */
public final class AppliedSmeltingRenderTypes {
    public static final RenderPipeline STATUS_LED_PIPELINE = RenderPipeline
            .builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
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

    private AppliedSmeltingRenderTypes() {
    }
}
