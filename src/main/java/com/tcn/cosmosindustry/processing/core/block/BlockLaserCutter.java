package com.tcn.cosmosindustry.processing.core.block;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityLaserCutter;
import com.tcn.cosmoslibrary.common.nbt.CosmosBlockRemovableNBT;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class BlockLaserCutter extends CosmosBlockRemovableNBT implements EntityBlock {
	
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	
	public BlockLaserCutter(Block.Properties properties) {
		super(properties);
		
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityLaserCutter(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, IndustryRegistrationManager.BLOCK_ENTITY_TYPE_LASER_CUTTER.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntityLaserCutter> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityLaserCutter::tick);
	}
	
	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player playerIn) { 
		if (levelIn.getBlockEntity(pos) instanceof BlockEntityLaserCutter blockEntity) {
			blockEntity.attack(state, levelIn, pos, playerIn);
		}
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level levelIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (levelIn.getBlockEntity(pos) instanceof BlockEntityLaserCutter blockEntity) {
			return blockEntity.useItemOn(stackIn, state, levelIn, pos, playerIn, handIn, hit);
		}
		return ItemInteractionResult.FAIL;
	}

	@Override
	public BlockState playerWillDestroy(Level levelIn, BlockPos pos, BlockState state, Player player) {
		if (levelIn.getBlockEntity(pos) instanceof BlockEntityLaserCutter blockEntity) {
			blockEntity.playerWillDestroy(levelIn, pos, state, player);
		}
		return super.playerWillDestroy(levelIn, pos, state, player);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor levelIn, BlockPos currentPos, BlockPos facingPos) {	
		return stateIn.setValue(FACING, stateIn.getValue(FACING));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@Override
	public void setPlacedBy(Level levelIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		levelIn.setBlockAndUpdate(pos, state.setValue(FACING, placer.getDirection().getOpposite()));
	}

	@Override
	public BlockState rotate(BlockState stateIn, Rotation rotationIn) {
		return stateIn.setValue(FACING, rotationIn.rotate(stateIn.getValue(FACING)));
	}

	@Override
	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState stateIn, Mirror mirrorIn) {
		return stateIn.rotate(mirrorIn.getRotation(stateIn.getValue(FACING)));
	}

}