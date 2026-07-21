package com.colinmoulds.ae2smelter.core;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.menu.implementations.MenuTypeBuilder;

import com.colinmoulds.ae2smelter.AE2Smelter;
import com.colinmoulds.ae2smelter.blockentity.SmeltingTerminalBlockEntity;
import com.colinmoulds.ae2smelter.menu.SmeltingTerminalMenu;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER =
            DeferredRegister.create(Registries.MENU, AE2Smelter.MOD_ID);

    public static final Supplier<MenuType<SmeltingTerminalMenu>> SMELTING_TERMINAL = REGISTER.register(
            "me_smelting_terminal",
            () -> MenuTypeBuilder.create(SmeltingTerminalMenu::new, SmeltingTerminalBlockEntity.class)
                    .buildUnregistered(AE2Smelter.id("me_smelting_terminal")));

    private ModMenus() {
    }
}
