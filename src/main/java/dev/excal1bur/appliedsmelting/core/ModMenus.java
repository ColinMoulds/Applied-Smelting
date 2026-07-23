package dev.excal1bur.appliedsmelting.core;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.menu.implementations.MenuTypeBuilder;

import dev.excal1bur.appliedsmelting.AppliedSmelting;
import dev.excal1bur.appliedsmelting.menu.SmeltingTerminalHost;
import dev.excal1bur.appliedsmelting.blockentity.MEBlastFurnaceBlockEntity;
import dev.excal1bur.appliedsmelting.blockentity.MECrucibleBlockEntity;
import dev.excal1bur.appliedsmelting.blockentity.MESmelterBlockEntity;
import dev.excal1bur.appliedsmelting.blockentity.MESmokerBlockEntity;
import dev.excal1bur.appliedsmelting.menu.MEBlastFurnaceMenu;
import dev.excal1bur.appliedsmelting.menu.MECrucibleMenu;
import dev.excal1bur.appliedsmelting.menu.MESmelterMenu;
import dev.excal1bur.appliedsmelting.menu.MESmokerMenu;
import dev.excal1bur.appliedsmelting.menu.SmeltingTerminalMenu;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER =
            DeferredRegister.create(Registries.MENU, AppliedSmelting.MOD_ID);

    public static final Supplier<MenuType<SmeltingTerminalMenu>> SMELTING_TERMINAL = REGISTER.register(
            "me_smelting_terminal",
            () -> MenuTypeBuilder.create(SmeltingTerminalMenu::new, SmeltingTerminalHost.class)
                    .buildUnregistered(AppliedSmelting.id("me_smelting_terminal")));

    public static final Supplier<MenuType<MESmelterMenu>> ME_SMELTER = REGISTER.register(
            "me_smelter",
            () -> MenuTypeBuilder.create(MESmelterMenu::new, MESmelterBlockEntity.class)
                    .buildUnregistered(AppliedSmelting.id("me_smelter")));

    public static final Supplier<MenuType<MEBlastFurnaceMenu>> ME_BLAST_FURNACE = REGISTER.register(
            "me_blast_furnace",
            () -> MenuTypeBuilder.create(MEBlastFurnaceMenu::new, MEBlastFurnaceBlockEntity.class)
                    .buildUnregistered(AppliedSmelting.id("me_blast_furnace")));

    public static final Supplier<MenuType<MESmokerMenu>> ME_SMOKER = REGISTER.register(
            "me_smoker",
            () -> MenuTypeBuilder.create(MESmokerMenu::new, MESmokerBlockEntity.class)
                    .buildUnregistered(AppliedSmelting.id("me_smoker")));

    public static final Supplier<MenuType<MECrucibleMenu>> ME_CRUCIBLE = REGISTER.register(
            "me_crucible",
            () -> MenuTypeBuilder.create(MECrucibleMenu::new, MECrucibleBlockEntity.class)
                    .buildUnregistered(AppliedSmelting.id("me_crucible")));

    private ModMenus() {
    }
}
