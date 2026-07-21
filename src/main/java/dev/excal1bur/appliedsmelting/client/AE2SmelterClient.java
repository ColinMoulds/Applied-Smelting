package dev.excal1bur.appliedsmelting.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import appeng.client.InitScreens;

import dev.excal1bur.appliedsmelting.AE2Smelter;
import dev.excal1bur.appliedsmelting.client.screen.SmeltingTerminalScreen;
import dev.excal1bur.appliedsmelting.core.ModMenus;

@Mod(value = AE2Smelter.MOD_ID, dist = Dist.CLIENT)
public final class AE2SmelterClient {
    public AE2SmelterClient(IEventBus modBus) {
        modBus.addListener(AE2SmelterClient::registerScreens);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(
                event,
                ModMenus.SMELTING_TERMINAL.get(),
                SmeltingTerminalScreen::new,
                "/screens/ae2smelter/me_smelting_terminal.json");
    }
}
