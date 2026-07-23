package dev.excal1bur.appliedsmelting.service;

import appeng.api.networking.IGrid;

import dev.excal1bur.appliedsmelting.blockentity.AbstractMENetworkFurnaceBlockEntity;
import dev.excal1bur.appliedsmelting.blockentity.MECrucibleBlockEntity;

public final class CrucibleService extends AbstractFurnaceNetworkService {
    public CrucibleService(IGrid grid) {
    }

    @Override
    protected Class<? extends AbstractMENetworkFurnaceBlockEntity> machineClass() {
        return MECrucibleBlockEntity.class;
    }

    @Override
    protected FurnaceType furnaceType() {
        return FurnaceType.CRUCIBLE;
    }
}
