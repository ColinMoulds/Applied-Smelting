package dev.excal1bur.appliedsmelting.service;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;

/** Which machine-type queue the terminal is currently showing. */
public enum FurnaceType {
    SMELTING(SmeltingService.class, RecipeType.SMELTING, RecipePropertySet.FURNACE_INPUT, "block.appliedsmelting.me_smelter"),
    BLASTING(BlastingService.class, RecipeType.BLASTING, RecipePropertySet.BLAST_FURNACE_INPUT, "block.appliedsmelting.me_blast_furnace"),
    SMOKING(SmokingService.class, RecipeType.SMOKING, RecipePropertySet.SMOKER_INPUT, "block.appliedsmelting.me_smoker"),
    // Not shown in the shared Terminal (fluid output doesn't fit its item-queue UI, see MECrucibleBlockEntity) -
    // the recipeType()/recipePropertySet() values here are unused placeholders, kept only so this enum constant
    // is valid for the generic per-type plumbing in AbstractFurnaceNetworkService.
    CRUCIBLE(CrucibleService.class, RecipeType.SMELTING, RecipePropertySet.FURNACE_INPUT, "block.appliedsmelting.me_crucible");

    private final Class<? extends AbstractFurnaceNetworkService> serviceClass;
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final ResourceKey<RecipePropertySet> recipePropertySet;
    private final String displayNameKey;

    FurnaceType(
            Class<? extends AbstractFurnaceNetworkService> serviceClass,
            RecipeType<? extends AbstractCookingRecipe> recipeType,
            ResourceKey<RecipePropertySet> recipePropertySet,
            String displayNameKey) {
        this.serviceClass = serviceClass;
        this.recipeType = recipeType;
        this.recipePropertySet = recipePropertySet;
        this.displayNameKey = displayNameKey;
    }

    public Class<? extends AbstractFurnaceNetworkService> serviceClass() {
        return serviceClass;
    }

    public RecipeType<? extends AbstractCookingRecipe> recipeType() {
        return recipeType;
    }

    public ResourceKey<RecipePropertySet> recipePropertySet() {
        return recipePropertySet;
    }

    public String displayNameKey() {
        return displayNameKey;
    }

    public String serializedName() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }

    public static FurnaceType fromSerializedName(String name) {
        for (var type : values()) {
            if (type.serializedName().equals(name)) {
                return type;
            }
        }
        return SMELTING;
    }
}
