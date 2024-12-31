package com.tcn.cosmosindustry.processing.client.screen;

import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.processing.client.container.ContainerSynthesiser;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiser;
import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeBE;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenSynthesiser extends CosmosScreenUIModeBE<ContainerSynthesiser> {
		
	public ScreenSynthesiser(ContainerSynthesiser containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(176, 177);
		this.setLight(IndustryReference.Resource.Processing.Gui.SYNTHESISER_LIGHT);
		this.setDark(IndustryReference.Resource.Processing.Gui.SYNTHESISER_DARK);
		this.setUIModeButtonIndex(159, 5);
		
		this.setTitleLabelDims(38, 4);
		this.setInventoryLabelDims(8, 85);
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void render(GuiGraphics graphicsIn, int mouseX, int mouseY, float partialTicks) {
		super.render(graphicsIn, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(GuiGraphics graphicsIn, float ticks, int mouseX, int mouseY) {
		super.renderBg(graphicsIn, ticks, mouseX, mouseY);
		
		if (this.getBlockEntity() instanceof BlockEntitySynthesiser blockEntity) {
			CosmosUISystem.Render.renderStaticElementToggled(graphicsIn, this.getScreenCoords(), 96,  16, 176, 36, 18, 18, blockEntity.isSetupFourWay(), IndustryReference.Resource.Processing.Gui.SYNTHESISER_LIGHT);
			CosmosUISystem.Render.renderStaticElementToggled(graphicsIn, this.getScreenCoords(), 116, 16, 176, 0,  18, 18, blockEntity.isSetupFourWay(), IndustryReference.Resource.Processing.Gui.SYNTHESISER_LIGHT);
			CosmosUISystem.Render.renderStaticElementToggled(graphicsIn, this.getScreenCoords(), 96,  60, 176, 18, 18, 18, blockEntity.isSetupEightWay(), IndustryReference.Resource.Processing.Gui.SYNTHESISER_LIGHT);
			CosmosUISystem.Render.renderStaticElementToggled(graphicsIn, this.getScreenCoords(), 116, 60, 176, 0,  18, 18, blockEntity.isSetupEightWay(), IndustryReference.Resource.Processing.Gui.SYNTHESISER_LIGHT);
			
			CosmosUISystem.Render.renderEnergyDisplay(graphicsIn, ComponentColour.RED, blockEntity, getScreenCoords(), 43, 17, 16, 60, false);
		}
	}
	
	@Override
	public void renderStandardHoverEffect(GuiGraphics graphicsIn, Style style, int mouseX, int mouseY) {
		if (this.getBlockEntity() instanceof BlockEntitySynthesiser blockEntity) {
			if (CosmosUISystem.Hovering.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 43, this.getScreenCoords()[0] + 43 + 15, this.getScreenCoords()[1] + 17, this.getScreenCoords()[1] + 17 + 59)) {
				DecimalFormat formatter = new DecimalFormat("#,###,###,###");
				String amount_string = formatter.format(blockEntity.getEnergyStored());
				String capacity_string = formatter.format(blockEntity.getMaxEnergyStored());
				
				Component[] comp = new Component[] { 
					ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.energy_bar.pre"),
					ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "cosmoslibrary.gui.energy_bar.suff") 
				};
				
				graphicsIn.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}
		}
	}
	
	@Override
	protected boolean isHovering(int positionX, int positionY, int width, int height, double mouseX, double mouseY) {
		return super.isHovering(positionX, positionY, width, height, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void slotClicked(Slot slotIn, int mouseX, int mouseY, ClickType clickTypeIn) {
		super.slotClicked(slotIn, mouseX, mouseY, clickTypeIn);
	}
}