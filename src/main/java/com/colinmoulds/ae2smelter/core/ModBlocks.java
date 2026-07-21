package com.colinmoulds.ae2smelter.core;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;

import com.colinmoulds.ae2smelter.AE2Smelter;
import com.colinmoulds.ae2smelter.block.MESmelterBlock;
import com.colinmoulds.ae2smelter.block.SmeltingTerminalBlock;

public final class ModBlocks {
    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(AE2Smelter.MOD_ID);

    public static final DeferredBlock<MESmelterBlock> ME_SMELTER = REGISTER.registerBlock(
            "me_smelter", MESmelterBlock::new, ModBlocks::machineProperties);
    public static final DeferredItem<BlockItem> ME_SMELTER_ITEM = ModItems.REGISTER.registerItem(
            "me_smelter", properties -> new AEBaseBlockItem(ME_SMELTER.get(), properties.useBlockDescriptionPrefix()));

    public static final DeferredBlock<SmeltingTerminalBlock> SMELTING_TERMINAL = REGISTER.registerBlock(
            "me_smelting_terminal", SmeltingTerminalBlock::new, ModBlocks::machineProperties);
    public static final DeferredItem<BlockItem> SMELTING_TERMINAL_ITEM = ModItems.REGISTER.registerItem(
            "me_smelting_terminal",
            properties -> new AEBaseBlockItem(SMELTING_TERMINAL.get(), properties.useBlockDescriptionPrefix()));

    private ModBlocks() {
    }

    private static BlockBehaviour.Properties machineProperties() {
        return AEBaseBlock.metalProps(BlockBehaviour.Properties.of())
                .mapColor(MapColor.METAL)
                .sound(SoundType.METAL)
                .strength(3.5F, 8.0F)
                .requiresCorrectToolForDrops();
    }
}
