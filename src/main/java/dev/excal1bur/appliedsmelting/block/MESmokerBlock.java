package dev.excal1bur.appliedsmelting.block;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.level.block.Block;

import dev.excal1bur.appliedsmelting.blockentity.MESmokerBlockEntity;
import dev.excal1bur.appliedsmelting.core.ModBlocks;
import dev.excal1bur.appliedsmelting.core.ModMenus;
import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;
import dev.excal1bur.appliedsmelting.service.SmokerTier;
import dev.excal1bur.appliedsmelting.service.SmokingService;

public final class MESmokerBlock extends AbstractCookingFurnaceBlock<MESmokerBlockEntity> {
    private final SmokerTier tier;

    public MESmokerBlock(Properties properties, SmokerTier tier) {
        super(properties);
        this.tier = tier;
    }

    public SmokerTier getTier() {
        return tier;
    }

    @Override
    protected ResourceKey<RecipePropertySet> recipePropertySet() {
        return RecipePropertySet.SMOKER_INPUT;
    }

    @Override
    protected MenuType<?> menuType() {
        return ModMenus.ME_SMOKER.get();
    }

    @Override
    protected int tierLevel() {
        return tier.ordinal();
    }

    @Override
    protected Block blockForTierLevel(int tierLevel) {
        return ModBlocks.blockForSmokerTier(SmokerTier.values()[tierLevel]).get();
    }

    @Override
    protected Class<? extends AbstractFurnaceNetworkService> serviceClass() {
        return SmokingService.class;
    }
}
