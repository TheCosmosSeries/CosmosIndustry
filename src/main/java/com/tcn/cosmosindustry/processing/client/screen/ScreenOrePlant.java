package com.tcn.cosmosindustry.processing.client.screen;

import java.util.Arrays;

import com.ibm.icu.text.DecimalFormat;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.network.packet.PacketEmptyTank;
import com.tcn.cosmosindustry.core.network.packet.PacketPlantMode;
import com.tcn.cosmosindustry.processing.client.container.ContainerOrePlant;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityOrePlant;
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
public class ScreenOrePlant extends CosmosScreenUIModeBE<ContainerOrePlant> {

	private CosmosButtonWithType buttonTankClear; private int[] TBCI = new int[] { 122, 60, 18 };
	private CosmosButtonWithType buttonPlantMode; private int[] BEMI = new int[] { 60,  38, 18 };
	
	public ScreenOrePlant(ContainerOrePlant containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
		
		this.setImageDims(176, 177);
		this.setLight(IndustryReference.Resource.Processing.Gui.ORE_PLANT_LIGHT);
		this.setDark(IndustryReference.Resource.Processing.Gui.ORE_PLANT_DARK);
		this.setUIModeButtonIndex(159, 5);
		
		this.setTitleLabelDims(9, 4);
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
	
		if (this.getBlockEntity() instanceof BlockEntityOrePlant blockEntity) {
			if (blockEntity.canProcess(blockEntity.getCurrentRecipe(), blockEntity.getLevel().registryAccess())) {
				CosmosUISystem.Render.renderScaledElementUpNestled(graphicsIn, this.getScreenCoords(), 84, 17, blockEntity.getMode().equals(BlockEntityOrePlant.PlantMode.CLEANING) ? 176 : 184, 0, 8, 60, blockEntity.getProcessProgressScaled(60), IndustryReference.Resource.Processing.Gui.ORE_PLANT_LIGHT);
			}

			CosmosUISystem.Render.renderEnergyDisplay(graphicsIn, ComponentColour.RED, blockEntity, this.getScreenCoords(), 37, 17, 16, 60, false);
			CosmosUISystem.Render.renderFluidTank(graphicsIn, getScreenCoords(), 123, 17, blockEntity.getFluidTank(), blockEntity.getFluidLevelScaled(37), 38);
		}
	}
	
	@Override
	protected void addButtons() {
		super.addButtons();

		if (this.getBlockEntity() instanceof BlockEntityOrePlant blockEntity) {
			this.buttonTankClear = this.addRenderableWidget(new CosmosButtonWithType(TYPE.FLUID, this.getScreenCoords()[0] + TBCI[0], this.getScreenCoords()[1] + TBCI[1], TBCI[2], !blockEntity.getFluidTank().isEmpty(), true, blockEntity.getFluidTank().isEmpty() ? 15 : 16, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonTankClear, isLeftClick); }));
			this.buttonPlantMode = this.addRenderableWidget(new CosmosButtonWithType(TYPE.FLUID, this.getScreenCoords()[0] + BEMI[0], this.getScreenCoords()[1] + BEMI[1], BEMI[2], true, true, 22 + blockEntity.getMode().getIndex(), ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.buttonPlantMode, isLeftClick); }));
		}
	}
	
	@Override
	public void clickButton(Button button, boolean isLeftClick) {
		super.clickButton(button, isLeftClick);
		
		if (isLeftClick) {
			if (this.getBlockEntity() instanceof BlockEntityOrePlant blockEntity) {
				if (button.equals(this.buttonTankClear)) {
					if (hasShiftDown()) {
						PacketDistributor.sendToServer(new PacketEmptyTank(blockEntity.getBlockPos()));
						blockEntity.emptyFluidTank();
					}
				}
				
				if (button.equals(this.buttonPlantMode)) {
					PacketDistributor.sendToServer(new PacketPlantMode(blockEntity.getBlockPos(), blockEntity.getMode().cycleMode().getIndex()));
				}
			}
		}
	}
	
	@Override
	public void renderStandardHoverEffect(GuiGraphics graphicsIn, Style style, int mouseX, int mouseY) {
		if (this.getBlockEntity() instanceof BlockEntityOrePlant blockEntity) {
			if (CosmosUISystem.Hovering.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 37,  this.getScreenCoords()[0] + 37 + 16,  this.getScreenCoords()[1] + 17,  this.getScreenCoords()[1] + 17 + 59)) {
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
			
			else if (CosmosUISystem.Hovering.isHovering(mouseX, mouseY, this.getScreenCoords()[0] + 123, this.getScreenCoords()[0] + 123 + 16, this.getScreenCoords()[1] + 17, this.getScreenCoords()[1] + 17 + 37)) {
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

			else if (this.buttonPlantMode.isMouseOver(mouseX, mouseY)) {
				Component[] comp = new Component[] { 
					ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.gui.button.plant_mode"),
					ComponentHelper.style(ComponentColour.LIGHT_GRAY, "bold", "cosmosindustry.gui.button.plant_mode_info").append(blockEntity.getMode().getLocNameComp())
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
								ComponentHelper.style(ComponentColour.WHITE, "cosmosindustry.gui.button.tank_clear"),
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