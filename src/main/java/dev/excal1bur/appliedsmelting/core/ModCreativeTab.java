package dev.excal1bur.appliedsmelting.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;

import dev.excal1bur.appliedsmelting.AppliedSmelting;

public final class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AppliedSmelting.MOD_ID);

    static {
        REGISTER.register("main", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.appliedsmelting"))
                .icon(() -> ModBlocks.ME_SMELTER_ITEM.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    output.accept(ModBlocks.ME_SMELTER_ITEM.get());
                    output.accept(ModBlocks.ME_SMELTER_MK1_ITEM.get());
                    output.accept(ModBlocks.ME_SMELTER_MK2_ITEM.get());
                    output.accept(ModBlocks.ME_SMELTER_MK3_ITEM.get());
                    output.accept(ModBlocks.ME_BLAST_FURNACE_ITEM.get());
                    output.accept(ModBlocks.ME_SMOKER_ITEM.get());
                    output.accept(ModBlocks.ME_CRUCIBLE_ITEM.get());
                    output.accept(ModBlocks.SMELTING_TERMINAL_ITEM.get());
                    output.accept(ModItems.FUEL_EFFICIENCY_CARD.get());
                    output.accept(ModItems.SMELTER_UPGRADE_TEMPLATE.get());
                    output.accept(ModItems.MK1_UPGRADE_KIT.get());
                    output.accept(ModItems.MK2_UPGRADE_KIT.get());
                    output.accept(ModItems.MK3_UPGRADE_KIT.get());
                })
                .build());
    }

    private ModCreativeTab() {
    }
}
