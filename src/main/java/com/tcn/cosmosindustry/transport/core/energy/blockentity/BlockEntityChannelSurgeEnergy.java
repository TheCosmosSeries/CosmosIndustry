package com.tcn.cosmosindustry.transport.core.energy.blockentity;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityChannelSurgeEnergy extends AbstractBlockEntityEnergyChannel {

	public BlockEntityChannelSurgeEnergy(BlockPos posIn, BlockState stateIn) {
		super(IndustryRegistrationManager.BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_SURGE.get(), posIn, stateIn, IndustryReference.Resource.Transport.ENERGY_SURGE, EnumIndustryTier.SURGE);
	}
}