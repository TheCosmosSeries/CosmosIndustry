package com.tcn.cosmosindustry.core.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public class SolidifierRecipeInput implements RecipeInput {

	private FluidStack fluidStack;
	
	public SolidifierRecipeInput(FluidStack fluidStackIn) {
		this.fluidStack = fluidStackIn;
	}
	
	@Override
	public ItemStack getItem(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public int size() {
		return 1;
	}

	public FluidStack getFluidStack() {
		return this.fluidStack;
	}

	@Override
    public boolean isEmpty() {
        return this.fluidStack.isEmpty();
    }
}