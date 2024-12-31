package com.tcn.cosmosindustry.processing.client.screen;

import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.processing.client.container.ContainerLaserCutter;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityLaserCutter;
import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeBE;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ScreenLaserCutter extends CosmosScreenUIModeBE<ContainerLaserCutter> {
	
	public ScreenLaserCutter(ContainerLaserCutter containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(176, 177);
		this.setLight(IndustryReference.Resource.Processing.Gui.LASER_CUTTER_LIGHT);
		this.setDark(IndustryReference.Resource.Processing.Gui.LASER_CUTTER_LIGHT);
		this.setUIModeButtonIndex(159, 5);
		
		this.setTitleLabelDims(28, 4);
		this.setInventoryLabelDims(7, 84);
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
		
		if (this.getBlockEntity() instanceof BlockEntityLaserCutter blockEntity) {
			if (blockEntity.canProcess(blockEntity.getRecipeUsed(), blockEntity.getLevel().registryAccess())) {
				int k = blockEntity.getProcessProgressScaled(16);
				
				CosmosUISystem.Render.renderScaledElementDownNestled(graphicsIn, this.getScreenCoords(), 99, 39, 176, 0, 16, blockEntity.getProcessProgressScaled(16), IndustryReference.Resource.Processing.Gui.LASER_CUTTER_LIGHT);
			}
			
			CosmosUISystem.Render.renderEnergyDisplay(graphicsIn, ComponentColour.RED, blockEntity, this.getScreenCoords(), 54, 17, 16, 60, false);
		}
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();
	}

	@Override
	public void clickButton(Button button, boolean isLeftClick) {
		super.clickButton(button, isLeftClick);
	}
	
	@Override
	public void renderStandardHoverEffect(GuiGraphics graphicsIn, Style style, int mouseX, int mouseY) {
		if (this.getBlockEntity() instanceof BlockEntityLaserCutter blockEntity) {
			if (CosmosUISystem.Hovering.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 54,  this.getScreenCoords()[0] + 70,  this.getScreenCoords()[1] + 16,  this.getScreenCoords()[1] + 76)) {
				DecimalFormat formatter = new DecimalFormat("#,###,###,###");
				String amount_string = formatter.format(blockEntity.getEnergyStored());
				String capacity_string = formatter.format(blockEntity.getMaxEnergyStored());
				
				Component[] comp = new Component[] {
					ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.energy_bar.pre"),
					ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "cosmoslibrary.gui.energy_bar.suff")
				};

				Component[] compProcessing = new Component[] {
					ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.energy_bar.pre"),
					ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "cosmoslibrary.gui.energy_bar.suff"),
					ComponentHelper.style3(ComponentColour.PURPLE, "cosmoslibrary.gui.energy.fe_pre", "" + blockEntity.getRFTickRate(), "cosmoslibrary.gui.energy.fe_rate")
				};
				
				graphicsIn.renderComponentTooltip(this.font, blockEntity.isProcessing() ? Arrays.asList(compProcessing) : Arrays.asList(comp), mouseX, mouseY);
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