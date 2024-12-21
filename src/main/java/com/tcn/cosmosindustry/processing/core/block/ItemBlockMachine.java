package com.tcn.cosmosindustry.processing.core.block;

import java.util.List;

import com.tcn.cosmoslibrary.common.block.CosmosItemBlock;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper.Value;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;

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
				ListTag list = (ListTag)stackTag.get("Items");
				
				if (list.size() > 0) {
					tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.tooltip.block_item.items_stored"));
				}
				tooltip.add(ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.tooltip.energy_item.stored").append(ComponentHelper.comp(Value.LIGHT_GRAY + "[ " + Value.RED + CosmosUtil.formatIntegerMillion(stackTag.getInt("energy")) + Value.LIGHT_GRAY + " ]" )));
				tooltip.add(ComponentHelper.ctrlForLessDetails());
			}
		}
	}
}