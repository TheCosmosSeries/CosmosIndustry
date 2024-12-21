package com.tcn.cosmosindustry.core.management;

import com.tcn.cosmoslibrary.client.enums.EnumBERColour;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

@Deprecated
public class SynthesiserRecipeManager {

	public void addAllRecipes() {
		this.addRecipe(new ItemStack(ModRegistrationManager.UPGRADE_SPEED.get()), new ItemStack(ModRegistrationManager.UPGRADE_BASE.get()),
			160, EnumBERColour.YELLOW, new Object[] {
			new ItemStack(Blocks.GLOWSTONE), new ItemStack(Blocks.GLOWSTONE),
			new ItemStack(Blocks.REDSTONE_BLOCK), new ItemStack(Blocks.REDSTONE_BLOCK),
			new ItemStack(ModRegistrationManager.ENERGY_WAFER.get()), new ItemStack(ModRegistrationManager.ENERGY_WAFER.get()),
			new ItemStack(ModRegistrationManager.CIRCUIT.get()), new ItemStack(ModRegistrationManager.CIRCUIT.get())
		});
		
		this.addRecipe(new ItemStack(ModRegistrationManager.UPGRADE_FLUID_SPEED.get()), new ItemStack(ModRegistrationManager.UPGRADE_SPEED.get()),
			160, EnumBERColour.RED, new Object[] {
			new ItemStack(Blocks.REDSTONE_BLOCK), new ItemStack(Blocks.REDSTONE_BLOCK),
			new ItemStack(Blocks.GLOWSTONE), new ItemStack(Blocks.GLOWSTONE),
			new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.WATER_BUCKET),
			new ItemStack(ModRegistrationManager.CIRCUIT.get()), new ItemStack(ModRegistrationManager.CIRCUIT.get())
		});
		
		this.addRecipe(new ItemStack(ModRegistrationManager.CIRCUIT_ADVANCED.get()), new ItemStack(ModRegistrationManager.CIRCUIT_ADVANCED_RAW.get()),
			200, EnumBERColour.ORANGE, new Object[] { 
			new ItemStack(Blocks.REDSTONE_BLOCK), new ItemStack(Blocks.REDSTONE_BLOCK),
			new ItemStack(ModRegistrationManager.CIRCUIT.get()), new ItemStack(ModRegistrationManager.CIRCUIT.get()),
			new ItemStack(Items.DIAMOND), new ItemStack(Items.DIAMOND),
			new ItemStack(ModRegistrationManager.ENERGY_WAFER.get()), new ItemStack(ModRegistrationManager.ENERGY_WAFER.get())
		});
		
		this.addRecipe(new ItemStack(ModRegistrationManager.ENERGY_INGOT.get()), new ItemStack(Items.IRON_INGOT),
			80, EnumBERColour.PURPLE, new Object[] { 
			new ItemStack(Items.REDSTONE), new ItemStack(Items.REDSTONE),
			new ItemStack(ModRegistrationManager.ENERGY_DUST.get()), new ItemStack(ModRegistrationManager.ENERGY_DUST.get())
		});
		
		this.addRecipe(new ItemStack(ModRegistrationManager.BLOCK_STRUCTURE), new ItemStack(Blocks.IRON_BLOCK),
			140, EnumBERColour.GRAY, new Object[] {
			new ItemStack(ModRegistrationManager.CIRCUIT.get()), new ItemStack(ModRegistrationManager.CIRCUIT.get()),
			new ItemStack(ModRegistrationManager.ENERGY_INGOT.get()), new ItemStack(ModRegistrationManager.ENERGY_INGOT.get())
		});
	}

	public void addRecipe(ItemStack output, ItemStack focus, Integer process_time, EnumBERColour colour, Object... inputs) {
		
	}
}