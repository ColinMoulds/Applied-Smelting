package dev.excal1bur.appliedsmelting.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import dev.excal1bur.appliedsmelting.block.MEBlastFurnaceBlock;
import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;
import dev.excal1bur.appliedsmelting.service.BlastFurnaceTier;
import dev.excal1bur.appliedsmelting.service.BlastingService;

public final class MEBlastFurnaceBlockEntity extends AbstractCookingFurnaceBlockEntity {
    private final BlastFurnaceTier tier;

    public MEBlastFurnaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, tierOf(state).upgradeSlots());
        tier = tierOf(state);
        updateIdlePowerUsage();
    }

    private static BlastFurnaceTier tierOf(BlockState state) {
        return state.getBlock() instanceof MEBlastFurnaceBlock block ? block.getTier() : BlastFurnaceTier.DEFAULT;
    }

    public BlastFurnaceTier getTier() {
        return tier;
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
    public float getGlowIntensity() {
        return switch (tier) {
            case DEFAULT, MK1 -> 1.0F;
            case MK2 -> 1.15F;
            case MK3 -> 1.35F;
        };
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
