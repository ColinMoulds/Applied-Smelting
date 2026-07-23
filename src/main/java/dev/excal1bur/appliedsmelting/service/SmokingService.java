package dev.excal1bur.appliedsmelting.service;

import appeng.api.networking.IGrid;

import dev.excal1bur.appliedsmelting.blockentity.AbstractMENetworkFurnaceBlockEntity;
import dev.excal1bur.appliedsmelting.blockentity.MESmokerBlockEntity;

public final class SmokingService extends AbstractFurnaceNetworkService {
    public SmokingService(IGrid grid) {
    }

    @Override
    protected Class<? extends AbstractMENetworkFurnaceBlockEntity> machineClass() {
        return MESmokerBlockEntity.class;
    }

    @Override
    protected FurnaceType furnaceType() {
        return FurnaceType.SMOKING;
    }
}
