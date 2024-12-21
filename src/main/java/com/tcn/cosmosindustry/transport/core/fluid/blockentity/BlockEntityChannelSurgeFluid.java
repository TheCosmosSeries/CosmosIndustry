package com.tcn.cosmosindustry.transport.core.fluid.blockentity;

import com.tcn.cosmosindustry.IndustryReference.RESOURCE.TRANSPORT;
import com.tcn.cosmosindustry.core.management.ModRegistrationManager;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.enums.EnumRenderType;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityChannelSurgeFluid extends AbstractBlockEntityFluidChannel {

	public BlockEntityChannelSurgeFluid(BlockPos posIn, BlockState stateIn) {
		super(ModRegistrationManager.BLOCK_ENTITY_TYPE_CHANNEL_FLUID_SURGE.get(), posIn, stateIn, TRANSPORT.FLUID_SURGE, EnumIndustryTier.SURGE, EnumRenderType.OPAQUE);
	}
}