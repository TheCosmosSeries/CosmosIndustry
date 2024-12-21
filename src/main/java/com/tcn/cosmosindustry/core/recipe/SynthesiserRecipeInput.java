package com.tcn.cosmosindustry.core.recipe;

import java.util.ArrayList;
import java.util.List;

import com.tcn.cosmosindustry.CosmosIndustry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class SynthesiserRecipeInput implements RecipeInput {

	private ItemStack focus;
	private List<ItemStack> items;
	
	public SynthesiserRecipeInput(ItemStack focus, List<ItemStack> items) {
		this.focus = focus;
		this.items = items;
		
		if (items.size() > 8) {
			CosmosIndustry.CONSOLE.warning("Synthesiser Recipe Incorrect: ingredient list longer than max.");
		}
	}

	public SynthesiserRecipeInput(ItemStack focus, ItemStack... items) {
		this.focus = focus;
		this.items = new ArrayList<ItemStack>();
		
		for (ItemStack item : items) {
			if (!item.isEmpty()) {
				this.items.add(item);
			}
		}

		if (items.length > 8) {
			CosmosIndustry.CONSOLE.warning("Synthesiser Recipe Incorrect: ingredient list longer than max.");
		}
	}
	
	@Override
	public ItemStack getItem(int index) {
		return index >= 0 && index < this.items.size() ? this.items.get(index) : ItemStack.EMPTY;
	}

	@Override
	public int size() {
		return this.items.size();
	}
	
	public List<ItemStack> getItems(){
		return this.items;
	}
	
	public ItemStack getFocus() {
		return this.focus;
	}

}