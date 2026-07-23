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

import dev.excal1bur.appliedsmelting.block.MECrucibleBlock;
import dev.excal1bur.appliedsmelting.core.ModRecipes;
import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;
import dev.excal1bur.appliedsmelting.service.CrucibleService;
import dev.excal1bur.appliedsmelting.service.CrucibleTier;

/** Ore/raw-metal -> molten-metal-fluid machine. Output goes to ME fluid storage, not item storage. */
public final class MECrucibleBlockEntity extends AbstractMENetworkFurnaceBlockEntity {
    private final CrucibleTier tier;

    public MECrucibleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, tierOf(state).upgradeSlots());
        tier = tierOf(state);
        updateIdlePowerUsage();
    }

    private static CrucibleTier tierOf(BlockState state) {
        return state.getBlock() instanceof MECrucibleBlock block ? block.getTier() : CrucibleTier.DEFAULT;
    }

    public CrucibleTier getTier() {
        return tier;
    }

    @Override
    protected Class<? extends AbstractFurnaceNetworkService> serviceClass() {
        return CrucibleService.class;
    }

    @Override
    public float getGlowIntensity() {
        return switch (tier) {
            case DEFAULT, MK1 -> 1.0F;
            case MK2 -> 1.15F;
            case MK3 -> 1.35F;
        };
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
        return tier.baseSpeedMultiplier();
    }

    @Override
    protected double accelerationCap() {
        return tier.accelerationCap();
    }

    @Override
    protected double idleDrawMultiplier() {
        return tier.idleDrawMultiplier();
    }

    @Override
    protected double aeFuelDrawMultiplier() {
        return tier.aeFuelDrawMultiplier();
    }

    @Override
    protected double lavaFuelDrawMultiplier() {
        return tier.lavaFuelDrawMultiplier();
    }

    @Override
    protected double fuelEfficiencyMultiplier() {
        return tier.fuelEfficiencyMultiplier();
    }

    @Override
    public int baseQueueCapacity() {
        return tier.baseQueueCapacity();
    }

    @Override
    public int capacityCardCap() {
        return tier.capacityCardCap();
    }
}
