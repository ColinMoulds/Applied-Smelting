package dev.excal1bur.appliedsmelting.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;

import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;

/** One machine type's persisted terminal-side selection/queue state, restored into a fresh grid service on demand. */
public final class TerminalQueueState {
    @Nullable private AEItemKey selectedInput;
    @Nullable private AEItemKey selectedFuel;
    private final List<AEItemKey> queuedInputs = new ArrayList<>();
    private long targetAmount;

    public void adoptInto(AbstractFurnaceNetworkService service) {
        service.adoptSelections(selectedInput, selectedFuel);
        service.adoptQueuedInputs(queuedInputs);
        service.adoptTargetAmount(targetAmount);
    }

    public void setSelections(@Nullable AEItemKey input, @Nullable AEItemKey fuel) {
        if (!Objects.equals(selectedInput, input)) {
            queuedInputs.clear();
            if (input != null) {
                queuedInputs.add(input);
            }
        }
        selectedInput = input;
        selectedFuel = fuel;
    }

    public void setQueuedInputs(List<AEItemKey> inputs) {
        queuedInputs.clear();
        queuedInputs.addAll(inputs);
        selectedInput = queuedInputs.isEmpty() ? null : queuedInputs.getFirst();
    }

    public void setTargetAmount(long targetAmount) {
        this.targetAmount = Math.max(0, targetAmount);
    }

    public void readFromNBT(ValueInput input, String prefix) {
        selectedInput = readItemKey(input.childOrEmpty(prefix + "SelectedInput"));
        selectedFuel = readItemKey(input.childOrEmpty(prefix + "SelectedFuel"));
        queuedInputs.clear();
        var queuedInputCount = input.getIntOr(prefix + "QueuedInputCount", -1);
        if (queuedInputCount >= 0) {
            for (int i = 0; i < queuedInputCount; i++) {
                var queuedInput = readItemKey(input.childOrEmpty(prefix + "QueuedInput" + i));
                if (queuedInput != null && !queuedInputs.contains(queuedInput)) {
                    queuedInputs.add(queuedInput);
                }
            }
        } else if (selectedInput != null) {
            queuedInputs.add(selectedInput);
        }
        targetAmount = input.getLongOr(prefix + "TargetAmount", 0);
    }

    public void writeToNBT(ValueOutput output, String prefix) {
        writeItemKey(output.child(prefix + "SelectedInput"), selectedInput);
        writeItemKey(output.child(prefix + "SelectedFuel"), selectedFuel);
        output.putInt(prefix + "QueuedInputCount", queuedInputs.size());
        for (int i = 0; i < queuedInputs.size(); i++) {
            writeItemKey(output.child(prefix + "QueuedInput" + i), queuedInputs.get(i));
        }
        output.putLong(prefix + "TargetAmount", targetAmount);
    }

    @Nullable
    private static AEItemKey readItemKey(ValueInput input) {
        var stack = GenericStack.readTag(input);
        return stack != null && stack.what() instanceof AEItemKey itemKey ? itemKey : null;
    }

    private static void writeItemKey(ValueOutput output, @Nullable AEItemKey key) {
        if (key != null) {
            GenericStack.writeTag(output, new GenericStack(key, 1));
        }
    }
}
