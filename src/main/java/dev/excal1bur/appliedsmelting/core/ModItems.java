package dev.excal1bur.appliedsmelting.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.jetbrains.annotations.Nullable;

import appeng.api.upgrades.Upgrades;

import dev.excal1bur.appliedsmelting.AppliedSmelting;
import dev.excal1bur.appliedsmelting.service.SmelterTier;

public final class ModItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(AppliedSmelting.MOD_ID);
    public static final DeferredItem<Item> FUEL_EFFICIENCY_CARD =
            REGISTER.registerItem("fuel_efficiency_card", Upgrades::createUpgradeCardItem);

    public static final DeferredItem<Item> SMELTER_UPGRADE_TEMPLATE =
            REGISTER.registerItem("smelter_upgrade_template", Item::new);
    public static final DeferredItem<Item> MK1_UPGRADE_KIT =
            REGISTER.registerItem("mk1_upgrade_kit", Item::new);
    public static final DeferredItem<Item> MK2_UPGRADE_KIT =
            REGISTER.registerItem("mk2_upgrade_kit", Item::new);
    public static final DeferredItem<Item> MK3_UPGRADE_KIT =
            REGISTER.registerItem("mk3_upgrade_kit", Item::new);

    private ModItems() {
    }

    /** The tier a held upgrade kit item would apply, or null if the stack isn't an upgrade kit. */
    @Nullable
    public static SmelterTier tierForUpgradeKit(ItemStack stack) {
        if (stack.is(MK1_UPGRADE_KIT.get())) {
            return SmelterTier.MK1;
        }
        if (stack.is(MK2_UPGRADE_KIT.get())) {
            return SmelterTier.MK2;
        }
        if (stack.is(MK3_UPGRADE_KIT.get())) {
            return SmelterTier.MK3;
        }
        return null;
    }
}
