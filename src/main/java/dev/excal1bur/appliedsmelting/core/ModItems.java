package dev.excal1bur.appliedsmelting.core;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.api.upgrades.Upgrades;

import dev.excal1bur.appliedsmelting.AppliedSmelting;

public final class ModItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(AppliedSmelting.MOD_ID);
    public static final DeferredItem<Item> FUEL_EFFICIENCY_CARD =
            REGISTER.registerItem("fuel_efficiency_card", Upgrades::createUpgradeCardItem);

    private ModItems() {
    }
}
