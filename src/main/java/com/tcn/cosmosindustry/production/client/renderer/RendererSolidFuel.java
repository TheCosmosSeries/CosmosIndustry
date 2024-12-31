package com.tcn.cosmosindustry.production.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.production.core.block.BlockSolidFuel;
import com.tcn.cosmosindustry.production.core.blockentity.BlockEntitySolidFuel;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class RendererSolidFuel implements BlockEntityRenderer<BlockEntitySolidFuel> {
	private static final ResourceLocation TEXTURE = IndustryReference.Resource.Production.Render.SOLID_FUEL;
	private static final RenderType RENDER_TYPE = RenderType.entitySolid(TEXTURE);

	private Internals internals;
	private BlockEntityRendererProvider.Context context;

	public RendererSolidFuel(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;

		this.internals = new Internals(RENDER_TYPE);
	}
	
	@Override
	public void render(BlockEntitySolidFuel blockEntity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
		
		poseStackIn.pushPose();

		Level level = blockEntity.getLevel();
		BlockState state = blockEntity.getBlockState();
		
		if (blockEntity.isProducing()) {
			poseStackIn.pushPose();
			if (state.getValue(BlockSolidFuel.FACING).equals(Direction.NORTH) || state.getValue(BlockSolidFuel.FACING).equals(Direction.SOUTH)) {
				poseStackIn.mulPose(Axis.YP.rotationDegrees(90));
				poseStackIn.translate(-14/16F, 3.5/16F, 8/16F);
				
				poseStackIn.mulPose(Axis.XN.rotationDegrees(level.getGameTime() * 6));
				this.internals.renderToBuffer(poseStackIn, builder, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.decOpaque());
			}
			
			if (state.getValue(BlockSolidFuel.FACING).equals(Direction.EAST) || state.getValue(BlockSolidFuel.FACING).equals(Direction.WEST)) {
				poseStackIn.translate(2/16F, 3.5/16F, 8/16F);
				poseStackIn.mulPose(Axis.XN.rotationDegrees(level.getGameTime() * 6));
				this.internals.renderToBuffer(poseStackIn, builder, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.decOpaque());
			}
			poseStackIn.popPose();
		}

		poseStackIn.popPose();
	}
	
	public class Internals extends Model {
		ModelPart moving;
		
		public Internals(RenderType renderType) {
			super((loc) -> { return renderType; });
			
			this.moving = createMoving().bakeRoot();
		}

		@Override
		public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
			this.moving.render(poseStack, buffer, packedLight, packedOverlay, color);
		}

		public static LayerDefinition createMoving() {
			MeshDefinition meshDef = new MeshDefinition();
			PartDefinition partDef = meshDef.getRoot();
			
			partDef.addOrReplaceChild("horizontal", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 2.0F, 12.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));
			partDef.addOrReplaceChild("vertical", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 4.0F, 12.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));

			return LayerDefinition.create(meshDef, 32, 32);
		}
	}
}