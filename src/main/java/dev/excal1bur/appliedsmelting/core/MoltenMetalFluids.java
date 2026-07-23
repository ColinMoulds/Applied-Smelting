package dev.excal1bur.appliedsmelting.core;

import java.util.Locale;
import java.util.function.Supplier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/**
 * Resolves a molten-metal fluid for the Crucible via the common {@code c:molten_<metal>} fluid
 * tag, so any mod that tags its own molten-metal fluid that way (Productive Metalworks included)
 * is picked up automatically with no per-mod hardcoding. Falls back to this mod's own fluid
 * (also tagged the same way) when nothing else provides one.
 */
public final class MoltenMetalFluids {
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
        var fallbackFluid = fallback.get();
        var tag = FluidTags.create(Identifier.fromNamespaceAndPath("c", "molten_" + metal.toLowerCase(Locale.ROOT)));
        for (var holder : BuiltInRegistries.FLUID.getTagOrEmpty(tag)) {
            var fluid = holder.value();
            if (fluid != fallbackFluid && fluid != Fluids.EMPTY) {
                return fluid;
            }
        }
        return fallbackFluid;
    }

    private MoltenMetalFluids() {
    }
}
