package com.tcn.cosmosindustry.production.core.block;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.text.WordUtils;

import com.tcn.cosmosindustry.processing.core.block.ItemBlockMachine;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper.Value;
import com.tcn.cosmoslibrary.common.nbt.CosmosNBTHelper.Const;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectFluidTankCustom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

@SuppressWarnings("deprecation")
public class ItemBlockPeltier extends ItemBlockMachine {

	public ItemBlockPeltier(Block block, Properties properties, String info, String shift_desc_one, String shift_desc_two) {
		super(block, properties, info, shift_desc_one, shift_desc_two);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);
	}
	
	@Override
	public void addCtrlInfo(ItemStack stackIn, List<Component> tooltip, CompoundTag stackTag) {
		super.addCtrlInfo(stackIn, tooltip, stackTag);
		
		if (stackTag.contains("fluidTankCold")) {
			CompoundTag fluidTag = stackTag.getCompound("fluidTankCold");
			CompoundTag resourceTag = fluidTag.getCompound(ObjectFluidTankCustom.NBT_FLUID_KEY);
			
			String name = WordUtils.capitalize(resourceTag.getString(Const.NBT_PATH_KEY));
			String[] splitName = name.split("_");
			String newName = "";
			
			for (int i = 0; i < splitName.length; i++) {
				newName = newName + (i == 0 ? "": " ") + WordUtils.capitalize(splitName[i].replace("_", " "));
			}
			
			int volume = fluidTag.getInt(ObjectFluidTankCustom.NBT_FLUID_VOLUME_KEY);
			int capacity = fluidTag.getInt(ObjectFluidTankCustom.NBT_FLUID_CAPACITY_KEY);
			int fillLevel = fluidTag.getInt(ObjectFluidTankCustom.NBT_FILL_LEVEL_KEY);
			
			tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.tooltip.block_item.fluid_stored")
				.append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.CYAN + newName + Value.LIGHT_GRAY + " (" + Value.CYAN + volume + Value.LIGHT_GRAY + " / " + Value.CYAN + capacity)
				.append(ComponentHelper.style2(ComponentColour.LIGHT_GRAY, "cosmoslibrary.tooltip.block_item.fluid_stored_suff", ") ]")))
			);
			
			tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.tooltip.block_item.fluid_fill_level")
				.append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.CYAN + fillLevel))
				.append(ComponentHelper.style(ComponentColour.LIGHT_GRAY, " ]"))
			);
		}

		if (stackTag.contains("fluidTankHot")) {
			CompoundTag fluidTag = stackTag.getCompound("fluidTankHot");
			CompoundTag resourceTag = fluidTag.getCompound(ObjectFluidTankCustom.NBT_FLUID_KEY);
			
			String name = WordUtils.capitalize(resourceTag.getString(Const.NBT_PATH_KEY));
			String[] splitName = name.split("_");
			String newName = "";
			
			for (int i = 0; i < splitName.length; i++) {
				newName = newName + (i == 0 ? "": " ") + WordUtils.capitalize(splitName[i].replace("_", " "));
			}
			
			int volume = fluidTag.getInt(ObjectFluidTankCustom.NBT_FLUID_VOLUME_KEY);
			int capacity = fluidTag.getInt(ObjectFluidTankCustom.NBT_FLUID_CAPACITY_KEY);
			int fillLevel = fluidTag.getInt(ObjectFluidTankCustom.NBT_FILL_LEVEL_KEY);
			
			tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.tooltip.block_item.fluid_stored")
				.append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.ORANGE + newName + Value.LIGHT_GRAY + " (" + Value.ORANGE + volume + Value.LIGHT_GRAY + " / " + Value.ORANGE + capacity)
				.append(ComponentHelper.style2(ComponentColour.LIGHT_GRAY, "cosmoslibrary.tooltip.block_item.fluid_stored_suff", ") ]")))
			);
			
			tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.tooltip.block_item.fluid_fill_level")
				.append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.ORANGE + fillLevel))
				.append(ComponentHelper.style(ComponentColour.LIGHT_GRAY, " ]"))
			);
		}
	}

	public double getScaledFluid(ItemStack stackIn, boolean hot, int scaleIn) {
		return this.getTankCapacity(stackIn, hot) > 0 ? (double) this.getFluidAmount(stackIn, hot) * scaleIn / (double) this.getTankCapacity(stackIn, hot) : 0;
	}

	public int getTanks(ItemStack stackIn) {
		return 1;
	}

	public FluidStack getFluidInTank(ItemStack stackIn, boolean hot) {
		return this.getFluidTank(stackIn, hot) != null ? this.getFluidTank(stackIn, hot).getFluidTank().getFluidInTank(0) : FluidStack.EMPTY;
	}

	public int getTankCapacity(ItemStack stackIn, boolean hot) {
		return this.getFluidTank(stackIn, hot) != null ? this.getFluidTank(stackIn, hot).getFluidTank().getCapacity() : 0;
	}

	public int getFluidAmount(ItemStack stackIn, boolean hot) {
		return this.getFluidInTank(stackIn, hot).getAmount();
	}

	public FluidTank getFluidTankTank(ItemStack stackIn, boolean hot) {
		ObjectFluidTankCustom customTank = this.getFluidTank(stackIn, hot);
		
		if (customTank != null) {
			return customTank.getFluidTank();
		}
		
		return null;
	}
	
	public @Nullable ObjectFluidTankCustom getFluidTank(ItemStack stackIn, boolean hot) {
		if (stackIn.has(DataComponents.BLOCK_ENTITY_DATA)) {
			CompoundTag stackTag = stackIn.get(DataComponents.BLOCK_ENTITY_DATA).copyTag();
			
			if (!hot) {
				if (stackTag.contains("fluidTankCold")) {
					return ObjectFluidTankCustom.readFromNBT(stackTag.getCompound("fluidTankCold"));
				} else {
					return (ObjectFluidTankCustom)null;
				}
			} else {
				if (stackTag.contains("fluidTankHot")) {
					return ObjectFluidTankCustom.readFromNBT(stackTag.getCompound("fluidTankHot"));
				} else {
					return (ObjectFluidTankCustom)null;
				}
			}
			
		}
		
		return (ObjectFluidTankCustom)null;
	}
}