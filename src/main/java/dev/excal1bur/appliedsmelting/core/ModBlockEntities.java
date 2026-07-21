package dev.excal1bur.appliedsmelting.core;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.blockentity.AEBaseBlockEntity;

import dev.excal1bur.appliedsmelting.AppliedSmelting;
import dev.excal1bur.appliedsmelting.blockentity.MESmelterBlockEntity;
import dev.excal1bur.appliedsmelting.blockentity.SmeltingTerminalBlockEntity;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AppliedSmelting.MOD_ID);

    public static final Supplier<BlockEntityType<MESmelterBlockEntity>> ME_SMELTER = REGISTER.register(
            "me_smelter",
            () -> {
                var reference = new AtomicReference<BlockEntityType<MESmelterBlockEntity>>();
                var type = new BlockEntityType<MESmelterBlockEntity>(
                        (pos, state) -> new MESmelterBlockEntity(reference.get(), pos, state), ModBlocks.ME_SMELTER.get());
                reference.set(type);
                ModBlocks.ME_SMELTER.get().setBlockEntity(MESmelterBlockEntity.class, type, null, null);
                AEBaseBlockEntity.registerBlockEntityItem(type, ModBlocks.ME_SMELTER_ITEM.get());
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
