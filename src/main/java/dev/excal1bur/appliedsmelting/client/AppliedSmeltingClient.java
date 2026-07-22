package dev.excal1bur.appliedsmelting.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import appeng.client.InitScreens;

import dev.excal1bur.appliedsmelting.AppliedSmelting;
import dev.excal1bur.appliedsmelting.client.render.MESmelterBlockEntityRenderer;
import dev.excal1bur.appliedsmelting.client.screen.SmeltingTerminalScreen;
import dev.excal1bur.appliedsmelting.client.screen.MESmelterScreen;
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
                ModMenus.SMELTING_TERMINAL.get(),
                SmeltingTerminalScreen::new,
                "/screens/appliedsmelting/me_smelting_terminal.json");
    }

    private static void registerRenderers(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                BlockEntityRenderers.register(ModBlockEntities.ME_SMELTER.get(), MESmelterBlockEntityRenderer::new));
    }
}
