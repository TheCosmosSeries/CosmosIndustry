package com.tcn.cosmosindustry.integration.jei.category;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.core.recipe.SolidifierRecipe;
import com.tcn.cosmosindustry.integration.jei.ModJEIRecipeTypes;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class CategorySolidifier implements IRecipeCategory<SolidifierRecipe> {

	private IGuiHelper helper;
	private final IDrawable background;
	private final IDrawable process;
	private final IDrawable stored;
	
	public CategorySolidifier(IGuiHelper helperIn) {
		this.helper = helperIn;
		
		this.background = this.helper.createDrawable(IndustryReference.Resource.Processing.Gui.SOLIDIFIER_JEI, 0, 0, 106, 68);

		this.process = this.helper.createAnimatedDrawable(helper.createDrawable(IndustryReference.Resource.Processing.Gui.SOLIDIFIER_LIGHT, 176, 0, 8, 38), 100, IDrawableAnimated.StartDirection.TOP, true);

		this.stored = this.helper.createAnimatedDrawable(helper.createDrawable(CosmosReference.RESOURCE.BASE.UI_ENERGY_VERTICAL, 0, 0, 16, 60), 200, IDrawableAnimated.StartDirection.TOP, true);
	}
	
	@Override
	public RecipeType<SolidifierRecipe> getRecipeType() {
		return ModJEIRecipeTypes.SOLIDIFIER;
	}

	@Override
	public Component getTitle() {
		return ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.integration.jei.solidifier_category");
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(IndustryRegistrationManager.BLOCK_SOLIDIFIER.get()));
	}
	
	@Override
	public void draw(SolidifierRecipe recipe, IRecipeSlotsView viewIn, GuiGraphics stack, double mouseX, double mouseY) {
		CosmosUISystem.Setup.setTextureColour(ComponentColour.RED);
		this.stored.draw(stack, 4, 4);
		CosmosUISystem.Setup.setTextureColour(ComponentColour.WHITE);

		this.process.draw(stack, 49, 16);
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder recipeLayout, SolidifierRecipe recipe, IFocusGroup ingredients) {
		int fluidAmount = recipe.getFluidStack().getAmount();
		
		recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 27, 27).addItemStack(recipe.getResult());
		recipeLayout.addSlot(RecipeIngredientRole.CATALYST, 86, 4).addFluidStack(recipe.getFluidStack().getFluid(), fluidAmount).setFluidRenderer(fluidAmount, true, 16, 38);
	}
}