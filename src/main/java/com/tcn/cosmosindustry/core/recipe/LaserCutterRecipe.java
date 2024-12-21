package com.tcn.cosmosindustry.core.recipe;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tcn.cosmosindustry.core.management.ModRecipeManager;
import com.tcn.cosmosindustry.core.management.ModRegistrationManager;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class LaserCutterRecipe implements Recipe<RecipeInput> {
	public final Ingredient input;
	public final ItemStack result;

	public LaserCutterRecipe(Ingredient inputIn, ItemStack resultIn) {
		this.input = inputIn;
		this.result = resultIn;
	}

	@Override
	public boolean matches(RecipeInput recipeInputIn, Level levelIn) {
		boolean flagInput = this.input.test(recipeInputIn.getItem(0));
		
		return flagInput;
	}

	@Override
	public ItemStack assemble(RecipeInput recipeInputIn, HolderLookup.Provider providerIn) {
		ItemStack itemstack = recipeInputIn.getItem(0).transmuteCopy(this.result.getItem(), this.result.getCount());
        itemstack.applyComponents(this.result.getComponentsPatch());
        return itemstack;
	}

	@Override
	public boolean canCraftInDimensions(int xIn, int yIn) {
		return xIn * yIn >= 2;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider providerIn) {
		return this.result;
	}
	
	public boolean isInputIngredient(ItemStack stackIn) {
		return this.input.test(stackIn);
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModRegistrationManager.BLOCK_LASER_CUTTER.get());
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipeManager.RECIPE_SERIALIZER_LASERING.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipeManager.RECIPE_TYPE_LASERING.get();
	}
	
	@Override
	public boolean isIncomplete() {
		return Stream.of(this.input).anyMatch((ingredient) -> {
			return ingredient.getItems().length == 0;
		});
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(this.input);
	}
	
	public ArrayList<ItemStack> getOutputs() {
		ArrayList<ItemStack> array = new ArrayList<>();
		
		array.add(this.result);
		
		return array;
	}
	
	public static class Serializer implements RecipeSerializer<LaserCutterRecipe> {
		public static final Serializer INSTANCE = new Serializer();
		public static final MapCodec<LaserCutterRecipe> CODEC = RecordCodecBuilder.mapCodec(
	            instance -> instance.group(
	    			Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(recipe -> recipe.input),
	    			ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
	            ).apply(instance, LaserCutterRecipe::new)
	        );
			
			public static final StreamCodec<RegistryFriendlyByteBuf, LaserCutterRecipe> STREAM_CODEC = StreamCodec.of(
					LaserCutterRecipe.Serializer::toNetwork, LaserCutterRecipe.Serializer::fromNetwork
			);
		    
	    @Override
	    public MapCodec<LaserCutterRecipe> codec() {
	        return CODEC;
	    }
	    
	    @Override
	    public StreamCodec<RegistryFriendlyByteBuf, LaserCutterRecipe> streamCodec() {
	        return STREAM_CODEC;
	    }
	    
		private static LaserCutterRecipe fromNetwork(RegistryFriendlyByteBuf extraDataIn) {
			return new LaserCutterRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(extraDataIn), ItemStack.STREAM_CODEC.decode(extraDataIn));
		}

		private static void toNetwork(RegistryFriendlyByteBuf extraDataIn, LaserCutterRecipe recipeIn) {
			Ingredient.CONTENTS_STREAM_CODEC.encode(extraDataIn, recipeIn.input);
			ItemStack.STREAM_CODEC.encode(extraDataIn, recipeIn.result);
		}
	}
}