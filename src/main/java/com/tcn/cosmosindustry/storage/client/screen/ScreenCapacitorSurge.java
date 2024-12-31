package com.tcn.cosmosindustry.storage.client.screen;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.storage.client.container.AbstractContainerCapacitor;
import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenCapacitorSurge extends AbstractScreenCapacitor {
	public ScreenCapacitorSurge(AbstractContainerCapacitor containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
	}
	
	@Override
	public void renderComponents(GuiGraphics graphics, int mouseX, int mouseY, float ticks) {
		super.renderComponents(graphics, mouseX, mouseY, ticks);

		CosmosUISystem.Render.renderStaticElement(graphics, this.getScreenCoords(), 95, 43, 176, 0, 12, 12, IndustryReference.Resource.Storage.Gui.CAPACITOR_LIGHT);
	}
}