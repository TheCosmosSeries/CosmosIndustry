package com.tcn.cosmosindustry.storage.core.block;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.apache.commons.lang3.text.WordUtils;

import com.tcn.cosmosindustry.storage.client.renderer.FluidTankBEWLR;
import com.tcn.cosmosindustry.storage.core.blockentity.BlockEntityFluidTank;
import com.tcn.cosmoslibrary.common.block.CosmosItemBlock;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper.Value;
import com.tcn.cosmoslibrary.common.nbt.CosmosNBTHelper.Const;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectFluidTankCustom;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@SuppressWarnings("deprecation")
public class ItemBlockFluidTank extends CosmosItemBlock {

	private String regName;
	private EnumIndustryTier tier;
	private ComponentColour barColour;
	private int fluidCapacity;
	
	public ItemBlockFluidTank(Block block, Properties properties, String description, String shift_desc_one, String shift_desc_two, String regName, EnumIndustryTier tier, ComponentColour barColour, int fluidCapacity) {
		super(block, properties, description, shift_desc_one, shift_desc_two);
		
		this.regName = regName;
		this.barColour = barColour;
		this.fluidCapacity = fluidCapacity;
		
		this.tier = tier;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);
		
		if (stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
			CompoundTag stackTag = stack.get(DataComponents.BLOCK_ENTITY_DATA).copyTag();

			if (stackTag.contains("fluidTank")) {
				CompoundTag fluidTag = stackTag.getCompound("fluidTank");
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
		}
	}
	
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return FluidTankBEWLR.INSTANCE;
			}
		});
	}

	@Override
	public boolean isBarVisible(ItemStack stackIn) {
		return stackIn.has(DataComponents.BLOCK_ENTITY_DATA) ? stackIn.get(DataComponents.BLOCK_ENTITY_DATA).copyTag().contains("fluidTank") : false;
	}
	
	@Override
	public int getBarColor(ItemStack stackIn) {
		return this.barColour.dec();
	}
	
	@Override
	public int getBarWidth(ItemStack stackIn) {
		return Mth.clamp(Math.round((float) this.getScaledFluid(stackIn, 13)), 0, 13);
	}
	
	public IFluidHandlerItem getFluidCapability(ItemStack stackIn) {
		return new IFluidHandlerItem() {

			@Override
			public int getTanks() {
				return ItemBlockFluidTank.this.getTanks(stackIn);
			}

			@Override
			public FluidStack getFluidInTank(int tank) {
				return ItemBlockFluidTank.this.getFluidInTank(stackIn, tank);
			}

			@Override
			public int getTankCapacity(int tank) {
				return ItemBlockFluidTank.this.getTankCapacity(stackIn, tank);
			}

			@Override
			public boolean isFluidValid(int tank, FluidStack stack) {
				return ItemBlockFluidTank.this.isFluidValid(stackIn, tank, stack);
			}

			@Override
			public int fill(FluidStack resource, FluidAction action) {
				return ItemBlockFluidTank.this.fill(stackIn, resource, action);
			}

			@Override
			public FluidStack drain(FluidStack resource, FluidAction action) {
				return ItemBlockFluidTank.this.drain(stackIn, resource, action);
			}

			@Override
			public FluidStack drain(int maxDrain, FluidAction action) {
				return ItemBlockFluidTank.this.drain(stackIn, maxDrain, action);
			}

			@Override
			public ItemStack getContainer() {
				return stackIn;
			}
		};
	}


	public double getScaledFluid(ItemStack stackIn, int scaleIn) {
		return this.getTankCapacity(stackIn, 0) > 0 ? (double) this.getFluidAmount(stackIn) * scaleIn / (double) this.getTankCapacity(stackIn, 0) : 0;
	}

	public int getTanks(ItemStack stackIn) {
		return 1;
	}

	public FluidStack getFluidInTank(ItemStack stackIn, int tank) {
		return this.getFluidTank(stackIn, false) != null ? this.getFluidTank(stackIn, false).getFluidTank().getFluidInTank(0) : FluidStack.EMPTY;
	}

	public int getTankCapacity(ItemStack stackIn, int tank) {
		return this.getFluidTank(stackIn, false) != null ? this.getFluidTank(stackIn, false).getFluidTank().getCapacity() : 0;
	}

	public boolean isFluidValid(ItemStack stackIn, int tank, FluidStack stack) {
		return true;
	}
	
	public int getFluidAmount(ItemStack stackIn) {
		return this.getFluidInTank(stackIn, 0).getAmount();
	}

	public int fill(ItemStack stackIn, FluidStack resource, FluidAction action) {
		int amount = 0;
		ObjectFluidTankCustom customTank = this.getFluidTank(stackIn, true);
		
		if (customTank != null) {
			amount = customTank.getFluidTank().fill(resource, action);
			
			if (action.execute()) {
				if (this.tier.creative()) {
					customTank.getFluidTank().setFluid(new FluidStack(resource.getFluid(), customTank.getFluidTank().getCapacity()));
					customTank.setFillLevel(16);
				} else {
					customTank.setFillLevel(this.updateFluidFillLevel(customTank.getFluidTank()));
				}
				this.saveFluidTank(stackIn, customTank);
				this.updateCustomName(stackIn, customTank.getFluidTank(), false);
			}
		}
		
		return amount;
	}

	public FluidStack drain(ItemStack stackIn, FluidStack resource, FluidAction action) {
		FluidStack fluidStack = FluidStack.EMPTY;
		ObjectFluidTankCustom customTank = this.getFluidTank(stackIn, true);
		
		if (customTank != null) {
			fluidStack = customTank.getFluidTank().drain(resource, this.tier.creative() ? FluidAction.SIMULATE : action);
			
			if (action.execute()) {
				customTank.setFillLevel(this.updateFluidFillLevel(customTank.getFluidTank()));
				this.saveFluidTank(stackIn, customTank);
			}
		}

		if (customTank.getFluidTank().isEmpty()) {
			this.updateCustomName(stackIn, null, true);
		}
		
		return fluidStack;
	}

	public FluidStack drain(ItemStack stackIn, int maxDrain, FluidAction action) {
		FluidStack fluidStack = FluidStack.EMPTY;
		ObjectFluidTankCustom customTank = this.getFluidTank(stackIn, true);
		
		if (customTank != null) {
			fluidStack = customTank.getFluidTank().drain(maxDrain, this.tier.creative() ? FluidAction.SIMULATE : action);
			
			if (action.execute()) {
				customTank.setFillLevel(this.updateFluidFillLevel(customTank.getFluidTank()));
				this.saveFluidTank(stackIn, customTank);
			}
		}
		
		if (customTank.getFluidTank().isEmpty()) {
			this.updateCustomName(stackIn, null, true);
		}
		
		return fluidStack;
	}
	
	public FluidTank getFluidTankTank(ItemStack stackIn) {
		ObjectFluidTankCustom customTank = this.getFluidTank(stackIn, false);
		
		if (customTank != null) {
			return customTank.getFluidTank();
		}
		
		return null;
	}
	
	public @Nullable ObjectFluidTankCustom getFluidTank(ItemStack stackIn, boolean createNew) {
		if (stackIn.has(DataComponents.BLOCK_ENTITY_DATA)) {
			CompoundTag stackTag = stackIn.get(DataComponents.BLOCK_ENTITY_DATA).copyTag();
			
			if (stackTag.contains("fluidTank")) {
				CompoundTag tankTag = stackTag.getCompound("fluidTank");
				
				return ObjectFluidTankCustom.readFromNBT(tankTag);
			} else {
				return (ObjectFluidTankCustom)null;
			}
		} else if (createNew) {
			CompoundTag stackTag = getStackTag();
			CompoundTag tankTag = new CompoundTag();
			
			ObjectFluidTankCustom tank = new ObjectFluidTankCustom(new FluidTank(this.fluidCapacity), 0);
			
			tank.writeToNBT(tankTag);
			stackTag.put("fluidTank", tankTag);
			
			stackIn.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(stackTag));
		}
		
		return (ObjectFluidTankCustom)null;
	}

	public void saveFluidTank(ItemStack stackIn, ObjectFluidTankCustom tankIn) {
		if (stackIn.has(DataComponents.BLOCK_ENTITY_DATA)) {
			CompoundTag stackTag = stackIn.get(DataComponents.BLOCK_ENTITY_DATA).copyTag();
			
			CompoundTag tankTag = new CompoundTag();
			tankIn.writeToNBT(tankTag);
			stackTag.put("fluidTank", tankTag);
			
			stackIn.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(stackTag));
		} else {
			CompoundTag stackTag = getStackTag();		
			CompoundTag tankTag = new CompoundTag();
			
			tankIn.writeToNBT(tankTag);
			stackTag.put("fluidTank", tankTag);
			
			stackIn.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(stackTag));
		}
	}

	public int updateFluidFillLevel(FluidTank tankIn) {
		if (!tankIn.isEmpty()) {
			if (this.getFluidLevelScaled(tankIn, 16) == 0) {
				return 1;
			} else {
				return Math.clamp(this.getFluidLevelScaled(tankIn, 16), 0, 16);
			}
		} else {
			return 0;
		}
	}
	
	public int getFluidLevelScaled(FluidTank tankIn, int one) {
		float scaled = tankIn.getFluidAmount() * one / tankIn.getCapacity() + 1;
		
		if (scaled == 0 && tankIn.getFluidAmount() > 0) {
			return 1;
		} else {
			return (int) scaled;
		}
	}
	
	public CompoundTag getStackTag() {
		return BlockEntityFluidTank.getNBT(this.regName);
	}
	
	public void updateCustomName(ItemStack stackIn, @Nullable FluidTank fluidTank, boolean remove) {
		if (!remove) {
			if (fluidTank != null) {
				FluidType type = fluidTank.getFluid().getFluid().getFluidType();
				ResourceLocation location = NeoForgeRegistries.FLUID_TYPES.getKey(type);

				String name = WordUtils.capitalize(location.getPath());
				String[] splitName = name.split("_");
				String newName = "";
				
				for (int i = 0; i < splitName.length; i++) {
					newName = newName + (i == 0 ? "": " ") + WordUtils.capitalize(splitName[i].replace("_", " "));
				}
				
				stackIn.set(DataComponents.CUSTOM_NAME, ComponentHelper.style(this.tier.getColour(), "", stackIn.getHoverName().getString() + ":").append(ComponentHelper.style(type.getTemperature() > 1000 ? ComponentColour.ORANGE : ComponentColour.CYAN, "bold", " (" + newName + ")")));
			} 
		} else {
			stackIn.remove(DataComponents.CUSTOM_NAME);
		}
	}
}