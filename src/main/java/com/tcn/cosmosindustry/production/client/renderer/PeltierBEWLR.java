package com.tcn.cosmosindustry.production.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.production.core.block.ItemBlockPeltier;
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
public class PeltierBEWLR extends BlockEntityWithoutLevelRenderer {	
	public static final BlockEntityWithoutLevelRenderer INSTANCE = new PeltierBEWLR();
	
	public PeltierBEWLR() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderByItem(ItemStack stackIn, ItemDisplayContext transformIn, PoseStack poseStackIn, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		Item item = stackIn.getItem();
		Minecraft mc = Minecraft.getInstance();
		
		ItemBlockPeltier itemPeltier = (ItemBlockPeltier) item;

		this.renderFluid(itemPeltier, stackIn, poseStackIn, buffer, false);
		this.renderFluid(itemPeltier, stackIn, poseStackIn, buffer, true);
				
		poseStackIn.pushPose();
		BakedModel itemModel = mc.getModelManager().getModel(CosmosRendererHelper.getStandalone(CosmosIndustry.MOD_ID, "item/block_peltier_item"));
		mc.getItemRenderer().renderModelLists(itemModel, stackIn, packedLight, packedOverlay, poseStackIn, ItemRenderer.getFoilBufferDirect(buffer, itemModel.getRenderTypes(stackIn, true).get(0), true, stackIn.hasFoil()));
		poseStackIn.popPose();
	}
	
	private void renderFluid(ItemBlockPeltier item, ItemStack stackIn, PoseStack poseStackIn, MultiBufferSource buffer, boolean hot) {
		poseStackIn.pushPose();

		ObjectFluidTankCustom customTank = item.getFluidTank(stackIn, hot);
		if (customTank != null) {
			if (item.getFluidAmount(stackIn, hot) > 0) {
				FluidTank fluidTank = item.getFluidTankTank(stackIn, hot);
				
				if (fluidTank != null) {
					FluidStack fluid = fluidTank.getFluid();
					if (fluid.isEmpty()) {
						return;
					}
		
					Fluid renderFluid = fluid.getFluid();
					if (renderFluid == null) {
						return;
					}

					IClientFluidTypeExtensions extensions = CosmosRendererHelper.getFluidExtention(renderFluid);  //IClientFluidTypeExtensions.of(renderFluid.defaultFluidState());
					TextureAtlasSprite sprite = CosmosRendererHelper.getFluidTexture(extensions);  //Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(props.getStillTexture());
					VertexConsumer builderA = buffer.getBuffer(RenderType.translucent());

					float[] topValues    = new float[] { 1.10F/16F, 9.10F/16F, 14.9F/16F };
					float[] sideValuesNS = new float[] { 1.10F/16F, 9.10F/16F, 14.9F/16F };
					float[] sideValuesEW = new float[] { 1.10F/16F, 6.90F/16F, 9.10F/16F, 14.9F/16F };
					
					float[] height = new float[] { 9.1F/16F, 15.9F/16F };
					float fillLevel = customTank.getFillLevel() / 7F;
					
					height[1] = (float) Mth.map(fillLevel, 0, 1, 9.1F/16F, 15.9/16F);
					
					float mappedHeight = CosmosRendererHelper.getMappedTextureHeight(sprite, fillLevel * 16);
					
					int color = extensions.getTintColor();
					
					float a = 1.0F;
					float r = (color >> 16 & 0xFF) / 255.0F;
					float g = (color >> 8 & 0xFF) / 255.0F;
					float b = (color & 0xFF) / 255.0F;

					poseStackIn.mulPose(Axis.YP.rotationDegrees(-90));
					poseStackIn.translate(0, 0, hot ? -1F : -(1F + 8/16F));

					poseStackIn.pushPose();
					
					// Top Face
					CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[1], topValues[2], sprite.getU0(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[1], topValues[2], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[1], topValues[1], sprite.getU1(), sprite.getV0(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[1], topValues[1], sprite.getU0(), sprite.getV0(), r, g, b, a);
					
					CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[1], topValues[2], sprite.getU0(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[1], topValues[2], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[1], topValues[1], sprite.getU1(), sprite.getV0(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[1], topValues[1], sprite.getU0(), sprite.getV0(), r, g, b, a);
					
					// Top of Bottom Face
					CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[0], topValues[2], sprite.getU0(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[0], topValues[2], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[0], topValues[1], sprite.getU1(), sprite.getV0(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[0], topValues[1], sprite.getU0(), sprite.getV0(), r, g, b, a);
					
					// Front Faces [NORTH - SOUTH]
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[1], sideValuesNS[2], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[1], sideValuesNS[2], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[0], sideValuesNS[2], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[0], sideValuesNS[2], sprite.getU0(), sprite.getV1(), r, g, b, a);

					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[0], sideValuesNS[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[0], sideValuesNS[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[1], sideValuesNS[1], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[1], sideValuesNS[1], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);

					
					// Back Faces
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[1], sideValuesNS[1], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[1], sideValuesNS[1], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[0], sideValuesNS[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[0], sideValuesNS[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
					
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[0], sideValuesNS[2], sprite.getU0(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[0], sideValuesNS[2], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[1], sideValuesNS[2], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[1], sideValuesNS[2], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);

					poseStackIn.mulPose(Axis.YP.rotationDegrees(90));
					poseStackIn.translate(-1f, 0, 0);
					
					// Back Faces
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[1], sideValuesEW[0], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[1], sideValuesEW[0], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[0], sideValuesEW[0], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[0], sideValuesEW[0], sprite.getU0(), sprite.getV1(), r, g, b, a);
					
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[0], sideValuesEW[3], sprite.getU0(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[0], sideValuesEW[3], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[1], sideValuesEW[3], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[1], sideValuesEW[3], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
					
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[1], sideValuesEW[3], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[1], sideValuesEW[3], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[0], sideValuesEW[3], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[0], sideValuesEW[3], sprite.getU0(), sprite.getV1(), r, g, b, a);
					
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[0], sideValuesEW[0], sprite.getU0(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[0], sideValuesEW[0], sprite.getU1(), sprite.getV1(), r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[1], sideValuesEW[0], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
					CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[1], sideValuesEW[0], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);

					poseStackIn.popPose();
				}
			}
		}
		poseStackIn.popPose();
	}
}