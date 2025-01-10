package com.tcn.cosmosindustry.storage.core.block;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.storage.core.blockentity.AbstractBlockEntityCapacitor;
import com.tcn.cosmosindustry.storage.core.blockentity.BlockEntityCapacitorCreative;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockCapacitorCreative extends AbstractBlockCapacitor {

	public BlockCapacitorCreative(Block.Properties properties) {
		super(properties, EnumIndustryTier.CREATIVE);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityCapacitorCreative(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, IndustryRegistrationManager.BLOCK_ENTITY_TYPE_CAPACITOR_CREATIVE.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends AbstractBlockEntityCapacitor> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityCapacitorCreative::tick);
	}
}