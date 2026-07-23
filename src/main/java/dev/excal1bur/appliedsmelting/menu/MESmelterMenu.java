package dev.excal1bur.appliedsmelting.menu;

import net.minecraft.world.entity.player.Inventory;

import appeng.core.definitions.AEItems;
import appeng.api.stacks.GenericStack;
import appeng.menu.guisync.ClientActionKey;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;

import dev.excal1bur.appliedsmelting.blockentity.MESmelterBlockEntity;
import dev.excal1bur.appliedsmelting.core.ModMenus;
import dev.excal1bur.appliedsmelting.service.SmelterTier;
import dev.excal1bur.appliedsmelting.service.SmeltingPowerMode;

public final class MESmelterMenu extends UpgradeableMenu<MESmelterBlockEntity> {
    private static final ClientActionKey<Integer> SET_POWER_MODE = new ClientActionKey<>("setPowerMode");

    @GuiSync(10)
    public int accelerationCards;

    @GuiSync(11)
    public int energyCards;

    @GuiSync(12)
    public int fuelEfficiencyCards;

    @GuiSync(13)
    public int capacityCards;

    @GuiSync(14)
    public boolean redstoneCard;

    @GuiSync(15)
    public int powerMode;

    @GuiSync(16)
    public int idleAeTimes100;

    @GuiSync(17)
    public int aeFuelTimes100;

    @GuiSync(18)
    public int fuelEfficiencyPercent;

    @GuiSync(19)
    public GenericStack pinnedInput;

    @GuiSync(20)
    public int tierOrdinal;

    @GuiSync(21)
    public int lavaFuelTimes100;

    public MESmelterMenu(int id, Inventory playerInventory, MESmelterBlockEntity smelter) {
        super(ModMenus.ME_SMELTER.get(), id, playerInventory, smelter);
        registerClientAction(
                SET_POWER_MODE,
                net.minecraft.network.codec.ByteBufCodecs.VAR_INT,
                this::setPowerMode);
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            accelerationCards = getHost().getUpgrades().getInstalledUpgrades(AEItems.SPEED_CARD);
            energyCards = getHost().getEnergyCardCount();
            fuelEfficiencyCards = getHost().getFuelEfficiencyCardCount();
            capacityCards = getHost().getCapacityCardCount();
            redstoneCard = getHost().hasRedstoneCard();
            powerMode = getHost().getPowerMode().ordinal();
            idleAeTimes100 = (int) Math.round(getHost().getIdleAePerTick() * 100);
            aeFuelTimes100 = (int) Math.round(getHost().getMaximumAeFuelPerTick() * 100);
            lavaFuelTimes100 = (int) Math.round(getHost().getMaximumLavaMbPerTick() * 100);
            fuelEfficiencyPercent = getHost().getFuelEfficiencyPercent();
            var pinned = getHost().getPinnedInput();
            pinnedInput = pinned == null ? null : new GenericStack(pinned, 1);
            tierOrdinal = getHost().getTier().ordinal();
        }
        super.broadcastChanges();
    }

    public SmelterTier getTier() {
        var tiers = SmelterTier.values();
        return tiers[Math.max(0, Math.min(tiers.length - 1, tierOrdinal))];
    }

    public int getSpeedMultiplier() {
        var raw = getTier().baseSpeedMultiplier() * (1 << Math.min(4, accelerationCards));
        return (int) Math.round(Math.min(getTier().accelerationCap(), raw));
    }

    public SmeltingPowerMode getPowerMode() {
        var modes = SmeltingPowerMode.values();
        return modes[Math.max(0, Math.min(modes.length - 1, powerMode))];
    }

    public void requestPowerMode(SmeltingPowerMode mode) {
        sendClientAction(SET_POWER_MODE, mode.ordinal());
    }

    private void setPowerMode(int mode) {
        var modes = SmeltingPowerMode.values();
        getHost().setPowerMode(modes[Math.max(0, Math.min(modes.length - 1, mode))]);
    }
}
