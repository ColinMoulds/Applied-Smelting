package dev.excal1bur.appliedsmelting.block;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipePropertySet;

import dev.excal1bur.appliedsmelting.blockentity.MEBlastFurnaceBlockEntity;
import dev.excal1bur.appliedsmelting.core.ModMenus;

public final class MEBlastFurnaceBlock extends AbstractCookingFurnaceBlock<MEBlastFurnaceBlockEntity> {
    public MEBlastFurnaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ResourceKey<RecipePropertySet> recipePropertySet() {
        return RecipePropertySet.BLAST_FURNACE_INPUT;
    }

    @Override
    protected MenuType<?> menuType() {
        return ModMenus.ME_BLAST_FURNACE.get();
    }
}
