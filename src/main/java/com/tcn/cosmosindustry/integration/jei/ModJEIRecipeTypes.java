package com.tcn.cosmosindustry.integration.jei;

import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.core.recipe.CompactorRecipe;
import com.tcn.cosmosindustry.core.recipe.FluidCrafterRecipe;
import com.tcn.cosmosindustry.core.recipe.GrinderRecipe;
import com.tcn.cosmosindustry.core.recipe.LaserCutterRecipe;
import com.tcn.cosmosindustry.core.recipe.OrePlantRecipe;
import com.tcn.cosmosindustry.core.recipe.SeparatorRecipe;
import com.tcn.cosmosindustry.core.recipe.SolidifierRecipe;
import com.tcn.cosmosindustry.core.recipe.SynthesiserRecipe;

import mezz.jei.api.recipe.RecipeType;

public class ModJEIRecipeTypes {

	public static final RecipeType<GrinderRecipe> GRINDING = RecipeType.create(CosmosIndustry.MOD_ID, "grinding", GrinderRecipe.class);
	public static final RecipeType<SeparatorRecipe> SEPARATING = RecipeType.create(CosmosIndustry.MOD_ID, "separating", SeparatorRecipe.class);
	public static final RecipeType<CompactorRecipe> COMPACTING = RecipeType.create(CosmosIndustry.MOD_ID, "compacting", CompactorRecipe.class);
	public static final RecipeType<LaserCutterRecipe> LASERING = RecipeType.create(CosmosIndustry.MOD_ID, "lasering", LaserCutterRecipe.class);
	
	public static final RecipeType<OrePlantRecipe> ORE_PLANT = RecipeType.create(CosmosIndustry.MOD_ID, "ore_plant", OrePlantRecipe.class);
	public static final RecipeType<FluidCrafterRecipe> FLUID_CRAFTER = RecipeType.create(CosmosIndustry.MOD_ID, "fluid_crafter", FluidCrafterRecipe.class);
	public static final RecipeType<SolidifierRecipe> SOLIDIFIER = RecipeType.create(CosmosIndustry.MOD_ID, "solidifier", SolidifierRecipe.class);
	
	public static final RecipeType<SynthesiserRecipe> SYNTHESISING = RecipeType.create(CosmosIndustry.MOD_ID, "synthesising", SynthesiserRecipe.class);
}