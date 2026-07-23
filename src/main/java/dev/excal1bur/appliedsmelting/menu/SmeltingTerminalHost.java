package dev.excal1bur.appliedsmelting.menu;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import appeng.api.stacks.AEItemKey;
import appeng.api.storage.ITerminalHost;

import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;
import dev.excal1bur.appliedsmelting.service.FurnaceType;

public interface SmeltingTerminalHost extends ITerminalHost {
    @Nullable
    AbstractFurnaceNetworkService getService(FurnaceType type);

    void setSelections(FurnaceType type, @Nullable AEItemKey input, @Nullable AEItemKey fuel);

    void setQueuedInputs(FurnaceType type, List<AEItemKey> inputs);

    void setTargetAmount(FurnaceType type, long targetAmount);
}
