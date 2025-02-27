package com.tcn.cosmosindustry.storage.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tcn.cosmosindustry.storage.core.blockentity.AbstractBlockEntityFluidTank;
import com.tcn.cosmoslibrary.client.renderer.CosmosRendererHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class RendererFluidTank implements BlockEntityRenderer<AbstractBlockEntityFluidTank> {

	private BlockEntityRendererProvider.Context context;
	
	public RendererFluidTank(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;
	}
	
	@Override
	public void render(AbstractBlockEntityFluidTank blockEntity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		poseStackIn.pushPose();
		
		if (blockEntity.getCurrentFluidAmount() > 0) {
			FluidTank fluidTank = blockEntity.getFluidTank();

			FluidStack fluid = fluidTank.getFluid();
			if (fluid.isEmpty()) {
				return;
			}

			Fluid renderFluid = fluid.getFluid();
			if (renderFluid == null) {
				return;
			}
			
			IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(renderFluid.defaultFluidState());
			TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(props.getStillTexture());
			VertexConsumer builderA = buffer.getBuffer(RenderType.translucent());

			float[] values = new float[] { 0.1F/16F, 15.9F/16F };
			float[] height = new float[] { 0.1F/16F, 15.9F/16F };
			float fillLevel = blockEntity.fluidTank.getFillLevel() / 16F;
			
			height[1] = (float) Mth.map(fillLevel, 0, 1, values[0], values[1]);
			
			float mappedHeight = CosmosRendererHelper.getMappedTextureHeight(sprite, fillLevel * 16F);
			
			int color = props.getTintColor();
			
			float a = 1.0F;
			float r = (color >> 16 & 0xFF) / 255.0F;
			float g = (color >> 8 & 0xFF) / 255.0F;
			float b = (color & 0xFF) / 255.0F;

			poseStackIn.pushPose();
			
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
			
			// Bottom Face
			CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[0], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[0], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[0], values[0], sprite.getU1(), sprite.getV0(), r, g, b, a);
			CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[0], values[0], sprite.getU0(), sprite.getV0(), r, g, b, a);
			
			// Top Face of Bottom
			CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[0], values[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[0], values[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
			CosmosRendererHelper.addF(builderA, poseStackIn, values[0], height[0], values[0], sprite.getU1(), sprite.getV0(), r, g, b, a);
			CosmosRendererHelper.addF(builderA, poseStackIn, values[1], height[0], values[0], sprite.getU0(), sprite.getV0(), r, g, b, a);
				
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
		poseStackIn.popPose();
	}
}