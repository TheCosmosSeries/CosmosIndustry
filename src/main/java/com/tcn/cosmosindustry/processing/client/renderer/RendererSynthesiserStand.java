package com.tcn.cosmosindustry.processing.client.renderer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiserStand;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererSynthesiserStand implements BlockEntityRenderer<BlockEntitySynthesiserStand> {

	@OnlyIn(Dist.CLIENT)
	public RendererSynthesiserStand(BlockEntityRendererProvider.Context contextIn) { }
	
	@Override
	public void render(BlockEntitySynthesiserStand blockEntity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		Level world = blockEntity.getLevel();
		
		int slot = 0;

		if (!blockEntity.getItem(slot).isEmpty()) {
			poseStackIn.pushPose();
			poseStackIn.translate(0.5, 1.1, 0.5);
			
			Quaternionf rotation = Axis.YP.rotationDegrees(world.getGameTime() * 2);
			
			poseStackIn.mulPose(rotation);
			if ((blockEntity.getItem(slot).getItem() instanceof BlockItem)) {
				poseStackIn.scale(0.7F, 0.7F, 0.7F);
			} else {
				poseStackIn.scale(0.5F, 0.5F, 0.5F);
			}
			
			Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(slot), ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStackIn, buffer, blockEntity.getLevel(), 0);
			
			poseStackIn.popPose();
		}
	}
}