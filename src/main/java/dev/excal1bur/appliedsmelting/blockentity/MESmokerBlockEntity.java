package dev.excal1bur.appliedsmelting.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import dev.excal1bur.appliedsmelting.core.AppliedSmeltingConfig;
import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;
import dev.excal1bur.appliedsmelting.service.SmokingService;

public final class MESmokerBlockEntity extends AbstractCookingFurnaceBlockEntity {
    public MESmokerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, AppliedSmeltingConfig.SMOKER_DEFAULT.upgradeSlots().get());
        updateIdlePowerUsage();
    }

    @Override
    protected RecipeType<? extends AbstractCookingRecipe> recipeType() {
        return RecipeType.SMOKING;
    }

    @Override
    protected Class<? extends AbstractFurnaceNetworkService> serviceClass() {
        return SmokingService.class;
    }

    @Override
    protected double baseSpeedMultiplier() {
        return AppliedSmeltingConfig.SMOKER_DEFAULT.baseSpeedMultiplier().get();
    }

    @Override
    protected double accelerationCap() {
        return AppliedSmeltingConfig.SMOKER_DEFAULT.accelerationCap().get();
    }

    @Override
    protected double idleDrawMultiplier() {
        return AppliedSmeltingConfig.SMOKER_DEFAULT.idleDrawMultiplier().get();
    }

    @Override
    protected double aeFuelDrawMultiplier() {
        return AppliedSmeltingConfig.SMOKER_DEFAULT.aeFuelDrawMultiplier().get();
    }

    @Override
    protected double lavaFuelDrawMultiplier() {
        return AppliedSmeltingConfig.SMOKER_DEFAULT.lavaFuelDrawMultiplier().get();
    }

    @Override
    protected double fuelEfficiencyMultiplier() {
        return AppliedSmeltingConfig.SMOKER_DEFAULT.fuelEfficiencyMultiplier().get();
    }

    @Override
    public int baseQueueCapacity() {
        return AppliedSmeltingConfig.SMOKER_DEFAULT.baseQueueCapacity().get();
    }

    @Override
    public int capacityCardCap() {
        return AppliedSmeltingConfig.SMOKER_DEFAULT.capacityCardCap().get();
    }
}
