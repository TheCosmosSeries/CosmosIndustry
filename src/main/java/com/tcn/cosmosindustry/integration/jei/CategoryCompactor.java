package com.tcn.cosmosindustry.integration.jei;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.core.recipe.CompactorRecipe;
import com.tcn.cosmoslibrary.CosmosReference;
import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class CategoryCompactor implements IRecipeCategory<CompactorRecipe> {

	private IGuiHelper helper;
	private final IDrawable background;
	
	private final IDrawable process;
	private final IDrawable stored;
	
	public CategoryCompactor(IGuiHelper helperIn) {
		this.helper = helperIn;
		
		this.background = this.helper.createDrawable(IndustryReference.Resource.Processing.Gui.COMPACTOR_JEI, 0, 0, 120, 68);

		IDrawableStatic process_draw = helper.createDrawable(IndustryReference.Resource.Processing.Gui.COMPACTOR_LIGHT, 176, 0, 16, 16);
		IDrawableStatic stored_draw = helper.createDrawable(CosmosReference.RESOURCE.BASE.UI_ENERGY_VERTICAL, 0, 0, 16, 60);

		this.process = this.helper.createAnimatedDrawable(process_draw, 100, IDrawableAnimated.StartDirection.TOP, false);
		this.stored = this.helper.createAnimatedDrawable(stored_draw, 200, IDrawableAnimated.StartDirection.TOP, true);
	}
	
	@Override
	public RecipeType<CompactorRecipe> getRecipeType() {
		return ModJEIRecipeTypes.COMPACTING;
	}

	@Override
	public Component getTitle() {
		return ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.integration.jei.compactor_category");
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(IndustryRegistrationManager.BLOCK_COMPACTOR));
	}
	
	@Override
	public void draw(CompactorRecipe recipe, IRecipeSlotsView viewIn, GuiGraphics stack, double mouseX, double mouseY) {
		this.process.draw(stack, 71, 26);
		
		CosmosUISystem.Setup.setTextureColour(ComponentColour.RED);
		this.stored.draw(stack, 26, 4);
		CosmosUISystem.Setup.setTextureColour(ComponentColour.WHITE);
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder recipeLayout, CompactorRecipe recipe, IFocusGroup ingredients) {
		recipeLayout.addSlot(RecipeIngredientRole.INPUT, 50, 26).addIngredients(recipe.input);
		recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 96, 26).addItemStack(recipe.result);
	}
}