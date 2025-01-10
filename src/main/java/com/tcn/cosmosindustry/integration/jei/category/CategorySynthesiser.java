package com.tcn.cosmosindustry.integration.jei.category;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.core.recipe.SynthesiserRecipe;
import com.tcn.cosmosindustry.integration.jei.ModJEIRecipeTypes;
import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CategorySynthesiser implements IRecipeCategory<SynthesiserRecipe> {

	private IGuiHelper helper;
	private final IDrawable background;
	
	private final IDrawable laser[][] = new IDrawable[8][8];

	private final ResourceLocation jeiLoc = IndustryReference.Resource.Processing.Gui.SYNTHESISER_JEI;
	private final ResourceLocation laserLoc = IndustryReference.Resource.Processing.Gui.SYNTHESISER_JEI_LASER;
	
	/**
	 *  UP 0 DOWN 1 LEFT 2  RIGHT 3 TOP_LEFT 4 TOP_RIGHT 5 BOTTOM_LEFT 6 BOTTOM_RIGHT 7
	 */
	private final int[][][] laserArray = new int[][][] {
		new int[][] { new int[] { 0, 0 } },
		new int[][] { new int[] { 0, 0 }, new int[] { 60,  0 }, },
		new int[][] { new int[] { 0, 0 }, new int[] { 120, 60 }, new int[] { 180, 60 }, },
		new int[][] { new int[] { 0, 0 }, new int[] { 60,  0 },  new int[] { 120, 0 }, new int[] { 180, 0 }, },
		new int[][] { new int[] { 0, 0 }, new int[] { 60,  0 },  new int[] { 120, 0 }, new int[] { 180, 0 }, new int[] { 0, 60 }, },
		new int[][] { new int[] { 0, 0 }, new int[] { 60,  0 },  new int[] { 120, 0 }, new int[] { 180, 0 }, new int[] { 0, 60 }, new int[] { 180, 60 }, },
		new int[][] { new int[] { 0, 0 }, new int[] { 60,  0 },  new int[] { 120, 0 }, new int[] { 180, 0 }, new int[] { 0, 60 }, new int[] { 180, 60 }, new int[] { 60, 60 }, },
		new int[][] { new int[] { 0, 0 }, new int[] { 60,  0 },  new int[] { 120, 0 }, new int[] { 180, 0 }, new int[] { 0, 60 }, new int[] { 180, 60 }, new int[] { 60, 60 }, new int[] { 120, 60 }, }
	};
	
	/**
	 *  UP 0 DOWN 1 LEFT 2  RIGHT 3 TOP_LEFT 4 TOP_RIGHT 5 BOTTOM_LEFT 6 BOTTOM_RIGHT 7
	 */
	private final int[][][] slotArray = new int[][][] { 
		new int[][] { new int[] { 54, 22 } }, new int[][] { new int[] { 54, 22 }, new int[] { 54, 106 }, },
		new int[][] { new int[] { 54, 22 },   new int[] { 20,  98 }, new int[] { 88, 98 }, },
		new int[][] { new int[] { 54, 22 },   new int[] { 54, 106 }, new int[] { 12, 64 }, new int[] { 96, 64 }, },
		new int[][] { new int[] { 54, 22 },   new int[] { 54, 106 }, new int[] { 12, 64 }, new int[] { 96, 64 }, new int[] { 20, 30 }, },
		new int[][] { new int[] { 54, 22 },   new int[] { 54, 106 }, new int[] { 12, 64 }, new int[] { 96, 64 }, new int[] { 20, 30 }, new int[] { 88, 98 }, },
		new int[][] { new int[] { 54, 22 },   new int[] { 54, 106 }, new int[] { 12, 64 }, new int[] { 96, 64 }, new int[] { 20, 30 }, new int[] { 88, 98 }, new int[] { 88, 30 }, },
		new int[][] { new int[] { 54, 22 },   new int[] { 54, 106 }, new int[] { 12, 64 }, new int[] { 96, 64 }, new int[] { 20, 30 }, new int[] { 88, 98 }, new int[] { 88, 30 }, new int[] { 20, 98 }, }
	};
	
	
	public CategorySynthesiser(IGuiHelper helperIn) {
		this.helper = helperIn;
		
		this.background = this.helper.createDrawable(jeiLoc, 0, 0, 182, 222);
	}

	@Override
	public RecipeType<SynthesiserRecipe> getRecipeType() {
		return ModJEIRecipeTypes.SYNTHESISING;
	}
	
	@Override
	public Component getTitle() {
		return ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.integration.jei.synthesiser_category");
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(IndustryRegistrationManager.BLOCK_SYNTHESISER));
	}
	
	@Override
	public void draw(SynthesiserRecipe recipe, IRecipeSlotsView viewIn, GuiGraphics graphicsIn, double mouseX, double mouseY) {
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		int size = recipe.inputs.size();
		
		this.helper.createDrawable(jeiLoc, 182, size < 5 ? 0 : 32, 32, 32).draw(graphicsIn, 140, 19);
		this.helper.createDrawable(jeiLoc, 182, 0, 32, 32).draw(graphicsIn, 140, 54);
		
		CosmosUISystem.Setup.setTextureColour(recipe.getRecipeColour());
		this.drawLaserArray(graphicsIn, size);
		
		CosmosUISystem.Setup.setTextureColour(ComponentColour.WHITE);

		CosmosUISystem.FontRenderer.drawString(graphicsIn, font, new int[] { 0, 0 }, 135, 5, false, ComponentHelper.style2(ComponentColour.BLACK, "cosmosindustry.integration.jei.synthesiser_time", Integer.toString(recipe.getProcessTime())));
		
		CosmosUISystem.FontRenderer.drawString(graphicsIn, font, new int[] { 0, 0 }, 5, 5, false, ComponentHelper.style(ComponentColour.BLACK, "cosmosindustry.integration.jei.synthesiser_category"));
		
		CosmosUISystem.FontRenderer.drawString(graphicsIn, font, new int[] { 0, 0 }, 6, 150, false, ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.gui.synthesiser.jei.one"));
		CosmosUISystem.FontRenderer.drawString(graphicsIn, font, new int[] { 0, 0 }, 6, 160, false, ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.gui.synthesiser.jei.two"));
		CosmosUISystem.FontRenderer.drawString(graphicsIn, font, new int[] { 0, 0 }, 6, 170, false, ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.gui.synthesiser.jei.three"));
		CosmosUISystem.FontRenderer.drawString(graphicsIn, font, new int[] { 0, 0 }, 6, 180, false, ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.gui.synthesiser.jei.four"));
		CosmosUISystem.FontRenderer.drawString(graphicsIn, font, new int[] { 0, 0 }, 6, 190, false, ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.gui.synthesiser.jei.five"));
		CosmosUISystem.FontRenderer.drawString(graphicsIn, font, new int[] { 0, 0 }, 6, 200, false, ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.gui.synthesiser.jei.six"));
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder recipeLayout, SynthesiserRecipe recipe, IFocusGroup ingredients) {
		int size = recipe.inputs.size();
		
		this.setUpLaserArray(size);
		recipeLayout.addSlot(RecipeIngredientRole.INPUT, 55, 65).addIngredients(recipe.focus);
				
		for (int i = 0; i < size; i++) {
			recipeLayout.addSlot(RecipeIngredientRole.INPUT, this.slotArray[size - 1][i][0] + 1, this.slotArray[size - 1][i][1] + 1).addIngredients(recipe.inputs.get(i));
		}

		recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 148, 103).addItemStack(recipe.result);
	}
	

	public void setUpLaserArray(int size) {
		for (int i = 0; i < size; i++) {
			this.laser[size - 1][i] = this.helper.createDrawable(this.laserLoc, laserArray[size - 1][i][0], laserArray[size - 1][i][1], 60, 60);
		}
	}
	
	public void drawLaserArray(GuiGraphics graphicsIn, int size) {
		for (int i = 0; i < size; i++) {
			IDrawable drawable = this.laser[size - 1][i];
			
			if (drawable != null) {
				drawable.draw(graphicsIn, 33, 43);
			}
		}
	}
}