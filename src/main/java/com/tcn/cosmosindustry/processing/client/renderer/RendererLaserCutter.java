package com.tcn.cosmosindustry.processing.client.renderer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.ModRegistrationManager;
import com.tcn.cosmosindustry.processing.core.block.BlockLaserCutter;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityLaserCutter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class RendererLaserCutter implements BlockEntityRenderer<BlockEntityLaserCutter> {
	private static final ResourceLocation TEXTURE = IndustryReference.RESOURCE.PROCESSING.LASER_CUTTER_LOC_BER;
	private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE);

//	private Internals internals;
	private BlockEntityRendererProvider.Context context;

	public RendererLaserCutter(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;

//		this.internals = new Internals(RENDER_TYPE);
	}
	
	@Override
	public void render(BlockEntityLaserCutter blockEntity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
		
		Level level = blockEntity.getLevel();
		BlockState state = blockEntity.getBlockState();
		
		if (state.getBlock().equals(ModRegistrationManager.BLOCK_LASER_CUTTER.get())) {
			Direction dir = state.getValue(BlockLaserCutter.FACING);
			
//			poseStackIn.pushPose();
//			poseStackIn.translate(0.5D, 0.375D, 0.5D);
			
//			if (dir.equals(Direction.SOUTH)) {
//				poseStackIn.mulPose(Axis.YP.rotationDegrees(180));
//			} else if (dir.equals(Direction.WEST)) {
//				poseStackIn.mulPose(Axis.YP.rotationDegrees(270));
//			} else if (dir.equals(Direction.EAST)) {
//				poseStackIn.mulPose(Axis.YP.rotationDegrees(90));
//			}
//			
//			poseStackIn.translate(0.282D, 0.275D, 0);
//			
//			if (dir.equals(Direction.SOUTH) || dir.equals(Direction.WEST)) {
//				//poseStackIn.mulPose(Axis.YP.rotationDegrees(180));
//			}
//			
//			if (blockEntity.isProcessing()) {
//				poseStackIn.mulPose(Axis.ZP.rotationDegrees(level.getGameTime() * 6));
//			}
//			
//			this.internals.renderLeftTooth(poseStackIn, builder, combinedLightIn, combinedOverlayIn);
//			poseStackIn.popPose();
//			
//			poseStackIn.pushPose();
//			poseStackIn.translate(0.5D, 0.375D, 0.5D);
//			
//			if (dir.equals(Direction.SOUTH)) {
//				poseStackIn.mulPose(Axis.YP.rotationDegrees(180));
//			} else if (dir.equals(Direction.WEST)) {
//				poseStackIn.mulPose(Axis.YP.rotationDegrees(270));
//			} else if (dir.equals(Direction.EAST)) {
//				poseStackIn.mulPose(Axis.YP.rotationDegrees(90));
//			}
//			
//			poseStackIn.translate(-0.282D, 0.275D, 0);
//			
//			if (dir.equals(Direction.SOUTH) || dir.equals(Direction.WEST)) {
//				//poseStackIn.mulPose(Axis.YP.rotationDegrees(180));
//			}
//			
//			if (blockEntity.isProcessing()) {
//				poseStackIn.mulPose(Axis.ZN.rotationDegrees(level.getGameTime() * 6));
//			}
//			
//			this.internals.renderRightTooth(poseStackIn, builder, combinedLightIn, combinedOverlayIn);
//			poseStackIn.popPose();
//			
//			poseStackIn.pushPose();
//			poseStackIn.translate(0.5D, 0.375D, 0.5D);
//			
//			if (dir.equals(Direction.SOUTH)) {
//				poseStackIn.mulPose(Axis.YP.rotationDegrees(90));
//			} else if (dir.equals(Direction.NORTH)) {
//				poseStackIn.mulPose(Axis.YP.rotationDegrees(270));
//			} else if (dir.equals(Direction.EAST)) {
//				poseStackIn.translate(-0.565D, 0, 0);
//			}
//			
//			poseStackIn.translate(0.282D, 0.275D, 0);
//			
//			if (blockEntity.isProcessing()) {
//				poseStackIn.mulPose(Axis.ZP.rotationDegrees(level.getGameTime() * 6));
//			}
//			
//			this.internals.renderRightTooth(poseStackIn, builder, combinedLightIn, combinedOverlayIn);
//			poseStackIn.popPose();

			poseStackIn.pushPose();
			if (blockEntity.getProcessTime(0) > (blockEntity.getProcessSpeed() - 10)) {
				RecipeHolder<?> iRecipe = blockEntity.getRecipeUsed();
				
				if (iRecipe != null) {
					ItemStack output = iRecipe.value().getResultItem(RegistryAccess.EMPTY);
					
					poseStackIn.pushPose();
					poseStackIn.translate(0.5, 0.6, 0.5);
					
					if (output.getItem() instanceof BlockItem) {
						poseStackIn.scale(0.6F, 0.6F, 0.6F);
					} else {
						Quaternionf rotation = Axis.XP.rotationDegrees(90);
						
						poseStackIn.mulPose(Axis.YP.rotationDegrees(dir.equals(Direction.SOUTH) ? 180 : dir.equals(Direction.WEST) ? 90 : dir.equals(Direction.EAST) ? 270 : 0));
						
						poseStackIn.translate(0, -0.119, 0);
						poseStackIn.mulPose(rotation);
						poseStackIn.scale(0.35F, 0.35F, 0.35F);
					}
					
					Minecraft.getInstance().getItemRenderer().renderStatic(output, ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStackIn, buffer, blockEntity.getLevel(), 0);
					
					poseStackIn.popPose();
				}
				
			} else if (!blockEntity.getItem(0).isEmpty()) {
				poseStackIn.pushPose();
				poseStackIn.translate(0.5, 0.6, 0.5);
				
				if ((blockEntity.getItem(0).getItem() instanceof BlockItem)) {
					poseStackIn.scale(0.6F, 0.6F, 0.6F);
				} else {
					Quaternionf rotation = Axis.XP.rotationDegrees(90);

					poseStackIn.mulPose(Axis.YP.rotationDegrees(dir.equals(Direction.SOUTH) ? 180 : dir.equals(Direction.WEST) ? 90 : dir.equals(Direction.EAST) ? 270 : 0));
					
					poseStackIn.translate(0, -0.119, 0);
					poseStackIn.mulPose(rotation);
					poseStackIn.scale(0.35F, 0.35F, 0.35F);
				}
				
				Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(0), ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStackIn, buffer, blockEntity.getLevel(), 0);
				
				poseStackIn.popPose();
			}
			poseStackIn.popPose();
		}
	}
}