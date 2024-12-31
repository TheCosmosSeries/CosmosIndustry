package com.tcn.cosmosindustry.storage.client.screen;

import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.network.packet.PacketEmptyTank;
import com.tcn.cosmosindustry.storage.client.container.AbstractContainerFluidTank;
import com.tcn.cosmosindustry.storage.core.blockentity.AbstractBlockEntityFluidTank;
import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenUIModeBE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType.TYPE;
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
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.network.PacketDistributor;

@SuppressWarnings({ "removal" })
@OnlyIn(Dist.CLIENT)
public abstract class AbstractScreenFluidTank extends CosmosScreenUIModeBE<AbstractContainerFluidTank> {

	private CosmosButtonWithType buttonTankClear; private int[] TBCI = new int[] { 66, 62, 18 };

	public AbstractScreenFluidTank(AbstractContainerFluidTank containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(176, 177);
		this.setLight(IndustryReference.Resource.Storage.Gui.FLUID_TANK_LIGHT);
		this.setLight(IndustryReference.Resource.Storage.Gui.FLUID_TANK_DARK);
		this.setUIModeButtonIndex(159, 5);
		
		this.setTitleLabelDimsCentered(0, 4);
		this.setInventoryLabelDims(8, 84);
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

		if (this.getBlockEntity() instanceof AbstractBlockEntityFluidTank blockEntity) {
			CosmosUISystem.Render.renderFluidTank(graphicsIn, getScreenCoords(), 67, 19, blockEntity.getFluidTank(), blockEntity.getFluidLevelScaled(37), 38);
		}
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();
		
		if (this.getBlockEntity() instanceof AbstractBlockEntityFluidTank blockEntity) {
			this.buttonTankClear = this.addRenderableWidget(new CosmosButtonWithType(TYPE.FLUID, this.getScreenCoords()[0] + TBCI[0], this.getScreenCoords()[1] + TBCI[1], TBCI[2], !blockEntity.getFluidTank().isEmpty(), true, blockEntity.getFluidTank().isEmpty() ? 15 : 16, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonTankClear, isLeftClick); }));
		}
	}
	
	@Override
	public void clickButton(Button button, boolean isLeftClick) {
		super.clickButton(button, isLeftClick);

		if (isLeftClick) {
			if (this.getBlockEntity() instanceof AbstractBlockEntityFluidTank blockEntity) {
				if (button.equals(this.buttonTankClear)) {
					if (hasShiftDown()) {
						PacketDistributor.sendToServer(new PacketEmptyTank(blockEntity.getBlockPos()));
						blockEntity.emptyFluidTank();
					}
				}
			}
		}
	}
	
	@Override
	public void renderStandardHoverEffect(GuiGraphics graphicsIn, Style style, int mouseX, int mouseY) {
		if (this.getBlockEntity() instanceof AbstractBlockEntityFluidTank blockEntity) {
			if (CosmosUISystem.Hovering.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 67, this.getScreenCoords()[0] + 67 + 16, this.getScreenCoords()[1] + 19, this.getScreenCoords()[1] + 19 + 37)) {
				FluidTank tank = blockEntity.getFluidTank();
				
				DecimalFormat formatter = new DecimalFormat("#,###,###,###");
				String amount_string = formatter.format(tank.getFluidAmount());
				String capacity_string = formatter.format(tank.getCapacity());
				String fluid_name = tank.getFluid().getTranslationKey();
				
				Component[] comp = new Component[] { 
					ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.gui.fluid_bar.pre").append(ComponentHelper.style3(ComponentColour.CYAN, "bold", "[ ", fluid_name, " ]")), 
					ComponentHelper.style2(ComponentColour.ORANGE, amount_string + " / " + capacity_string, "cosmosindustry.gui.fluid_bar.suff") 
				};
				graphicsIn.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}
		} else if (this.buttonTankClear != null) {
			if (this.buttonTankClear.isMouseOver(mouseX, mouseY)) {
				if (this.buttonTankClear.active) {
					if (!hasShiftDown()) {
						graphicsIn.renderTooltip(this.font, ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.gui.button.tank_clear"), mouseX, mouseY);
					} else {
						Component[] comp = new Component[] { 
							ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.gui.button.tank_clear"),
							ComponentHelper.style(ComponentColour.RED, "bold", "cosmosindustry.gui.button.tank_clear_shift") 
						};
						graphicsIn.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
					}
				}
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