package com.tcn.cosmosindustry.production.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.processing.core.block.BlockOrePlant;
import com.tcn.cosmosindustry.production.core.block.BlockPeltier;
import com.tcn.cosmosindustry.production.core.block.BlockSolidFuel;
import com.tcn.cosmosindustry.production.core.blockentity.BlockEntityPeltier;
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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class RendererPeltier implements BlockEntityRenderer<BlockEntityPeltier> {
	private static final ResourceLocation TEXTURE = IndustryReference.Resource.Production.Render.PELTIER;
	private static final RenderType RENDER_TYPE = RenderType.entitySolid(TEXTURE);

	private Internals internals;
	private BlockEntityRendererProvider.Context context;

	public RendererPeltier(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;

		this.internals = new Internals(RENDER_TYPE);
	}
	
	@Override
	public void render(BlockEntityPeltier blockEntity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		poseStackIn.pushPose();

		Level level = blockEntity.getLevel();
		BlockState state = blockEntity.getBlockState();
		
		if (blockEntity.getCurrentFluidAmount(0) > 0) {
			poseStackIn.pushPose();
			this.renderFluidTank(poseStackIn, buffer, blockEntity.getFluidTank(0), blockEntity.getFluidFillLevel(0), state.getValue(BlockPeltier.FACING), false);
			poseStackIn.popPose();
			
			poseStackIn.pushPose();
			this.renderFluidTank(poseStackIn, buffer, blockEntity.getFluidTank(1), blockEntity.getFluidFillLevel(1), state.getValue(BlockPeltier.FACING), true);
			poseStackIn.popPose();
		}
		
		VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
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
	
	public void renderFluidTank(PoseStack poseStackIn, MultiBufferSource buffer, FluidTank fluidTank, int fillLevelIn, Direction facing, boolean second) {
		FluidStack fluid = fluidTank.getFluid();
		if (fluid.isEmpty()) {
			return;
		}

		Fluid renderFluid = fluid.getFluid();
		if (renderFluid == null) {
			return;
		}
		
		IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(renderFluid.defaultFluidState());
		TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(props.getStillTexture());
		VertexConsumer builderA = buffer.getBuffer(RenderType.translucent());

		float[] topValues    = new float[] { 1.10F/16F, 9.10F/16F, 14.9F/16F };
		float[] sideValuesNS = new float[] { 1.10F/16F, 9.10F/16F, 14.9F/16F };
		float[] sideValuesEW = new float[] { 1.10F/16F, 6.90F/16F, 9.10F/16F, 14.9F/16F };
		
		float[] height = new float[] { 9.1F/16F, 15.9F/16F };
		float fillLevel = fillLevelIn / 7F;
		
		height[1] = (float) Mth.map(fillLevel, 0, 1, 9.1F/16F, 15.9/16F);
		
		float mappedHeight = CosmosRendererHelper.getMappedTextureHeight(sprite, fillLevel * 16);
		
		int color = props.getTintColor();
		
		float a = 1.0F;
		float r = (color >> 16 & 0xFF) / 255.0F;
		float g = (color >> 8 & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;

		poseStackIn.pushPose();
		if (facing.equals(Direction.NORTH)) {
			poseStackIn.mulPose(Axis.YP.rotationDegrees(-90));
			poseStackIn.translate(0, 0, second ? -1F : -(1F + 8/16F));
		} else if (facing.equals(Direction.SOUTH)) {
			poseStackIn.mulPose(Axis.YP.rotationDegrees(90));
			poseStackIn.translate(-1F, 0, second ? 0F : -8/16F);
		} else if (facing.equals(Direction.WEST)) {
			poseStackIn.translate(0, 0, second ? 0F : -8/16F);
		} else if (facing.equals(Direction.EAST)) {
			poseStackIn.translate(0, 0, second ? -8/16F : 0F);
		}
		
		poseStackIn.pushPose();
		
		// Top Face
		CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[1], topValues[2], sprite.getU0(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[1], topValues[2], sprite.getU1(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[1], topValues[1], sprite.getU1(), sprite.getV0(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[1], topValues[1], sprite.getU0(), sprite.getV0(), r, g, b, a);
		
		CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[1], topValues[2], sprite.getU0(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[1], topValues[2], sprite.getU1(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[1], topValues[1], sprite.getU1(), sprite.getV0(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[1], topValues[1], sprite.getU0(), sprite.getV0(), r, g, b, a);
		
		// Top of Bottom Face
		CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[0], topValues[2], sprite.getU0(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[0], topValues[2], sprite.getU1(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[0], topValues[1], sprite.getU1(), sprite.getV0(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[0], topValues[1], sprite.getU0(), sprite.getV0(), r, g, b, a);
		
		// Front Faces [NORTH - SOUTH]
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[1], sideValuesNS[2], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[1], sideValuesNS[2], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[0], sideValuesNS[2], sprite.getU1(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[0], sideValuesNS[2], sprite.getU0(), sprite.getV1(), r, g, b, a);

		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[0], sideValuesNS[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[0], sideValuesNS[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[1], sideValuesNS[1], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[1], sideValuesNS[1], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);

		
		// Back Faces
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[1], sideValuesNS[1], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[1], sideValuesNS[1], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[0], sideValuesNS[1], sprite.getU1(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[0], sideValuesNS[1], sprite.getU0(), sprite.getV1(), r, g, b, a);
		
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[0], sideValuesNS[2], sprite.getU0(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[0], sideValuesNS[2], sprite.getU1(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[0], height[1], sideValuesNS[2], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesNS[2], height[1], sideValuesNS[2], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);

		poseStackIn.mulPose(Axis.YP.rotationDegrees(90));
		poseStackIn.translate(-1f, 0, 0);
		
		// Back Faces
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[1], sideValuesEW[0], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[1], sideValuesEW[0], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[0], sideValuesEW[0], sprite.getU1(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[0], sideValuesEW[0], sprite.getU0(), sprite.getV1(), r, g, b, a);
		
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[0], sideValuesEW[3], sprite.getU0(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[0], sideValuesEW[3], sprite.getU1(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[1], sideValuesEW[3], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[1], sideValuesEW[3], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
		
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[1], sideValuesEW[3], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[1], sideValuesEW[3], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[0], sideValuesEW[3], sprite.getU1(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[0], sideValuesEW[3], sprite.getU0(), sprite.getV1(), r, g, b, a);
		
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[0], sideValuesEW[0], sprite.getU0(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[0], sideValuesEW[0], sprite.getU1(), sprite.getV1(), r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[0], height[1], sideValuesEW[0], sprite.getU1(), sprite.getV0() + mappedHeight, r, g, b, a);
		CosmosRendererHelper.addF(builderA, poseStackIn, sideValuesEW[1], height[1], sideValuesEW[0], sprite.getU0(), sprite.getV0() + mappedHeight, r, g, b, a);

		poseStackIn.popPose();
		poseStackIn.popPose();
	}
	
	public class Internals extends Model {
		ModelPart moving;
		
		public Internals(RenderType renderType) {
			super((loc) -> { return renderType; });
			
			this.moving = createMoving().bakeRoot();
		}

		@Override
		public void renderToBuffer(PoseStack poseStackIn, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
			this.moving.render(poseStackIn, buffer, packedLight, packedOverlay, color);
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