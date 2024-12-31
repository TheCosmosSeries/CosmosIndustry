package com.tcn.cosmosindustry.production.client.screen;

import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.network.packet.PacketEmptyTank;
import com.tcn.cosmosindustry.production.client.container.ContainerLiquidFuel;
import com.tcn.cosmosindustry.production.core.blockentity.BlockEntityLiquidFuel;
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

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("removal")
public class ScreenLiquidFuel extends CosmosScreenUIModeBE<ContainerLiquidFuel> {

	private CosmosButtonWithType buttonTankClear; private int[] TBCI = new int[] { 126, 60, 18 };
	
	public ScreenLiquidFuel(ContainerLiquidFuel containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(176, 177);
		this.setLight(IndustryReference.Resource.Production.Gui.LIQUID_FUEL_LIGHT);
		this.setDark(IndustryReference.Resource.Production.Gui.LIQUID_FUEL_DARK);
		this.setUIModeButtonIndex(164, 4);
		this.setUIModeButtonSmall();
		
		this.setTitleLabelDims(7, 4);
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
		
		if (this.getBlockEntity() instanceof BlockEntityLiquidFuel blockEntity) {
			CosmosUISystem.Render.renderEnergyDisplay(graphicsIn, ComponentColour.RED, blockEntity, this.getScreenCoords(), 33, 17, 16, 60, false);
			CosmosUISystem.Render.renderFluidTank(graphicsIn, getScreenCoords(), 127, 17, blockEntity.getFluidTank(), blockEntity.getFluidLevelScaled(37), 38);
			
			if (blockEntity.isProducing()) {
				CosmosUISystem.Render.renderScaledElementUpNestled(graphicsIn, this.getScreenCoords(), 68, 38, 176, 0, 18, 19, blockEntity.getProduceProgressScaled(19), IndustryReference.Resource.Production.Gui.LIQUID_FUEL_LIGHT);
			}
		}
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();
		
		if (this.getBlockEntity() instanceof BlockEntityLiquidFuel blockEntity) {
			this.buttonTankClear = this.addRenderableWidget(new CosmosButtonWithType(TYPE.FLUID, this.getScreenCoords()[0] + TBCI[0], this.getScreenCoords()[1] + TBCI[1], TBCI[2], !blockEntity.getFluidTank().isEmpty(), true, blockEntity.getFluidTank().isEmpty() ? 15 : 16, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonTankClear, isLeftClick); }));
		}
	}

	@Override
	public void clickButton(Button button, boolean isLeftClick) {
		super.clickButton(button, isLeftClick);
		
		if (this.getBlockEntity() instanceof BlockEntityLiquidFuel blockEntity) {
			if (button.equals(this.buttonTankClear)) {
				if (hasShiftDown()) {
					PacketDistributor.sendToServer(new PacketEmptyTank(blockEntity.getBlockPos()));
				}
			}
		}
	}
	
	@Override
	public void renderStandardHoverEffect(GuiGraphics graphicsIn, Style style, int mouseX, int mouseY) {
		if (this.getBlockEntity() instanceof BlockEntityLiquidFuel blockEntity) {
			if (CosmosUISystem.Hovering.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 33, this.getScreenCoords()[0] + 33 + 15, this.getScreenCoords()[1] + 17, this.getScreenCoords()[1] + 17 + 59)) {
				DecimalFormat formatter = new DecimalFormat("#,###,###,###");
				String amount_string = formatter.format(blockEntity.getEnergyStored());
				String capacity_string = formatter.format(blockEntity.getMaxEnergyStored());
				
				Component[] comp = new Component[] {
					ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.energy_bar.pre"),
					ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "cosmoslibrary.gui.energy_bar.suff")
				};

				Component[] compProducing = new Component[] { 
					ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.energy_bar.pre"),
					ComponentHelper.style2(ComponentColour.RED, amount_string + " / " + capacity_string, "cosmoslibrary.gui.energy_bar.suff"),
					ComponentHelper.style3(ComponentColour.PURPLE, "cosmoslibrary.gui.generation.pre", "" + blockEntity.getRFTickRate(), "cosmoslibrary.gui.generation.suff"),
					ComponentHelper.style3(ComponentColour.ORANGE, "cosmoslibrary.gui.generation.pre_output", "" + Math.min(blockEntity.getRFTickRate(), blockEntity.getRFOutputRate()), "cosmoslibrary.gui.generation.suff")
				};
				
				graphicsIn.renderComponentTooltip(this.font, blockEntity.isProducing() ? Arrays.asList(compProducing) : Arrays.asList(comp), mouseX, mouseY);
			} else if (CosmosUISystem.Hovering.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 127, this.getScreenCoords()[0] + 127 + 16, this.getScreenCoords()[1] + 17, this.getScreenCoords()[1] + 17 + 37)) {
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

			else if (this.buttonTankClear != null) {
				if (this.buttonTankClear.isMouseOver(mouseX, mouseY)) {
					if (this.buttonTankClear.active) {
						if (!hasShiftDown()) {
							graphicsIn.renderTooltip(this.font, ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.gui.button.tank_clear"), mouseX, mouseY);
						} else {
							Component[] comp = new Component[] { 
								ComponentHelper.style(ComponentColour.WHITE, "", "cosmosindustry.gui.button.tank_clear"),
								ComponentHelper.style(ComponentColour.RED, "bold", "cosmosindustry.gui.button.tank_clear_shift") 
							};
							
							graphicsIn.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
						}
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