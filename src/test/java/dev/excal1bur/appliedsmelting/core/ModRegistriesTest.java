package dev.excal1bur.appliedsmelting.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.server.MinecraftServer;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * These run against a real (test) MinecraftServer via NeoForge's test framework, unlike
 * ProcessingMathTest - they catch registration/recipe-loading mistakes that plain JUnit can't see
 * because DeferredRegister entries and RecipeManager contents don't exist outside a booted game.
 */
@ExtendWith(EphemeralTestServerProvider.class)
class ModRegistriesTest {
    @Test
    void meSmelterBlockEntityTypeSupportsAllTiers(MinecraftServer server) {
        var type = ModBlockEntities.ME_SMELTER.get();
        assertNotNull(type);
        assertTrue(type.isValid(ModBlocks.ME_SMELTER.get().defaultBlockState()));
        assertTrue(type.isValid(ModBlocks.ME_SMELTER_MK1.get().defaultBlockState()));
        assertTrue(type.isValid(ModBlocks.ME_SMELTER_MK2.get().defaultBlockState()));
        assertTrue(type.isValid(ModBlocks.ME_SMELTER_MK3.get().defaultBlockState()));
    }

    @Test
    void crucibleMeltingRecipesLoadIntoTheRecipeManager(MinecraftServer server) {
        assertNotNull(ModRecipes.CRUCIBLE_MELTING.get());
        assertNotNull(ModRecipes.CRUCIBLE_MELTING_SERIALIZER.get());

        var recipes = server.getRecipeManager().getRecipes().stream()
                .filter(recipe -> recipe.value().getType() == ModRecipes.CRUCIBLE_MELTING.get())
                .toList();
        assertFalse(recipes.isEmpty());
    }
}
