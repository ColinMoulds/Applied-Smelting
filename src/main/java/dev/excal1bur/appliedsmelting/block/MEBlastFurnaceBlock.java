package dev.excal1bur.appliedsmelting.block;

import dev.excal1bur.appliedsmelting.blockentity.MEBlastFurnaceBlockEntity;
import dev.excal1bur.appliedsmelting.core.ModBlocks;
import dev.excal1bur.appliedsmelting.core.ModMenus;
import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;
import dev.excal1bur.appliedsmelting.service.BlastFurnaceTier;
import dev.excal1bur.appliedsmelting.service.BlastingService;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.level.block.Block;

public final class MEBlastFurnaceBlock extends AbstractCookingFurnaceBlock<MEBlastFurnaceBlockEntity> {
    private final BlastFurnaceTier tier;

    public MEBlastFurnaceBlock(Properties properties, BlastFurnaceTier tier) {
        super(properties);
        this.tier = tier;
    }

    public BlastFurnaceTier getTier() {
        return tier;
    }

    @Override
    protected ResourceKey<RecipePropertySet> recipePropertySet() {
        return RecipePropertySet.BLAST_FURNACE_INPUT;
    }

    @Override
    protected MenuType<?> menuType() {
        return ModMenus.ME_BLAST_FURNACE.get();
    }

    @Override
    protected int tierLevel() {
        return tier.ordinal();
    }

    @Override
    protected Block blockForTierLevel(int tierLevel) {
        return ModBlocks.blockForBlastFurnaceTier(BlastFurnaceTier.values()[tierLevel])
                .get();
    }

    @Override
    protected Class<? extends AbstractFurnaceNetworkService> serviceClass() {
        return BlastingService.class;
    }
}
