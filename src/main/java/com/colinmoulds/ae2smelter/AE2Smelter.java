package com.colinmoulds.ae2smelter;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import appeng.api.AECapabilities;
import appeng.api.networking.GridServices;
import appeng.api.networking.IInWorldGridNodeHost;

import com.colinmoulds.ae2smelter.core.ModBlockEntities;
import com.colinmoulds.ae2smelter.core.ModBlocks;
import com.colinmoulds.ae2smelter.core.ModCreativeTab;
import com.colinmoulds.ae2smelter.core.ModItems;
import com.colinmoulds.ae2smelter.core.ModMenus;
import com.colinmoulds.ae2smelter.service.SmeltingService;

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
