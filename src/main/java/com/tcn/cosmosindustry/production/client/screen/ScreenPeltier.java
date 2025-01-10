package com.tcn.cosmosindustry.production.client.screen;

import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.network.packet.PacketEmptyTankDual;
import com.tcn.cosmosindustry.core.network.packet.PacketSelectedTank;
import com.tcn.cosmosindustry.production.client.container.ContainerPeltier;
import com.tcn.cosmosindustry.production.core.blockentity.BlockEntityPeltier;
import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenBlockEntityUI;
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
public class ScreenPeltier extends CosmosScreenBlockEntityUI<ContainerPeltier> {

	private CosmosButtonWithType buttonTankClear1; private int[] TBC1 = new int[] { 104, 60, 18 };
	private CosmosButtonWithType buttonTankClear2; private int[] TBC2 = new int[] { 126, 60, 18 };
	
	private CosmosButtonWithType selectedTankButton; private int[] SBI = new int[] { 82, 38, 18 };
	
	public ScreenPeltier(ContainerPeltier containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(176, 189);
		this.setLight(IndustryReference.Resource.Production.Gui.PELTIER_LIGHT);
		this.setDark(IndustryReference.Resource.Production.Gui.PELTIER_DARK);
		this.setUIModeButtonIndex(164, 4);
		this.setUIModeButtonSmall();
		
		this.setTitleLabelDims(7, 4);
		this.setInventoryLabelDims(7, 96);
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
		
		if (this.getBlockEntity() instanceof BlockEntityPeltier blockEntity) {
			CosmosUISystem.Render.renderEnergyDisplay(graphicsIn, ComponentColour.RED, blockEntity, this.getScreenCoords(), 33, 17, 16, 60, false);
			
			CosmosUISystem.Render.renderFluidTank(graphicsIn, getScreenCoords(), 105, 17, blockEntity.getFluidTank(0), blockEntity.getFluidLevelScaled(0, 37), 38);
			CosmosUISystem.Render.renderFluidTank(graphicsIn, getScreenCoords(), 127, 17, blockEntity.getFluidTank(1), blockEntity.getFluidLevelScaled(1, 37), 38);
			
			CosmosUISystem.Render.renderStaticElement(graphicsIn, this.getScreenCoords(), blockEntity.getSelectedTank().equals(BlockEntityPeltier.SelectedTank.COLD) ? 103 : 125, 15, 176, 19, 20, 42, IndustryReference.Resource.Production.Gui.PELTIER_LIGHT);
			
			if (blockEntity.isProducing()) {
				CosmosUISystem.Render.renderScaledElementUpNestled(graphicsIn, this.getScreenCoords(), 57, 38, 176, 0, 18, 19, blockEntity.getProduceProgressScaled(19), IndustryReference.Resource.Production.Gui.LIQUID_FUEL_LIGHT);
			}
			
			CosmosUISystem.FontRenderer.drawString(graphicsIn, font, this.getScreenCoords(), 82, 82, true, ComponentHelper.style(ComponentColour.LIGHT_GRAY, "", "Delta: ").append(ComponentHelper.style(ComponentColour.GREEN, "bold", "" + blockEntity.getDifferentialFluidTemp())), true);
		}
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();
		
		if (this.getBlockEntity() instanceof BlockEntityPeltier blockEntity) {
			this.buttonTankClear1 = this.addRenderableWidget(new CosmosButtonWithType(TYPE.FLUID, this.getScreenCoords()[0] + TBC1[0], this.getScreenCoords()[1] + TBC1[1], TBC1[2], !blockEntity.getFluidTank(0).isEmpty(), true, blockEntity.getFluidTank(0).isEmpty() ? 15 : 16, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonTankClear1, isLeftClick); }));
			this.buttonTankClear2 = this.addRenderableWidget(new CosmosButtonWithType(TYPE.FLUID, this.getScreenCoords()[0] + TBC2[0], this.getScreenCoords()[1] + TBC2[1], TBC2[2], !blockEntity.getFluidTank(1).isEmpty(), true, blockEntity.getFluidTank(1).isEmpty() ? 15 : 16, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonTankClear2, isLeftClick); }));
			
			this.selectedTankButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.FLUID, this.getScreenCoords()[0] + SBI[0], this.getScreenCoords()[1] + SBI[1], SBI[2], true, true, blockEntity.getSelectedTank().getIndex() + 22, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.selectedTankButton, isLeftClick); }));
		}
	}

	@Override
	public void clickButton(Button button, boolean isLeftClick) {
		super.clickButton(button, isLeftClick);
		
		if (this.getBlockEntity() instanceof BlockEntityPeltier blockEntity) {
			if (button.equals(this.buttonTankClear1)) {
				if (hasShiftDown()) {
					PacketDistributor.sendToServer(new PacketEmptyTankDual(blockEntity.getBlockPos(), 0));
				}
			}
			
			if (button.equals(this.buttonTankClear2)) {
				if (hasShiftDown()) {
					PacketDistributor.sendToServer(new PacketEmptyTankDual(blockEntity.getBlockPos(), 1));
				}
			}

			if (button.equals(this.selectedTankButton)) {
				PacketDistributor.sendToServer(new PacketSelectedTank(blockEntity.getBlockPos()));
			}
		}
	}
	
	@Override
	public void renderStandardHoverEffect(GuiGraphics graphicsIn, Style style, int mouseX, int mouseY) {
		if (this.getBlockEntity() instanceof BlockEntityPeltier blockEntity) {
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
			} else if (CosmosUISystem.Hovering.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 105, this.getScreenCoords()[0] + 105 + 16, this.getScreenCoords()[1] + 17, this.getScreenCoords()[1] + 17 + 37)) {
				FluidTank tank = blockEntity.getFluidTank(0);
				
				DecimalFormat formatter = new DecimalFormat("#,###,###,###");
				String amount_string = formatter.format(tank.getFluidAmount());
				String capacity_string = formatter.format(tank.getCapacity());
				String fluid_name = tank.getFluid().getTranslationKey();
				int fluid_temp = tank.getFluid().getFluidType() != null ? tank.getFluid().getFluidType().getTemperature() : 0;
				
				Component[] comp = new Component[] {
					ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.gui.fluid_bar.pre").append(ComponentHelper.style3(ComponentColour.BLURPLE, "bold", "[ ", fluid_name, " ]")), 
					ComponentHelper.style2(ComponentColour.CYAN, amount_string + " / " + capacity_string, "cosmosindustry.gui.fluid_bar.suff"),
					ComponentHelper.style(ComponentColour.LIGHT_GRAY, "", "cosmosindustry.gui.fluid_bar.temp").append(ComponentHelper.style(ComponentColour.BLURPLE, "bold", "" + fluid_temp + " °C"))
				};
				
				graphicsIn.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			} else if (CosmosUISystem.Hovering.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 127, this.getScreenCoords()[0] + 127 + 16, this.getScreenCoords()[1] + 17, this.getScreenCoords()[1] + 17 + 37)) {
				FluidTank tank = blockEntity.getFluidTank(1);
				
				DecimalFormat formatter = new DecimalFormat("#,###,###,###");
				String amount_string = formatter.format(tank.getFluidAmount());
				String capacity_string = formatter.format(tank.getCapacity());
				String fluid_name = tank.getFluid().getTranslationKey();
				int fluid_temp = tank.getFluid().getFluidType() != null ? tank.getFluid().getFluidType().getTemperature() : 0;
				
				Component[] comp = new Component[] {
					ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.gui.fluid_bar.pre").append(ComponentHelper.style3(ComponentColour.ORANGE, "bold", "[ ", fluid_name, " ]")), 
					ComponentHelper.style2(ComponentColour.CYAN, amount_string + " / " + capacity_string, "cosmosindustry.gui.fluid_bar.suff"),
					ComponentHelper.style(ComponentColour.LIGHT_GRAY, "", "cosmosindustry.gui.fluid_bar.temp").append(ComponentHelper.style(ComponentColour.ORANGE, "bold", "" + fluid_temp + " °C"))
				};
				
				graphicsIn.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}

			else if (this.buttonTankClear1 != null) {
				if (this.buttonTankClear1.isMouseOver(mouseX, mouseY)) {
					if (this.buttonTankClear1.active) {
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

			else if (this.buttonTankClear2 != null) {
				if (this.buttonTankClear2.isMouseOver(mouseX, mouseY)) {
					if (this.buttonTankClear2.active) {
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

			if (this.selectedTankButton.isMouseOver(mouseX, mouseY)) {
				Component[] comp = new Component[] {
					ComponentHelper.style(ComponentColour.WHITE, "", "cosmosindustry.gui.button.selected_tank"),
					ComponentHelper.style(ComponentColour.LIGHT_GRAY, "", "cosmosindustry.gui.button.selected_tank_info").append(ComponentHelper.style(blockEntity.getSelectedTank().getTextColour(), "bold", blockEntity.getSelectedTank().getName()))
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