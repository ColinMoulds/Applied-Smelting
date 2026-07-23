package dev.excal1bur.appliedsmelting.blockentity;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.api.stacks.AEItemKey;

/** Base for machines that process a vanilla cooking-style recipe type (smelting/blasting/smoking). */
public abstract class AbstractCookingFurnaceBlockEntity extends AbstractMENetworkFurnaceBlockEntity {
    protected AbstractCookingFurnaceBlockEntity(
            BlockEntityType<?> type, BlockPos pos, BlockState state, int upgradeSlots) {
        super(type, pos, state, upgradeSlots);
    }

    /** The vanilla cooking recipe type this machine processes (smelting/blasting/smoking). */
    protected abstract RecipeType<? extends AbstractCookingRecipe> recipeType();

    @Override
    protected Optional<ResolvedRecipe> resolveRecipe(ServerLevel level, ItemStack input) {
        var recipeInput = new SingleRecipeInput(input);
        var recipe = level.recipeAccess().getRecipeFor(recipeType(), recipeInput, level);
        if (recipe.isEmpty()) {
            return Optional.empty();
        }
        var result = recipe.get().value().assemble(recipeInput);
        var resultKey = AEItemKey.of(result);
        if (resultKey == null) {
            return Optional.empty();
        }
        return Optional.of(new ResolvedRecipe(resultKey, result.getCount(), recipe.get().value().cookingTime()));
    }
}
