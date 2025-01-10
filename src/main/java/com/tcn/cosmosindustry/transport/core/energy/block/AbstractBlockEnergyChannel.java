package com.tcn.cosmosindustry.transport.core.energy.block;

import java.util.ArrayList;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.transport.core.energy.blockentity.AbstractBlockEntityEnergyChannel;
import com.tcn.cosmoslibrary.common.block.CosmosBlockRemovable;
import com.tcn.cosmoslibrary.common.enums.EnumChannelSideState;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public abstract class AbstractBlockEnergyChannel extends CosmosBlockRemovable implements EntityBlock {
	
	private EnumIndustryTier tier;
	
	public static final BooleanProperty DOWN = BooleanProperty.create("down");
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	
	protected VoxelShape[] BOUNDING_BOXES;
	
	public AbstractBlockEnergyChannel(Block.Properties properties, EnumIndustryTier tierIn) {
		super(properties);
		
		this.tier = tierIn;
		this.BOUNDING_BOXES = tier.surge() || tier.creative() ? IndustryReference.Resource.Transport.BOUNDING_BOXES_STANDARD_SURGE : IndustryReference.Resource.Transport.BOUNDING_BOXES_STANDARD;
		
		this.registerDefaultState(this.defaultBlockState().setValue(NORTH, false).setValue(SOUTH, false).setValue(EAST, false).setValue(WEST, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
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
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player playerIn) { 
		if (worldIn.getBlockEntity(pos) instanceof AbstractBlockEntityEnergyChannel blockEntity) {
			blockEntity.attack(state, worldIn, pos, playerIn);
		}
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (worldIn.getBlockEntity(pos) instanceof AbstractBlockEntityEnergyChannel blockEntity) {
			return blockEntity.useItemOn(stackIn, state, worldIn, pos, playerIn, handIn, hit);
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}
	
	@Override
	public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		for	(Direction c : Direction.values()) {
			BlockState newState = this.updateShape(state, c, worldIn.getBlockState(pos.relative(c)), worldIn, pos, pos.relative(c));
			
			worldIn.sendBlockUpdated(pos, state, newState, 3);
			worldIn.setBlockAndUpdate(pos, newState);
		}
	}

	@Override
	public void neighborChanged(BlockState state, Level levelIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (levelIn.getBlockEntity(pos) instanceof AbstractBlockEntityEnergyChannel blockEntity) {
			blockEntity.neighborChanged(state, levelIn, pos, blockIn, fromPos, isMoving);
		}
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {	
		BlockEntity tile_this = worldIn.getBlockEntity(currentPos);
		
		EnumChannelSideState[] side_array = EnumChannelSideState.getStandardArray();
		
		if (tile_this != null && tile_this instanceof AbstractBlockEntityEnergyChannel blockEntity) {
			side_array = blockEntity.getSideArray();
		}
		
		// Order is: [D-U-N-S-W-E]
		boolean[] bool_array = new boolean[] { false, false, false, false, false, false };
		
		for (Direction c : Direction.values()) {
			int i = c.get3DDataValue();
			
			if (side_array[i].equals(EnumChannelSideState.DISABLED)) {
				bool_array[i] = false;
			} else if (side_array[i].equals(EnumChannelSideState.INTERFACE_INPUT) || side_array[i].equals(EnumChannelSideState.INTERFACE_OUTPUT)) {
				bool_array[i] = true;
			}else {
				bool_array[i] = this.connectsTo(worldIn, currentPos, c);
			}
		}
		
		return stateIn.setValue(DOWN, bool_array[0]).setValue(UP, bool_array[1]).setValue(NORTH, bool_array[2]).setValue(SOUTH, bool_array[3]).setValue(WEST, bool_array[4]).setValue(EAST, bool_array[5]);
	}
	
	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter blockReader, BlockPos pos, CollisionContext context) {
		VoxelShape[] shapes = new VoxelShape[] { BOUNDING_BOXES[0], Shapes.empty(), Shapes.empty(), Shapes.empty(), Shapes.empty(), Shapes.empty(), Shapes.empty() };
		VoxelShape[] shapesInterface = new VoxelShape[] { Shapes.empty(), Shapes.empty(), Shapes.empty(), Shapes.empty(), Shapes.empty(), Shapes.empty() };
		
		BlockEntity entity = blockReader.getBlockEntity(pos);
		
		for (Direction dir : Direction.values()) {
			if (entity instanceof AbstractBlockEntityEnergyChannel blockEntity) {
				EnumChannelSideState state = blockEntity.getStateForConnection(dir);
				
				ArrayList<Property<?>> propArray = new ArrayList<>(blockState.getProperties());
				
				for (int i = 0; i < propArray.size(); i++) {
					Property<?> rawProp = propArray.get(i);
					
					if(rawProp instanceof BooleanProperty prop) {
						String propName = prop.getName();
						
						if (dir.getName().equals(propName)) {
							if (blockState.getValue(prop).booleanValue()) {
								shapes[dir.get3DDataValue() + 1] = BOUNDING_BOXES[dir.get3DDataValue() + 1];
							}
						}
					}
				}
				
				if (state.isInterface() || state.equals(EnumChannelSideState.DISABLED)) {
					shapesInterface[dir.get3DDataValue()] = IndustryReference.Resource.Transport.BOUNDING_BOXES_INTERFACE[dir.get3DDataValue()];
				}
			}
		}
		
		return Shapes.or(shapes[0], shapes[1], shapes[2], shapes[3], shapes[4], shapes[5], shapes[6], 
				shapesInterface[0], shapesInterface[1], shapesInterface[2], shapesInterface[3], shapesInterface[4], shapesInterface[5]);
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockReader, BlockPos pos) {
		return this.getShape(blockState, blockReader, pos, CollisionContext.empty());
	}

	@Override
	public VoxelShape getVisualShape(BlockState blockState, BlockGetter blockReader, BlockPos pos, CollisionContext context) {
		return this.getShape(blockState, blockReader, pos, context);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockReader, BlockPos pos, CollisionContext context) {
		return this.getShape(blockState, blockReader, pos, context);
	}

	public boolean connectsTo(LevelAccessor world, BlockPos pos, Direction dir) {
		BlockEntity blockEntityOffset = world.getBlockEntity(pos.relative(dir));
		
		if (blockEntityOffset != null) {
			Object object = blockEntityOffset.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(dir), dir.getOpposite());
			
			if (object != null) {
				if (object instanceof IEnergyStorage storage) {
					if (storage.canReceive() || storage.canExtract()) {
						return true;
					}
				}
			}
		}
		
		return false;
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