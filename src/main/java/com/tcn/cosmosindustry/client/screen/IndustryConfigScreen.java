package com.tcn.cosmosindustry.client.screen;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.tcn.cosmosindustry.core.management.IndustryConfigManager;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionBoolean;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionBoolean.TYPE;
import com.tcn.cosmoslibrary.client.ui.screen.option.CosmosOptionsList;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModContainer;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public final class IndustryConfigScreen extends OptionsSubScreen {

	private final Screen parent;

	private final int OPTIONS_LIST_TOP_HEIGHT = 24;
	private final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
	private final int OPTIONS_LIST_ITEM_HEIGHT = 25;
	private final int OPTIONS_LIST_BUTTON_HEIGHT = 20;

	private final int BIG_WIDTH = 310;
	private final int SMALL_WIDTH = 150;
	
	private final int DONE_BUTTON_TOP_OFFSET = 26;
	
	private CosmosOptionsList OPTIONS_ROW_LIST;
	
	@SuppressWarnings("resource")
	public IndustryConfigScreen(ModContainer containerIn, Screen parent) {
		super(parent, Minecraft.getInstance().options, ComponentHelper.style(ComponentColour.RED, "bold", "cosmosindustry.gui.config.name"));
		
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		this.initRowList();
		this.addWidget(this.OPTIONS_ROW_LIST);
	}

	protected void initRowList() {
		this.OPTIONS_ROW_LIST.addSmall(
			new CosmosOptionBoolean(
				ComponentColour.YELLOW, "", "cosmosindustry.gui.config.info_messages", TYPE.ON_OFF,
				IndustryConfigManager.getInstance().getInfoMessage(),
				(newValue) -> IndustryConfigManager.getInstance().setInfoMessage(newValue)
			),
			new CosmosOptionBoolean(
				ComponentColour.YELLOW, "", "cosmosindustry.gui.config.debug_messages", TYPE.ON_OFF,
				IndustryConfigManager.getInstance().getDebugMessage(),
				(newValue) -> IndustryConfigManager.getInstance().setDebugMessage(newValue)
			)
		);
	}

	@Override
	public void renderBackground(GuiGraphics graphicsIn, int mouseX, int mouseY, float ticks) {
		super.renderBackground(graphicsIn, mouseX, mouseY, ticks);
	}

	@Override
	public void render(GuiGraphics graphicsIn, int mouseX, int mouseY, float ticks) {
		super.render(graphicsIn, mouseX, mouseY, ticks);
		this.OPTIONS_ROW_LIST.render(graphicsIn, mouseX, mouseY, ticks);
	}

	public void updateWidgets() {
		double scroll = this.OPTIONS_ROW_LIST.getScrollAmount();
		this.OPTIONS_ROW_LIST.clear();
		this.initRowList();
		this.OPTIONS_ROW_LIST.setScrollAmount(scroll);
	}

    @Override
    public void onClose() {
    	this.minecraft.setScreen(parent);
        IndustryConfigManager.save();
    }

	@Override
	protected void addOptions() {
		this.OPTIONS_ROW_LIST = new CosmosOptionsList(
			this.minecraft, this.width, this.height, 40, 33,
			OPTIONS_LIST_ITEM_HEIGHT, OPTIONS_LIST_BUTTON_HEIGHT, 310, 26
		);
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		this.OPTIONS_ROW_LIST.resize(minecraft, width, height);
		this.updateWidgets();
		super.resize(minecraft, width, height);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int ticks, double dragX, double dragY) {
		if (this.getChildAt(mouseX, mouseY).isPresent()) {
			for (GuiEventListener listener : this.OPTIONS_ROW_LIST.children()) {
				if (listener.isMouseOver(mouseX, mouseY)) {
					this.updateWidgets();
				}
			}
		}
		return super.mouseDragged(mouseX, mouseY, ticks, dragX, dragY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int ticks) {
		boolean clicked = super.mouseClicked(mouseX, mouseY, ticks);
		
		if (this.getChildAt(mouseX, mouseY).isPresent()) {
			for (GuiEventListener listener : this.OPTIONS_ROW_LIST.children()) {
				if (listener.isMouseOver(mouseX, mouseY)) {
					this.updateWidgets();
				}
			}
		}
		return clicked;
	}
	

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (this.OPTIONS_ROW_LIST.isMouseOver(mouseX, mouseY)) {
			return this.OPTIONS_ROW_LIST.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@SuppressWarnings("unchecked")
	public static List<FormattedCharSequence> tooltipAt(CosmosOptionsList listIn, int mouseX, int mouseY) {
		Optional<AbstractWidget> optional = listIn.getMouseOver((double)  mouseX, (double) mouseY);
		return (List<FormattedCharSequence>) (optional.isPresent() && optional.get() instanceof AbstractWidget ? ((AbstractWidget) optional.get()).getTooltip() : ImmutableList.of());
	}
}