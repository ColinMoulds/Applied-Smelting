package dev.excal1bur.appliedsmelting.blockentity;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import appeng.api.stacks.AEFluidKey;

import dev.excal1bur.appliedsmelting.core.AppliedSmeltingConfig;
import dev.excal1bur.appliedsmelting.core.ModRecipes;
import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;
import dev.excal1bur.appliedsmelting.service.CrucibleService;

/** Ore/raw-metal -> molten-metal-fluid machine. Output goes to ME fluid storage, not item storage. */
public final class MECrucibleBlockEntity extends AbstractMENetworkFurnaceBlockEntity {
    public MECrucibleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, AppliedSmeltingConfig.CRUCIBLE.upgradeSlots().get());
        updateIdlePowerUsage();
    }

    @Override
    protected Class<? extends AbstractFurnaceNetworkService> serviceClass() {
        return CrucibleService.class;
    }

    @Override
    protected Optional<ResolvedRecipe> resolveRecipe(ServerLevel level, ItemStack input) {
        var recipeInput = new SingleRecipeInput(input);
        var recipe = level.recipeAccess().getRecipeFor(ModRecipes.CRUCIBLE_MELTING.get(), recipeInput, level);
        if (recipe.isEmpty()) {
            return Optional.empty();
        }
        var value = recipe.get().value();
        var fluid = value.resolveFluid();
        if (fluid == Fluids.EMPTY) {
            return Optional.empty();
        }
        return Optional.of(new ResolvedRecipe(AEFluidKey.of(fluid), value.amount(), value.cookingTime()));
    }

    @Override
    protected double baseSpeedMultiplier() {
        return AppliedSmeltingConfig.CRUCIBLE.baseSpeedMultiplier().get();
    }

    @Override
    protected double accelerationCap() {
        return AppliedSmeltingConfig.CRUCIBLE.accelerationCap().get();
    }

    @Override
    protected double idleDrawMultiplier() {
        return AppliedSmeltingConfig.CRUCIBLE.idleDrawMultiplier().get();
    }

    @Override
    protected double aeFuelDrawMultiplier() {
        return AppliedSmeltingConfig.CRUCIBLE.aeFuelDrawMultiplier().get();
    }

    @Override
    protected double lavaFuelDrawMultiplier() {
        return AppliedSmeltingConfig.CRUCIBLE.lavaFuelDrawMultiplier().get();
    }

    @Override
    protected double fuelEfficiencyMultiplier() {
        return AppliedSmeltingConfig.CRUCIBLE.fuelEfficiencyMultiplier().get();
    }

    @Override
    public int baseQueueCapacity() {
        return AppliedSmeltingConfig.CRUCIBLE.baseQueueCapacity().get();
    }

    @Override
    public int capacityCardCap() {
        return AppliedSmeltingConfig.CRUCIBLE.capacityCardCap().get();
    }
}
