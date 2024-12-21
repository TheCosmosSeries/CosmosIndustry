package com.tcn.cosmosindustry.processing.client.renderer;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.processing.client.renderer.model.ModelSynthesiserConnection;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiser;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiserStand;
import com.tcn.cosmoslibrary.client.renderer.lib.CosmosRendererHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererSynthesiser implements BlockEntityRenderer<BlockEntitySynthesiser> {

	private static final ResourceLocation TEXTURE = IndustryReference.RESOURCE.PROCESSING.SYNTHESISER_CONNECTION;
	private static final RenderType RENDER_TYPE = RenderType.entitySolid(TEXTURE);
	
	private ModelSynthesiserConnection model;

	@OnlyIn(Dist.CLIENT)
	public RendererSynthesiser(BlockEntityRendererProvider.Context contextIn) { 
		this.model = new ModelSynthesiserConnection();
	}
	
	@Override
	public void render(BlockEntitySynthesiser blockEntity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		BlockPos pos = blockEntity.getBlockPos();
		Level world = blockEntity.getLevel();
		HolderLookup.Provider provider = world.registryAccess();
		
		RecipeHolder<?> recipe = blockEntity.getRecipeUsed();
		
		if (blockEntity != null) {
			if (!blockEntity.getItem(0).isEmpty()) {
				poseStackIn.pushPose();
				poseStackIn.translate(0.5, 1.3, 0.5);
				
				poseStackIn.mulPose(Axis.YP.rotationDegrees(world.getGameTime() * 2));
				
				if ((blockEntity.getItem(0).getItem() instanceof BlockItem)) {
					poseStackIn.scale(0.7F, 0.7F, 0.7F);
				} else {
					poseStackIn.scale(0.5F, 0.5F, 0.5F);
				}
				
				Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(0), ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStackIn, buffer, blockEntity.getLevel(), 0);
				
				poseStackIn.popPose();
			}
	
			if (recipe != null) {
				if (blockEntity.canProcessFourWay(recipe, provider) || blockEntity.canProcessEightWay(recipe, provider)) {
					ArrayList<BlockEntity> tiles = blockEntity.getBlockEntitiesEightWay();

					for (int i = 0; i < tiles.size(); i++) {
						if (tiles.get(i) instanceof BlockEntitySynthesiserStand blockEntityStand) {
						
							if (!(blockEntityStand.getItem(0).isEmpty())) {
								CosmosRendererHelper.renderLaser(buffer, poseStackIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, blockEntityStand.getBlockPos().getX() + 0.5, blockEntityStand.getBlockPos().getY() + 0.5, blockEntityStand.getBlockPos().getZ() + 0.5, 80, 0.2F, 0.1F, blockEntity.getColour(recipe, provider));
							}
						}
					}
				}
			}

			VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
			poseStackIn.pushPose();
			poseStackIn.translate(0.5D, 0.5D, 0.5D);
			
			this.model.renderBasedOnTile(blockEntity, poseStackIn, builder, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.decOpaque());
			
			poseStackIn.popPose();
		}
	}
}