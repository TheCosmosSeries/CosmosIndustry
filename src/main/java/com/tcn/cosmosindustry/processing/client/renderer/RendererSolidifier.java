package com.tcn.cosmosindustry.processing.client.renderer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.processing.core.block.BlockSolidifier;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntitySolidifier;
import com.tcn.cosmoslibrary.client.renderer.CosmosRendererHelper;

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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
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
public class RendererSolidifier implements BlockEntityRenderer<BlockEntitySolidifier> {
//	private static final ResourceLocation TEXTURE = IndustryReference.Resource.Processing.Render.SOLIDIFIER;
	private static final RenderType RENDER_TYPE = RenderType.solid();
	
	private BlockEntityRendererProvider.Context context;

	public RendererSolidifier(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;
	}
	
	@Override
	public void render(BlockEntitySolidifier blockEntity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		Level level = blockEntity.getLevel();
		BlockState state = blockEntity.getBlockState();
		
		if (blockEntity.getCurrentFluidAmount() > 0) {
			if (state.getBlock().equals(IndustryRegistrationManager.BLOCK_SOLIDIFIER.get())) {
				Direction dir = state.getValue(BlockSolidifier.FACING);
				
				FluidTank fluidTank = blockEntity.getFluidTank();
	
				FluidStack fluid = fluidTank.getFluid();
				if (fluid.isEmpty()) {
					return;
				}
	
				Fluid renderFluid = fluid.getFluid();
				if (renderFluid == null) {
					return;
				}
				
				IClientFluidTypeExtensions props = CosmosRendererHelper.getFluidExtention(renderFluid);  //IClientFluidTypeExtensions.of(renderFluid.defaultFluidState());
				TextureAtlasSprite sprite = CosmosRendererHelper.getFluidTexture(props);  //Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(props.getStillTexture());
				VertexConsumer builderA = buffer.getBuffer(RenderType.translucent());
	
				float[] topValues    = new float[] { 1.10F/16F, 9.10F/16F, 14.9F/16F };
				float[] sideValuesNS = new float[] { 1.10F/16F, 9.10F/16F, 14.9F/16F };
				float[] sideValuesEW = new float[] { 1.10F/16F, 6.90F/16F, 9.10F/16F, 14.9F/16F };
				
				float[] height = new float[] { 6/16F, 14.9F/16F };
				float fillLevel = blockEntity.getFluidFillLevel() / 9F;
				
				height[1] = (float) Mth.map(fillLevel, 0, 1, 6/16F, 14.9/16F);
				
				float mappedHeight = CosmosRendererHelper.getMappedTextureHeight(sprite, fillLevel * 16);
				
				int color = props.getTintColor();
				
				float a = 1.0F;
				float r = (color >> 16 & 0xFF) / 255.0F;
				float g = (color >> 8 & 0xFF) / 255.0F;
				float b = (color & 0xFF) / 255.0F;

				poseStackIn.pushPose();
				if (dir.equals(Direction.SOUTH)) {
					poseStackIn.mulPose(Axis.YP.rotationDegrees(180));
					poseStackIn.translate(-1F, 0, -1F);
				} else if (dir.equals(Direction.WEST)) {
					poseStackIn.mulPose(Axis.YP.rotationDegrees(90));
					poseStackIn.translate(-1F, 0, 0);
				} else if (dir.equals(Direction.EAST)) {
					poseStackIn.mulPose(Axis.YP.rotationDegrees(-90));
					poseStackIn.translate(0, 0, -1F);
				}
				
				poseStackIn.pushPose();
//				poseStackIn.translate(0,1,0);
	
				// Top Face
				CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[1], topValues[2], sprite.getU0(), sprite.getV1(), r, g, b, a);
				CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[1], topValues[2], sprite.getU1(), sprite.getV1(), r, g, b, a);
				CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[1], topValues[1], sprite.getU1(), sprite.getV0(), r, g, b, a);
				CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[1], topValues[1], sprite.getU0(), sprite.getV0(), r, g, b, a);
				
				// Bottom Face of Top
				CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[1], topValues[2], sprite.getU0(), sprite.getV1(), r, g, b, a);
				CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[1], topValues[2], sprite.getU1(), sprite.getV1(), r, g, b, a);
				CosmosRendererHelper.addF(builderA, poseStackIn, topValues[0], height[1], topValues[1], sprite.getU1(), sprite.getV0(), r, g, b, a);
				CosmosRendererHelper.addF(builderA, poseStackIn, topValues[2], height[1], topValues[1], sprite.getU0(), sprite.getV0(), r, g, b, a);
				
				
				// Front Faces [NORTH - SOUTH]
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
	
				poseStackIn.popPose();
				poseStackIn.popPose();
			}
		}
		
		VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
		
		if (state.getBlock().equals(IndustryRegistrationManager.BLOCK_ORE_PLANT.get())) {
			Direction dir = state.getValue(BlockSolidifier.FACING);
			
			poseStackIn.pushPose();
			if (blockEntity.getProcessTime(0) > (blockEntity.getProcessSpeed() - 10)) {
				RecipeHolder<?> iRecipe = blockEntity.getRecipeUsed();
				
				if (iRecipe != null) {
					ItemStack output = iRecipe.value().getResultItem(level.registryAccess());
					
					poseStackIn.pushPose();
					poseStackIn.translate(0.5, 0.6, 0.25);

					if (dir.equals(Direction.SOUTH)) {
						poseStackIn.translate(0, 0, 8/16F);
					} else if (dir.equals(Direction.WEST)) {
						poseStackIn.translate(-0.25, 0, 0.25);
					} else if (dir.equals(Direction.EAST)) {
						poseStackIn.translate(0.25, 0, 0.25);
					}

					if (output.getItem() instanceof BlockItem && !output.getItem().equals(Items.REDSTONE)) {
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
				
			} else if (!blockEntity.getItem(3).isEmpty()) {
				poseStackIn.pushPose();
				poseStackIn.translate(0.5, 0.6, 0.25);
				
				if (dir.equals(Direction.SOUTH)) {
					poseStackIn.translate(0, 0, 8/16F);
				} else if (dir.equals(Direction.WEST)) {
					poseStackIn.translate(-0.25, 0, 0.25);
				} else if (dir.equals(Direction.EAST)) {
					poseStackIn.translate(0.25, 0, 0.25);
				}

				if ((blockEntity.getItem(3).getItem() instanceof BlockItem) && !blockEntity.getItem(3).getItem().equals(Items.REDSTONE)) {
					poseStackIn.scale(0.6F, 0.6F, 0.6F);
				} else {
					Quaternionf rotation = Axis.XP.rotationDegrees(90);

					poseStackIn.mulPose(Axis.YP.rotationDegrees(dir.equals(Direction.SOUTH) ? 180 : dir.equals(Direction.WEST) ? 90 : dir.equals(Direction.EAST) ? 270 : 0));
					
					poseStackIn.translate(0, -0.119, 0);
					poseStackIn.mulPose(rotation);
					poseStackIn.scale(0.35F, 0.35F, 0.35F);
				}
				
				Minecraft.getInstance().getItemRenderer().renderStatic(blockEntity.getItem(3), ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, poseStackIn, buffer, blockEntity.getLevel(), 0);
				
				poseStackIn.popPose();
			}
			poseStackIn.popPose();
		}
	}
}