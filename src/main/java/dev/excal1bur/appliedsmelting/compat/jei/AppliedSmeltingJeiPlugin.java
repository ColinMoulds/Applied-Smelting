package dev.excal1bur.appliedsmelting.compat.jei;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import dev.excal1bur.appliedsmelting.AppliedSmelting;
import dev.excal1bur.appliedsmelting.core.ModBlocks;
import dev.excal1bur.appliedsmelting.recipe.CrucibleMeltingRecipe;

/**
 * Vanilla smelting/blasting/smoking recipes (including the Sky Stone ones) already show up under
 * JEI's own built-in categories - this only needs to add the Crucible's custom recipe type and
 * register every machine as a catalyst so clicking one in JEI jumps to its recipes.
 */
@JeiPlugin
public final class AppliedSmeltingJeiPlugin implements IModPlugin {
    private static final Identifier UID = AppliedSmelting.id("jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CrucibleMeltingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        List<CrucibleMeltingRecipe> recipes = level.registryAccess()
                .lookupOrThrow(Registries.RECIPE)
                .listElements()
                .map(Holder.Reference::value)
                .filter(CrucibleMeltingRecipe.class::isInstance)
                .map(CrucibleMeltingRecipe.class::cast)
                .toList();
        registration.addRecipes(CrucibleMeltingCategory.TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.ME_SMELTER_ITEM.get().getDefaultInstance(), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(ModBlocks.ME_SMELTER_MK1_ITEM.get().getDefaultInstance(), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(ModBlocks.ME_SMELTER_MK2_ITEM.get().getDefaultInstance(), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(ModBlocks.ME_SMELTER_MK3_ITEM.get().getDefaultInstance(), RecipeTypes.SMELTING);

        registration.addRecipeCatalyst(ModBlocks.ME_BLAST_FURNACE_ITEM.get().getDefaultInstance(), RecipeTypes.BLASTING);
        registration.addRecipeCatalyst(ModBlocks.ME_BLAST_FURNACE_MK1_ITEM.get().getDefaultInstance(), RecipeTypes.BLASTING);
        registration.addRecipeCatalyst(ModBlocks.ME_BLAST_FURNACE_MK2_ITEM.get().getDefaultInstance(), RecipeTypes.BLASTING);
        registration.addRecipeCatalyst(ModBlocks.ME_BLAST_FURNACE_MK3_ITEM.get().getDefaultInstance(), RecipeTypes.BLASTING);

        registration.addRecipeCatalyst(ModBlocks.ME_SMOKER_ITEM.get().getDefaultInstance(), RecipeTypes.SMOKING);
        registration.addRecipeCatalyst(ModBlocks.ME_SMOKER_MK1_ITEM.get().getDefaultInstance(), RecipeTypes.SMOKING);
        registration.addRecipeCatalyst(ModBlocks.ME_SMOKER_MK2_ITEM.get().getDefaultInstance(), RecipeTypes.SMOKING);
        registration.addRecipeCatalyst(ModBlocks.ME_SMOKER_MK3_ITEM.get().getDefaultInstance(), RecipeTypes.SMOKING);

        registration.addRecipeCatalyst(ModBlocks.ME_CRUCIBLE_ITEM.get().getDefaultInstance(), CrucibleMeltingCategory.TYPE);
        registration.addRecipeCatalyst(ModBlocks.ME_CRUCIBLE_MK1_ITEM.get().getDefaultInstance(), CrucibleMeltingCategory.TYPE);
        registration.addRecipeCatalyst(ModBlocks.ME_CRUCIBLE_MK2_ITEM.get().getDefaultInstance(), CrucibleMeltingCategory.TYPE);
        registration.addRecipeCatalyst(ModBlocks.ME_CRUCIBLE_MK3_ITEM.get().getDefaultInstance(), CrucibleMeltingCategory.TYPE);
    }
}
