package dev.excal1bur.appliedsmelting.block;

import dev.excal1bur.appliedsmelting.blockentity.MESmelterBlockEntity;
import dev.excal1bur.appliedsmelting.core.ModBlocks;
import dev.excal1bur.appliedsmelting.core.ModMenus;
import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;
import dev.excal1bur.appliedsmelting.service.SmelterTier;
import dev.excal1bur.appliedsmelting.service.SmeltingService;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.level.block.Block;

public final class MESmelterBlock extends AbstractCookingFurnaceBlock<MESmelterBlockEntity> {
    private final SmelterTier tier;

    public MESmelterBlock(Properties properties, SmelterTier tier) {
        super(properties);
        this.tier = tier;
    }

    public SmelterTier getTier() {
        return tier;
    }

    @Override
    protected ResourceKey<RecipePropertySet> recipePropertySet() {
        return RecipePropertySet.FURNACE_INPUT;
    }

    @Override
    protected MenuType<?> menuType() {
        return ModMenus.ME_SMELTER.get();
    }

    @Override
    protected int tierLevel() {
        return tier.ordinal();
    }

    @Override
    protected Block blockForTierLevel(int tierLevel) {
        return ModBlocks.blockForTier(SmelterTier.values()[tierLevel]).get();
    }

    @Override
    protected Class<? extends AbstractFurnaceNetworkService> serviceClass() {
        return SmeltingService.class;
    }
}
