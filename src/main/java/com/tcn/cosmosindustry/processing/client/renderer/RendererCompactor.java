package com.tcn.cosmosindustry.processing.client.renderer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.processing.core.block.BlockCompactor;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityCompactor;
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
public class RendererCompactor implements BlockEntityRenderer<BlockEntityCompactor> {
	private static final ResourceLocation TEXTURE = IndustryReference.Resource.Processing.Render.COMPACTOR;
	private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE);

	private Internals internals;
	private BlockEntityRendererProvider.Context context;

	public RendererCompactor(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;
		
		this.internals = new Internals(RENDER_TYPE);
	}
	
	@Override
	public void render(BlockEntityCompactor blockEntity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
		
		Level level = blockEntity.getLevel();
		BlockState state = blockEntity.getBlockState();
		
		if (state.getBlock().equals(IndustryRegistrationManager.BLOCK_COMPACTOR.get())) {
			Direction dir = state.getValue(BlockCompactor.FACING);
			
			poseStackIn.pushPose();
			poseStackIn.translate(0.5D, -0.25F, 0.5D);
			
			if (dir.equals(Direction.SOUTH)) {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(90));
			} else if (dir.equals(Direction.WEST)) {
				
			} else if (dir.equals(Direction.EAST)) {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(180));
			} else {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(-90));
			}
			
			if (blockEntity.isProcessing()) {
				poseStackIn.translate(0.0F, 0.0F, 0.1F + Math.sin(level.getGameTime() * 6) / 16F);
			}

			this.internals.renderLeft(poseStackIn, builder, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.decOpaque());
			poseStackIn.popPose();

			poseStackIn.pushPose();
			poseStackIn.translate(0.5D, -0.25F, 0.5D);
			
			if (dir.equals(Direction.SOUTH)) {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(-90));
			} else if (dir.equals(Direction.WEST)) {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(180));
			} else if (dir.equals(Direction.EAST)) {
			
			} else {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(90));
			}

			if (blockEntity.isProcessing()) {
				poseStackIn.translate(0.0F, 0.0F, 0.1F + Math.sin(level.getGameTime() * 6) / 16F);
			}

			this.internals.renderRight(poseStackIn, builder, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.decOpaque());
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

	public class Internals extends Model {
		ModelPart left;
		ModelPart right;

		public Internals(RenderType renderType) {
			super((type) -> {return renderType; });
			
			this.left = createLeft().bakeRoot();
			this.right = createRight().bakeRoot();
		}

		@Override
		public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) { }
		
		public void renderLeft(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
			this.left.render(poseStack, buffer, packedLight, packedOverlay, color);
		}

		public void renderRight(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
			this.right.render(poseStack, buffer, packedLight, packedOverlay, color);
		}

		public static LayerDefinition createLeft() {
			MeshDefinition meshDef = new MeshDefinition();
			PartDefinition partDef = meshDef.getRoot();
			
			partDef.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 7).addBox(4.5F, 12.0F, -2.0F, 1.0F, 4.0F, 4.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("shaft", CubeListBuilder.create().texOffs(11, 0).addBox(4.75F, 13.5F, -0.5F, 3.0F, 1.0F, 1.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));

			return LayerDefinition.create(meshDef, 32, 32);
		}

		public static LayerDefinition createRight() {
			MeshDefinition meshDef = new MeshDefinition();
			PartDefinition partDef = meshDef.getRoot();

			partDef.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 7).addBox(4.5F, 12.0F, -2.0F, 1.0F, 4.0F, 4.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("shaft", CubeListBuilder.create().texOffs(11, 0).addBox(4.75F, 13.5F, -0.5F, 3.0F, 1.0F, 1.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));

			return LayerDefinition.create(meshDef, 32, 32);
		}
	}
}