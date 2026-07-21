package dev.excal1bur.appliedsmelting.service;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridService;
import appeng.api.networking.IGridServiceProvider;
import appeng.api.stacks.AEItemKey;

import dev.excal1bur.appliedsmelting.blockentity.MESmelterBlockEntity;

public final class SmeltingService implements IGridService, IGridServiceProvider {
    private final Set<MESmelterBlockEntity> smelters = new LinkedHashSet<>();
    private AEItemKey selectedInput;
    private AEItemKey selectedFuel;
    private long targetAmount = -1;

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
        return (int) smelters.stream().filter(MESmelterBlockEntity::isActivelySmelting).count();
    }

    public int getAverageProgressPercent() {
        return (int) Math.round(smelters.stream()
                .filter(MESmelterBlockEntity::isWorking)
                .mapToInt(MESmelterBlockEntity::getProgressPercent)
                .average()
                .orElse(0));
    }

    public int getAverageFuelPercent() {
        return (int) Math.round(smelters.stream()
                .mapToInt(MESmelterBlockEntity::getFuelPercent)
                .filter(percent -> percent > 0)
                .average()
                .orElse(0));
    }

    public SmelterStatus getOverallStatus() {
        if (smelters.isEmpty()) {
            return SmelterStatus.NO_SMELTERS;
        }

        var priority = new SmelterStatus[] {
            SmelterStatus.SMELTING,
            SmelterStatus.OUTPUT_FULL,
            SmelterStatus.MISSING_POWER,
            SmelterStatus.MISSING_FUEL,
            SmelterStatus.MISSING_INPUT,
            SmelterStatus.INVALID_RECIPE,
            SmelterStatus.TARGET_REACHED,
            SmelterStatus.WAITING_FOR_SELECTION,
            SmelterStatus.PAUSED,
            SmelterStatus.OFFLINE
        };
        for (var candidate : priority) {
            if (smelters.stream().anyMatch(smelter -> smelter.getMachineStatus() == candidate)) {
                return candidate;
            }
        }
        return SmelterStatus.OFFLINE;
    }

    public boolean isEnabled() {
        return smelters.stream().anyMatch(MESmelterBlockEntity::isEnabled);
    }

    public void setEnabled(boolean enabled) {
        smelters.forEach(smelter -> smelter.setEnabled(enabled));
    }

    public AEItemKey getSelectedInput() {
        return selectedInput;
    }

    public AEItemKey getSelectedFuel() {
        return selectedFuel;
    }

    public void setSelectedInput(AEItemKey selectedInput) {
        this.selectedInput = selectedInput;
        wakeSmelters();
    }

    public void setSelectedFuel(AEItemKey selectedFuel) {
        this.selectedFuel = selectedFuel;
        wakeSmelters();
    }

    public void adoptSelections(AEItemKey input, AEItemKey fuel) {
        if (selectedInput == null && input != null) {
            selectedInput = input;
        }
        if (selectedFuel == null && fuel != null) {
            selectedFuel = fuel;
        }
    }

    public void adoptTargetAmount(long targetAmount) {
        if (this.targetAmount < 0) {
            this.targetAmount = Math.max(0, targetAmount);
        }
    }

    public long getTargetAmount() {
        return Math.max(0, targetAmount);
    }

    public void setTargetAmount(long targetAmount) {
        this.targetAmount = Math.max(0, targetAmount);
        wakeSmelters();
    }

    public boolean canStartJob(
            MESmelterBlockEntity requester, AEItemKey output, long outputAmount, long storedAmount) {
        var target = getTargetAmount();
        if (target == 0) {
            return true;
        }

        long pendingAmount = smelters.stream()
                .filter(smelter -> smelter != requester)
                .mapToLong(smelter -> smelter.getPendingOutputAmount(output))
                .sum();
        return storedAmount + pendingAmount + outputAmount <= target;
    }

    private void wakeSmelters() {
        smelters.forEach(MESmelterBlockEntity::wakeForSelectionChange);
    }
}
