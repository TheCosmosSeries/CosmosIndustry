package com.tcn.cosmosindustry.core.recipe;

import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tcn.cosmosindustry.core.management.IndustryRecipeManager;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityOrePlant;

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

public class OrePlantRecipe implements Recipe<RecipeInput> {
	private final Ingredient input;
	private final ItemStack result;
	private final FluidStack fluidStack;
	private final BlockEntityOrePlant.PlantMode mode;

	public OrePlantRecipe(Ingredient inputIn, ItemStack resultIn, FluidStack fluidStackIn, BlockEntityOrePlant.PlantMode modeIn) {
		this.input = inputIn;
		this.result = resultIn;
		this.fluidStack = fluidStackIn;
		this.mode = modeIn;
	}

	@Override
	public boolean matches(RecipeInput recipeInput, Level levelIn) {
		if (recipeInput instanceof OrePlantRecipeInput oreRecipeInput) {
			BlockEntityOrePlant.PlantMode recipeMode = oreRecipeInput.getMode();
			
			if (recipeMode.equals(this.mode)) {
				if (recipeMode.equals(BlockEntityOrePlant.PlantMode.CLEANING)) {
					boolean flagInput = this.input.test(oreRecipeInput.getItem(0));
					boolean flagFluid = oreRecipeInput.getFluidStack().getFluid().equals(this.fluidStack.getFluid());

					return flagInput & flagFluid;
				} else {
					boolean flagInput = this.input.test(oreRecipeInput.getItem(0));
					return flagInput;
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
		return new ItemStack(IndustryRegistrationManager.BLOCK_ORE_PLANT.get());
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return IndustryRecipeManager.RECIPE_SERIALIZER_ORE_PLANT.get();
	}

	@Override
	public RecipeType<?> getType() {
		return IndustryRecipeManager.RECIPE_TYPE_ORE_PLANT.get();
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
	
	public BlockEntityOrePlant.PlantMode getPlantMode() {
		return this.mode;
	}
	
	public static class Serializer implements RecipeSerializer<OrePlantRecipe> {
		public static final Serializer INSTANCE = new Serializer();
		public static final MapCodec<OrePlantRecipe> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
    			Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(recipe -> recipe.input),
    			ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
    			FluidStack.CODEC.optionalFieldOf("fluid", FluidStack.EMPTY).forGetter(recipe -> recipe.fluidStack),
    			BlockEntityOrePlant.PlantMode.CODEC.fieldOf("mode").forGetter(recipe -> recipe.mode)
            ).apply(instance, OrePlantRecipe::new)
        );
		
		public static final StreamCodec<RegistryFriendlyByteBuf, OrePlantRecipe> STREAM_CODEC = StreamCodec.of(
				OrePlantRecipe.Serializer::toNetwork, OrePlantRecipe.Serializer::fromNetwork
		);
		    
	    @Override
	    public MapCodec<OrePlantRecipe> codec() {
	        return CODEC;
	    }
	    
	    @Override
	    public StreamCodec<RegistryFriendlyByteBuf, OrePlantRecipe> streamCodec() {
	        return STREAM_CODEC;
	    }
	    
		private static OrePlantRecipe fromNetwork(RegistryFriendlyByteBuf extraDataIn) {
			Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(extraDataIn);
			ItemStack output = ItemStack.STREAM_CODEC.decode(extraDataIn);
			
			boolean fluid = extraDataIn.readBoolean();
			
			return new OrePlantRecipe(input, output, fluid ? FluidStack.STREAM_CODEC.decode(extraDataIn) : FluidStack.EMPTY, BlockEntityOrePlant.PlantMode.STREAM_CODEC.decode(extraDataIn));
		}

		private static void toNetwork(RegistryFriendlyByteBuf extraDataIn, OrePlantRecipe recipeIn) {
			Ingredient.CONTENTS_STREAM_CODEC.encode(extraDataIn, recipeIn.input);
			ItemStack.STREAM_CODEC.encode(extraDataIn, recipeIn.result);
			
			boolean fluid = recipeIn.getPlantMode().equals(BlockEntityOrePlant.PlantMode.CLEANING);
			extraDataIn.writeBoolean(fluid);
			
			if (fluid) {
				FluidStack.STREAM_CODEC.encode(extraDataIn, recipeIn.fluidStack);
			}
			
			BlockEntityOrePlant.PlantMode.STREAM_CODEC.encode(extraDataIn, recipeIn.mode);
		}
	}
}