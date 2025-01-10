package com.tcn.cosmosindustry.storage.core.item;

import java.util.List;

import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyItem;
import com.tcn.cosmoslibrary.energy.item.CosmosEnergyStorageItem;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemEnergyCell extends CosmosEnergyStorageItem {

	private EnumIndustryTier tier;
	
	public ItemEnergyCell(Item.Properties properties, CosmosEnergyItem.Properties energyProperties, EnumIndustryTier tierIn) {
		super(properties, energyProperties);
		
		this.tier = tierIn;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		if (!ComponentHelper.isShiftKeyDown(Minecraft.getInstance())) {
//			tooltip.add(ComponentHelper.getTooltipInfo("dimensionalpocketsii.info.energy_cell_info"));
			
			if (ComponentHelper.displayShiftForDetail) {
				tooltip.add(ComponentHelper.shiftForMoreDetails());
			}
		} else {
//			tooltip.add(ComponentHelper.getTooltipOne("dimensionalpocketsii.info.energy_cell_shift_one"));
//			tooltip.add(ComponentHelper.getTooltipThree("dimensionalpocketsii.info.energy_cell_shift_two"));
//			tooltip.add(ComponentHelper.getTooltipFour("dimensionalpocketsii.info.energy_cell_shift_three"));
//			tooltip.add(ComponentHelper.getTooltipLimit("dimensionalpocketsii.info.energy_cell_limitation"));
			
			tooltip.add(ComponentHelper.shiftForLessDetails());
		}
		
		super.appendHoverText(stack, context, tooltip, flagIn);
	}
	
	@Override
	public void onCraftedBy(ItemStack stackIn, Level levelIn, Player playerIn) {
		if (this.tier.creative()) {
			this.setEnergy(stackIn, this.getMaxEnergyStored(stackIn));
		}
	}
	
	@Override
	public int getEnergy(ItemStack stackIn) {
		if (this.tier.creative()) {
			if (super.getEnergy(stackIn) < this.getMaxEnergyStored(stackIn)) {
				this.setEnergy(stackIn, this.getMaxEnergyStored(stackIn));
			}
		}
		return super.getEnergy(stackIn);
	}
	
	@Override
	public int extractEnergy(ItemStack stackIn, int energy, boolean simulate) {
		if (this.canExtractEnergy(stackIn)) {
			if (this.doesExtract(stackIn)) {
				int storedExtracted = Math.min(this.getEnergy(stackIn), Math.min(this.getMaxExtract(stackIn), energy));
				
				if (!simulate) {
					if (this.tier.notCreative()) {
						this.setEnergy(stackIn, this.getEnergy(stackIn) - storedExtracted);
					}
				}
				
				return storedExtracted;
			}
		}
		
		return 0;
	}
	
}