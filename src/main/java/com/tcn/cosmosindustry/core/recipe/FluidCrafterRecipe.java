package com.tcn.cosmosindustry.core.recipe;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tcn.cosmosindustry.core.management.IndustryRecipeManager;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityFluidCrafter;

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

public class FluidCrafterRecipe implements Recipe<RecipeInput> {
	private final Ingredient input;
	private final ItemStack result;
	private final FluidStack fluidStack;
	private final BlockEntityFluidCrafter.PlantMode mode;

	public FluidCrafterRecipe(Ingredient inputIn, ItemStack resultIn, FluidStack fluidStackIn, BlockEntityFluidCrafter.PlantMode modeIn) {
		this.input = inputIn;
		this.result = resultIn;
		this.fluidStack = fluidStackIn;
		this.mode = modeIn;
	}

	@Override
	public boolean matches(RecipeInput recipeInput, Level levelIn) {
		if (recipeInput instanceof FluidCrafterRecipeInput fluidRecipeInput) {
			BlockEntityFluidCrafter.PlantMode recipeMode = fluidRecipeInput.getMode();
			
			if (recipeMode.equals(this.mode)) {
				if (recipeMode.equals(BlockEntityFluidCrafter.PlantMode.INFUSING)) {
					boolean flagInput = this.input.test(fluidRecipeInput.getItem(0));
					boolean flagFluid = fluidRecipeInput.getFluidStack().getFluid().equals(this.fluidStack.getFluid());
					boolean result = !this.getResult().isEmpty();

					return flagInput && flagFluid && result;
				} else {
					boolean flagInput = this.input.test(fluidRecipeInput.getItem(0));
					boolean flagFluid = fluidRecipeInput.getFluidStack().getFluid().equals(this.fluidStack.getFluid()) || fluidRecipeInput.getFluidStack().isEmpty();
					
					return flagInput && flagFluid;
				}
			} else {
				return false;
			}
		} else {
			return this.input.test(recipeInput.getItem(0));
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
	
	public boolean isInputIngredient(ItemStack stackIn) {
		return this.input.test(stackIn);
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(IndustryRegistrationManager.BLOCK_FLUID_CRAFTER.get());
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return IndustryRecipeManager.RECIPE_SERIALIZER_FLUID_CRAFTER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return IndustryRecipeManager.RECIPE_TYPE_FLUID_CRAFTER.get();
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
	
	public Ingredient getInput() {
		return this.input;
	}

	public ItemStack getResult() {
		return this.result;
	}
	
	public FluidStack getFluidStack() {
		return this.fluidStack;
	}
	
	public BlockEntityFluidCrafter.PlantMode getPlantMode() {
		return this.mode;
	}
	
	public static class Serializer implements RecipeSerializer<FluidCrafterRecipe> {
		public static final Serializer INSTANCE = new Serializer();
		public static final MapCodec<FluidCrafterRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
    			Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(recipe -> recipe.input),
    			ItemStack.CODEC.optionalFieldOf("result", ItemStack.EMPTY).forGetter(recipe -> recipe.result),
    			FluidStack.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.fluidStack),
    			BlockEntityFluidCrafter.PlantMode.CODEC.fieldOf("mode").forGetter(recipe -> recipe.mode)
            ).apply(instance, FluidCrafterRecipe::new)
        );
		
		public static final StreamCodec<RegistryFriendlyByteBuf, FluidCrafterRecipe> STREAM_CODEC = StreamCodec.of(
				FluidCrafterRecipe.Serializer::toNetwork, FluidCrafterRecipe.Serializer::fromNetwork
		);
		    
	    @Override
	    public MapCodec<FluidCrafterRecipe> codec() {
	        return CODEC;
	    }
	    
	    @Override
	    public StreamCodec<RegistryFriendlyByteBuf, FluidCrafterRecipe> streamCodec() {
	        return STREAM_CODEC;
	    }
	    
		private static FluidCrafterRecipe fromNetwork(RegistryFriendlyByteBuf extraDataIn) {
			Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(extraDataIn);
			boolean result = extraDataIn.readBoolean();
			
			return new FluidCrafterRecipe(input, result ? ItemStack.STREAM_CODEC.decode(extraDataIn) : ItemStack.EMPTY, FluidStack.STREAM_CODEC.decode(extraDataIn), BlockEntityFluidCrafter.PlantMode.STREAM_CODEC.decode(extraDataIn));
		}

		private static void toNetwork(RegistryFriendlyByteBuf extraDataIn, FluidCrafterRecipe recipeIn) {
			Ingredient.CONTENTS_STREAM_CODEC.encode(extraDataIn, recipeIn.input);
			
			boolean result = !recipeIn.result.isEmpty();
			extraDataIn.writeBoolean(result);
			if (result) {
				ItemStack.STREAM_CODEC.encode(extraDataIn, recipeIn.result);
			}
			
			FluidStack.STREAM_CODEC.encode(extraDataIn, recipeIn.fluidStack);
			BlockEntityFluidCrafter.PlantMode.STREAM_CODEC.encode(extraDataIn, recipeIn.mode);
		}
	}
}