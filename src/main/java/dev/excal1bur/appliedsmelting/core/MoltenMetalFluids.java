package dev.excal1bur.appliedsmelting.core;

import java.util.Locale;
import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;

/**
 * Resolves a molten-metal fluid for the Crucible: prefers Productive Metalworks's own
 * {@code productivemetalworks:molten_<metal>} fluid (runtime lookup, no compile dependency),
 * falling back to this mod's own fluid when PMW isn't installed or doesn't have that metal.
 */
public final class MoltenMetalFluids {
    private static final String PMW_MOD_ID = "productivemetalworks";

    public static Fluid iron() {
        return resolve("iron", ModFluids.MOLTEN_IRON);
    }

    public static Fluid copper() {
        return resolve("copper", ModFluids.MOLTEN_COPPER);
    }

    public static Fluid gold() {
        return resolve("gold", ModFluids.MOLTEN_GOLD);
    }

    private static Fluid resolve(String metal, Supplier<Fluid> fallback) {
        return BuiltInRegistries.FLUID
                .get(Identifier.fromNamespaceAndPath(PMW_MOD_ID, "molten_" + metal.toLowerCase(Locale.ROOT)))
                .map(Holder::value)
                .orElseGet(fallback);
    }

    private MoltenMetalFluids() {
    }
}
