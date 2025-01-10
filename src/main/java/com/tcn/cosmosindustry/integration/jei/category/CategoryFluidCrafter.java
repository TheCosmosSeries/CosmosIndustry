package com.tcn.cosmosindustry.integration.jei.category;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.core.recipe.FluidCrafterRecipe;
import com.tcn.cosmosindustry.integration.jei.ModJEIRecipeTypes;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityFluidCrafter;
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

public class CategoryFluidCrafter implements IRecipeCategory<FluidCrafterRecipe> {

	private IGuiHelper helper;
	private final IDrawable background;

	private final IDrawable infusing;
	private final IDrawable extracting;
	private final IDrawable processInfusing;
	private final IDrawable processExtracting;
	
	private final IDrawable stored;
	
	private final IDrawable modeInfusing;
	private final IDrawable modeExtracting;
	
	public CategoryFluidCrafter(IGuiHelper helperIn) {
		this.helper = helperIn;
		
		this.background = this.helper.createDrawable(IndustryReference.Resource.Processing.Gui.FLUID_CRAFTER_JEI, 0, 0, 106, 80);

		this.processInfusing = this.helper.createAnimatedDrawable(helper.createDrawable(IndustryReference.Resource.Processing.Gui.FLUID_CRAFTER_LIGHT, 176, 42, 21, 14), 100, IDrawableAnimated.StartDirection.RIGHT, false);
		this.processExtracting = this.helper.createAnimatedDrawable(helper.createDrawable(IndustryReference.Resource.Processing.Gui.FLUID_CRAFTER_LIGHT, 176, 14, 21, 14), 100, IDrawableAnimated.StartDirection.LEFT, false);

		this.infusing = helper.createDrawable(IndustryReference.Resource.Processing.Gui.FLUID_CRAFTER_LIGHT, 176, 28, 21, 14);
		this.extracting = helper.createDrawable(IndustryReference.Resource.Processing.Gui.FLUID_CRAFTER_LIGHT, 176, 0, 21, 14);
		
		this.stored = this.helper.createAnimatedDrawable(helper.createDrawable(CosmosReference.RESOURCE.BASE.UI_ENERGY_VERTICAL, 0, 0, 16, 60), 200, IDrawableAnimated.StartDirection.TOP, true);
		
		this.modeInfusing = this.helper.createDrawable(CosmosReference.RESOURCE.BASE.BUTTON_FLUID_PATH_ALT, 96, 60, 18, 18);
		this.modeExtracting = this.helper.createDrawable(CosmosReference.RESOURCE.BASE.BUTTON_FLUID_PATH_ALT, 134, 60, 18, 18);
	}
	
	@Override
	public RecipeType<FluidCrafterRecipe> getRecipeType() {
		return ModJEIRecipeTypes.FLUID_CRAFTER;
	}

	@Override
	public Component getTitle() {
		return ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.integration.jei.fluid_crafter_category");
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(IndustryRegistrationManager.BLOCK_FLUID_CRAFTER.get()));
	}
	
	@Override
	public void draw(FluidCrafterRecipe recipe, IRecipeSlotsView viewIn, GuiGraphics stack, double mouseX, double mouseY) {
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		
		CosmosUISystem.Setup.setTextureColour(ComponentColour.RED);
		this.stored.draw(stack, 4, 4);
		CosmosUISystem.Setup.setTextureColour(ComponentColour.WHITE);

		if (recipe.getPlantMode().equals(BlockEntityFluidCrafter.PlantMode.INFUSING)) {
			this.infusing.draw(stack, 32, 27);
			this.processInfusing.draw(stack, 32, 27);
			this.modeInfusing.draw(stack, 44, 45);
		} else {
			this.extracting.draw(stack, 32, 27);
			this.processExtracting.draw(stack, 32, 27);
			this.modeExtracting.draw(stack, 44, 45);
		}
		
		CosmosUISystem.FontRenderer.drawString(stack, font, new int[] { 0, 0 }, 3, 68, true, recipe.getPlantMode().getLocNameComp());
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder recipeLayout, FluidCrafterRecipe recipe, IFocusGroup ingredients) {
		recipeLayout.addSlot(RecipeIngredientRole.INPUT, 35, 6).addIngredients(recipe.getInput());
		int fluidAmount = recipe.getFluidStack().getAmount();
		
		if (recipe.getPlantMode().equals(BlockEntityFluidCrafter.PlantMode.INFUSING)) {
			recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 25, 46).addItemStack(recipe.getResult());
			recipeLayout.addSlot(RecipeIngredientRole.CATALYST, 86, 4).addFluidStack(recipe.getFluidStack().getFluid(), fluidAmount).setFluidRenderer(fluidAmount, true, 16, 38);
		} else {
			recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 86, 18).addFluidStack(recipe.getFluidStack().getFluid(), fluidAmount).setFluidRenderer(fluidAmount, true, 16, 24);
		}
	}
}