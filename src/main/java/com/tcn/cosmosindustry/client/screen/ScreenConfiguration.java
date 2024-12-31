package com.tcn.cosmosindustry.client.screen;

import java.io.File;

import com.tcn.cosmosindustry.core.management.IndustryConfigManager;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionBoolean;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionBoolean.TYPE;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptions;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionsList;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModContainer;

@OnlyIn(Dist.CLIENT)
public final class ScreenConfiguration extends Screen {

	private final Screen parent;

	private final int TITLE_HEIGHT = 8;

	private final int OPTIONS_LIST_TOP_HEIGHT = 24;
	private final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
	private final int OPTIONS_LIST_ITEM_HEIGHT = 25;

	private final int BUTTON_WIDTH = 200;
	private final int BUTTON_HEIGHT = 20;
	private final int DONE_BUTTON_TOP_OFFSET = 26;

	private CosmosOptionsList optionsRowList;
	
	public ScreenConfiguration(ModContainer containerIn, Screen parent) {
		super(ComponentHelper.style(ComponentColour.LIGHT_GRAY, "bold", "cosmosindustry.gui.config.name"));
		
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.optionsRowList = new CosmosOptionsList(
			this.minecraft, this.width, this.height,
			OPTIONS_LIST_TOP_HEIGHT,
			this.height - OPTIONS_LIST_BOTTOM_OFFSET,
			OPTIONS_LIST_ITEM_HEIGHT, 310, new CosmosOptions(Minecraft.getInstance(), new File("."))
		);
		
		this.optionsRowList.addBig(
			new CosmosOptionBoolean(
				ComponentColour.CYAN, "", "cosmosindustry.gui.config.messages", TYPE.ON_OFF,
				IndustryConfigManager.getInstance().getDebugMessage(),
				(newValue) -> IndustryConfigManager.getInstance().setDebugMessage(newValue)
			)
		);
		
		this.addWidget(this.optionsRowList);
		
		this.addRenderableWidget(Button.builder(
			ComponentHelper.style(ComponentColour.GREEN, "bold", "cosmosindustry.gui.done"), (button) -> { 
				this.onClose(); 
			}).pos((this.width) /2, this.height - DONE_BUTTON_TOP_OFFSET).size(BUTTON_WIDTH, BUTTON_HEIGHT).build()
		);
	}
	
	@Override
	public void render(GuiGraphics graphicsIn, int mouseX, int mouseY, float ticks) {
		this.renderBackground(graphicsIn, mouseX, mouseY, ticks);
		
		this.optionsRowList.render(graphicsIn, mouseX, mouseY, ticks);
		graphicsIn.drawCenteredString(this.font, this.title, width / 2, TITLE_HEIGHT, 0xFFFFFF);
		
		super.render(graphicsIn, mouseX, mouseY, ticks);
	}
	
    @Override
    public void onClose() {
    	this.minecraft.setScreen(parent);
        IndustryConfigManager.save();
    }
}