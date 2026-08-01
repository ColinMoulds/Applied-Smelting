package dev.excal1bur.appliedsmelting.client;

import appeng.client.InitScreens;
import dev.excal1bur.appliedsmelting.AppliedSmelting;
import dev.excal1bur.appliedsmelting.client.render.MENetworkFurnaceBlockEntityRenderer;
import dev.excal1bur.appliedsmelting.client.screen.MEBlastFurnaceScreen;
import dev.excal1bur.appliedsmelting.client.screen.MECrucibleScreen;
import dev.excal1bur.appliedsmelting.client.screen.MESmelterScreen;
import dev.excal1bur.appliedsmelting.client.screen.MESmokerScreen;
import dev.excal1bur.appliedsmelting.client.screen.SmeltingTerminalScreen;
import dev.excal1bur.appliedsmelting.core.ModBlockEntities;
import dev.excal1bur.appliedsmelting.core.ModFluids;
import dev.excal1bur.appliedsmelting.core.ModMenus;
import java.util.function.Supplier;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.fluid.FluidTintSources;

@Mod(value = AppliedSmelting.MOD_ID, dist = Dist.CLIENT)
public final class AppliedSmeltingClient {
    public AppliedSmeltingClient(IEventBus modBus) {
        modBus.addListener(AppliedSmeltingClient::registerScreens);
        modBus.addListener(AppliedSmeltingClient::registerRenderers);
        modBus.addListener(AppliedSmeltingClient::registerFluidModels);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(
                event, ModMenus.ME_SMELTER.get(), MESmelterScreen::new, "/screens/appliedsmelting/me_smelter.json");
        InitScreens.register(
                event,
                ModMenus.ME_BLAST_FURNACE.get(),
                MEBlastFurnaceScreen::new,
                "/screens/appliedsmelting/me_blast_furnace.json");
        InitScreens.register(
                event, ModMenus.ME_SMOKER.get(), MESmokerScreen::new, "/screens/appliedsmelting/me_smoker.json");
        InitScreens.register(
                event, ModMenus.ME_CRUCIBLE.get(), MECrucibleScreen::new, "/screens/appliedsmelting/me_crucible.json");
        InitScreens.register(
                event,
                ModMenus.SMELTING_TERMINAL.get(),
                SmeltingTerminalScreen::new,
                "/screens/appliedsmelting/me_smelting_terminal.json");
    }

    private static void registerRenderers(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BlockEntityRenderers.register(ModBlockEntities.ME_SMELTER.get(), MENetworkFurnaceBlockEntityRenderer::new);
            BlockEntityRenderers.register(
                    ModBlockEntities.ME_BLAST_FURNACE.get(), MENetworkFurnaceBlockEntityRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.ME_SMOKER.get(), MENetworkFurnaceBlockEntityRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.ME_CRUCIBLE.get(), MENetworkFurnaceBlockEntityRenderer::new);
        });
    }

    // Reuses vanilla's lava sprites, tinted per metal, since these fallback fluids ship no textures of their own.
    private static void registerFluidModels(RegisterFluidModelsEvent event) {
        registerFluidModel(event, ModFluids.MOLTEN_IRON, ModFluids.MOLTEN_IRON_FLOWING, 0xFFE6E6E6);
        registerFluidModel(event, ModFluids.MOLTEN_COPPER, ModFluids.MOLTEN_COPPER_FLOWING, 0xFFDA8A67);
        registerFluidModel(event, ModFluids.MOLTEN_GOLD, ModFluids.MOLTEN_GOLD_FLOWING, 0xFFFFD700);
    }

    private static void registerFluidModel(
            RegisterFluidModelsEvent event, Supplier<Fluid> still, Supplier<Fluid> flowing, int tintColor) {
        event.register(
                new FluidModel.Unbaked(
                        new Material(Identifier.fromNamespaceAndPath("minecraft", "block/lava_still")),
                        new Material(Identifier.fromNamespaceAndPath("minecraft", "block/lava_flow")),
                        null,
                        FluidTintSources.constant(tintColor)),
                still,
                flowing);
    }
}
