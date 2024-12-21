package com.tcn.cosmosindustry.core.recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tcn.cosmosindustry.core.management.ModRecipeManager;
import com.tcn.cosmosindustry.core.management.ModRegistrationManager;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class SynthesiserRecipe implements Recipe<RecipeInput> {
	public final Ingredient focus;
	public final List<Ingredient> inputs;
	public final ItemStack result;
	public final int processTime;
	public final ComponentColour colour;

	public SynthesiserRecipe(Ingredient focusIn, List<Ingredient> inputsIn, ItemStack resultIn, int processTimeIn,
			ComponentColour colourIn) {
		this.focus = focusIn;
		this.inputs = inputsIn;
		this.result = resultIn;

		this.processTime = processTimeIn;
		this.colour = colourIn;
	}

	@Override
	public boolean matches(RecipeInput recipeInputIn, Level levelIn) {
		if (recipeInputIn instanceof SynthesiserRecipeInput input) {
			if (!ItemStack.isSameItem(input.getFocus(), this.focus.getItems()[0])) {
				return false;
			}
			if (recipeInputIn.size() != this.inputs.size()) {
				return false;
			}
			
			int ingredients = input.size();
			
			for (int j = 0; j <= (int)(input.size() / 2); j++) {
				for (int i = 0; i < input.size(); i++) {
					if (this.inputs.get(i).test(input.getItem(i))) {
						if (ingredients > 0) {
							ingredients--;
						}
					}
				}
			}
			
			return ingredients == 0;
		}
		return false;
	}

	@Override
	public ItemStack assemble(RecipeInput recipeInputIn, HolderLookup.Provider provider) {
		ItemStack itemstack = recipeInputIn.getItem(0).transmuteCopy(this.result.getItem(), this.result.getCount());
		itemstack.applyComponents(this.result.getComponentsPatch());
		return itemstack;
	}

	public int getProcessTime() {
		return this.processTime;
	}

	public ComponentColour getRecipeColour() {
		return this.colour;
	}

	public ItemStack getFocus() {
		return this.focus.getItems()[0];
	}
	
	public static <T> boolean equalsIgnoreOrder(List<ItemStack> a, List<ItemStack> b) {
		return new HashSet<>(a).equals(new HashSet<>(b));
	}

	@Override
	public boolean canCraftInDimensions(int xIn, int yIn) {
		return xIn * yIn >= 2;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider providerIn) {
		return this.result;
	}

	public boolean isFocusIngredient(ItemStack stackIn) {
		return this.focus.test(stackIn);
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(ModRegistrationManager.BLOCK_SYNTHESISER.get());
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipeManager.RECIPE_SERIALIZER_SYNTHESISING.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModRecipeManager.RECIPE_TYPE_SYNTHESISING.get();
	}

	@Override
	public boolean isIncomplete() {
		List<Ingredient> nonnulllist = this.getIngredientList();
		return nonnulllist.isEmpty() || this.focus.isEmpty() || nonnulllist.stream().filter(ingredient -> !ingredient.isEmpty()).anyMatch(Ingredient::hasNoItems);
	}

	public List<ItemStack> getStackList() {
		List<ItemStack> stackList = new ArrayList<>();

		stackList.add(this.focus.getItems()[0]);

		for (int i = 0; i < this.inputs.size(); i++) {
			Ingredient ingredient = this.inputs.get(i);

			if (!ingredient.isEmpty()) {
				stackList.add(ingredient.getItems()[0]);
			}
		}

		return stackList;
	}

	public List<Ingredient> getIngredientList() {
		List<Ingredient> stackList = new ArrayList<>();

		for (int i = 0; i < this.inputs.size(); i++) {
			Ingredient ingredient = this.inputs.get(i);

			if (!ingredient.isEmpty()) {
				stackList.add(ingredient);
			}
		}

		return stackList;
	}

	public static class Serializer implements RecipeSerializer<SynthesiserRecipe> {
		public static final MapCodec<SynthesiserRecipe> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(Ingredient.CODEC_NONEMPTY.fieldOf("focus").forGetter(recipe -> recipe.focus),
					Ingredient.LIST_CODEC.fieldOf("inputs").forGetter(recipe -> recipe.inputs),
					ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
					Codec.INT.fieldOf("processTime").forGetter(recipe -> recipe.processTime),
					ComponentColour.CODEC.fieldOf("recipeColour").forGetter(recipe -> recipe.colour)

			).apply(instance, SynthesiserRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, SynthesiserRecipe> STREAM_CODEC = StreamCodec
				.of(SynthesiserRecipe.Serializer::toNetwork, SynthesiserRecipe.Serializer::fromNetwork);

		@Override
		public MapCodec<SynthesiserRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, SynthesiserRecipe> streamCodec() {
			return STREAM_CODEC;
		}

		private static SynthesiserRecipe fromNetwork(RegistryFriendlyByteBuf extraDataIn) {
			int size = extraDataIn.readInt();

			Ingredient focus = Ingredient.CONTENTS_STREAM_CODEC.decode(extraDataIn);

			ArrayList<Ingredient> inputs = new ArrayList<Ingredient>();
			for (int i = 0; i < size; i++) {
				inputs.add(i, Ingredient.CONTENTS_STREAM_CODEC.decode(extraDataIn));
			}

			ItemStack result = ItemStack.STREAM_CODEC.decode(extraDataIn);

			int processTime = extraDataIn.readInt();
			ComponentColour colour = ComponentColour.STREAM_CODEC.decode(extraDataIn);

			return new SynthesiserRecipe(focus, inputs, result, processTime, colour);
		}

		private static void toNetwork(RegistryFriendlyByteBuf extraDataIn, SynthesiserRecipe recipeIn) {
			extraDataIn.writeInt(recipeIn.inputs.size());

			Ingredient.CONTENTS_STREAM_CODEC.encode(extraDataIn, recipeIn.focus);

			for (Ingredient ingredient : recipeIn.inputs) {
				Ingredient.CONTENTS_STREAM_CODEC.encode(extraDataIn, ingredient);
			}

			ItemStack.STREAM_CODEC.encode(extraDataIn, recipeIn.result);

			extraDataIn.writeInt(recipeIn.processTime);

			ComponentColour.STREAM_CODEC.encode(extraDataIn, recipeIn.colour);
		}
	}
}