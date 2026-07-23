package dev.excal1bur.appliedsmelting.part;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import appeng.api.parts.IPartItem;
import appeng.api.stacks.AEItemKey;
import appeng.parts.reporting.AbstractTerminalPart;

import dev.excal1bur.appliedsmelting.core.ModMenus;
import dev.excal1bur.appliedsmelting.menu.SmeltingTerminalHost;
import dev.excal1bur.appliedsmelting.menu.TerminalQueueState;
import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;
import dev.excal1bur.appliedsmelting.service.FurnaceType;

public final class SmeltingTerminalPart extends AbstractTerminalPart implements SmeltingTerminalHost {
    private final Map<FurnaceType, TerminalQueueState> states = new EnumMap<>(FurnaceType.class);

    public SmeltingTerminalPart(IPartItem<?> partItem) {
        super(partItem);
        for (var type : FurnaceType.values()) {
            states.put(type, new TerminalQueueState());
        }
    }

    @Override
    public MenuType<?> getMenuType(Player player) {
        return ModMenus.SMELTING_TERMINAL.get();
    }

    @Nullable
    @Override
    public AbstractFurnaceNetworkService getService(FurnaceType type) {
        var grid = getMainNode().getGrid();
        if (grid == null) {
            return null;
        }
        var service = grid.getService(type.serviceClass());
        states.get(type).adoptInto(service);
        return service;
    }

    @Override
    public void setSelections(FurnaceType type, @Nullable AEItemKey input, @Nullable AEItemKey fuel) {
        states.get(type).setSelections(input, fuel);
        saveChanges();
    }

    @Override
    public void setQueuedInputs(FurnaceType type, List<AEItemKey> inputs) {
        states.get(type).setQueuedInputs(inputs);
        saveChanges();
    }

    @Override
    public void setTargetAmount(FurnaceType type, long targetAmount) {
        states.get(type).setTargetAmount(targetAmount);
        saveChanges();
    }

    @Override
    public void readFromNBT(ValueInput input) {
        super.readFromNBT(input);
        for (var type : FurnaceType.values()) {
            states.get(type).readFromNBT(input, type.serializedName());
        }
    }

    @Override
    public void writeToNBT(ValueOutput output) {
        super.writeToNBT(output);
        for (var type : FurnaceType.values()) {
            states.get(type).writeToNBT(output, type.serializedName());
        }
    }
}
