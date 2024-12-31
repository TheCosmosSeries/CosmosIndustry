package com.tcn.cosmosindustry.processing.core.block;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import com.tcn.cosmoslibrary.common.block.CosmosItemBlock;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper.Value;
import com.tcn.cosmoslibrary.common.nbt.CosmosNBTHelper.Const;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectFluidTankCustom;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@SuppressWarnings("deprecation")
public class ItemBlockMachine extends CosmosItemBlock {

	public ItemBlockMachine(Block block, Item.Properties properties, String description, String shift_desc_one, String shift_desc_two) {
		super(block, properties, description, shift_desc_one, shift_desc_two);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);
		
		if (stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
			CompoundTag stackTag = stack.get(DataComponents.BLOCK_ENTITY_DATA).copyTag();
			
			if (!ComponentHelper.isControlKeyDown(Minecraft.getInstance())) {
				tooltip.add(ComponentHelper.ctrlForMoreDetails());
			} else {
				if (stackTag.contains("Items")) {
					ListTag list = (ListTag)stackTag.get("Items");
					
					if (list.size() > 0) {
						tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.tooltip.block_item.items_stored"));
					}
				}
				
				if (stackTag.contains("energy")) {
					tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.tooltip.energy_item.stored").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.RED + CosmosUtil.formatIntegerMillion(stackTag.getInt("energy")) + Value.LIGHT_GRAY + " ]" )));
				}

				if (stackTag.contains("fluidTank")) {
					CompoundTag fluidTag = stackTag.getCompound("fluidTank");
					CompoundTag resourceTag = fluidTag.getCompound(ObjectFluidTankCustom.NBT_FLUID_KEY);
					String fluidName = WordUtils.capitalize(resourceTag.getString(Const.NBT_PATH_KEY));
					
					int volume = fluidTag.getInt(ObjectFluidTankCustom.NBT_FLUID_VOLUME_KEY);
					int capacity = fluidTag.getInt(ObjectFluidTankCustom.NBT_FLUID_CAPACITY_KEY);
					int fillLevel = fluidTag.getInt(ObjectFluidTankCustom.NBT_FILL_LEVEL_KEY);
					
					tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.tooltip.block_item.fluid_stored")
						.append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.CYAN + fluidName + Value.LIGHT_GRAY + " (" + Value.CYAN + volume + Value.LIGHT_GRAY + " / " + Value.CYAN + capacity)
						.append(ComponentHelper.style2(ComponentColour.LIGHT_GRAY, "cosmoslibrary.tooltip.block_item.fluid_stored_suff", ") ]")))
					);
					
					tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.tooltip.block_item.fluid_fill_level")
						.append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.CYAN + fillLevel))
						.append(ComponentHelper.style(ComponentColour.LIGHT_GRAY, " ]"))
					);
				}
				tooltip.add(ComponentHelper.ctrlForLessDetails());
			}
		}
	}
}