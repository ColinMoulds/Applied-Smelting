package dev.excal1bur.appliedsmelting.block;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipePropertySet;

import dev.excal1bur.appliedsmelting.blockentity.MESmokerBlockEntity;
import dev.excal1bur.appliedsmelting.core.ModMenus;

public final class MESmokerBlock extends AbstractCookingFurnaceBlock<MESmokerBlockEntity> {
    public MESmokerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ResourceKey<RecipePropertySet> recipePropertySet() {
        return RecipePropertySet.SMOKER_INPUT;
    }

    @Override
    protected MenuType<?> menuType() {
        return ModMenus.ME_SMOKER.get();
    }
}
