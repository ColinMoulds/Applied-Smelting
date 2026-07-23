package dev.excal1bur.appliedsmelting.core;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import dev.excal1bur.appliedsmelting.AppliedSmelting;
import dev.excal1bur.appliedsmelting.recipe.CrucibleMeltingRecipe;

public final class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTER =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, AppliedSmelting.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, AppliedSmelting.MOD_ID);

    public static final Supplier<RecipeSerializer<CrucibleMeltingRecipe>> CRUCIBLE_MELTING_SERIALIZER =
            REGISTER.register("crucible_melting", () -> CrucibleMeltingRecipe.SERIALIZER);
    public static final Supplier<RecipeType<CrucibleMeltingRecipe>> CRUCIBLE_MELTING =
            TYPES.register("crucible_melting", () -> new RecipeType<CrucibleMeltingRecipe>() {
                @Override
                public String toString() {
                    return "appliedsmelting:crucible_melting";
                }
            });

    private ModRecipes() {
    }
}
