package com.tcn.cosmosindustry.processing.client.renderer;

import java.util.ArrayList;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiser;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySynthesiserStand;
import com.tcn.cosmoslibrary.client.renderer.CosmosRendererHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

	private static final ResourceLocation TEXTURE = IndustryReference.Resource.Processing.Render.SYNTHESISER_CONNECTION;
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
	
	public class ModelSynthesiserConnection extends Model {
		
		private ModelPart INTERFACE_NORMAL;
	
		public ModelSynthesiserConnection() {
			super((loc) -> RenderType.entitySolid(loc));
			
			MeshDefinition meshDef = new MeshDefinition();
			PartDefinition partDef = meshDef.getRoot();
			partDef.addOrReplaceChild("interface_normal", CubeListBuilder.create().texOffs(0, 0).addBox(6.0F, -4.0F, -4.0F, 2.0F, 8.0F, 8.0F).mirror(), PartPose.ZERO);
			this.INTERFACE_NORMAL = LayerDefinition.create(meshDef, 22, 16).bakeRoot();
		}
	
		@Override
		public void renderToBuffer(PoseStack matrixStack, VertexConsumer builder, int combinedLightIn, int combinedOverlayIn, int colour) { }
		
		public void renderBasedOnTile(BlockEntitySynthesiser tile, PoseStack matrixStack, VertexConsumer builder, int combinedLightIn, int combinedOverlayIn, int colour) {
			this.renderSide(tile.getStateForConnection(Direction.SOUTH), -1.5707964F, 0F, matrixStack, builder, combinedLightIn, combinedOverlayIn, colour);
			this.renderSide(tile.getStateForConnection(Direction.NORTH), 1.5707964F, 0F, matrixStack, builder, combinedLightIn, combinedOverlayIn, colour);
			
			this.renderSide(tile.getStateForConnection(Direction.EAST), 0F, 0F, matrixStack, builder, combinedLightIn, combinedOverlayIn, colour);
			this.renderSide(tile.getStateForConnection(Direction.WEST), 3.1415927F, 0F, matrixStack, builder, combinedLightIn, combinedOverlayIn, colour);
		}
	
		private void renderSide(boolean doRender, float Y, float Z, PoseStack matrixStack, VertexConsumer builder, int combinedLightIn, int combinedOverlayIn, int colour) {
			this.renderSideState(doRender, Y, Z, matrixStack, builder, combinedLightIn, combinedOverlayIn, colour);
		}
		
		private void renderSideState(boolean doRender, float Y, float Z, PoseStack matrixStack, VertexConsumer builder, int combinedLightIn, int combinedOverlayIn, int colour) {
			if (doRender) {
				this.INTERFACE_NORMAL.yRot = Y;
				this.INTERFACE_NORMAL.zRot = Z;
				this.INTERFACE_NORMAL.render(matrixStack, builder, combinedLightIn, combinedOverlayIn, colour);
			}
		}
	}
}