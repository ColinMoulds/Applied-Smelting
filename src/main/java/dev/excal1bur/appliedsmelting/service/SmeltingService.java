package dev.excal1bur.appliedsmelting.service;

import appeng.api.networking.IGrid;

import dev.excal1bur.appliedsmelting.blockentity.AbstractMENetworkFurnaceBlockEntity;
import dev.excal1bur.appliedsmelting.blockentity.MESmelterBlockEntity;

public final class SmeltingService extends AbstractFurnaceNetworkService {
    public SmeltingService(IGrid grid) {
    }

    @Override
    protected Class<? extends AbstractMENetworkFurnaceBlockEntity> machineClass() {
        return MESmelterBlockEntity.class;
    }

    @Override
    protected FurnaceType furnaceType() {
        return FurnaceType.SMELTING;
    }
}
