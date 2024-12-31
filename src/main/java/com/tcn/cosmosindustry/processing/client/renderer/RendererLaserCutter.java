package com.tcn.cosmosindustry.processing.client.renderer;

import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.core.recipe.LaserCutterRecipe;
import com.tcn.cosmosindustry.processing.core.block.BlockLaserCutter;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityLaserCutter;
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
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class RendererLaserCutter implements BlockEntityRenderer<BlockEntityLaserCutter> {
	private static final ResourceLocation TEXTURE = IndustryReference.Resource.Processing.Render.LASER_CUTTER;
	private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE);

	private Internals internals;
	private BlockEntityRendererProvider.Context context;

	public RendererLaserCutter(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;

		this.internals = new Internals(RENDER_TYPE);
	}
	
	@Override
	public void render(BlockEntityLaserCutter blockEntity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
		
		Level level = blockEntity.getLevel();
		BlockState state = blockEntity.getBlockState();
		BlockPos pos = blockEntity.getBlockPos();
		
		if (state.getBlock().equals(IndustryRegistrationManager.BLOCK_LASER_CUTTER.get())) {
			Direction dir = state.getValue(BlockLaserCutter.FACING);

			poseStackIn.pushPose();
			poseStackIn.translate(0.0F, 0.0F, 1.0F);

			GL11.glDepthMask(true);
			this.internals.renderTopFace(poseStackIn, builder, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.withAlpha(0.4F));
			GL11.glDepthMask(false);
			poseStackIn.popPose();

			poseStackIn.pushPose();
			poseStackIn.translate(0.0F, -(3.0F/16.0F), 0.0F);
			
			if (dir.equals(Direction.NORTH)) {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(-90));
			} else if (dir.equals(Direction.SOUTH)) {
				poseStackIn.translate(0.0F, 0.0F, 1.0F);
				poseStackIn.mulPose(Axis.YP.rotationDegrees(-90));
			} else if (dir.equals(Direction.WEST)) {
				poseStackIn.translate(0.0F, 0.0F, 1.0F);
			} else if (dir.equals(Direction.EAST)) {
				poseStackIn.translate(1.0F, 0.0F, 1.0F);
			}

			GL11.glDepthMask(true);
			this.internals.renderFrontFace(poseStackIn, builder, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.withAlpha(0.4F));
			GL11.glDepthMask(false);
			
			//Have to put this here to enable Items to render through translucent textures when NOT processing
			CosmosRendererHelper.renderLaser(buffer, poseStackIn, pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ(), 50, 0.5F, 0.005, ComponentColour.WHITE);
			poseStackIn.popPose();

			poseStackIn.pushPose();
			if (blockEntity.isProcessing()) {
				RecipeHolder<?> iRecipe = blockEntity.getRecipeUsed();
				if (iRecipe != null) {
					ComponentColour colour = ((LaserCutterRecipe) iRecipe.value()).getRecipeColour();
					
					float offsetZ = 0.4F;
					float offsetY = 6.6F/16F;
					float width = 0.5F/16F;
					CosmosRendererHelper.renderLaser(buffer, poseStackIn, pos.getX(), pos.getY(), pos.getZ(), pos.getX() - offsetZ, pos.getY() + offsetY, pos.getZ() - offsetZ, 50, 0.5F, width, colour);
					CosmosRendererHelper.renderLaser(buffer, poseStackIn, pos.getX(), pos.getY(), pos.getZ(), pos.getX() + offsetZ, pos.getY() + offsetY, pos.getZ() - offsetZ, 50, 0.5F, width, colour);
					CosmosRendererHelper.renderLaser(buffer, poseStackIn, pos.getX(), pos.getY(), pos.getZ(), pos.getX() - offsetZ, pos.getY() + offsetY, pos.getZ() + offsetZ, 50, 0.5F, width, colour);
					CosmosRendererHelper.renderLaser(buffer, poseStackIn, pos.getX(), pos.getY(), pos.getZ(), pos.getX() + offsetZ, pos.getY() + offsetY, pos.getZ() + offsetZ, 50, 0.5F, width, colour);
				}
			}
			poseStackIn.popPose();
						
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

					GL11.glDepthMask(false);
					Minecraft.getInstance().getItemRenderer().renderStatic(output, ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStackIn, buffer, level, 0);
					GL11.glDepthMask(true);
					
					poseStackIn.popPose();
				}
			} else if (!blockEntity.getItem(0).isEmpty()) {
				poseStackIn.pushPose();
				poseStackIn.translate(0.5, 0.6, 0.5);
				
				if (blockEntity.getItem(0).getItem() instanceof BlockItem && !blockEntity.getItem(0).getItem().equals(Items.REDSTONE)) {
					poseStackIn.scale(0.6F, 0.6F, 0.6F);
				} else {
					Quaternionf rotation = Axis.XP.rotationDegrees(90);
					poseStackIn.mulPose(Axis.YP.rotationDegrees(dir.equals(Direction.SOUTH) ? 180 : dir.equals(Direction.WEST) ? 90 : dir.equals(Direction.EAST) ? 270 : 0));
					poseStackIn.translate(0, -0.119, 0);
					poseStackIn.mulPose(rotation);
					poseStackIn.scale(0.35F, 0.35F, 0.35F);
				}

				GL11.glDepthMask(false);
				Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(0), ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStackIn, buffer, level, 0);
				GL11.glDepthMask(true);
				
				poseStackIn.popPose();
			}
			poseStackIn.popPose();
			GL11.glDepthMask(true);
		}
	}
	
	public class Internals extends Model {
		ModelPart topFace;
		ModelPart frontFace;

		public Internals(RenderType renderType) {
			super((loc) -> { return renderType;});
			
			this.topFace = createTopFace().bakeRoot();
			this.frontFace = createFrontFace().bakeRoot();
		}
		
		public static LayerDefinition createTopFace() {
			MeshDefinition meshDef = new MeshDefinition();
			PartDefinition partDef = meshDef.getRoot();

			partDef.addOrReplaceChild("face", CubeListBuilder.create().texOffs(0, 0).addBox(2.0F, 16.0F, 2.0F, 12F, 0.0F, 12F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			
			return LayerDefinition.create(meshDef, 64, 32);
		}

		public static LayerDefinition createFrontFace() {
			MeshDefinition meshDef = new MeshDefinition();
			PartDefinition partDef = meshDef.getRoot();

			partDef.addOrReplaceChild("face", CubeListBuilder.create().texOffs(0, 12).addBox(1.0F, 9.0F, 0.0F, 14.0F, 8.0F, 0.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			
			return LayerDefinition.create(meshDef, 64, 32);
		}

		@Override
		public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) { }
		
		public void renderTopFace(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
			this.topFace.render(poseStack, buffer, packedLight, packedOverlay, color);
		}
		
		public void renderFrontFace(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) { 
			this.frontFace.render(poseStack, buffer, packedLight, packedOverlay, color);
		}
	}
	
}