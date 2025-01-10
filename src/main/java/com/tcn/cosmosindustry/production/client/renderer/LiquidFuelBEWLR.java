package com.tcn.cosmosindustry.production.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.production.core.block.ItemBlockLiquidFuel;
import com.tcn.cosmoslibrary.client.renderer.CosmosRendererHelper;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectFluidTankCustom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

@OnlyIn(Dist.CLIENT)
public class LiquidFuelBEWLR extends BlockEntityWithoutLevelRenderer {	
	public static final BlockEntityWithoutLevelRenderer INSTANCE = new LiquidFuelBEWLR();
	
	public LiquidFuelBEWLR() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderByItem(ItemStack stackIn, ItemDisplayContext transformIn, PoseStack poseStackIn, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		Item item = stackIn.getItem();
		Minecraft mc = Minecraft.getInstance();

		this.renderFluid((ItemBlockLiquidFuel)item, stackIn, poseStackIn, buffer);
				
		poseStackIn.pushPose();
		BakedModel itemModel = mc.getModelManager().getModel(CosmosRendererHelper.getStandaloneItem(CosmosIndustry.MOD_ID, "block_liquid_fuel_item"));
		mc.getItemRenderer().renderModelLists(itemModel, stackIn, packedLight, packedOverlay, poseStackIn, ItemRenderer.getFoilBufferDirect(buffer, itemModel.getRenderTypes(stackIn, true).get(0), true, stackIn.hasFoil()));
		poseStackIn.popPose();
	}
	
	private void renderFluid(ItemBlockLiquidFuel item, ItemStack stackIn, PoseStack poseStackIn, MultiBufferSource buffer) {
		poseStackIn.pushPose();

		ObjectFluidTankCustom customTank = item.getFluidTank(stackIn);
		if (customTank != null) {
			if (item.getFluidAmount(stackIn) > 0) {
				FluidTank fluidTank = item.getFluidTankTank(stackIn);
				
				if (fluidTank != null) {
					FluidStack fluid = fluidTank.getFluid();
					if (fluid.isEmpty()) {
						return;
					}
		
					Fluid renderFluid = fluid.getFluid();
					if (renderFluid == null) {
						return;
					}

					IClientFluidTypeExtensions props = CosmosRendererHelper.getFluidExtention(renderFluid);  //IClientFluidTypeExtensions.of(renderFluid.defaultFluidState());
					TextureAtlasSprite sprite = CosmosRendererHelper.getFluidTexture(props);  //Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(props.getStillTexture());
					VertexConsumer builderA = buffer.getBuffer(RenderType.translucent());

					float[] values = new float[] { 1.1F/16F, 14.9F/16F, 10/16F };
					float[] height = new float[] { 10/16F, 14.9F/16F };
					float fillLevel = customTank.getFillLevel() / 6F;
					
					height[1] = (float) Mth.map(fillLevel, 0, 1, 10/16F, 14.9/16F);
					
					float mappedHeight = CosmosRendererHelper.getMappedTextureHeight(sprite, fillLevel * 16);
					
					int color = props.getTintColor();
					
					float a = 1.0F;
					float r = (color >> 16 & 0xFF) / 255.0F;
					float g = (color >> 8 & 0xFF) / 255.0F;
					float b = (color & 0xFF) / 255.0F;

					poseStackIn.pushPose();
					poseStackIn.mulPose(Axis.YP.rotationDegrees(0));
					
					// Top Face
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[1], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[1], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[1], values[0], sprite.getU1(), sprite.getV0(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[1], values[0], sprite.getU0(), sprite.getV0(), r, g, b, a);
					
					// Bottom Face of Top
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[1], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[1], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[1], values[0], sprite.getU1(), sprite.getV0(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[1], values[0], sprite.getU0(), sprite.getV0(), r, g, b, a);
								
					// Front Faces [NORTH - SOUTH]
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[1], values[1], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[1], values[1], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[0], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[0], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);

					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[0], values[0], sprite.getU0(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[0], values[0], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[1], values[0], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[1], values[0], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);

					// Back Faces
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[1], values[0], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[1], values[0], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[0], values[0], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[0], values[0], sprite.getU0(), sprite.getV1(), r, g, b, a);

					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[0], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[0], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[1], values[1], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[1], values[1], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);

					poseStackIn.mulPose(Axis.YP.rotationDegrees(90));
					poseStackIn.translate(-1f, 0, 0);
					
					// Front Faces [EAST - WEST]
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[1], values[1], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[1], values[1], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[0], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[0], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);

					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[0], values[0], sprite.getU0(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[0], values[0], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[1], values[0], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[1], values[0], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);

					// Back Faces
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[1], values[0], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[1], values[0], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[0], values[0], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[0], values[0], sprite.getU0(), sprite.getV1(), r, g, b, a);

					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[0], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[0], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[1], values[1], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[1], values[1], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);

					poseStackIn.popPose();
				}
			}
		}
		poseStackIn.popPose();
	}
}