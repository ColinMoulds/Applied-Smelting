package dev.excal1bur.appliedsmelting.core;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import dev.excal1bur.appliedsmelting.AppliedSmelting;

/**
 * Fallback molten-metal fluids used by the ME Crucible when Productive Metalworks isn't installed.
 * Network-only: no bucket or placeable block, since the Crucible reads/writes ME fluid storage directly
 * and never places these in the world.
 */
public final class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, AppliedSmelting.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, AppliedSmelting.MOD_ID);

    public static final Supplier<FluidType> MOLTEN_IRON_TYPE = fluidType("molten_iron");
    public static final Supplier<Fluid> MOLTEN_IRON = FLUIDS.register("molten_iron", () -> new BaseFlowingFluid.Source(
            new BaseFlowingFluid.Properties(MOLTEN_IRON_TYPE, ModFluids.MOLTEN_IRON, ModFluids.MOLTEN_IRON_FLOWING)));
    public static final Supplier<Fluid> MOLTEN_IRON_FLOWING = FLUIDS.register("molten_iron_flowing", () -> new BaseFlowingFluid.Flowing(
            new BaseFlowingFluid.Properties(MOLTEN_IRON_TYPE, ModFluids.MOLTEN_IRON, ModFluids.MOLTEN_IRON_FLOWING)));

    public static final Supplier<FluidType> MOLTEN_COPPER_TYPE = fluidType("molten_copper");
    public static final Supplier<Fluid> MOLTEN_COPPER = FLUIDS.register("molten_copper", () -> new BaseFlowingFluid.Source(
            new BaseFlowingFluid.Properties(MOLTEN_COPPER_TYPE, ModFluids.MOLTEN_COPPER, ModFluids.MOLTEN_COPPER_FLOWING)));
    public static final Supplier<Fluid> MOLTEN_COPPER_FLOWING = FLUIDS.register("molten_copper_flowing", () -> new BaseFlowingFluid.Flowing(
            new BaseFlowingFluid.Properties(MOLTEN_COPPER_TYPE, ModFluids.MOLTEN_COPPER, ModFluids.MOLTEN_COPPER_FLOWING)));

    public static final Supplier<FluidType> MOLTEN_GOLD_TYPE = fluidType("molten_gold");
    public static final Supplier<Fluid> MOLTEN_GOLD = FLUIDS.register("molten_gold", () -> new BaseFlowingFluid.Source(
            new BaseFlowingFluid.Properties(MOLTEN_GOLD_TYPE, ModFluids.MOLTEN_GOLD, ModFluids.MOLTEN_GOLD_FLOWING)));
    public static final Supplier<Fluid> MOLTEN_GOLD_FLOWING = FLUIDS.register("molten_gold_flowing", () -> new BaseFlowingFluid.Flowing(
            new BaseFlowingFluid.Properties(MOLTEN_GOLD_TYPE, ModFluids.MOLTEN_GOLD, ModFluids.MOLTEN_GOLD_FLOWING)));

    private static Supplier<FluidType> fluidType(String name) {
        return FLUID_TYPES.register(name, () -> new FluidType(FluidType.Properties.create()
                .descriptionId("fluid.appliedsmelting." + name)
                .lightLevel(15)
                .density(3500)
                .temperature(1300)
                .viscosity(6000)
                .canConvertToSource(false)));
    }

    private ModFluids() {
    }
}
