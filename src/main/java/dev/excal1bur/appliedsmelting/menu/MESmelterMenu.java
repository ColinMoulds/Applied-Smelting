package dev.excal1bur.appliedsmelting.menu;

import net.minecraft.world.entity.player.Inventory;

import appeng.core.definitions.AEItems;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;

import dev.excal1bur.appliedsmelting.blockentity.MESmelterBlockEntity;
import dev.excal1bur.appliedsmelting.core.ModMenus;

public final class MESmelterMenu extends UpgradeableMenu<MESmelterBlockEntity> {
    @GuiSync(10)
    public int accelerationCards;

    public MESmelterMenu(int id, Inventory playerInventory, MESmelterBlockEntity smelter) {
        super(ModMenus.ME_SMELTER.get(), id, playerInventory, smelter);
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            accelerationCards = getHost().getUpgrades().getInstalledUpgrades(AEItems.SPEED_CARD);
        }
        super.broadcastChanges();
    }

    public int getSpeedMultiplier() {
        return 1 << Math.min(4, accelerationCards);
    }
}
