package com.tcn.cosmosindustry.transport.core.energy.blockentity;

import com.tcn.cosmosindustry.IndustryReference.RESOURCE.TRANSPORT;
import com.tcn.cosmosindustry.core.management.ModRegistrationManager;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.enums.EnumRenderType;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityChannelTransparentCreativeEnergy extends AbstractBlockEntityEnergyChannel {

	public BlockEntityChannelTransparentCreativeEnergy(BlockPos posIn, BlockState stateIn) {
		super(ModRegistrationManager.BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_TRANSPARENT_CREATIVE.get(), posIn, stateIn, TRANSPORT.ENERGY_CREATIVE, EnumIndustryTier.CREATIVE, EnumRenderType.TRANSPARENT);
	}
}