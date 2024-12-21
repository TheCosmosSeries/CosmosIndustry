package com.tcn.cosmosindustry.processing.client.screen;

import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.tcn.cosmosindustry.IndustryReference.RESOURCE.PROCESSING;
import com.tcn.cosmosindustry.processing.client.container.ContainerGrinder;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityGrinder;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.lib.CosmosUISystem.IS_HOVERING;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenBlockEntity;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ScreenGrinder extends CosmosScreenBlockEntity<ContainerGrinder> {
	
	public ScreenGrinder(ContainerGrinder containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(176, 177);
		this.setTexture(PROCESSING.GRINDER_LOC_GUI);
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
	
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityGrinder blockEntity) {
			if (blockEntity.canProcess(blockEntity.getCurrentRecipe(), blockEntity.getLevel().registryAccess())) {
				int k = blockEntity.getProcessProgressScaled(16);
				
				CosmosUISystem.renderScaledElementDownNestled(this, graphicsIn, PROCESSING.GRINDER_LOC_GUI, this.getScreenCoords(), 99, 39, 176, 0, 16, blockEntity.getProcessProgressScaled(16));
			}

			CosmosUISystem.renderEnergyDisplay(graphicsIn, ComponentColour.RED, blockEntity, this.getScreenCoords(), 54, 17, 16, 60, false);
		}
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();
	}
	
	@Override
	public void pushButton(Button button) {
		super.pushButton(button);
	}
	
	@Override
	public void renderComponentHoverEffect(GuiGraphics graphicsIn, Style style, int mouseX, int mouseY) {
		BlockEntity entity = this.getBlockEntity();
		
		if (entity instanceof BlockEntityGrinder) {
			BlockEntityGrinder blockEntity = (BlockEntityGrinder) entity;

			if (IS_HOVERING.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 54,  this.getScreenCoords()[0] + 70,  this.getScreenCoords()[1] + 16,  this.getScreenCoords()[1] + 76)) {
				DecimalFormat formatter = new DecimalFormat("#,###,###,###");
				String amount_string = formatter.format(blockEntity.getEnergyStored());
				String capacity_string = formatter.format(blockEntity.getMaxEnergyStored());
				
				Component[] comp = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.energy_bar.pre"),
					ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "cosmoslibrary.gui.energy_bar.suff") 
				};

				Component[] compProcessing = new Component[] { ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.energy_bar.pre"),
					ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "cosmoslibrary.gui.energy_bar.suff"),
					ComponentHelper.style2(ComponentColour.PURPLE, "cosmoslibrary.gui.energy.fe_pre",  "" + blockEntity.getRFTickRate(), "cosmoslibrary.gui.energy.fe_rate")
				};
			
				if (blockEntity.isProcessing()) {
					graphicsIn.renderComponentTooltip(this.font, Arrays.asList(compProcessing), mouseX, mouseY);
				} else {
					graphicsIn.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
				}
			}
			/*
			if (IS_HOVERING.isEnergy(mouse_x, mouse_y, screen_coords[0] + 79, screen_coords[1] + 15)) {
				if (this.INVENTORY.getProcessTime(0) > 0 || this.INVENTORY.canProcess() && this.INVENTORY.hasEnergy()) {
					this.drawHoveringText(TEXT_LIST.storedRF(this.INVENTORY.getEnergyStored(EnumFacing.DOWN), cosmosindustryReference.RESOURCE.PROCESSING.RF_TICK_RATE[this.INVENTORY.getStackInSlot(3).getCount()], this.INVENTORY.capacity_internal, false), mouse_x - screen_coords[0], mouse_y - screen_coords[1]);
				} else {
					this.drawHoveringText(TEXT_LIST.storedRF(this.INVENTORY.getEnergyStored(EnumFacing.DOWN), 0, this.INVENTORY.capacity_internal, false), mouse_x - screen_coords[0], mouse_y - screen_coords[1]);
				}
			}**/
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