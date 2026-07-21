package dev.excal1bur.appliedsmelting;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import appeng.api.AECapabilities;
import appeng.api.networking.GridServices;
import appeng.api.networking.IInWorldGridNodeHost;

import dev.excal1bur.appliedsmelting.core.ModBlockEntities;
import dev.excal1bur.appliedsmelting.core.ModBlocks;
import dev.excal1bur.appliedsmelting.core.ModCreativeTab;
import dev.excal1bur.appliedsmelting.core.ModItems;
import dev.excal1bur.appliedsmelting.core.ModMenus;
import dev.excal1bur.appliedsmelting.service.SmeltingService;

@Mod(AE2Smelter.MOD_ID)
public final class AE2Smelter {
    public static final String MOD_ID = "ae2smelter";

    public AE2Smelter(ModContainer container, IEventBus modBus) {
        ModBlocks.REGISTER.register(modBus);
        ModItems.REGISTER.register(modBus);
        ModBlockEntities.REGISTER.register(modBus);
        ModMenus.REGISTER.register(modBus);
        ModCreativeTab.REGISTER.register(modBus);
        modBus.addListener(AE2Smelter::registerCapabilities);

        GridServices.register(SmeltingService.class, SmeltingService.class);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (var type : ModBlockEntities.REGISTER.getEntries()) {
            event.registerBlockEntity(
                    AECapabilities.IN_WORLD_GRID_NODE_HOST,
                    type.get(),
                    (blockEntity, context) -> (IInWorldGridNodeHost) blockEntity);
        }
    }
}
