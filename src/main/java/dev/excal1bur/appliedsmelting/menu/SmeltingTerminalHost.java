package dev.excal1bur.appliedsmelting.menu;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import appeng.api.stacks.AEItemKey;
import appeng.api.storage.ITerminalHost;

import dev.excal1bur.appliedsmelting.service.SmeltingService;

public interface SmeltingTerminalHost extends ITerminalHost {
    @Nullable
    SmeltingService getSmeltingService();

    void setSelections(@Nullable AEItemKey input, @Nullable AEItemKey fuel);

    void setQueuedInputs(List<AEItemKey> inputs);

    void setTargetAmount(long targetAmount);
}
