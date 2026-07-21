package dev.excal1bur.appliedsmelting;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import appeng.api.AECapabilities;
import appeng.api.networking.GridServices;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;

import dev.excal1bur.appliedsmelting.core.ModBlockEntities;
import dev.excal1bur.appliedsmelting.core.ModBlocks;
import dev.excal1bur.appliedsmelting.core.ModCreativeTab;
import dev.excal1bur.appliedsmelting.core.ModItems;
import dev.excal1bur.appliedsmelting.core.ModMenus;
import dev.excal1bur.appliedsmelting.service.SmeltingService;

@Mod(AppliedSmelting.MOD_ID)
public final class AppliedSmelting {
    public static final String MOD_ID = "appliedsmelting";

    public AppliedSmelting(ModContainer container, IEventBus modBus) {
        ModBlocks.REGISTER.register(modBus);
        ModItems.REGISTER.register(modBus);
        ModBlockEntities.REGISTER.register(modBus);
        ModMenus.REGISTER.register(modBus);
        ModCreativeTab.REGISTER.register(modBus);
        modBus.addListener(AppliedSmelting::registerCapabilities);
        modBus.addListener(AppliedSmelting::registerUpgrades);

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

    private static void registerUpgrades(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> Upgrades.add(AEItems.SPEED_CARD, ModBlocks.ME_SMELTER_ITEM.get(), 4));
    }
}
