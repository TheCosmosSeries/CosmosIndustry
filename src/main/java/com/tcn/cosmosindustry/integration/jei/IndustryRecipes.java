package com.tcn.cosmosindustry.integration.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

public class IndustryRecipes {
	private final RecipeManager recipeManager;

	public IndustryRecipes() {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel world = minecraft.level;
		this.recipeManager = world.getRecipeManager();
	}
	
	public <C extends RecipeInput, T extends Recipe<C>> List<T> getRecipes(IRecipeCategory<T> stationCategory, RecipeType<T> recipeType) {
		List<T> list = new ArrayList<T>();
		
		getRecipes(recipeManager, recipeType).forEach((holder) -> {
			list.add(holder.value());
		});
		
		return list;
	}
	
	private static <C extends RecipeInput, T extends Recipe<C>> Collection<RecipeHolder<T>> getRecipes(RecipeManager recipeManager, RecipeType<T> recipeType) {
		return recipeManager.getAllRecipesFor(recipeType);
	}
}