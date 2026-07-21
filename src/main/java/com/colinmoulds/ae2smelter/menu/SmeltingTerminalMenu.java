package com.colinmoulds.ae2smelter.menu;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.player.Inventory;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.ClientActionKey;
import appeng.menu.guisync.GuiSync;

import com.colinmoulds.ae2smelter.blockentity.SmeltingTerminalBlockEntity;
import com.colinmoulds.ae2smelter.core.ModMenus;

public final class SmeltingTerminalMenu extends AEBaseMenu {
    private static final ClientActionKey<Boolean> SET_ENABLED = new ClientActionKey<>("setEnabled");

    private final SmeltingTerminalBlockEntity terminal;

    @GuiSync(1)
    public int smelterCount;

    @GuiSync(2)
    public int workingCount;

    @GuiSync(3)
    public boolean enabled;

    public SmeltingTerminalMenu(int id, Inventory playerInventory, SmeltingTerminalBlockEntity terminal) {
        super(ModMenus.SMELTING_TERMINAL.get(), id, playerInventory, terminal);
        this.terminal = terminal;
        createPlayerInventorySlots(playerInventory);
        registerClientAction(SET_ENABLED, ByteBufCodecs.BOOL, this::setNetworkEnabled);
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            var service = terminal.getSmeltingService();
            smelterCount = service == null ? 0 : service.getSmelterCount();
            workingCount = service == null ? 0 : service.getWorkingCount();
            enabled = service != null && service.isEnabled();
        }
        super.broadcastChanges();
    }

    public void requestNetworkEnabled(boolean enabled) {
        sendClientAction(SET_ENABLED, enabled);
    }

    private void setNetworkEnabled(boolean enabled) {
        var service = terminal.getSmeltingService();
        if (service != null) {
            service.setEnabled(enabled);
        }
    }
}
