package dev.excal1bur.appliedsmelting.compat.jei;

import net.minecraft.network.chat.Component;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;

import dev.excal1bur.appliedsmelting.core.ModBlocks;
import dev.excal1bur.appliedsmelting.recipe.CrucibleMeltingRecipe;

/** Shows ME Crucible recipes (item -> molten metal) in JEI. */
public final class CrucibleMeltingCategory implements IRecipeCategory<CrucibleMeltingRecipe> {
    public static final IRecipeType<CrucibleMeltingRecipe> TYPE =
            IRecipeType.create(net.minecraft.resources.Identifier.fromNamespaceAndPath(
                    "appliedsmelting", "crucible_melting"), CrucibleMeltingRecipe.class);

    private final IDrawable icon;

    public CrucibleMeltingCategory(IGuiHelper guiHelper) {
        icon = guiHelper.createDrawableItemStack(ModBlocks.ME_CRUCIBLE_ITEM.get().getDefaultInstance());
    }

    @Override
    public IRecipeType<CrucibleMeltingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.appliedsmelting.me_crucible");
    }

    @Override
    public int getWidth() {
        return 100;
    }

    @Override
    public int getHeight() {
        return 30;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrucibleMeltingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 5)
                .setStandardSlotBackground()
                .add(recipe.ingredient());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 75, 5)
                .setFluidRenderer(recipe.amount(), false, 16, 16)
                .addFluidStack(
                        recipe.resolveFluid(), recipe.amount(), net.minecraft.core.component.DataComponentPatch.EMPTY);
    }
}
