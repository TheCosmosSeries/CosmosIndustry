package com.tcn.cosmosindustry.processing.core.block;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.core.management.ModRegistrationManager;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityKiln;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class BlockKiln extends CosmosBlockRemovableNBT implements EntityBlock {
	
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty LIT = BooleanProperty.create("lit");
	
	public BlockKiln(Block.Properties properties) {
		super(properties);
		
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(LIT, false));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntityKiln(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, ModRegistrationManager.BLOCK_ENTITY_TYPE_KILN.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntityKiln> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntityKiln::tick);
	}
	
	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player playerIn) { 
		BlockEntity entity = worldIn.getBlockEntity(pos);
		
		if (entity instanceof BlockEntityKiln blockEntity) {
			blockEntity.attack(state, worldIn, pos, playerIn);
		}
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		BlockEntity entity = worldIn.getBlockEntity(pos);
		
		if (entity instanceof BlockEntityKiln blockEntity) {
			return blockEntity.useItemOn(stackIn, state, worldIn, pos, playerIn, handIn, hit);
		}
		
		return ItemInteractionResult.FAIL;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT);
	}
	
	public BlockState updateState(BlockState stateIn, LevelAccessor levelIn, BlockPos currentPos) {
		if (!levelIn.isClientSide()) {
			BlockEntity entity = levelIn.getBlockEntity(currentPos);
			
			if (entity instanceof BlockEntityKiln) {
				BlockEntityKiln blockEntity = (BlockEntityKiln) entity;
				
				return stateIn.setValue(FACING, stateIn.getValue(FACING)).setValue(LIT, blockEntity.isProcessing());
			}
		}
		
		return stateIn.setValue(FACING, stateIn.getValue(FACING));
	}
	
	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor levelIn, BlockPos currentPos, BlockPos facingPos) {
		if (!levelIn.isClientSide()) {
			BlockEntity entity = levelIn.getBlockEntity(currentPos);
			
			if (entity instanceof BlockEntityKiln) {
				BlockEntityKiln blockEntity = (BlockEntityKiln) entity;
				
				return stateIn.setValue(FACING, stateIn.getValue(FACING)).setValue(LIT, blockEntity.isProcessing());
			}
		}
		
		return stateIn.setValue(FACING, stateIn.getValue(FACING));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		worldIn.setBlockAndUpdate(pos, state.setValue(FACING, placer.getDirection().getOpposite()));
	}

	public BlockState rotate(BlockState stateIn, Rotation rotationIn) {
		return stateIn.setValue(FACING, rotationIn.rotate(stateIn.getValue(FACING)));
	}

	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState stateIn, Mirror mirrorIn) {
		return stateIn.rotate(mirrorIn.getRotation(stateIn.getValue(FACING)));
	}

}