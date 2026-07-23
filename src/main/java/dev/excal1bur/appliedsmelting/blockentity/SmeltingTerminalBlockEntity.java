package dev.excal1bur.appliedsmelting.blockentity;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
import dev.excal1bur.appliedsmelting.menu.TerminalQueueState;
import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;
import dev.excal1bur.appliedsmelting.service.FurnaceType;

public final class SmeltingTerminalBlockEntity extends AENetworkedBlockEntity implements SmeltingTerminalHost {
    private final IConfigManager configManager = IConfigManager.builder(this::saveChanges)
            .registerSetting(Settings.SORT_BY, SortOrder.NAME)
            .registerSetting(Settings.VIEW_MODE, ViewItems.ALL)
            .registerSetting(Settings.SORT_DIRECTION, SortDir.ASCENDING)
            .build();
    private final Map<FurnaceType, TerminalQueueState> states = new EnumMap<>(FurnaceType.class);

    public SmeltingTerminalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        getMainNode().setFlags(GridFlags.REQUIRE_CHANNEL).setIdlePowerUsage(1.0);
        for (var furnaceType : FurnaceType.values()) {
            states.put(furnaceType, new TerminalQueueState());
        }
    }

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
    public void setSelections(FurnaceType type, AEItemKey input, AEItemKey fuel) {
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
    public void loadTag(ValueInput input) {
        super.loadTag(input);
        configManager.readFromNBT(input);
        for (var type : FurnaceType.values()) {
            states.get(type).readFromNBT(input, type.serializedName());
        }
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        configManager.writeToNBT(output);
        for (var type : FurnaceType.values()) {
            states.get(type).writeToNBT(output, type.serializedName());
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
