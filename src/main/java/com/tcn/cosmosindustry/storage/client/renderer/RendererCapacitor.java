package com.tcn.cosmosindustry.storage.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.storage.core.blockentity.AbstractBlockEntityCapacitor;
import com.tcn.cosmoslibrary.common.enums.EnumSideState;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RendererCapacitor implements BlockEntityRenderer<AbstractBlockEntityCapacitor> {

	private static final ResourceLocation TEXTURE = IndustryReference.Resource.Storage.Render.CAPACITOR_CONNECTION;
	private static final RenderType RENDER_TYPE = RenderType.entitySolid(TEXTURE);
	
	private Internals model;

	public RendererCapacitor(BlockEntityRendererProvider.Context contextIn) {
		this.model = new Internals();
	}
	
	@Override
	public void render(AbstractBlockEntityCapacitor blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
		
		if (blockEntity != null) {
			poseStack.pushPose();
			poseStack.translate(0.5D, 0.5D, 0.5D);
			
			this.model.renderBasedOnTile(blockEntity, poseStack, builder, combinedLightIn, combinedOverlayIn, ComponentColour.WHITE.decOpaque());
			
			poseStack.popPose();
		}
	}

	public class Internals extends Model {
		private ModelPart INTERFACE_NORMAL;
		private ModelPart INTERFACE_OUTPUT;
		private ModelPart INTERFACE_INPUT;
	
		public Internals() {
			super((loc) -> RenderType.entitySolid(loc));
			
			this.INTERFACE_NORMAL = createConnection(EnumSideState.INTERFACE_NORMAL).bakeRoot();
			this.INTERFACE_OUTPUT = createConnection(EnumSideState.INTERFACE_OUTPUT).bakeRoot();
			this.INTERFACE_INPUT = createConnection(EnumSideState.INTERFACE_INPUT).bakeRoot();
		}
		
		private LayerDefinition createConnection(EnumSideState state) {
			int textureOffsetY = state.equals(EnumSideState.INTERFACE_NORMAL) ? 0 : state.equals(EnumSideState.INTERFACE_OUTPUT) ? 32 : 16;
			
			MeshDefinition meshDef = new MeshDefinition();
			PartDefinition partDef = meshDef.getRoot();
			partDef.addOrReplaceChild(state.getName(), CubeListBuilder.create().texOffs(0, textureOffsetY).addBox(4.0F, -4.0F, -4.0F, 4.0F, 8.0F, 8.0F).mirror(), PartPose.ZERO);
			return LayerDefinition.create(meshDef, 24, 48);
		}
	
		@Override
		public void renderToBuffer(PoseStack poseStack, VertexConsumer builder, int combinedLightIn, int combinedOverlayIn, int colour) { }
		
		public void renderBasedOnTile(AbstractBlockEntityCapacitor blockEntity, PoseStack poseStack, VertexConsumer builder, int combinedLightIn, int combinedOverlayIn, int colour) {
			this.renderSide(poseStack, builder, -1.5707964F, 0F, combinedLightIn, combinedOverlayIn, colour, blockEntity.getStateForConnection(Direction.SOUTH), blockEntity.getSide(Direction.SOUTH));
			this.renderSide(poseStack, builder, 1.5707964F, 0F, combinedLightIn, combinedOverlayIn, colour, blockEntity.getStateForConnection(Direction.NORTH), blockEntity.getSide(Direction.NORTH));
			
			this.renderSide(poseStack, builder, 0F, 0F, combinedLightIn, combinedOverlayIn, colour, blockEntity.getStateForConnection(Direction.EAST), blockEntity.getSide(Direction.EAST));
			this.renderSide(poseStack, builder, 3.1415927F, 0F, combinedLightIn, combinedOverlayIn, colour, blockEntity.getStateForConnection(Direction.WEST), blockEntity.getSide(Direction.WEST));
			
			this.renderSide(poseStack, builder, 0.0F, 1.5707964F, combinedLightIn, combinedOverlayIn, colour, blockEntity.getStateForConnection(Direction.UP), blockEntity.getSide(Direction.UP));
			this.renderSide(poseStack, builder, 0.0F, -1.5707964F, combinedLightIn, combinedOverlayIn, colour, blockEntity.getStateForConnection(Direction.DOWN), blockEntity.getSide(Direction.DOWN));
		}
		
		private void renderSide(PoseStack poseStack, VertexConsumer builder, float Y, float Z, int combinedLightIn, int combinedOverlayIn, int colour, boolean doRender, EnumSideState stateIn) {
			if (doRender) {
				switch(stateIn) {
					case INTERFACE_NORMAL:
						renderSimple(poseStack, builder, Y, Z, combinedLightIn, combinedOverlayIn, colour, this.INTERFACE_NORMAL);
						return;
					case INTERFACE_OUTPUT:
						renderSimple(poseStack, builder, Y, Z, combinedLightIn, combinedOverlayIn, colour, this.INTERFACE_OUTPUT);
						return;
					case INTERFACE_INPUT:
						renderSimple(poseStack, builder, Y, Z, combinedLightIn, combinedOverlayIn, colour, this.INTERFACE_INPUT);
						return;
					default:
						return;
				}
			}
		}
		
		private void renderSimple(PoseStack poseStack, VertexConsumer builder, float Y, float Z, int combinedLightIn, int combinedOverlayIn, int colour, ModelPart modelPart) {
			modelPart.yRot = Y;
			modelPart.zRot = Z;
			modelPart.render(poseStack, builder, combinedLightIn, combinedOverlayIn, colour);
		}
	}
}