package com.tcn.cosmosindustry.integration.jei;

import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.core.management.*;
import com.tcn.cosmosindustry.core.recipe.*;
import com.tcn.cosmosindustry.integration.jei.category.*;
import com.tcn.cosmosindustry.processing.client.container.*;
import com.tcn.cosmosindustry.processing.client.screen.*;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class IndustryJEIPlugin implements IModPlugin {

	private IRecipeCategory<GrinderRecipe> RECIPE_CATEGORY_GRINDER;
	private IRecipeCategory<SeparatorRecipe> RECIPE_CATEGORY_SEPARATOR;
	private IRecipeCategory<CompactorRecipe> RECIPE_CATEGORY_COMPACTOR;
	private IRecipeCategory<LaserCutterRecipe> RECIPE_CATEGORY_LASER_CUTTER;
	private IRecipeCategory<OrePlantRecipe> RECIPE_CATEGORY_ORE_PLANT;
	private IRecipeCategory<FluidCrafterRecipe> RECIPE_CATEGORY_FLUID_CRAFTER;
	private IRecipeCategory<SolidifierRecipe> RECIPE_CATEGORY_SOLIDIFIER;
	
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
		registration.addRecipeCategories(RECIPE_CATEGORY_LASER_CUTTER = new CategoryLaserCutter(guiHelper));
		
		registration.addRecipeCategories(RECIPE_CATEGORY_ORE_PLANT = new CategoryOrePlant(guiHelper));
		registration.addRecipeCategories(RECIPE_CATEGORY_FLUID_CRAFTER = new CategoryFluidCrafter(guiHelper));
		registration.addRecipeCategories(RECIPE_CATEGORY_SOLIDIFIER = new CategorySolidifier(guiHelper));
		
		registration.addRecipeCategories(RECIPE_CATEGORY_SYNTHESISER = new CategorySynthesiser(guiHelper));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		IndustryRecipes recipes = new IndustryRecipes();
		
		registration.addRecipes(ModJEIRecipeTypes.GRINDING, recipes.getRecipes(RECIPE_CATEGORY_GRINDER, IndustryRecipeManager.RECIPE_TYPE_GRINDING.get()));
		registration.addRecipes(ModJEIRecipeTypes.SEPARATING, recipes.getRecipes(RECIPE_CATEGORY_SEPARATOR, IndustryRecipeManager.RECIPE_TYPE_SEPARATING.get()));
		registration.addRecipes(ModJEIRecipeTypes.COMPACTING, recipes.getRecipes(RECIPE_CATEGORY_COMPACTOR, IndustryRecipeManager.RECIPE_TYPE_COMPACTING.get()));
		registration.addRecipes(ModJEIRecipeTypes.LASERING, recipes.getRecipes(RECIPE_CATEGORY_LASER_CUTTER, IndustryRecipeManager.RECIPE_TYPE_LASERING.get()));
		
		registration.addRecipes(ModJEIRecipeTypes.ORE_PLANT, recipes.getRecipes(RECIPE_CATEGORY_ORE_PLANT, IndustryRecipeManager.RECIPE_TYPE_ORE_PLANT.get()));
		registration.addRecipes(ModJEIRecipeTypes.FLUID_CRAFTER, recipes.getRecipes(RECIPE_CATEGORY_FLUID_CRAFTER, IndustryRecipeManager.RECIPE_TYPE_FLUID_CRAFTER.get()));
		registration.addRecipes(ModJEIRecipeTypes.SOLIDIFIER, recipes.getRecipes(RECIPE_CATEGORY_SOLIDIFIER, IndustryRecipeManager.RECIPE_TYPE_SOLIDIFIER.get()));
		
		registration.addRecipes(ModJEIRecipeTypes.SYNTHESISING, recipes.getRecipes(RECIPE_CATEGORY_SYNTHESISER, IndustryRecipeManager.RECIPE_TYPE_SYNTHESISING.get()));
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(ContainerGrinder.class, IndustryRegistrationManager.CONTAINER_TYPE_GRINDER.get(), ModJEIRecipeTypes.GRINDING, 0, 1, 6, 32);
		registration.addRecipeTransferHandler(ContainerSeparator.class, IndustryRegistrationManager.CONTAINER_TYPE_SEPARATOR.get(), ModJEIRecipeTypes.SEPARATING, 0, 1, 6, 32);
		registration.addRecipeTransferHandler(ContainerCompactor.class, IndustryRegistrationManager.CONTAINER_TYPE_COMPACTOR.get(), ModJEIRecipeTypes.COMPACTING, 0, 1, 5, 32);
		registration.addRecipeTransferHandler(ContainerLaserCutter.class, IndustryRegistrationManager.CONTAINER_TYPE_LASER_CUTTER.get(), ModJEIRecipeTypes.LASERING, 0, 1, 5, 32);
		
		registration.addRecipeTransferHandler(ContainerOrePlant.class, IndustryRegistrationManager.CONTAINER_TYPE_ORE_PLANT.get(), ModJEIRecipeTypes.ORE_PLANT, 0, 1, 10, 32);
		registration.addRecipeTransferHandler(ContainerFluidCrafter.class, IndustryRegistrationManager.CONTAINER_TYPE_FLUID_CRAFTER.get(), ModJEIRecipeTypes.FLUID_CRAFTER, 0, 1, 10, 32);
		
	}
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(IndustryRegistrationManager.BLOCK_KILN.get()), RecipeTypes.SMELTING);
		
		registration.addRecipeCatalyst(new ItemStack(IndustryRegistrationManager.BLOCK_GRINDER.get()), ModJEIRecipeTypes.GRINDING);
		registration.addRecipeCatalyst(new ItemStack(IndustryRegistrationManager.BLOCK_SEPARATOR.get()), ModJEIRecipeTypes.SEPARATING);
		registration.addRecipeCatalyst(new ItemStack(IndustryRegistrationManager.BLOCK_COMPACTOR.get()), ModJEIRecipeTypes.COMPACTING);
		registration.addRecipeCatalyst(new ItemStack(IndustryRegistrationManager.BLOCK_LASER_CUTTER.get()), ModJEIRecipeTypes.LASERING);
		
		registration.addRecipeCatalyst(new ItemStack(IndustryRegistrationManager.BLOCK_ORE_PLANT.get()), ModJEIRecipeTypes.ORE_PLANT);
		registration.addRecipeCatalyst(new ItemStack(IndustryRegistrationManager.BLOCK_FLUID_CRAFTER.get()), ModJEIRecipeTypes.FLUID_CRAFTER);
		registration.addRecipeCatalyst(new ItemStack(IndustryRegistrationManager.BLOCK_SOLIDIFIER.get()), ModJEIRecipeTypes.SOLIDIFIER);
		
		registration.addRecipeCatalyst(new ItemStack(IndustryRegistrationManager.BLOCK_SYNTHESISER.get()), ModJEIRecipeTypes.SYNTHESISING);
		registration.addRecipeCatalyst(new ItemStack(IndustryRegistrationManager.BLOCK_SYNTHESISER_STAND.get()), ModJEIRecipeTypes.SYNTHESISING);
		
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(ScreenKiln.class, 99, 39, 16, 16, RecipeTypes.SMELTING);
		
		registration.addRecipeClickArea(ScreenGrinder.class, 99, 39, 16, 16, ModJEIRecipeTypes.GRINDING);
		registration.addRecipeClickArea(ScreenSeparator.class, 99, 39, 16, 16, ModJEIRecipeTypes.SEPARATING);
		registration.addRecipeClickArea(ScreenCompactor.class, 99, 39, 16, 16, ModJEIRecipeTypes.COMPACTING);
		registration.addRecipeClickArea(ScreenLaserCutter.class, 99, 39, 16, 16, ModJEIRecipeTypes.LASERING);
		
		registration.addRecipeClickArea(ScreenOrePlant.class, 84, 17, 8, 60, ModJEIRecipeTypes.ORE_PLANT);
		registration.addRecipeClickArea(ScreenFluidCrafter.class, 66, 36, 21, 14, ModJEIRecipeTypes.FLUID_CRAFTER);
		registration.addRecipeClickArea(ScreenSolidifier.class, 84, 29, 8, 38, ModJEIRecipeTypes.SOLIDIFIER);
		
		registration.addRecipeClickArea(ScreenSynthesiser.class, 107, 39, 18, 18, ModJEIRecipeTypes.SYNTHESISING);
	}
}