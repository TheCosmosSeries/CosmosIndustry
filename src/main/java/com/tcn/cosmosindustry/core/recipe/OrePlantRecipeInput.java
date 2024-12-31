package com.tcn.cosmosindustry.core.recipe;

import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityOrePlant;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public class OrePlantRecipeInput implements RecipeInput {

	private ItemStack input;
	private FluidStack fluidStack;
	private BlockEntityOrePlant.PlantMode mode;
	
	public OrePlantRecipeInput(ItemStack inputIn, FluidStack fluidStackIn, BlockEntityOrePlant.PlantMode modeIn) {
		this.input = inputIn;
		this.fluidStack = fluidStackIn;
		this.mode = modeIn;
	}
		

	@Override
	public ItemStack getItem(int index) {
		return input;
	}

	@Override
	public int size() {
		return 1;
	}

	public FluidStack getFluidStack() {
		return this.fluidStack;
	}
	
	public BlockEntityOrePlant.PlantMode getMode(){
		return this.mode;
	}
}