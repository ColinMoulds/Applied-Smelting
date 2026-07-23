package dev.excal1bur.appliedsmelting.block;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import dev.excal1bur.appliedsmelting.blockentity.MECrucibleBlockEntity;
import dev.excal1bur.appliedsmelting.core.ModMenus;
import dev.excal1bur.appliedsmelting.core.ModRecipes;

public final class MECrucibleBlock extends AbstractMENetworkFurnaceBlock<MECrucibleBlockEntity> {
    public MECrucibleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isValidPinInput(Level level, ItemStack stack) {
        return level instanceof ServerLevel serverLevel
                && serverLevel.recipeAccess()
                        .getRecipeFor(ModRecipes.CRUCIBLE_MELTING.get(), new SingleRecipeInput(stack), serverLevel)
                        .isPresent();
    }

    @Override
    protected MenuType<?> menuType() {
        return ModMenus.ME_CRUCIBLE.get();
    }
}
