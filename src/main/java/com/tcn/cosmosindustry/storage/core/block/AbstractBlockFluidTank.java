package com.tcn.cosmosindustry.storage.core.block;

import com.tcn.cosmosindustry.storage.core.blockentity.AbstractBlockEntityFluidTank;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.nbt.CosmosBlockRemovableNBT;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

abstract public class AbstractBlockFluidTank extends CosmosBlockRemovableNBT implements EntityBlock {

	private EnumIndustryTier tier;
	
	public static final IntegerProperty DOWN = IntegerProperty.create("down", 0, 3);
	public static final IntegerProperty UP = IntegerProperty.create("up", 0, 3);
	public static final IntegerProperty NORTH = IntegerProperty.create("north", 0, 3);
	public static final IntegerProperty SOUTH = IntegerProperty.create("south", 0, 3);
	public static final IntegerProperty WEST = IntegerProperty.create("west", 0, 3);
	public static final IntegerProperty EAST = IntegerProperty.create("east", 0, 3);
	
	public AbstractBlockFluidTank(Block.Properties properties, EnumIndustryTier tierIn) {
		super(properties);
		
		this.tier = tierIn;
		
		this.registerDefaultState(this.defaultBlockState()
			.setValue(DOWN, 0).setValue(UP, 0).setValue(NORTH, 0)
			.setValue(SOUTH, 0).setValue(WEST, 0).setValue(EAST, 0)
		);
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState stateIn, BlockGetter level, BlockPos pos) {
		return true;
	}
	
	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player playerIn) {
		if (levelIn.getBlockEntity(pos) instanceof AbstractBlockEntityFluidTank blockEntity) {
			blockEntity.attack(state, levelIn, pos, playerIn);
		}
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level levelIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (levelIn.getBlockEntity(pos) instanceof AbstractBlockEntityFluidTank blockEntity) {
			return blockEntity.useItemOn(stackIn, state, levelIn, pos, playerIn, handIn, hit);
		}
		return ItemInteractionResult.SUCCESS;
	}

	@Override
	public BlockState playerWillDestroy(Level levelIn, BlockPos pos, BlockState state, Player player) {
		if (levelIn.getBlockEntity(pos) instanceof AbstractBlockEntityFluidTank blockEntity) {
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
		builder.add(DOWN, UP, NORTH, SOUTH, WEST, EAST);
	}
	
	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor levelIn, BlockPos currentPos, BlockPos facingPos) {
		if (levelIn.getBlockEntity(currentPos) instanceof AbstractBlockEntityFluidTank blockEntity) {
			return stateIn
				.setValue(DOWN, blockEntity.getSide(Direction.DOWN).getIndex()).setValue(UP, blockEntity.getSide(Direction.UP).getIndex())
				.setValue(NORTH, blockEntity.getSide(Direction.NORTH).getIndex()).setValue(SOUTH, blockEntity.getSide(Direction.SOUTH).getIndex())
				.setValue(WEST, blockEntity.getSide(Direction.WEST).getIndex()).setValue(EAST, blockEntity.getSide(Direction.EAST).getIndex()
			);
		} else {
			return stateIn;
		}
	}

	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
        return !this.tier.creative();
    }
	
	@Override
	public boolean canEntityDestroy(BlockState state, BlockGetter world, BlockPos pos, Entity entity) {
		return !this.tier.creative();
	}
}