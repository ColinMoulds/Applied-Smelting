package dev.excal1bur.appliedsmelting.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import dev.excal1bur.appliedsmelting.core.ModRecipes;
import dev.excal1bur.appliedsmelting.core.MoltenMetalFluids;

/** Ore/raw-metal-item -> molten-metal-fluid recipe, processed by the ME Crucible. Never assemble()s an ItemStack. */
public final class CrucibleMeltingRecipe implements Recipe<SingleRecipeInput> {
    public static final MapCodec<CrucibleMeltingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(r -> r.ingredient),
                    Codec.STRING.fieldOf("metal").forGetter(r -> r.metal),
                    Codec.INT.fieldOf("amount").forGetter(r -> r.amount),
                    Codec.INT.optionalFieldOf("cookingtime", 200).forGetter(r -> r.cookingTime))
            .apply(instance, CrucibleMeltingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleMeltingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
            ByteBufCodecs.STRING_UTF8, r -> r.metal,
            ByteBufCodecs.VAR_INT, r -> r.amount,
            ByteBufCodecs.VAR_INT, r -> r.cookingTime,
            CrucibleMeltingRecipe::new);

    public static final RecipeSerializer<CrucibleMeltingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final Ingredient ingredient;
    private final String metal;
    private final int amount;
    private final int cookingTime;

    public CrucibleMeltingRecipe(Ingredient ingredient, String metal, int amount, int cookingTime) {
        this.ingredient = ingredient;
        this.metal = metal;
        this.amount = amount;
        this.cookingTime = cookingTime;
    }

    public int amount() {
        return amount;
    }

    public int cookingTime() {
        return cookingTime;
    }

    /** Resolves the actual output fluid at call time (Productive Metalworks first, our own fallback otherwise). */
    public Fluid resolveFluid() {
        return switch (metal) {
            case "iron" -> MoltenMetalFluids.iron();
            case "copper" -> MoltenMetalFluids.copper();
            case "gold" -> MoltenMetalFluids.gold();
            default -> Fluids.EMPTY;
        };
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
        return ModRecipes.CRUCIBLE_MELTING.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(ingredient);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.FURNACE_MISC;
    }
}
