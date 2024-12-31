package com.tcn.cosmosindustry.transport.core.fluid.blockentity;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityChannelCreativeFluid extends AbstractBlockEntityFluidChannel {

	public BlockEntityChannelCreativeFluid(BlockPos posIn, BlockState stateIn) {
		super(IndustryRegistrationManager.BLOCK_ENTITY_TYPE_CHANNEL_FLUID_CREATIVE.get(), posIn, stateIn, IndustryReference.Resource.Transport.FLUID_CREATIVE, EnumIndustryTier.CREATIVE);
	}
}