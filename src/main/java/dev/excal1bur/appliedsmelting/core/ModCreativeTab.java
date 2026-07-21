package dev.excal1bur.appliedsmelting.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;

import dev.excal1bur.appliedsmelting.AE2Smelter;

public final class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AE2Smelter.MOD_ID);

    static {
        REGISTER.register("main", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.ae2smelter"))
                .icon(() -> ModBlocks.ME_SMELTER_ITEM.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    output.accept(ModBlocks.ME_SMELTER_ITEM.get());
                    output.accept(ModBlocks.SMELTING_TERMINAL_ITEM.get());
                })
                .build());
    }

    private ModCreativeTab() {
    }
}
