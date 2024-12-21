package com.tcn.cosmosindustry.transport.core.fluid.block;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.core.management.ModRegistrationManager;
import com.tcn.cosmosindustry.transport.core.fluid.blockentity.BlockEntityChannelTransparentFluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockChannelTransparentFluid extends AbstractBlockFluidChannel {

	public BlockChannelTransparentFluid(Properties properties) {
		super(properties, false);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityChannelTransparentFluid(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, ModRegistrationManager.BLOCK_ENTITY_TYPE_CHANNEL_FLUID_TRANSPARENT.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntityChannelTransparentFluid> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityChannelTransparentFluid::tick);
	}
}