package com.tcn.cosmosindustry.transport.core.energy.block;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.transport.core.energy.blockentity.BlockEntityChannelSurgeEnergy;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockChannelSurgeEnergy extends AbstractBlockEnergyChannel implements EntityBlock {
	
	public BlockChannelSurgeEnergy(Block.Properties properties) {
		super(properties, EnumIndustryTier.SURGE);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityChannelSurgeEnergy(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, IndustryRegistrationManager.BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_SURGE.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntityChannelSurgeEnergy> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityChannelSurgeEnergy::tick);
	}
}