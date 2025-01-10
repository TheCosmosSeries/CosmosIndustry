package com.tcn.cosmosindustry.transport.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.transport.client.renderer.model.ModelChannel;
import com.tcn.cosmosindustry.transport.core.energy.blockentity.AbstractBlockEntityEnergyChannel;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererEnergyChannel implements BlockEntityRenderer<AbstractBlockEntityEnergyChannel> {
	
	private ModelChannel MODEL;

	public RendererEnergyChannel(BlockEntityRendererProvider.Context contextIn) {
		this.MODEL = new ModelChannel((res) -> RenderType.entitySolid(res));
	}
	
	@Override
	public void render(AbstractBlockEntityEnergyChannel blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		ResourceLocation channelTexture = IndustryReference.Resource.Transport.ENERGY_TEXTURES[0];
		ResourceLocation shellTexture = blockEntity.getChannelTier() == EnumIndustryTier.SURGE ? IndustryReference.Resource.Transport.ENERGY_TEXTURES[2] : IndustryReference.Resource.Transport.ENERGY_TEXTURES[3];
		ResourceLocation interfaceTexture = IndustryReference.Resource.Transport.ENERGY_TEXTURES[4];
				
		RenderType channelType = RenderType.entitySolid(channelTexture);
		RenderType shellType = RenderType.entityCutoutNoCull(shellTexture);
		RenderType interfaceType = RenderType.entitySolid(interfaceTexture);
		
		if (buffer instanceof BufferSource source) {
			if (blockEntity != null) {
				poseStack.pushPose();
				poseStack.translate(0.5D, 0.5D, 0.5D);
				
				this.MODEL.renderBasedOnTile(blockEntity, poseStack, source.getBuffer(channelType), null, null, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.decOpaque());
				source.endBatch(channelType);
				
				if (blockEntity.getChannelTier() != EnumIndustryTier.NORMAL) {
					this.MODEL.renderBasedOnTile(blockEntity, poseStack, null, source.getBuffer(shellType), null, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.decOpaque());
					source.endBatch(shellType);
				}
				
				this.MODEL.renderBasedOnTile(blockEntity, poseStack, null, null, source.getBuffer(interfaceType), combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.decOpaque());
				source.endBatch(interfaceType);
				
				poseStack.popPose();
			}
		}
	}
}