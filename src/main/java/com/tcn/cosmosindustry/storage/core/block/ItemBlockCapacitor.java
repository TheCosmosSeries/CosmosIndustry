package com.tcn.cosmosindustry.storage.core.block;

import com.tcn.cosmosindustry.processing.core.block.ItemBlockMachine;
import com.tcn.cosmosindustry.storage.core.blockentity.AbstractBlockEntityCapacitor;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.energy.interfaces.IEnergyStorageBulk;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

public class ItemBlockCapacitor extends ItemBlockMachine {
	
	private String regName;
	private EnumIndustryTier tier;
	private ComponentColour barColour;

	public ItemBlockCapacitor(Block block, Properties properties, String description, String shift_desc_one, String shift_desc_two, String regName, EnumIndustryTier tier, ComponentColour barColour) {
		super(block, properties, description, shift_desc_one, shift_desc_two);
		
		this.regName = regName;
		this.tier = tier;
		this.barColour = barColour;
	}

	@Override
	public boolean isBarVisible(ItemStack stackIn) {
		return stackIn.has(DataComponents.BLOCK_ENTITY_DATA) ? stackIn.get(DataComponents.BLOCK_ENTITY_DATA).copyTag().contains("energy") : false;
	}
	
	@Override
	public int getBarColor(ItemStack stackIn) {
		return this.barColour.dec();
	}
	
	@Override
	public int getBarWidth(ItemStack stackIn) {
		return Mth.clamp(Math.round((float) this.getScaledEnergy(stackIn, 13)), 0, 13);
	}
	
	public IEnergyStorageBulk getEnergyCapability(ItemStack stackIn) {
        return new IEnergyStorageBulk() {
        	
            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return ItemBlockCapacitor.this.extractEnergy(stackIn, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return ItemBlockCapacitor.this.getEnergyStored(stackIn);
            }

            @Override
            public int getMaxEnergyStored() {
                return ItemBlockCapacitor.this.getMaxEnergyStored(stackIn);
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return ItemBlockCapacitor.this.receiveEnergy(stackIn, maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return ItemBlockCapacitor.this.canReceive(stackIn);
            }

            @Override
            public boolean canExtract() {
                return ItemBlockCapacitor.this.canExtract(stackIn);
            }
        };
    }

	public double getScaledEnergy(ItemStack stackIn, int scaleIn) {
		return this.getMaxEnergyStored(stackIn) > 0 ? (double) this.getEnergyStored(stackIn) * scaleIn / (double) this.getMaxEnergyStored(stackIn) : 0;
	}

	public int receiveEnergy(ItemStack stackIn, int toReceive, boolean simulate) {
		int storedReceived = Math.min(this.getMaxEnergyStored(stackIn) - this.getEnergyStored(stackIn), toReceive);

		if (!simulate) {
			this.setEnergyStored(stackIn, this.getEnergyStored(stackIn) + storedReceived);
		}
		
		return storedReceived;
	}

	public int extractEnergy(ItemStack stackIn, int toExtract, boolean simulate) {
		int storedExtracted = Math.min(this.getEnergyStored(stackIn), toExtract);

		if (!simulate) {
			if (this.tier.notCreative()) {
				this.setEnergyStored(stackIn, this.getEnergyStored(stackIn) - storedExtracted);
			}
		}
		
		return storedExtracted;
	}
	
	public void setEnergyStored(ItemStack stackIn, int energyToSet) {
		CompoundTag stackTag = this.getStackTag(stackIn);
		
		if (stackTag != null) {
			stackTag.putInt("energy", energyToSet);
			stackIn.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(stackTag));
		}
	}

	public int getEnergyStored(ItemStack stackIn) {
		CompoundTag stackTag = this.getStackTag(stackIn);
		
		if (stackTag != null) {
			return stackTag.getInt("energy");
		}
		
		return 0;
	}

	public int getMaxEnergyStored(ItemStack stackIn) {
		CompoundTag stackTag = this.getStackTag(stackIn);
		
		if (stackTag != null) {
			return stackTag.getInt("maxEnergy");
		}
		
		return 0;
	}

	public boolean canExtract(ItemStack stackIn) {
		return this.getEnergyStored(stackIn) > 0;
	}

	public boolean canReceive(ItemStack stackIn) {
		return this.getEnergyStored(stackIn) < this.getMaxEnergyStored(stackIn);
	}
	
	public CompoundTag getStackTag(ItemStack stackIn) {
		if (stackIn.has(DataComponents.BLOCK_ENTITY_DATA)) {
			return stackIn.get(DataComponents.BLOCK_ENTITY_DATA).copyTag();
		} else {
			CompoundTag stackTag = this.getStackTag();
			
			stackIn.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(stackTag));
			return stackTag;
		}
	}

	public CompoundTag getStackTag() {
		return AbstractBlockEntityCapacitor.getNBT(regName);
	}
}