package com.tcn.cosmosindustry.integration.jei;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.core.recipe.OrePlantRecipe;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityOrePlant;
import com.tcn.cosmoslibrary.CosmosReference;
import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class CategoryOrePlant implements IRecipeCategory<OrePlantRecipe> {

	private IGuiHelper helper;
	private final IDrawable background;
	
	private final IDrawable processCleaning;
	private final IDrawable processRefining;
	private final IDrawable stored;
	private final IDrawable modeCleaning;
	private final IDrawable modeRefining;
	
	public CategoryOrePlant(IGuiHelper helperIn) {
		this.helper = helperIn;
		
		this.background = this.helper.createDrawable(IndustryReference.Resource.Processing.Gui.ORE_PLANT_JEI, 0, 0, 120, 78);

		this.processCleaning = this.helper.createAnimatedDrawable(helper.createDrawable(IndustryReference.Resource.Processing.Gui.ORE_PLANT_LIGHT, 176, 0, 8, 60), 100, IDrawableAnimated.StartDirection.BOTTOM, false);
		this.processRefining = this.helper.createAnimatedDrawable(helper.createDrawable(IndustryReference.Resource.Processing.Gui.ORE_PLANT_LIGHT, 184, 0, 8, 60), 100, IDrawableAnimated.StartDirection.BOTTOM, false);
		
		this.stored = this.helper.createAnimatedDrawable(helper.createDrawable(CosmosReference.RESOURCE.BASE.UI_ENERGY_VERTICAL, 0, 0, 16, 60), 200, IDrawableAnimated.StartDirection.TOP, true);
		
		this.modeCleaning = this.helper.createDrawable(CosmosReference.RESOURCE.BASE.BUTTON_FLUID_PATH, 172, 180, 18, 18);
		this.modeRefining = this.helper.createDrawable(CosmosReference.RESOURCE.BASE.BUTTON_FLUID_PATH, 210, 180, 18, 18);
	}
	
	@Override
	public RecipeType<OrePlantRecipe> getRecipeType() {
		return ModJEIRecipeTypes.ORE_PLANT;
	}

	@Override
	public Component getTitle() {
		return ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.integration.jei.ore_plant_category");
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(IndustryRegistrationManager.BLOCK_ORE_PLANT.get()));
	}
	
	@Override
	public void draw(OrePlantRecipe recipe, IRecipeSlotsView viewIn, GuiGraphics stack, double mouseX, double mouseY) {
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		
		if (recipe.getPlantMode().equals(BlockEntityOrePlant.PlantMode.CLEANING)) {
			this.processCleaning.draw(stack, 56, 4);
		} else {
			this.processRefining.draw(stack, 56, 4);
		}
		
		CosmosUISystem.Setup.setTextureColour(ComponentColour.RED);
		this.stored.draw(stack, 9, 4);
		CosmosUISystem.Setup.setTextureColour(ComponentColour.WHITE);
		
		if (recipe.getPlantMode().equals(BlockEntityOrePlant.PlantMode.CLEANING)) {
			this.modeCleaning.draw(stack, 32, 25);
		} else {
			this.modeRefining.draw(stack, 32, 25);
		}
		
		CosmosUISystem.FontRenderer.drawString(stack, font, new int[] { 0, 0 }, 8, 67, true, recipe.getPlantMode().getLocNameComp());
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder recipeLayout, OrePlantRecipe recipe, IFocusGroup ingredients) {
		recipeLayout.addSlot(RecipeIngredientRole.INPUT, 33, 6).addIngredients(recipe.getInput());
		recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 33, 46).addItemStack(recipe.getResult());
		
		if (recipe.getPlantMode().equals(BlockEntityOrePlant.PlantMode.CLEANING)) {
			int fluidAmount = recipe.getFluidStack().getAmount();
			recipeLayout.addSlot(RecipeIngredientRole.CATALYST, 95, 4).addFluidStack(recipe.getFluidStack().getFluid(), fluidAmount).setFluidRenderer(fluidAmount, true, 16, 38);
		}
	}
}