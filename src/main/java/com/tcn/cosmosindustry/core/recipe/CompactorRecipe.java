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

public class CompactorRecipe implements Recipe<RecipeInput> {
	public final Ingredient input;
	public final ItemStack result;

	public CompactorRecipe(Ingredient inputIn, ItemStack resultIn) {
		this.input = inputIn;
		this.result = resultIn;
	}

	@Override
	public boolean matches(RecipeInput recipeInput, Level levelIn) {
		boolean flagInput = this.input.test(recipeInput.getItem(0));
		
		return flagInput;
	}

	@Override
	public ItemStack assemble(RecipeInput recipeInputIn, HolderLookup.Provider accessIn) {
		ItemStack itemstack = recipeInputIn.getItem(0).transmuteCopy(this.result.getItem(), this.result.getCount());
        itemstack.applyComponents(this.result.getComponentsPatch());
        return itemstack;
	}

	@Override
	public boolean canCraftInDimensions(int xIn, int yIn) {
		return xIn * yIn >= 2;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider accessIn) {
		return this.result;
	}
	
	public boolean isInputIngredient(ItemStack stackIn) {
		return this.input.test(stackIn);
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModRegistrationManager.BLOCK_COMPACTOR.get());
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipeManager.RECIPE_SERIALIZER_COMPACTING.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipeManager.RECIPE_TYPE_COMPACTING.get();
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

	public static class Serializer implements RecipeSerializer<CompactorRecipe> {
		public static final Serializer INSTANCE = new Serializer();
		public static final MapCodec<CompactorRecipe> CODEC = RecordCodecBuilder.mapCodec(
	            instance -> instance.group(
	    			Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(recipe -> recipe.input),
	    			ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
	            ).apply(instance, CompactorRecipe::new)
	        );
			
			public static final StreamCodec<RegistryFriendlyByteBuf, CompactorRecipe> STREAM_CODEC = StreamCodec.of(
					CompactorRecipe.Serializer::toNetwork, CompactorRecipe.Serializer::fromNetwork
			);
		    
	    @Override
	    public MapCodec<CompactorRecipe> codec() {
	        return CODEC;
	    }
	    
	    @Override
	    public StreamCodec<RegistryFriendlyByteBuf, CompactorRecipe> streamCodec() {
	        return STREAM_CODEC;
	    }
	    
		private static CompactorRecipe fromNetwork(RegistryFriendlyByteBuf extraDataIn) {
			return new CompactorRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(extraDataIn), ItemStack.STREAM_CODEC.decode(extraDataIn));
		}

		private static void toNetwork(RegistryFriendlyByteBuf extraDataIn, CompactorRecipe recipeIn) {
			Ingredient.CONTENTS_STREAM_CODEC.encode(extraDataIn, recipeIn.input);
			
			ItemStack.STREAM_CODEC.encode(extraDataIn, recipeIn.result);
		}
	}
}