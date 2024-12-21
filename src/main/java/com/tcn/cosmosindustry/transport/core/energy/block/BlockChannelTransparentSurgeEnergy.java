package com.tcn.cosmosindustry.transport.core.energy.block;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.core.management.ModRegistrationManager;
import com.tcn.cosmosindustry.transport.core.energy.blockentity.BlockEntityChannelTransparentSurgeEnergy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockChannelTransparentSurgeEnergy extends AbstractBlockEnergyChannel implements EntityBlock {
	
	public BlockChannelTransparentSurgeEnergy(Block.Properties properties) {
		super(properties, true);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityChannelTransparentSurgeEnergy(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, ModRegistrationManager.BLOCK_ENTITY_TYPE_CHANNEL_ENERGY_TRANSPARENT_SURGE.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntityChannelTransparentSurgeEnergy> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityChannelTransparentSurgeEnergy::tick);
	}
}