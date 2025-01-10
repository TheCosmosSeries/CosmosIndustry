package com.tcn.cosmosindustry.production.core.block;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.processing.core.block.ItemBlockMachine;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectFluidTankCustom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class ItemBlockLiquidFuel extends ItemBlockMachine {

	public ItemBlockLiquidFuel(Block block, Properties properties, String description, String shift_desc_one, String shift_desc_two) {
		super(block, properties, description, shift_desc_one, shift_desc_two);
	}
	
	public double getScaledFluid(ItemStack stackIn, int scaleIn) {
		return this.getTankCapacity(stackIn, 0) > 0 ? (double) this.getFluidAmount(stackIn) * scaleIn / (double) this.getTankCapacity(stackIn, 0) : 0;
	}

	public int getTanks(ItemStack stackIn) {
		return 1;
	}

	public FluidStack getFluidInTank(ItemStack stackIn, int tank) {
		return this.getFluidTank(stackIn) != null ? this.getFluidTank(stackIn).getFluidTank().getFluidInTank(0) : FluidStack.EMPTY;
	}

	public int getTankCapacity(ItemStack stackIn, int tank) {
		return this.getFluidTank(stackIn) != null ? this.getFluidTank(stackIn).getFluidTank().getCapacity() : 0;
	}

	public int getFluidAmount(ItemStack stackIn) {
		return this.getFluidInTank(stackIn, 0).getAmount();
	}

	public FluidTank getFluidTankTank(ItemStack stackIn) {
		ObjectFluidTankCustom customTank = this.getFluidTank(stackIn);
		
		if (customTank != null) {
			return customTank.getFluidTank();
		}
		
		return null;
	}
	
	public @Nullable ObjectFluidTankCustom getFluidTank(ItemStack stackIn) {
		if (stackIn.has(DataComponents.BLOCK_ENTITY_DATA)) {
			CompoundTag stackTag = stackIn.get(DataComponents.BLOCK_ENTITY_DATA).copyTag();
			
			if (stackTag.contains("fluidTank")) {
				return ObjectFluidTankCustom.readFromNBT(stackTag.getCompound("fluidTank"));
			} else {
				return (ObjectFluidTankCustom)null;
			}
		}
		
		return (ObjectFluidTankCustom)null;
	}
}