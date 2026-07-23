package dev.excal1bur.appliedsmelting.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.api.upgrades.Upgrades;

import dev.excal1bur.appliedsmelting.AppliedSmelting;
import dev.excal1bur.appliedsmelting.item.SmelterUpgradeKitItem;

public final class ModItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(AppliedSmelting.MOD_ID);
    public static final DeferredItem<Item> FUEL_EFFICIENCY_CARD =
            REGISTER.registerItem("fuel_efficiency_card", Upgrades::createUpgradeCardItem);

    // Universal across all machine types - the template/kit recipes aren't Smelter-specific, and
    // each machine's own tier enum maps a kit's mark level (1/2/3) to its own tier constants.
    public static final DeferredItem<Item> SMELTER_UPGRADE_TEMPLATE =
            REGISTER.registerItem("smelter_upgrade_template", Item::new);
    public static final DeferredItem<Item> MK1_UPGRADE_KIT =
            REGISTER.registerItem("mk1_upgrade_kit", SmelterUpgradeKitItem::new);
    public static final DeferredItem<Item> MK2_UPGRADE_KIT =
            REGISTER.registerItem("mk2_upgrade_kit", SmelterUpgradeKitItem::new);
    public static final DeferredItem<Item> MK3_UPGRADE_KIT =
            REGISTER.registerItem("mk3_upgrade_kit", SmelterUpgradeKitItem::new);

    private ModItems() {
    }

    /** Which mark level a held upgrade kit item applies (1/2/3), or 0 if the stack isn't a kit. */
    public static int upgradeKitLevel(ItemStack stack) {
        if (stack.is(MK1_UPGRADE_KIT.get())) {
            return 1;
        }
        if (stack.is(MK2_UPGRADE_KIT.get())) {
            return 2;
        }
        if (stack.is(MK3_UPGRADE_KIT.get())) {
            return 3;
        }
        return 0;
    }
}
