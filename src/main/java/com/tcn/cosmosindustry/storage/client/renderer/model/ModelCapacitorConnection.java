package com.tcn.cosmosindustry.storage.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tcn.cosmosindustry.storage.core.blockentity.AbstractBlockEntityCapacitor;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;

public class ModelCapacitorConnection extends Model {
	
	private ModelPart INTERFACE_NORMAL;

	public ModelCapacitorConnection() {
		super((loc) -> RenderType.entitySolid(loc));
		
		MeshDefinition meshDef = new MeshDefinition();
		PartDefinition partDef = meshDef.getRoot();
		partDef.addOrReplaceChild("interface_normal", CubeListBuilder.create().texOffs(0, 0).addBox(5.0F, -4.0F, -4.0F, 3.0F, 8.0F, 8.0F).mirror(), PartPose.ZERO);
		this.INTERFACE_NORMAL = LayerDefinition.create(meshDef, 22, 16).bakeRoot();
	}

	@Override
	public void renderToBuffer(PoseStack matrixStack, VertexConsumer builder, int combinedLightIn, int combinedOverlayIn, int colour) { }
	
	public void renderBasedOnTile(AbstractBlockEntityCapacitor tile, PoseStack matrixStack, VertexConsumer builder, int combinedLightIn, int combinedOverlayIn, int colour) {
		this.renderSide(tile.getStateForConnection(Direction.SOUTH), -1.5707964F, 0F, matrixStack, builder, combinedLightIn, combinedOverlayIn, colour);
		this.renderSide(tile.getStateForConnection(Direction.NORTH), 1.5707964F, 0F, matrixStack, builder, combinedLightIn, combinedOverlayIn, colour);
		
		this.renderSide(tile.getStateForConnection(Direction.EAST), 0F, 0F, matrixStack, builder, combinedLightIn, combinedOverlayIn, colour);
		this.renderSide(tile.getStateForConnection(Direction.WEST), 3.1415927F, 0F, matrixStack, builder, combinedLightIn, combinedOverlayIn, colour);
		
		this.renderSide(tile.getStateForConnection(Direction.UP), 0.0F, 1.5707964F, matrixStack, builder, combinedLightIn, combinedOverlayIn, colour);
		this.renderSide(tile.getStateForConnection(Direction.DOWN), 0.0F, -1.5707964F, matrixStack, builder, combinedLightIn, combinedOverlayIn, colour);
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