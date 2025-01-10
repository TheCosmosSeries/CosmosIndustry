package com.tcn.cosmosindustry.core.management;

import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.core.recipe.CompactorRecipe;
import com.tcn.cosmosindustry.core.recipe.FluidCrafterRecipe;
import com.tcn.cosmosindustry.core.recipe.GrinderRecipe;
import com.tcn.cosmosindustry.core.recipe.LaserCutterRecipe;
import com.tcn.cosmosindustry.core.recipe.OrePlantRecipe;
import com.tcn.cosmosindustry.core.recipe.SeparatorRecipe;
import com.tcn.cosmosindustry.core.recipe.SolidifierRecipe;
import com.tcn.cosmosindustry.core.recipe.SynthesiserRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IndustryRecipeManager {

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CosmosIndustry.MOD_ID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, CosmosIndustry.MOD_ID);

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GrinderRecipe>> RECIPE_SERIALIZER_GRINDING = RECIPE_SERIALIZERS.register("grinding", () -> new GrinderRecipe.Serializer());
	public static final DeferredHolder<RecipeType<?>, RecipeType<GrinderRecipe>> RECIPE_TYPE_GRINDING = RECIPE_TYPES.register("grinding", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "grinding")));

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SeparatorRecipe>> RECIPE_SERIALIZER_SEPARATING = RECIPE_SERIALIZERS.register("separating", () -> new SeparatorRecipe.Serializer());
	public static final DeferredHolder<RecipeType<?>, RecipeType<SeparatorRecipe>> RECIPE_TYPE_SEPARATING = RECIPE_TYPES.register("separating", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "separating")));

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CompactorRecipe>> RECIPE_SERIALIZER_COMPACTING = RECIPE_SERIALIZERS.register("compacting", () -> new CompactorRecipe.Serializer());
	public static final DeferredHolder<RecipeType<?>, RecipeType<CompactorRecipe>> RECIPE_TYPE_COMPACTING = RECIPE_TYPES.register("compacting", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "compacting")));

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SynthesiserRecipe>> RECIPE_SERIALIZER_SYNTHESISING = RECIPE_SERIALIZERS.register("synthesising", () -> new SynthesiserRecipe.Serializer());
	public static final DeferredHolder<RecipeType<?>, RecipeType<SynthesiserRecipe>> RECIPE_TYPE_SYNTHESISING = RECIPE_TYPES.register("synthesising", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "synthesising")));

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LaserCutterRecipe>> RECIPE_SERIALIZER_LASERING = RECIPE_SERIALIZERS.register("lasering", () -> new LaserCutterRecipe.Serializer());
	public static final DeferredHolder<RecipeType<?>, RecipeType<LaserCutterRecipe>> RECIPE_TYPE_LASERING = RECIPE_TYPES.register("lasering", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "lasering")));

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<OrePlantRecipe>> RECIPE_SERIALIZER_ORE_PLANT = RECIPE_SERIALIZERS.register("ore_plant", () -> new OrePlantRecipe.Serializer());
	public static final DeferredHolder<RecipeType<?>, RecipeType<OrePlantRecipe>> RECIPE_TYPE_ORE_PLANT = RECIPE_TYPES.register("ore_plant", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "ore_plant")));

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FluidCrafterRecipe>> RECIPE_SERIALIZER_FLUID_CRAFTER = RECIPE_SERIALIZERS.register("fluid_crafter", () -> new FluidCrafterRecipe.Serializer());
	public static final DeferredHolder<RecipeType<?>, RecipeType<FluidCrafterRecipe>> RECIPE_TYPE_FLUID_CRAFTER = RECIPE_TYPES.register("fluid_crafter", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "fluid_crafter")));

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SolidifierRecipe>> RECIPE_SERIALIZER_SOLIDIFIER = RECIPE_SERIALIZERS.register("solidifier", () -> new SolidifierRecipe.Serializer());
	public static final DeferredHolder<RecipeType<?>, RecipeType<SolidifierRecipe>> RECIPE_TYPE_SOLIDIFIER = RECIPE_TYPES.register("solidifier", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "solidifier")));

	public static void register(IEventBus bus) {
		RECIPE_TYPES.register(bus);
		RECIPE_SERIALIZERS.register(bus);
	}
}