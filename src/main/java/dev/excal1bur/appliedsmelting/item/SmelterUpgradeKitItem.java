package dev.excal1bur.appliedsmelting.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;

/**
 * Vanilla suppresses a block's {@code useItemOn} while the player is sneaking and holding almost any
 * item (see ServerPlayerGameMode#useItemOn's suppressUsingBlock check), so shift-right-clicking a kit
 * onto a smelter would otherwise never reach MESmelterBlock's upgrade logic at all. Overriding this to
 * true opts back into normal block interaction while sneaking.
 */
public final class SmelterUpgradeKitItem extends Item {
    public SmelterUpgradeKitItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return true;
    }
}
