package dev.excal1bur.appliedsmelting.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;

/** Opts back into block interaction while sneaking, which vanilla otherwise suppresses for held items. */
public final class SmelterUpgradeKitItem extends Item {
    public SmelterUpgradeKitItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return true;
    }
}
