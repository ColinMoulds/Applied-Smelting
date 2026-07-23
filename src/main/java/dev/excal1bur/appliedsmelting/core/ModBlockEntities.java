package dev.excal1bur.appliedsmelting.core;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.blockentity.AEBaseBlockEntity;

import dev.excal1bur.appliedsmelting.AppliedSmelting;
import dev.excal1bur.appliedsmelting.blockentity.MEBlastFurnaceBlockEntity;
import dev.excal1bur.appliedsmelting.blockentity.MECrucibleBlockEntity;
import dev.excal1bur.appliedsmelting.blockentity.MESmelterBlockEntity;
import dev.excal1bur.appliedsmelting.blockentity.MESmokerBlockEntity;
import dev.excal1bur.appliedsmelting.blockentity.SmeltingTerminalBlockEntity;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AppliedSmelting.MOD_ID);

    public static final Supplier<BlockEntityType<MESmelterBlockEntity>> ME_SMELTER = REGISTER.register(
            "me_smelter",
            () -> {
                var reference = new AtomicReference<BlockEntityType<MESmelterBlockEntity>>();
                var type = new BlockEntityType<MESmelterBlockEntity>(
                        (pos, state) -> new MESmelterBlockEntity(reference.get(), pos, state),
                        ModBlocks.ME_SMELTER.get(),
                        ModBlocks.ME_SMELTER_MK1.get(),
                        ModBlocks.ME_SMELTER_MK2.get(),
                        ModBlocks.ME_SMELTER_MK3.get());
                reference.set(type);
                ModBlocks.ME_SMELTER.get().setBlockEntity(MESmelterBlockEntity.class, type, null, null);
                ModBlocks.ME_SMELTER_MK1.get().setBlockEntity(MESmelterBlockEntity.class, type, null, null);
                ModBlocks.ME_SMELTER_MK2.get().setBlockEntity(MESmelterBlockEntity.class, type, null, null);
                ModBlocks.ME_SMELTER_MK3.get().setBlockEntity(MESmelterBlockEntity.class, type, null, null);
                AEBaseBlockEntity.registerBlockEntityItem(type, ModBlocks.ME_SMELTER_ITEM.get());
                return type;
            });

    public static final Supplier<BlockEntityType<MEBlastFurnaceBlockEntity>> ME_BLAST_FURNACE = REGISTER.register(
            "me_blast_furnace",
            () -> {
                var reference = new AtomicReference<BlockEntityType<MEBlastFurnaceBlockEntity>>();
                var type = new BlockEntityType<MEBlastFurnaceBlockEntity>(
                        (pos, state) -> new MEBlastFurnaceBlockEntity(reference.get(), pos, state),
                        ModBlocks.ME_BLAST_FURNACE.get(),
                        ModBlocks.ME_BLAST_FURNACE_MK1.get(),
                        ModBlocks.ME_BLAST_FURNACE_MK2.get(),
                        ModBlocks.ME_BLAST_FURNACE_MK3.get());
                reference.set(type);
                ModBlocks.ME_BLAST_FURNACE.get().setBlockEntity(MEBlastFurnaceBlockEntity.class, type, null, null);
                ModBlocks.ME_BLAST_FURNACE_MK1.get().setBlockEntity(MEBlastFurnaceBlockEntity.class, type, null, null);
                ModBlocks.ME_BLAST_FURNACE_MK2.get().setBlockEntity(MEBlastFurnaceBlockEntity.class, type, null, null);
                ModBlocks.ME_BLAST_FURNACE_MK3.get().setBlockEntity(MEBlastFurnaceBlockEntity.class, type, null, null);
                AEBaseBlockEntity.registerBlockEntityItem(type, ModBlocks.ME_BLAST_FURNACE_ITEM.get());
                return type;
            });

    public static final Supplier<BlockEntityType<MESmokerBlockEntity>> ME_SMOKER = REGISTER.register(
            "me_smoker",
            () -> {
                var reference = new AtomicReference<BlockEntityType<MESmokerBlockEntity>>();
                var type = new BlockEntityType<MESmokerBlockEntity>(
                        (pos, state) -> new MESmokerBlockEntity(reference.get(), pos, state),
                        ModBlocks.ME_SMOKER.get(),
                        ModBlocks.ME_SMOKER_MK1.get(),
                        ModBlocks.ME_SMOKER_MK2.get(),
                        ModBlocks.ME_SMOKER_MK3.get());
                reference.set(type);
                ModBlocks.ME_SMOKER.get().setBlockEntity(MESmokerBlockEntity.class, type, null, null);
                ModBlocks.ME_SMOKER_MK1.get().setBlockEntity(MESmokerBlockEntity.class, type, null, null);
                ModBlocks.ME_SMOKER_MK2.get().setBlockEntity(MESmokerBlockEntity.class, type, null, null);
                ModBlocks.ME_SMOKER_MK3.get().setBlockEntity(MESmokerBlockEntity.class, type, null, null);
                AEBaseBlockEntity.registerBlockEntityItem(type, ModBlocks.ME_SMOKER_ITEM.get());
                return type;
            });

    public static final Supplier<BlockEntityType<MECrucibleBlockEntity>> ME_CRUCIBLE = REGISTER.register(
            "me_crucible",
            () -> {
                var reference = new AtomicReference<BlockEntityType<MECrucibleBlockEntity>>();
                var type = new BlockEntityType<MECrucibleBlockEntity>(
                        (pos, state) -> new MECrucibleBlockEntity(reference.get(), pos, state),
                        ModBlocks.ME_CRUCIBLE.get());
                reference.set(type);
                ModBlocks.ME_CRUCIBLE.get().setBlockEntity(MECrucibleBlockEntity.class, type, null, null);
                AEBaseBlockEntity.registerBlockEntityItem(type, ModBlocks.ME_CRUCIBLE_ITEM.get());
                return type;
            });

    public static final Supplier<BlockEntityType<SmeltingTerminalBlockEntity>> SMELTING_TERMINAL = REGISTER.register(
            "me_smelting_terminal",
            () -> {
                var reference = new AtomicReference<BlockEntityType<SmeltingTerminalBlockEntity>>();
                var type = new BlockEntityType<SmeltingTerminalBlockEntity>(
                        (pos, state) -> new SmeltingTerminalBlockEntity(reference.get(), pos, state),
                        ModBlocks.SMELTING_TERMINAL.get());
                reference.set(type);
                ModBlocks.SMELTING_TERMINAL.get()
                        .setBlockEntity(SmeltingTerminalBlockEntity.class, type, null, null);
                return type;
            });

    private ModBlockEntities() {
    }
}
