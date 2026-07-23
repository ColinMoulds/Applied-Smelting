package dev.excal1bur.appliedsmelting.core;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.items.parts.PartItem;

import dev.excal1bur.appliedsmelting.AppliedSmelting;
import dev.excal1bur.appliedsmelting.block.MEBlastFurnaceBlock;
import dev.excal1bur.appliedsmelting.block.MECrucibleBlock;
import dev.excal1bur.appliedsmelting.block.MESmelterBlock;
import dev.excal1bur.appliedsmelting.block.MESmokerBlock;
import dev.excal1bur.appliedsmelting.block.SmeltingTerminalBlock;
import dev.excal1bur.appliedsmelting.part.SmeltingTerminalPart;
import dev.excal1bur.appliedsmelting.service.BlastFurnaceTier;
import dev.excal1bur.appliedsmelting.service.SmelterTier;

public final class ModBlocks {
    public static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(AppliedSmelting.MOD_ID);

    public static final DeferredBlock<MESmelterBlock> ME_SMELTER = REGISTER.registerBlock(
            "me_smelter", properties -> new MESmelterBlock(properties, SmelterTier.DEFAULT), ModBlocks::machineProperties);
    public static final DeferredItem<BlockItem> ME_SMELTER_ITEM = ModItems.REGISTER.registerItem(
            "me_smelter", properties -> new AEBaseBlockItem(ME_SMELTER.get(), properties.useBlockDescriptionPrefix()));

    public static final DeferredBlock<MESmelterBlock> ME_SMELTER_MK1 = REGISTER.registerBlock(
            "me_smelter_mk1", properties -> new MESmelterBlock(properties, SmelterTier.MK1), ModBlocks::machineProperties);
    public static final DeferredItem<BlockItem> ME_SMELTER_MK1_ITEM = ModItems.REGISTER.registerItem(
            "me_smelter_mk1", properties -> new AEBaseBlockItem(ME_SMELTER_MK1.get(), properties.useBlockDescriptionPrefix()));

    public static final DeferredBlock<MESmelterBlock> ME_SMELTER_MK2 = REGISTER.registerBlock(
            "me_smelter_mk2", properties -> new MESmelterBlock(properties, SmelterTier.MK2), ModBlocks::machineProperties);
    public static final DeferredItem<BlockItem> ME_SMELTER_MK2_ITEM = ModItems.REGISTER.registerItem(
            "me_smelter_mk2", properties -> new AEBaseBlockItem(ME_SMELTER_MK2.get(), properties.useBlockDescriptionPrefix()));

    public static final DeferredBlock<MESmelterBlock> ME_SMELTER_MK3 = REGISTER.registerBlock(
            "me_smelter_mk3", properties -> new MESmelterBlock(properties, SmelterTier.MK3), ModBlocks::machineProperties);
    public static final DeferredItem<BlockItem> ME_SMELTER_MK3_ITEM = ModItems.REGISTER.registerItem(
            "me_smelter_mk3", properties -> new AEBaseBlockItem(ME_SMELTER_MK3.get(), properties.useBlockDescriptionPrefix()));

    public static final DeferredBlock<MEBlastFurnaceBlock> ME_BLAST_FURNACE = REGISTER.registerBlock(
            "me_blast_furnace",
            properties -> new MEBlastFurnaceBlock(properties, BlastFurnaceTier.DEFAULT),
            ModBlocks::machineProperties);
    public static final DeferredItem<BlockItem> ME_BLAST_FURNACE_ITEM = ModItems.REGISTER.registerItem(
            "me_blast_furnace",
            properties -> new AEBaseBlockItem(ME_BLAST_FURNACE.get(), properties.useBlockDescriptionPrefix()));

    public static final DeferredBlock<MEBlastFurnaceBlock> ME_BLAST_FURNACE_MK1 = REGISTER.registerBlock(
            "me_blast_furnace_mk1",
            properties -> new MEBlastFurnaceBlock(properties, BlastFurnaceTier.MK1),
            ModBlocks::machineProperties);
    public static final DeferredItem<BlockItem> ME_BLAST_FURNACE_MK1_ITEM = ModItems.REGISTER.registerItem(
            "me_blast_furnace_mk1",
            properties -> new AEBaseBlockItem(ME_BLAST_FURNACE_MK1.get(), properties.useBlockDescriptionPrefix()));

    public static final DeferredBlock<MEBlastFurnaceBlock> ME_BLAST_FURNACE_MK2 = REGISTER.registerBlock(
            "me_blast_furnace_mk2",
            properties -> new MEBlastFurnaceBlock(properties, BlastFurnaceTier.MK2),
            ModBlocks::machineProperties);
    public static final DeferredItem<BlockItem> ME_BLAST_FURNACE_MK2_ITEM = ModItems.REGISTER.registerItem(
            "me_blast_furnace_mk2",
            properties -> new AEBaseBlockItem(ME_BLAST_FURNACE_MK2.get(), properties.useBlockDescriptionPrefix()));

    public static final DeferredBlock<MEBlastFurnaceBlock> ME_BLAST_FURNACE_MK3 = REGISTER.registerBlock(
            "me_blast_furnace_mk3",
            properties -> new MEBlastFurnaceBlock(properties, BlastFurnaceTier.MK3),
            ModBlocks::machineProperties);
    public static final DeferredItem<BlockItem> ME_BLAST_FURNACE_MK3_ITEM = ModItems.REGISTER.registerItem(
            "me_blast_furnace_mk3",
            properties -> new AEBaseBlockItem(ME_BLAST_FURNACE_MK3.get(), properties.useBlockDescriptionPrefix()));

    public static final DeferredBlock<MESmokerBlock> ME_SMOKER = REGISTER.registerBlock(
            "me_smoker", MESmokerBlock::new, ModBlocks::machineProperties);
    public static final DeferredItem<BlockItem> ME_SMOKER_ITEM = ModItems.REGISTER.registerItem(
            "me_smoker", properties -> new AEBaseBlockItem(ME_SMOKER.get(), properties.useBlockDescriptionPrefix()));

    public static final DeferredBlock<MECrucibleBlock> ME_CRUCIBLE = REGISTER.registerBlock(
            "me_crucible", MECrucibleBlock::new, ModBlocks::machineProperties);
    public static final DeferredItem<BlockItem> ME_CRUCIBLE_ITEM = ModItems.REGISTER.registerItem(
            "me_crucible", properties -> new AEBaseBlockItem(ME_CRUCIBLE.get(), properties.useBlockDescriptionPrefix()));

    public static final DeferredBlock<SmeltingTerminalBlock> SMELTING_TERMINAL = REGISTER.registerBlock(
            "me_smelting_terminal", SmeltingTerminalBlock::new, ModBlocks::machineProperties);
    public static final DeferredItem<PartItem<SmeltingTerminalPart>> SMELTING_TERMINAL_ITEM = ModItems.REGISTER.registerItem(
            "me_smelting_terminal",
            properties -> new PartItem<>(properties, SmeltingTerminalPart.class, SmeltingTerminalPart::new));

    private ModBlocks() {
    }

    public static DeferredBlock<MESmelterBlock> blockForTier(SmelterTier tier) {
        return switch (tier) {
            case DEFAULT -> ME_SMELTER;
            case MK1 -> ME_SMELTER_MK1;
            case MK2 -> ME_SMELTER_MK2;
            case MK3 -> ME_SMELTER_MK3;
        };
    }

    public static DeferredBlock<MEBlastFurnaceBlock> blockForBlastFurnaceTier(BlastFurnaceTier tier) {
        return switch (tier) {
            case DEFAULT -> ME_BLAST_FURNACE;
            case MK1 -> ME_BLAST_FURNACE_MK1;
            case MK2 -> ME_BLAST_FURNACE_MK2;
            case MK3 -> ME_BLAST_FURNACE_MK3;
        };
    }

    private static BlockBehaviour.Properties machineProperties() {
        return AEBaseBlock.metalProps(BlockBehaviour.Properties.of())
                .mapColor(MapColor.METAL)
                .sound(SoundType.METAL)
                .strength(3.5F, 8.0F)
                .requiresCorrectToolForDrops();
    }
}
