package dev.excal1bur.appliedsmelting.compat.jade;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.JadeUI;

import dev.excal1bur.appliedsmelting.AppliedSmelting;
import dev.excal1bur.appliedsmelting.blockentity.AbstractMENetworkFurnaceBlockEntity;
import dev.excal1bur.appliedsmelting.service.SmeltingPowerMode;
import dev.excal1bur.appliedsmelting.service.SmelterStatus;

public final class AppliedSmeltingJadeProvider
        implements StreamServerDataProvider<BlockAccessor, AppliedSmeltingJadeProvider.Data> {
    public static final AppliedSmeltingJadeProvider INSTANCE = new AppliedSmeltingJadeProvider();
    private static final Identifier UID = AppliedSmelting.id("machine_status");

    private AppliedSmeltingJadeProvider() {
    }

    @Override
    public Data streamData(BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof AbstractMENetworkFurnaceBlockEntity smelter)) {
            return null;
        }
        return new Data(
                smelter.getMachineStatus().id(),
                smelter.getProgressPercent(),
                smelter.getFuelPercent(),
                smelter.getPowerMode() == SmeltingPowerMode.ITEM_FUEL,
                smelter.getInternalInventory().getStackInSlot(0));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Data> streamCodec() {
        return Data.STREAM_CODEC;
    }

    @Override
    public Identifier getUid() {
        return UID;
    }

    public record Data(
            int statusId, int progressPercent, int fuelPercent, boolean itemFuelMode, ItemStack bufferedItem) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                Data::statusId,
                ByteBufCodecs.VAR_INT,
                Data::progressPercent,
                ByteBufCodecs.VAR_INT,
                Data::fuelPercent,
                ByteBufCodecs.BOOL,
                Data::itemFuelMode,
                ItemStack.OPTIONAL_STREAM_CODEC,
                Data::bufferedItem,
                Data::new);
    }

    public static final class Client implements IBlockComponentProvider {
        public static final Client INSTANCE = new Client();

        private Client() {
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            var data = AppliedSmeltingJadeProvider.INSTANCE.decodeFromData(accessor).orElse(null);
            if (data == null) {
                return;
            }
            var status = SmelterStatus.fromId(data.statusId());
            tooltip.add(Component.translatable(status.translationKey()));
            if (status == SmelterStatus.SMELTING && !data.bufferedItem().isEmpty()) {
                tooltip.add(JadeUI.item(data.bufferedItem()).alignSelfCenter());
                tooltip.append(JadeUI.progressArrow(data.progressPercent() / 100F).alignSelfCenter());
            }
            if (data.itemFuelMode() && data.fuelPercent() > 0) {
                tooltip.add(Component.translatable("gui.appliedsmelting.jade.fuel_percent", data.fuelPercent()));
            }
        }

        @Override
        public Identifier getUid() {
            return UID;
        }
    }
}
