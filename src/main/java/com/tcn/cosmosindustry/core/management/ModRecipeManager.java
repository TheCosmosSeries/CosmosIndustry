package com.tcn.cosmosindustry.core.management;

import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.core.recipe.CompactorRecipe;
import com.tcn.cosmosindustry.core.recipe.GrinderRecipe;
import com.tcn.cosmosindustry.core.recipe.LaserCutterRecipe;
import com.tcn.cosmosindustry.core.recipe.SeparatorRecipe;
import com.tcn.cosmosindustry.core.recipe.SynthesiserRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeManager {

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

	public static void register(IEventBus bus) {
		RECIPE_TYPES.register(bus);
		RECIPE_SERIALIZERS.register(bus);
	}
}