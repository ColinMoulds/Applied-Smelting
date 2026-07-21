package dev.excal1bur.appliedsmelting.menu;

import org.jetbrains.annotations.Nullable;

import appeng.api.stacks.AEItemKey;
import appeng.api.storage.ITerminalHost;

import dev.excal1bur.appliedsmelting.service.SmeltingService;

public interface SmeltingTerminalHost extends ITerminalHost {
    @Nullable
    SmeltingService getSmeltingService();

    void setSelections(@Nullable AEItemKey input, @Nullable AEItemKey fuel);

    void setTargetAmount(long targetAmount);
}
