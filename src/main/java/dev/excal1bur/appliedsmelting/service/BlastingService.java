package dev.excal1bur.appliedsmelting.service;

import appeng.api.networking.IGrid;

import dev.excal1bur.appliedsmelting.blockentity.AbstractMENetworkFurnaceBlockEntity;
import dev.excal1bur.appliedsmelting.blockentity.MEBlastFurnaceBlockEntity;

public final class BlastingService extends AbstractFurnaceNetworkService {
    public BlastingService(IGrid grid) {
    }

    @Override
    protected Class<? extends AbstractMENetworkFurnaceBlockEntity> machineClass() {
        return MEBlastFurnaceBlockEntity.class;
    }

    @Override
    protected FurnaceType furnaceType() {
        return FurnaceType.BLASTING;
    }
}
