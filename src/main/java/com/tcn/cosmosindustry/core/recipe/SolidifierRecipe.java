package com.tcn.cosmosindustry.core.recipe;

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
import net.neoforged.neoforge.fluids.FluidStack;

public class SolidifierRecipe implements Recipe<RecipeInput> {
	private final ItemStack result;
	private final FluidStack fluidStack;

	public SolidifierRecipe(ItemStack resultIn, FluidStack fluidStackIn) {
		this.result = resultIn;
		this.fluidStack = fluidStackIn;
	}

	@Override
	public boolean matches(RecipeInput recipeInput, Level levelIn) {
		if (recipeInput instanceof SolidifierRecipeInput fluidRecipeInput) {
			boolean fluidFlag = fluidRecipeInput.getFluidStack().getFluid().equals(this.fluidStack.getFluid());
			
			return fluidFlag;
		} else {
			return false;
		}
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
	
	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(IndustryRegistrationManager.BLOCK_SOLIDIFIER.get());
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return IndustryRecipeManager.RECIPE_SERIALIZER_SOLIDIFIER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return IndustryRecipeManager.RECIPE_TYPE_SOLIDIFIER.get();
	}
	
	@Override
	public boolean isIncomplete() {
		return true;
//		return Stream.of(this.input).anyMatch((ingredient) -> {
//			return ingredient.getItems().length == 0;
//		});
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY);
	}
	
	public ItemStack getResult() {
		return this.result;
	}
	
	public FluidStack getFluidStack() {
		return this.fluidStack;
	}
	
	public static class Serializer implements RecipeSerializer<SolidifierRecipe> {
		public static final Serializer INSTANCE = new Serializer();
		public static final MapCodec<SolidifierRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
    			ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
    			FluidStack.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.fluidStack)
            ).apply(instance, SolidifierRecipe::new)
        );
		
		public static final StreamCodec<RegistryFriendlyByteBuf, SolidifierRecipe> STREAM_CODEC = StreamCodec.of(
				SolidifierRecipe.Serializer::toNetwork, SolidifierRecipe.Serializer::fromNetwork
		);
		    
	    @Override
	    public MapCodec<SolidifierRecipe> codec() {
	        return CODEC;
	    }
	    
	    @Override
	    public StreamCodec<RegistryFriendlyByteBuf, SolidifierRecipe> streamCodec() {
	        return STREAM_CODEC;
	    }
	    
		private static SolidifierRecipe fromNetwork(RegistryFriendlyByteBuf extraDataIn) {
			return new SolidifierRecipe(ItemStack.STREAM_CODEC.decode(extraDataIn), FluidStack.STREAM_CODEC.decode(extraDataIn));
		}

		private static void toNetwork(RegistryFriendlyByteBuf extraDataIn, SolidifierRecipe recipeIn) {
			ItemStack.STREAM_CODEC.encode(extraDataIn, recipeIn.result);
			FluidStack.STREAM_CODEC.encode(extraDataIn, recipeIn.fluidStack);
		}
	}
}