package com.tcn.cosmosindustry.integration.jei;

import com.tcn.cosmosindustry.core.recipe.CompactorRecipe;
import com.tcn.cosmosindustry.core.recipe.GrinderRecipe;
import com.tcn.cosmosindustry.core.recipe.SeparatorRecipe;
import com.tcn.cosmosindustry.core.recipe.SynthesiserRecipe;

import mezz.jei.api.recipe.RecipeType;

public class ModJEIRecipeTypes {

	public static final RecipeType<GrinderRecipe> GRINDING = RecipeType.create("cosmosindustry", "grinding", GrinderRecipe.class);
	public static final RecipeType<SeparatorRecipe> SEPARATING = RecipeType.create("cosmosindustry", "separating", SeparatorRecipe.class);
	public static final RecipeType<CompactorRecipe> COMPACTING = RecipeType.create("cosmosindustry", "compacting", CompactorRecipe.class);
	public static final RecipeType<SynthesiserRecipe> SYNTHESISING = RecipeType.create("cosmosindustry", "synthesising", SynthesiserRecipe.class);

}