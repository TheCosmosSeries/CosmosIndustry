package com.tcn.cosmosindustry.processing.client.renderer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.ModRegistrationManager;
import com.tcn.cosmosindustry.processing.core.block.BlockGrinder;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityGrinder;

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
public class RendererGrinder implements BlockEntityRenderer<BlockEntityGrinder> {
	private static final ResourceLocation TEXTURE = IndustryReference.RESOURCE.PROCESSING.GRINDER_LOC_BER;
	private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE);
	
	private Internals internals;
	private BlockEntityRendererProvider.Context context;

	public RendererGrinder(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;
		
		this.internals = new Internals(RENDER_TYPE);
	}
	
	@Override
	public void render(BlockEntityGrinder blockEntity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
		
		Level level = blockEntity.getLevel();
		BlockState state = blockEntity.getBlockState();
		
		if (state.getBlock().equals(ModRegistrationManager.BLOCK_GRINDER.get())) {
			Direction dir = state.getValue(BlockGrinder.FACING);
			
			poseStackIn.pushPose();
			poseStackIn.translate(0.5D, 0.375D, 0.5D);
			
			if (dir.equals(Direction.SOUTH)) {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(180));
			} else if (dir.equals(Direction.WEST)) {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(270));
			} else if (dir.equals(Direction.EAST)) {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(90));
			}
			
			poseStackIn.translate(0.282D, 0.275D, 0);
			
			if (dir.equals(Direction.SOUTH) || dir.equals(Direction.WEST)) {
				//poseStackIn.mulPose(Axis.YP.rotationDegrees(180));
			}
			
			if (blockEntity.isProcessing()) {
				poseStackIn.mulPose(Axis.ZP.rotationDegrees(level.getGameTime() * 6));
			}
			
			this.internals.renderLeftTooth(poseStackIn, builder, combinedLightIn, combinedOverlayIn);
			poseStackIn.popPose();
			
			poseStackIn.pushPose();
			poseStackIn.translate(0.5D, 0.375D, 0.5D);
			
			if (dir.equals(Direction.SOUTH)) {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(180));
			} else if (dir.equals(Direction.WEST)) {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(270));
			} else if (dir.equals(Direction.EAST)) {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(90));
			}
			
			poseStackIn.translate(-0.282D, 0.275D, 0);
			
			if (dir.equals(Direction.SOUTH) || dir.equals(Direction.WEST)) {
				//poseStackIn.mulPose(Axis.YP.rotationDegrees(180));
			}
			
			if (blockEntity.isProcessing()) {
				poseStackIn.mulPose(Axis.ZN.rotationDegrees(level.getGameTime() * 6));
			}
			
			this.internals.renderRightTooth(poseStackIn, builder, combinedLightIn, combinedOverlayIn);
			poseStackIn.popPose();
			
			poseStackIn.pushPose();
			poseStackIn.translate(0.5D, 0.375D, 0.5D);
			
			if (dir.equals(Direction.SOUTH)) {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(90));
			} else if (dir.equals(Direction.NORTH)) {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(270));
			} else if (dir.equals(Direction.EAST)) {
				poseStackIn.translate(-0.565D, 0, 0);
			}
			
			poseStackIn.translate(0.282D, 0.275D, 0);
			
			if (blockEntity.isProcessing()) {
				poseStackIn.mulPose(Axis.ZP.rotationDegrees(level.getGameTime() * 6));
			}
			
			this.internals.renderRightTooth(poseStackIn, builder, combinedLightIn, combinedOverlayIn);
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
		ModelPart leftTooth;
		ModelPart rightTooth;
		
		public Internals(RenderType renderType) {
			super((type) -> { return renderType; });
			
			this.leftTooth = createLeftTooth().bakeRoot();
			this.rightTooth = createRightTooth().bakeRoot();
		}
		
		public static LayerDefinition createLeftTooth(){
			MeshDefinition meshDef = new MeshDefinition();
			PartDefinition partDef = meshDef.getRoot();
			
			partDef.addOrReplaceChild("a", CubeListBuilder.create().texOffs(6, 8).addBox(-3.5F, -0.5F, -0.5F, 7.0F, 1.0F, 1.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("b", CubeListBuilder.create().texOffs(8, 0).addBox(1.50F, -1.0F, -2.0F, 1.0F, 2.0F, 4.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("c", CubeListBuilder.create().texOffs(0, 0).addBox(1.50F, -2.0F, -1.0F, 1.0F, 4.0F, 2.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("d", CubeListBuilder.create().texOffs(8, 0).addBox(-2.5F, -1.0F, -2.0F, 1.0F, 2.0F, 4.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("e", CubeListBuilder.create().texOffs(8, 0).addBox(-0.5F, -1.0F, -2.0F, 1.0F, 2.0F, 4.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("f", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -2.0F, -1.0F, 1.0F, 4.0F, 2.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("g", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -2.0F, -1.0F, 1.0F, 4.0F, 2.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			
			return LayerDefinition.create(meshDef, 32, 32);
		}

		public static LayerDefinition createRightTooth(){
			MeshDefinition meshDef = new MeshDefinition();
			PartDefinition partDef = meshDef.getRoot();

			partDef.addOrReplaceChild("a", CubeListBuilder.create().texOffs(8, 0).addBox(-0.5F, -1.0F, -2.0F, 1.0F, 2.0F, 4.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("b", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -2.0F, -1.0F, 1.0F, 4.0F, 2.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("c", CubeListBuilder.create().texOffs(8, 0).addBox(-2.5F, -1.0F, -2.0F, 1.0F, 2.0F, 4.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("d", CubeListBuilder.create().texOffs(0, 0).addBox(1.50F, -2.0F, -1.0F, 1.0F, 4.0F, 2.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("e", CubeListBuilder.create().texOffs(8, 0).addBox(1.50F, -1.0F, -2.0F, 1.0F, 2.0F, 4.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("f", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -2.0F, -1.0F, 1.0F, 4.0F, 2.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("g", CubeListBuilder.create().texOffs(6, 8).addBox(-3.5F, -0.5F, -0.5F, 7.0F, 1.0F, 1.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			
			return LayerDefinition.create(meshDef, 32, 32);
		}
		
		public void renderLeftTooth(PoseStack poseStackIn, VertexConsumer builder, int combinedLightIn, int combinedOverlayIn) {
			this.leftTooth.render(poseStackIn, builder, combinedLightIn, combinedOverlayIn);
		}
		
		public void renderRightTooth(PoseStack poseStackIn, VertexConsumer builder, int combinedLightIn, int combinedOverlayIn) {
			this.rightTooth.render(poseStackIn, builder, combinedLightIn, combinedOverlayIn);
		}
		
		@Override
		public void renderToBuffer(PoseStack poseStackIn, VertexConsumer builder, int combinedLightIn, int combinedOverlayIn, int colour) {
			this.renderLeftTooth(poseStackIn, builder, combinedLightIn, combinedOverlayIn);
			this.renderRightTooth(poseStackIn, builder, combinedLightIn, combinedOverlayIn);
		}	
	}
}