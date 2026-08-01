package dev.excal1bur.appliedsmelting.block;

import dev.excal1bur.appliedsmelting.blockentity.MECrucibleBlockEntity;
import dev.excal1bur.appliedsmelting.core.ModBlocks;
import dev.excal1bur.appliedsmelting.core.ModMenus;
import dev.excal1bur.appliedsmelting.core.ModRecipes;
import dev.excal1bur.appliedsmelting.service.AbstractFurnaceNetworkService;
import dev.excal1bur.appliedsmelting.service.CrucibleService;
import dev.excal1bur.appliedsmelting.service.CrucibleTier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public final class MECrucibleBlock extends AbstractMENetworkFurnaceBlock<MECrucibleBlockEntity> {
    private final CrucibleTier tier;

    public MECrucibleBlock(Properties properties, CrucibleTier tier) {
        super(properties);
        this.tier = tier;
    }

    public CrucibleTier getTier() {
        return tier;
    }

    @Override
    protected boolean isValidPinInput(Level level, ItemStack stack) {
        return level instanceof ServerLevel serverLevel
                && serverLevel
                        .recipeAccess()
                        .getRecipeFor(ModRecipes.CRUCIBLE_MELTING.get(), new SingleRecipeInput(stack), serverLevel)
                        .isPresent();
    }

    @Override
    protected MenuType<?> menuType() {
        return ModMenus.ME_CRUCIBLE.get();
    }

    @Override
    protected int tierLevel() {
        return tier.ordinal();
    }

    @Override
    protected Block blockForTierLevel(int tierLevel) {
        return ModBlocks.blockForCrucibleTier(CrucibleTier.values()[tierLevel]).get();
    }

    @Override
    protected Class<? extends AbstractFurnaceNetworkService> serviceClass() {
        return CrucibleService.class;
    }
}
