package dev.excal1bur.appliedsmelting.compat.jade;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

import dev.excal1bur.appliedsmelting.block.AbstractMENetworkFurnaceBlock;
import dev.excal1bur.appliedsmelting.blockentity.AbstractMENetworkFurnaceBlockEntity;

/**
 * Every ME machine (Smelter, Blast Furnace, Smoker, Crucible, all tiers) shares
 * {@link AbstractMENetworkFurnaceBlock}/{@link AbstractMENetworkFurnaceBlockEntity}, so registering against
 * those base classes covers all of them at once.
 */
@WailaPlugin
public final class AppliedSmeltingJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(
                AppliedSmeltingJadeProvider.INSTANCE, AbstractMENetworkFurnaceBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(
                AppliedSmeltingJadeProvider.Client.INSTANCE, AbstractMENetworkFurnaceBlock.class);
    }
}
