package dev.excal1bur.appliedsmelting.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import appeng.client.InitScreens;

import dev.excal1bur.appliedsmelting.AppliedSmelting;
import dev.excal1bur.appliedsmelting.client.render.MENetworkFurnaceBlockEntityRenderer;
import dev.excal1bur.appliedsmelting.client.screen.MEBlastFurnaceScreen;
import dev.excal1bur.appliedsmelting.client.screen.MECrucibleScreen;
import dev.excal1bur.appliedsmelting.client.screen.SmeltingTerminalScreen;
import dev.excal1bur.appliedsmelting.client.screen.MESmelterScreen;
import dev.excal1bur.appliedsmelting.client.screen.MESmokerScreen;
import dev.excal1bur.appliedsmelting.core.ModBlockEntities;
import dev.excal1bur.appliedsmelting.core.ModMenus;

@Mod(value = AppliedSmelting.MOD_ID, dist = Dist.CLIENT)
public final class AppliedSmeltingClient {
    public AppliedSmeltingClient(IEventBus modBus) {
        modBus.addListener(AppliedSmeltingClient::registerScreens);
        modBus.addListener(AppliedSmeltingClient::registerRenderers);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(
                event,
                ModMenus.ME_SMELTER.get(),
                MESmelterScreen::new,
                "/screens/appliedsmelting/me_smelter.json");
        InitScreens.register(
                event,
                ModMenus.ME_BLAST_FURNACE.get(),
                MEBlastFurnaceScreen::new,
                "/screens/appliedsmelting/me_blast_furnace.json");
        InitScreens.register(
                event,
                ModMenus.ME_SMOKER.get(),
                MESmokerScreen::new,
                "/screens/appliedsmelting/me_smoker.json");
        InitScreens.register(
                event,
                ModMenus.ME_CRUCIBLE.get(),
                MECrucibleScreen::new,
                "/screens/appliedsmelting/me_crucible.json");
        InitScreens.register(
                event,
                ModMenus.SMELTING_TERMINAL.get(),
                SmeltingTerminalScreen::new,
                "/screens/appliedsmelting/me_smelting_terminal.json");
    }

    private static void registerRenderers(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BlockEntityRenderers.register(ModBlockEntities.ME_SMELTER.get(), MENetworkFurnaceBlockEntityRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.ME_BLAST_FURNACE.get(), MENetworkFurnaceBlockEntityRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.ME_SMOKER.get(), MENetworkFurnaceBlockEntityRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.ME_CRUCIBLE.get(), MENetworkFurnaceBlockEntityRenderer::new);
        });
    }
}
