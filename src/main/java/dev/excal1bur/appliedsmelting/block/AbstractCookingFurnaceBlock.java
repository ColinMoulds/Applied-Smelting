package dev.excal1bur.appliedsmelting.block;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.level.Level;

import dev.excal1bur.appliedsmelting.blockentity.AbstractCookingFurnaceBlockEntity;

/** Base for blocks whose machine processes a vanilla cooking-style recipe type (smelting/blasting/smoking). */
public abstract class AbstractCookingFurnaceBlock<T extends AbstractCookingFurnaceBlockEntity>
        extends AbstractMENetworkFurnaceBlock<T> {
    protected AbstractCookingFurnaceBlock(Properties properties) {
        super(properties);
    }

    /** Which vanilla input set identifies smeltable items for this machine, for the pin-by-right-click check. */
    protected abstract ResourceKey<RecipePropertySet> recipePropertySet();

    @Override
    protected final boolean isValidPinInput(Level level, ItemStack stack) {
        return level.recipeAccess().propertySet(recipePropertySet()).test(stack);
    }
}
