package dev.excal1bur.appliedsmelting.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import dev.excal1bur.appliedsmelting.menu.SmeltingTerminalHost;

public final class SmeltingService implements IGridService, IGridServiceProvider {
    private final Set<MESmelterBlockEntity> smelters = new LinkedHashSet<>();
    private final Set<SmeltingTerminalHost> terminals = new LinkedHashSet<>();
    private final List<AEItemKey> queuedInputs = new ArrayList<>();
    private final Map<MESmelterBlockEntity, AEItemKey> assignments = new HashMap<>();
    private final Map<MESmelterBlockEntity, AEItemKey> deferredAssignments = new HashMap<>();
    private AEItemKey selectedFuel;
    private long targetAmount = -1;
    private int assignmentCursor;

    public SmeltingService(IGrid grid) {
    }

    @Override
    public void addNode(IGridNode node, @Nullable CompoundTag savedData) {
        if (node.getOwner() instanceof MESmelterBlockEntity smelter) {
            smelters.add(smelter);
        }
        if (node.getOwner() instanceof SmeltingTerminalHost terminal) {
            terminals.add(terminal);
        }
    }

    @Override
    public void removeNode(IGridNode node) {
        if (node.getOwner() instanceof MESmelterBlockEntity smelter) {
            smelters.remove(smelter);
            assignments.remove(smelter);
            deferredAssignments.remove(smelter);
        }
        if (node.getOwner() instanceof SmeltingTerminalHost terminal) {
            terminals.remove(terminal);
        }
    }

    public int getSmelterCount() {
        return smelters.size();
    }

    public int getWorkingCount() {
        return (int) smelters.stream().filter(MESmelterBlockEntity::isActivelySmelting).count();
    }

    public int getItemFuelSmelterCount() {
        return (int) smelters.stream()
                .filter(smelter -> smelter.getPowerMode() == SmeltingPowerMode.ITEM_FUEL)
                .count();
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
            SmelterStatus.REDSTONE_PAUSED,
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
        return queuedInputs.isEmpty() ? null : queuedInputs.getFirst();
    }

    /**
     * The queued item that is actually being smelted right now, falling back to the first
     * queued item when nothing is currently active (e.g. still waiting to pull input).
     */
    public AEItemKey getDisplayInput() {
        var active = getActiveInputCounts().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        return active != null ? active : getSelectedInput();
    }

    /** Every queued item that at least one connected smelter is actively smelting right now. */
    public Set<AEItemKey> getActiveInputs() {
        return getActiveInputCounts().keySet();
    }

    private Map<AEItemKey, Integer> getActiveInputCounts() {
        var counts = new HashMap<AEItemKey, Integer>();
        for (var smelter : smelters) {
            if (!smelter.isActivelySmelting()) {
                continue;
            }
            var item = smelter.getPinnedInput();
            if (item == null) {
                item = assignments.get(smelter);
            }
            if (item != null) {
                counts.merge(item, 1, Integer::sum);
            }
        }
        return counts;
    }

    public AEItemKey getSelectedFuel() {
        return selectedFuel;
    }

    public void setSelectedInput(AEItemKey selectedInput) {
        queuedInputs.clear();
        if (selectedInput != null) {
            queuedInputs.add(selectedInput);
        }
        assignments.clear();
        deferredAssignments.clear();
        persistQueue();
        wakeSmelters();
    }

    public void setSelectedFuel(AEItemKey selectedFuel) {
        this.selectedFuel = selectedFuel;
        terminals.forEach(terminal -> terminal.setSelections(getSelectedInput(), selectedFuel));
        wakeSmelters();
    }

    public void adoptSelections(AEItemKey input, AEItemKey fuel) {
        if (queuedInputs.isEmpty() && input != null) {
            queuedInputs.add(input);
        }
        if (selectedFuel == null && fuel != null) {
            selectedFuel = fuel;
        }
    }

    public void adoptQueuedInputs(List<AEItemKey> inputs) {
        if (queuedInputs.isEmpty() && !inputs.isEmpty()) {
            queuedInputs.addAll(inputs.stream().distinct().toList());
        }
    }

    public List<AEItemKey> getQueuedInputs() {
        return List.copyOf(queuedInputs);
    }

    public boolean toggleQueuedInput(AEItemKey input) {
        if (queuedInputs.remove(input)) {
            assignments.entrySet().removeIf(entry -> entry.getValue().equals(input));
            persistQueue();
            wakeSmelters();
            return true;
        }
        if (queuedInputs.size() >= getQueueCapacity()) {
            return false;
        }
        queuedInputs.add(input);
        persistQueue();
        wakeSmelters();
        return true;
    }

    public boolean removeQueuedInput(int index) {
        if (index < 0 || index >= queuedInputs.size()) {
            return false;
        }
        var removed = queuedInputs.remove(index);
        assignments.entrySet().removeIf(entry -> entry.getValue().equals(removed));
        persistQueue();
        wakeSmelters();
        return true;
    }

    public int getQueueCapacity() {
        if (smelters.isEmpty()) {
            return 1;
        }
        var baseCapacity = smelters.stream().mapToInt(s -> s.getTier().baseQueueCapacity()).max().orElse(1);
        var capCeiling = smelters.stream().mapToInt(s -> s.getTier().capacityCardCap()).max().orElse(9);
        var capacityCards = smelters.stream().mapToInt(MESmelterBlockEntity::getCapacityCardCount).sum();
        return Math.min(capCeiling, baseCapacity + capacityCards);
    }

    /** Current network-queue assignment for a smelter, if any. Used to carry work over a tier upgrade. */
    @Nullable
    public AEItemKey getAssignment(MESmelterBlockEntity smelter) {
        return assignments.get(smelter);
    }

    @Nullable
    public AEItemKey getDeferredAssignment(MESmelterBlockEntity smelter) {
        return deferredAssignments.get(smelter);
    }

    /** Carries a captured assignment/deferral over to a smelter that just replaced another (e.g. a tier upgrade). */
    public void transferAssignment(MESmelterBlockEntity newSmelter, @Nullable AEItemKey assignment, @Nullable AEItemKey deferred) {
        if (assignment != null) {
            assignments.put(newSmelter, assignment);
        }
        if (deferred != null) {
            deferredAssignments.put(newSmelter, deferred);
        }
    }

    public AEItemKey assignInput(MESmelterBlockEntity smelter) {
        var pinned = smelter.getPinnedInput();
        if (pinned != null) {
            assignments.remove(smelter);
            deferredAssignments.remove(smelter);
            return pinned;
        }
        var activeInputs = getActiveQueuedInputs();
        var existing = assignments.get(smelter);
        if (existing != null && activeInputs.contains(existing)) {
            return existing;
        }
        assignments.remove(smelter);
        var deferred = deferredAssignments.remove(smelter);
        if (activeInputs.isEmpty()) {
            return null;
        }

        var assignmentCounts = new HashMap<AEItemKey, Integer>();
        activeInputs.forEach(input -> assignmentCounts.put(input, 0));
        assignments.values().forEach(input -> assignmentCounts.computeIfPresent(input, (key, count) -> count + 1));
        var leastAssigned = assignmentCounts.values().stream().mapToInt(Integer::intValue).min().orElse(0);
        for (int offset = 0; offset < activeInputs.size(); offset++) {
            var index = Math.floorMod(assignmentCursor + offset, activeInputs.size());
            var candidate = activeInputs.get(index);
            if (activeInputs.size() > 1 && candidate.equals(deferred)) {
                continue;
            }
            if (assignmentCounts.get(candidate) == leastAssigned) {
                assignments.put(smelter, candidate);
                assignmentCursor = index + 1;
                return candidate;
            }
        }
        for (int offset = 0; offset < activeInputs.size(); offset++) {
            var index = Math.floorMod(assignmentCursor + offset, activeInputs.size());
            var candidate = activeInputs.get(index);
            if (activeInputs.size() == 1 || !candidate.equals(deferred)) {
                assignments.put(smelter, candidate);
                assignmentCursor = index + 1;
                return candidate;
            }
        }
        return activeInputs.getFirst();
    }

    public void releaseAssignment(MESmelterBlockEntity smelter) {
        assignments.remove(smelter);
    }

    public void deferAssignment(MESmelterBlockEntity smelter, AEItemKey input) {
        assignments.remove(smelter);
        if (smelter.getPinnedInput() == null && input != null) {
            deferredAssignments.put(smelter, input);
        }
    }

    public int getCombinedSpeedMultiplier() {
        return smelters.stream().mapToInt(MESmelterBlockEntity::getSpeedMultiplier).sum();
    }

    public double getCombinedIdleAePerTick() {
        return smelters.stream().mapToDouble(MESmelterBlockEntity::getIdleAePerTick).sum();
    }

    public double getCombinedMaximumAeFuelPerTick() {
        return smelters.stream().mapToDouble(MESmelterBlockEntity::getMaximumAeFuelPerTick).sum();
    }

    private List<AEItemKey> getActiveQueuedInputs() {
        return queuedInputs.subList(0, Math.min(queuedInputs.size(), getQueueCapacity()));
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
        terminals.forEach(terminal -> terminal.setTargetAmount(this.targetAmount));
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

    private void persistQueue() {
        var queue = getQueuedInputs();
        terminals.forEach(terminal -> terminal.setQueuedInputs(queue));
    }
}
