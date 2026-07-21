package dev.excal1bur.appliedsmelting.core;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.menu.implementations.MenuTypeBuilder;

import dev.excal1bur.appliedsmelting.AppliedSmelting;
import dev.excal1bur.appliedsmelting.menu.SmeltingTerminalHost;
import dev.excal1bur.appliedsmelting.blockentity.MESmelterBlockEntity;
import dev.excal1bur.appliedsmelting.menu.MESmelterMenu;
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

    private ModMenus() {
    }
}
