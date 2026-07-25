package dev.excal1bur.appliedsmelting.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import dev.excal1bur.appliedsmelting.block.MESmokerBlock;
import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;
import dev.excal1bur.appliedsmelting.service.SmokerTier;
import dev.excal1bur.appliedsmelting.service.SmokingService;

public final class MESmokerBlockEntity extends AbstractCookingFurnaceBlockEntity {
    private final SmokerTier tier;

    public MESmokerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, tierOf(state).upgradeSlots());
        tier = tierOf(state);
        updateIdlePowerUsage();
    }

    private static SmokerTier tierOf(BlockState state) {
        return state.getBlock() instanceof MESmokerBlock block ? block.getTier() : SmokerTier.DEFAULT;
    }

    public SmokerTier getTier() {
        return tier;
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
