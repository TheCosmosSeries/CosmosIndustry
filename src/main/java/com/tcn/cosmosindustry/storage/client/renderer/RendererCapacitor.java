package com.tcn.cosmosindustry.storage.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.storage.client.renderer.model.ModelCapacitorConnection;
import com.tcn.cosmosindustry.storage.core.blockentity.AbstractBlockEntityCapacitor;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererCapacitor implements BlockEntityRenderer<AbstractBlockEntityCapacitor> {

	private static final ResourceLocation TEXTURE = IndustryReference.Resource.Storage.Render.CAPACITOR_CONNECTION;
	private static final RenderType RENDER_TYPE = RenderType.entitySolid(TEXTURE);
	
	private ModelCapacitorConnection model;

	public RendererCapacitor(BlockEntityRendererProvider.Context contextIn) {
		this.model = new ModelCapacitorConnection();
	}
	
	@Override
	public void render(AbstractBlockEntityCapacitor blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
		
		if (blockEntity != null) {
			poseStack.pushPose();
			poseStack.translate(0.5D, 0.5D, 0.5D);
			
			this.model.renderBasedOnTile(blockEntity, poseStack, builder, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.decOpaque());
			
			poseStack.popPose();
		}
	}
}