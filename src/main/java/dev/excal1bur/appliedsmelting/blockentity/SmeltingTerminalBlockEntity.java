package dev.excal1bur.appliedsmelting.blockentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import appeng.api.config.Settings;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.networking.GridFlags;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.ILinkStatus;
import appeng.api.storage.MEStorage;
import appeng.api.storage.SupplierStorage;
import appeng.api.util.IConfigManager;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;

import dev.excal1bur.appliedsmelting.core.ModBlocks;
import dev.excal1bur.appliedsmelting.core.ModMenus;
import dev.excal1bur.appliedsmelting.menu.SmeltingTerminalHost;
import dev.excal1bur.appliedsmelting.service.SmeltingService;

public final class SmeltingTerminalBlockEntity extends AENetworkedBlockEntity implements SmeltingTerminalHost {
    private final IConfigManager configManager = IConfigManager.builder(this::saveChanges)
            .registerSetting(Settings.SORT_BY, SortOrder.NAME)
            .registerSetting(Settings.VIEW_MODE, ViewItems.ALL)
            .registerSetting(Settings.SORT_DIRECTION, SortDir.ASCENDING)
            .build();
    private AEItemKey selectedInput;
    private AEItemKey selectedFuel;
    private final List<AEItemKey> queuedInputs = new ArrayList<>();
    private long targetAmount;

    public SmeltingTerminalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL).setIdlePowerUsage(1.0);
    }

    public SmeltingService getSmeltingService() {
        var grid = getMainNode().getGrid();
        if (grid == null) {
            return null;
        }
        var service = grid.getService(SmeltingService.class);
        service.adoptSelections(selectedInput, selectedFuel);
        service.adoptQueuedInputs(queuedInputs);
        service.adoptTargetAmount(targetAmount);
        return service;
    }

    public void setSelections(AEItemKey input, AEItemKey fuel) {
        if (!Objects.equals(selectedInput, input)) {
            queuedInputs.clear();
            if (input != null) {
                queuedInputs.add(input);
            }
        }
        selectedInput = input;
        selectedFuel = fuel;
        saveChanges();
    }

    @Override
    public void setQueuedInputs(List<AEItemKey> inputs) {
        queuedInputs.clear();
        queuedInputs.addAll(inputs);
        selectedInput = queuedInputs.isEmpty() ? null : queuedInputs.getFirst();
        saveChanges();
    }

    @Override
    public void setTargetAmount(long targetAmount) {
        this.targetAmount = Math.max(0, targetAmount);
        saveChanges();
    }

    @Override
    public void loadTag(ValueInput input) {
        super.loadTag(input);
        configManager.readFromNBT(input);
        selectedInput = readItemKey(input.childOrEmpty("selectedInput"));
        selectedFuel = readItemKey(input.childOrEmpty("selectedFuel"));
        queuedInputs.clear();
        var queuedInputCount = input.getIntOr("queuedInputCount", -1);
        if (queuedInputCount >= 0) {
            for (int i = 0; i < queuedInputCount; i++) {
                var queuedInput = readItemKey(input.childOrEmpty("queuedInput" + i));
                if (queuedInput != null && !queuedInputs.contains(queuedInput)) {
                    queuedInputs.add(queuedInput);
                }
            }
        } else if (selectedInput != null) {
            queuedInputs.add(selectedInput);
        }
        targetAmount = input.getLongOr("targetAmount", 0);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        configManager.writeToNBT(output);
        writeItemKey(output.child("selectedInput"), selectedInput);
        writeItemKey(output.child("selectedFuel"), selectedFuel);
        output.putInt("queuedInputCount", queuedInputs.size());
        for (int i = 0; i < queuedInputs.size(); i++) {
            writeItemKey(output.child("queuedInput" + i), queuedInputs.get(i));
        }
        output.putLong("targetAmount", targetAmount);
    }

    private static AEItemKey readItemKey(ValueInput input) {
        var stack = GenericStack.readTag(input);
        return stack != null && stack.what() instanceof AEItemKey itemKey ? itemKey : null;
    }

    private static void writeItemKey(ValueOutput output, AEItemKey key) {
        if (key != null) {
            GenericStack.writeTag(output, new GenericStack(key, 1));
        }
    }

    @Override
    public MEStorage getInventory() {
        return new SupplierStorage(() -> {
            var grid = getMainNode().getGrid();
            return grid == null ? null : grid.getStorageService().getInventory();
        });
    }

    @Override
    public ILinkStatus getLinkStatus() {
        return ILinkStatus.ofManagedNode(getMainNode());
    }

    @Override
    public IConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(ModMenus.SMELTING_TERMINAL.get(), player, MenuLocators.forBlockEntity(this));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return ModBlocks.SMELTING_TERMINAL_ITEM.get().getDefaultInstance();
    }
}
