package com.tcn.cosmosindustry.production.client.renderer;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.processing.client.renderer.RendererCompactor.Internals;
import com.tcn.cosmosindustry.production.core.blockentity.BlockEntitySolarPanel;
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
public class RendererSolarPanel implements BlockEntityRenderer<BlockEntitySolarPanel> {
	private static final ResourceLocation TEXTURE = IndustryReference.Resource.Production.Render.SOLAR_PANEL;
	private static final RenderType RENDER_TYPE = RenderType.entitySolid(TEXTURE);

	private Internals internals;
	private BlockEntityRendererProvider.Context context;

	public RendererSolarPanel(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;

		this.internals = new Internals(RENDER_TYPE);
	}
	
	@Override
	public void render(BlockEntitySolarPanel blockEntity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
		
		poseStackIn.pushPose();

		Level level = blockEntity.getLevel();
		BlockState state = blockEntity.getBlockState();
		
		if (blockEntity.renderBit(Direction.NORTH, Direction.EAST)) {
			poseStackIn.pushPose();
			poseStackIn.translate(15/16F, 0, 1/16F);
			this.internals.renderToBuffer(poseStackIn, builder, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.decOpaque());
			poseStackIn.popPose();
		}

		if (blockEntity.renderBit(Direction.NORTH, Direction.WEST)) {
			poseStackIn.pushPose();
			poseStackIn.translate(0, 0, 1/16F);
			this.internals.renderToBuffer(poseStackIn, builder, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.decOpaque());
			poseStackIn.popPose();
		}
		
		if (blockEntity.renderBit(Direction.SOUTH, Direction.EAST)) {
			poseStackIn.pushPose();
			poseStackIn.translate(15/16F, 0, 1);
			this.internals.renderToBuffer(poseStackIn, builder, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.decOpaque());
			poseStackIn.popPose();
		}

		if (blockEntity.renderBit(Direction.SOUTH, Direction.WEST)) {
			poseStackIn.pushPose();
			poseStackIn.translate(0, 0, 1);
			this.internals.renderToBuffer(poseStackIn, builder, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.decOpaque());
			poseStackIn.popPose();
		}

		poseStackIn.popPose();
	}
	
	public class Internals extends Model {
		ModelPart bit;
		
		public Internals(RenderType renderType) {
			super((loc) -> { return renderType; });
			
			this.bit = createBit().bakeRoot();
		}

		@Override
		public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
			this.bit.render(poseStack, buffer, packedLight, packedOverlay, color);
		}

		public static LayerDefinition createBit() {
			MeshDefinition meshDef = new MeshDefinition();
			PartDefinition partDef = meshDef.getRoot();
			
			partDef.addOrReplaceChild("bit", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 5.0F, 0.0F, 1.0F, 1.0F, 1.0F).mirror(), PartPose.rotation(0.0F, 1.570796F, 0.0F));

			return LayerDefinition.create(meshDef, 16, 16);
		}
	}
}