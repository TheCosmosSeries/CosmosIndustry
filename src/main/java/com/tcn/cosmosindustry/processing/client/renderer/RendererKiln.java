package com.tcn.cosmosindustry.processing.client.renderer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tcn.cosmosindustry.core.management.ModRegistrationManager;
import com.tcn.cosmosindustry.processing.core.block.BlockKiln;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityKiln;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public class RendererKiln implements BlockEntityRenderer<BlockEntityKiln> {
	
	private BlockEntityRendererProvider.Context context;

	public RendererKiln(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;
	}
	
	@Override
	public void render(BlockEntityKiln blockEntity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		poseStackIn.pushPose();
		poseStackIn.translate(0.5D, 0.375D, 0.5D);

		Level level = blockEntity.getLevel();
		BlockState state = blockEntity.getBlockState();
		
		if (state.getBlock().equals(ModRegistrationManager.BLOCK_KILN.get())) {
			Direction dir = state.getValue(BlockKiln.FACING);
			
			if (blockEntity.getProcessTime(0) > (blockEntity.getProcessSpeed() - 10)) {
				RecipeHolder<?> iRecipe = blockEntity.getRecipeUsed();
				
				if (iRecipe != null) {
					ItemStack output = iRecipe.value().getResultItem(RegistryAccess.EMPTY);
					
					poseStackIn.pushPose();
					poseStackIn.translate(0, 0.2, 0);
					
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
				poseStackIn.translate(0, 0.2, 0);
				
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
}