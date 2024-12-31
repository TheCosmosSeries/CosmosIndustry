package com.tcn.cosmosindustry.processing.client.renderer;

import java.util.function.Function;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.processing.core.block.BlockSeparator;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySeparator;
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
public class RendererSeparator implements BlockEntityRenderer<BlockEntitySeparator> {
	private static final ResourceLocation TEXTURE = IndustryReference.Resource.Processing.Render.SEPARATOR;
	private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE);

	private Internals internals;
	private BlockEntityRendererProvider.Context context;

	public RendererSeparator(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;

		this.internals = new Internals(RENDER_TYPE);
	}
	
	@Override
	public void render(BlockEntitySeparator blockEntity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
		
		Level level = blockEntity.getLevel();
		BlockState state = blockEntity.getBlockState();
		
		if (state.getBlock().equals(IndustryRegistrationManager.BLOCK_SEPARATOR.get())) {
			Direction dir = state.getValue(BlockSeparator.FACING);
			
			poseStackIn.pushPose();
			poseStackIn.translate(0.5D, 1.5D, 0.5D);
			poseStackIn.mulPose(Axis.XP.rotationDegrees(180));
			poseStackIn.mulPose(Axis.YP.rotationDegrees(45));
			
			if (blockEntity.isProcessing()) {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(level.getGameTime() * 10));
			}
			
			this.internals.renderToBuffer(poseStackIn, builder, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.decOpaque());
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
		ModelPart model;
		
		public Internals(RenderType renderType) {
			super((loc) -> { return renderType; });
			
			this.model = createModel().bakeRoot();
		}

		@Override
		public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
			this.model.render(poseStack, buffer, packedLight, packedOverlay, color); 
		}
		
		public static LayerDefinition createModel(){
			MeshDefinition meshDef = new MeshDefinition();
			PartDefinition partDef = meshDef.getRoot();
			
			partDef.addOrReplaceChild("arm1", CubeListBuilder.create().texOffs(0, 0).addBox(4.0F, 15.0F, -1.0F, 1.0F, 4.0F, 2.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("arm2", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 15.0F, -1.0F, 1.0F, 4.0F, 2.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("arm3", CubeListBuilder.create().texOffs(0, 8).addBox(-4.0F, 15.0F, -1.0F, 1.0F, 1.0F, 2.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("arm4", CubeListBuilder.create().texOffs(0, 8).addBox(3.0F, 15.0F, -1.0F, 1.0F, 1.0F, 2.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("arm5", CubeListBuilder.create().texOffs(8, 0).addBox(-1.0F, 15.0F, -5.0F, 2.0F, 4.0F, 1.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("arm6", CubeListBuilder.create().texOffs(8, 0).addBox(-1.0F, 15.0F, 4.0F, 2.0F, 4.0F, 1.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("arm7", CubeListBuilder.create().texOffs(8, 7).addBox(-1.0F, 15.0F, -4.0F, 2.0F, 1.0F, 1.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("arm8", CubeListBuilder.create().texOffs(8, 7).addBox(-1.0F, 15.0F, 3.0F, 2.0F, 1.0F, 1.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			
			return LayerDefinition.create(meshDef, 32, 32);
		}
	}
	
}