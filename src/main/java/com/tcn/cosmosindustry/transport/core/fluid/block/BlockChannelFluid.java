package com.tcn.cosmosindustry.transport.core.fluid.block;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.transport.core.fluid.blockentity.BlockEntityChannelFluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockChannelFluid extends AbstractBlockFluidChannel {

	public BlockChannelFluid(Properties properties) {
		super(properties, false);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityChannelFluid(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, IndustryRegistrationManager.BLOCK_ENTITY_TYPE_CHANNEL_FLUID.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntityChannelFluid> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityChannelFluid::tick);
	}
}