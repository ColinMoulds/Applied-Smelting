package dev.excal1bur.appliedsmelting.service;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridService;
import appeng.api.networking.IGridServiceProvider;

import dev.excal1bur.appliedsmelting.blockentity.MESmelterBlockEntity;

public final class SmeltingService implements IGridService, IGridServiceProvider {
    private final Set<MESmelterBlockEntity> smelters = new LinkedHashSet<>();

    public SmeltingService(IGrid grid) {
    }

    @Override
    public void addNode(IGridNode node, @Nullable CompoundTag savedData) {
        if (node.getOwner() instanceof MESmelterBlockEntity smelter) {
            smelters.add(smelter);
        }
    }

    @Override
    public void removeNode(IGridNode node) {
        if (node.getOwner() instanceof MESmelterBlockEntity smelter) {
            smelters.remove(smelter);
        }
    }

    public int getSmelterCount() {
        return smelters.size();
    }

    public int getWorkingCount() {
        return (int) smelters.stream().filter(MESmelterBlockEntity::isWorking).count();
    }

    public boolean isEnabled() {
        return smelters.stream().anyMatch(MESmelterBlockEntity::isEnabled);
    }

    public void setEnabled(boolean enabled) {
        smelters.forEach(smelter -> smelter.setEnabled(enabled));
    }
}
