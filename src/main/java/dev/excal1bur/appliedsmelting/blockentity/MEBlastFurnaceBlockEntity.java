package dev.excal1bur.appliedsmelting.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import dev.excal1bur.appliedsmelting.core.AppliedSmeltingConfig;
import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;
import dev.excal1bur.appliedsmelting.service.BlastingService;

public final class MEBlastFurnaceBlockEntity extends AbstractCookingFurnaceBlockEntity {
    public MEBlastFurnaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, AppliedSmeltingConfig.BLAST_FURNACE.upgradeSlots().get());
        updateIdlePowerUsage();
    }

    @Override
    protected RecipeType<? extends AbstractCookingRecipe> recipeType() {
        return RecipeType.BLASTING;
    }

    @Override
    protected Class<? extends AbstractFurnaceNetworkService> serviceClass() {
        return BlastingService.class;
    }

    @Override
    protected double baseSpeedMultiplier() {
        return AppliedSmeltingConfig.BLAST_FURNACE.baseSpeedMultiplier().get();
    }

    @Override
    protected double accelerationCap() {
        return AppliedSmeltingConfig.BLAST_FURNACE.accelerationCap().get();
    }

    @Override
    protected double idleDrawMultiplier() {
        return AppliedSmeltingConfig.BLAST_FURNACE.idleDrawMultiplier().get();
    }

    @Override
    protected double aeFuelDrawMultiplier() {
        return AppliedSmeltingConfig.BLAST_FURNACE.aeFuelDrawMultiplier().get();
    }

    @Override
    protected double lavaFuelDrawMultiplier() {
        return AppliedSmeltingConfig.BLAST_FURNACE.lavaFuelDrawMultiplier().get();
    }

    @Override
    protected double fuelEfficiencyMultiplier() {
        return AppliedSmeltingConfig.BLAST_FURNACE.fuelEfficiencyMultiplier().get();
    }

    @Override
    public int baseQueueCapacity() {
        return AppliedSmeltingConfig.BLAST_FURNACE.baseQueueCapacity().get();
    }

    @Override
    public int capacityCardCap() {
        return AppliedSmeltingConfig.BLAST_FURNACE.capacityCardCap().get();
    }
}
