package com.tcn.cosmosindustry.core.recipe;

import java.util.ArrayList;
import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tcn.cosmosindustry.core.management.IndustryRecipeManager;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;

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

public class SeparatorRecipe implements Recipe<RecipeInput> {
	public final Ingredient input;
	public final ItemStack result;
	public final ItemStack secondaryOutput;

	public SeparatorRecipe(Ingredient inputIn, ItemStack resultIn, ItemStack secondaryOutputIn) {
		this.input = inputIn;
		this.result = resultIn;
		this.secondaryOutput = secondaryOutputIn;
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
	
	public ItemStack getSecondaryResultItem() {
		return this.secondaryOutput;
	}

	public boolean isInputIngredient(ItemStack stackIn) {
		return this.input.test(stackIn);
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(IndustryRegistrationManager.BLOCK_SEPARATOR.get());
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return IndustryRecipeManager.RECIPE_SERIALIZER_SEPARATING.get();
	}

	@Override
	public RecipeType<?> getType() {
		return IndustryRecipeManager.RECIPE_TYPE_SEPARATING.get();
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
		
		if (!this.secondaryOutput.isEmpty()) {
			array.add(this.secondaryOutput);
		}
		
		return array;
	}
	
	public static class Serializer implements RecipeSerializer<SeparatorRecipe> {
		public static final Serializer INSTANCE = new Serializer();
		public static final MapCodec<SeparatorRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
    			Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(recipe -> recipe.input),
    			ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                ItemStack.CODEC.optionalFieldOf("secondaryResult", ItemStack.EMPTY).forGetter(recipe -> recipe.secondaryOutput)
            ).apply(instance, SeparatorRecipe::new)
        );
		
		public static final StreamCodec<RegistryFriendlyByteBuf, SeparatorRecipe> STREAM_CODEC = StreamCodec.of(
				SeparatorRecipe.Serializer::toNetwork, SeparatorRecipe.Serializer::fromNetwork
		);
		    
	    @Override
	    public MapCodec<SeparatorRecipe> codec() {
	        return CODEC;
	    }
	    
	    @Override
	    public StreamCodec<RegistryFriendlyByteBuf, SeparatorRecipe> streamCodec() {
	        return STREAM_CODEC;
	    }

		private static SeparatorRecipe fromNetwork(RegistryFriendlyByteBuf extraDataIn) {
			boolean secondary = extraDataIn.readBoolean();
			
			Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(extraDataIn);
			ItemStack result =  ItemStack.STREAM_CODEC.decode(extraDataIn);
			
			ItemStack secondaryStack = secondary ? ItemStack.STREAM_CODEC.decode(extraDataIn) : ItemStack.EMPTY;
			return new SeparatorRecipe(input, result, secondaryStack);
		}

		private static void toNetwork(RegistryFriendlyByteBuf extraDataIn, SeparatorRecipe recipeIn) {
			boolean secondary = recipeIn.secondaryOutput != ItemStack.EMPTY;
			extraDataIn.writeBoolean(secondary);
			
			Ingredient.CONTENTS_STREAM_CODEC.encode(extraDataIn, recipeIn.input);
			ItemStack.STREAM_CODEC.encode(extraDataIn, recipeIn.result);
			
			if (secondary) {
				ItemStack.STREAM_CODEC.encode(extraDataIn, recipeIn.secondaryOutput);
			}
		}
	}
}