package com.tcn.cosmosindustry.production.client.screen;

import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.production.client.container.ContainerSolarPanel;
import com.tcn.cosmosindustry.production.core.blockentity.BlockEntitySolarPanel;
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

@OnlyIn(Dist.CLIENT)
public class ScreenSolarPanel extends CosmosScreenUIModeBE<ContainerSolarPanel> {
	
	public ScreenSolarPanel(ContainerSolarPanel containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(176, 177);
		this.setLight(IndustryReference.Resource.Production.Gui.SOLAR_PANEL_LIGHT);
		this.setDark(IndustryReference.Resource.Production.Gui.SOLAR_PANEL_DARK);
		this.setUIModeButtonIndex(159, 5);
		
		this.setTitleLabelDimsCentered(0, 4);
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

		if (this.getBlockEntity() instanceof BlockEntitySolarPanel blockEntity) {
			CosmosUISystem.Render.renderEnergyDisplay(graphicsIn, ComponentColour.RED, blockEntity, this.getScreenCoords(), 77, 17, 16, 60, false);
			
			if (blockEntity.isProducing()) {
				CosmosUISystem.Render.renderStaticElement(graphicsIn, this.getScreenCoords(), 104, 38, 176, 0, 18, 18, IndustryReference.Resource.Production.Gui.SOLAR_PANEL_LIGHT);
			}
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
		if (this.getBlockEntity() instanceof BlockEntitySolarPanel blockEntity) {
			if (CosmosUISystem.Hovering.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 77, this.getScreenCoords()[0] + 77 + 15, this.getScreenCoords()[1] + 17, this.getScreenCoords()[1] + 17 + 59)) {
				DecimalFormat formatter = new DecimalFormat("#,###,###,###");
				String amount_string = formatter.format(blockEntity.getEnergyStored());
				String capacity_string = formatter.format(blockEntity.getMaxEnergyStored());
				
				Component[] comp = new Component[] {
					ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.energy_bar.pre"),
					ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "cosmoslibrary.gui.energy_bar.suff")
				};

				Component[] compProducing = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.energy_bar.pre"),
					ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "cosmoslibrary.gui.energy_bar.suff"),
					ComponentHelper.style3(ComponentColour.PURPLE, "cosmoslibrary.gui.generation.pre", "" + blockEntity.getRFTickRate(), "cosmoslibrary.gui.generation.suff"),
					ComponentHelper.style3(ComponentColour.ORANGE, "cosmoslibrary.gui.generation.pre_output", "" + Math.min(blockEntity.getRFTickRate(), blockEntity.getRFOutputRate()), "cosmoslibrary.gui.generation.suff")
				};
				
				graphicsIn.renderComponentTooltip(this.font, blockEntity.isProducing() ? Arrays.asList(compProducing) : Arrays.asList(comp), mouseX, mouseY);
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