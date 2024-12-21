package com.tcn.cosmosindustry.integration.jei;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.core.management.ModRecipeManager;
import com.tcn.cosmosindustry.core.management.ModRegistrationManager;
import com.tcn.cosmosindustry.core.recipe.CompactorRecipe;
import com.tcn.cosmosindustry.core.recipe.GrinderRecipe;
import com.tcn.cosmosindustry.core.recipe.SeparatorRecipe;
import com.tcn.cosmosindustry.core.recipe.SynthesiserRecipe;
import com.tcn.cosmosindustry.processing.client.container.ContainerCompactor;
import com.tcn.cosmosindustry.processing.client.container.ContainerGrinder;
import com.tcn.cosmosindustry.processing.client.container.ContainerSeparator;
import com.tcn.cosmosindustry.processing.client.screen.ScreenCompactor;
import com.tcn.cosmosindustry.processing.client.screen.ScreenGrinder;
import com.tcn.cosmosindustry.processing.client.screen.ScreenKiln;
import com.tcn.cosmosindustry.processing.client.screen.ScreenSeparator;
import com.tcn.cosmosindustry.processing.client.screen.ScreenSynthesiser;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class IndustryJEIPlugin implements IModPlugin {

	@Nullable
	private IRecipeCategory<GrinderRecipe> RECIPE_CATEGORY_GRINDER;

	@Nullable
	private IRecipeCategory<SeparatorRecipe> RECIPE_CATEGORY_SEPARATOR;

	@Nullable
	private IRecipeCategory<CompactorRecipe> RECIPE_CATEGORY_COMPACTOR;

	@Nullable
	private IRecipeCategory<SynthesiserRecipe> RECIPE_CATEGORY_SYNTHESISER;
	
	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "integration_jei");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		
		registration.addRecipeCategories(RECIPE_CATEGORY_GRINDER = new CategoryGrinder(guiHelper));
		registration.addRecipeCategories(RECIPE_CATEGORY_SEPARATOR = new CategorySeparator(guiHelper));
		registration.addRecipeCategories(RECIPE_CATEGORY_COMPACTOR = new CategoryCompactor(guiHelper));
		registration.addRecipeCategories(RECIPE_CATEGORY_SYNTHESISER = new CategorySynthesiser(guiHelper));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		IndustryRecipes recipes = new IndustryRecipes();
		
		registration.addRecipes(ModJEIRecipeTypes.GRINDING, recipes.getRecipes(RECIPE_CATEGORY_GRINDER, ModRecipeManager.RECIPE_TYPE_GRINDING.get()));
		registration.addRecipes(ModJEIRecipeTypes.SEPARATING, recipes.getRecipes(RECIPE_CATEGORY_SEPARATOR, ModRecipeManager.RECIPE_TYPE_SEPARATING.get()));
		registration.addRecipes(ModJEIRecipeTypes.COMPACTING, recipes.getRecipes(RECIPE_CATEGORY_COMPACTOR, ModRecipeManager.RECIPE_TYPE_COMPACTING.get()));
		registration.addRecipes(ModJEIRecipeTypes.SYNTHESISING, recipes.getRecipes(RECIPE_CATEGORY_SYNTHESISER, ModRecipeManager.RECIPE_TYPE_SYNTHESISING.get()));
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(ContainerGrinder.class, ModRegistrationManager.CONTAINER_TYPE_GRINDER.get(), ModJEIRecipeTypes.GRINDING, 0, 1, 6, 32);
		registration.addRecipeTransferHandler(ContainerSeparator.class, ModRegistrationManager.CONTAINER_TYPE_SEPARATOR.get(), ModJEIRecipeTypes.SEPARATING, 0, 1, 6, 32);
		registration.addRecipeTransferHandler(ContainerCompactor.class, ModRegistrationManager.CONTAINER_TYPE_COMPACTOR.get(), ModJEIRecipeTypes.COMPACTING, 0, 1, 6, 32);
		
	}
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(ModRegistrationManager.BLOCK_GRINDER.get()), ModJEIRecipeTypes.GRINDING);
		registration.addRecipeCatalyst(new ItemStack(ModRegistrationManager.BLOCK_SEPARATOR.get()), ModJEIRecipeTypes.SEPARATING);
		registration.addRecipeCatalyst(new ItemStack(ModRegistrationManager.BLOCK_COMPACTOR.get()), ModJEIRecipeTypes.COMPACTING);
		registration.addRecipeCatalyst(new ItemStack(ModRegistrationManager.BLOCK_SYNTHESISER.get()), ModJEIRecipeTypes.SYNTHESISING);
		registration.addRecipeCatalyst(new ItemStack(ModRegistrationManager.BLOCK_SYNTHESISER_STAND.get()), ModJEIRecipeTypes.SYNTHESISING);
		
		registration.addRecipeCatalyst(new ItemStack(ModRegistrationManager.BLOCK_KILN.get()), RecipeTypes.SMELTING);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(ScreenGrinder.class, 99, 39, 16, 16, ModJEIRecipeTypes.GRINDING);
		registration.addRecipeClickArea(ScreenSeparator.class, 99, 39, 16, 16, ModJEIRecipeTypes.SEPARATING);
		registration.addRecipeClickArea(ScreenCompactor.class, 99, 39, 16, 16, ModJEIRecipeTypes.COMPACTING);
		registration.addRecipeClickArea(ScreenSynthesiser.class, 107, 39, 18, 18, ModJEIRecipeTypes.SYNTHESISING);
		
		registration.addRecipeClickArea(ScreenKiln.class, 99, 39, 16, 16, RecipeTypes.SMELTING);
	}
}