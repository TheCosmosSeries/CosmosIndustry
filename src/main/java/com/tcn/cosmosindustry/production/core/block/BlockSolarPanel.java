package com.tcn.cosmosindustry.production.core.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.production.core.blockentity.BlockEntitySolarPanel;
import com.tcn.cosmoslibrary.common.nbt.CosmosBlockRemovableNBT;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockSolarPanel extends CosmosBlockRemovableNBT implements EntityBlock {
	
	private static final VoxelShape BOUNDING_BOX = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
	
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	
	public BlockSolarPanel(BlockBehaviour.Properties builder) {
		super(builder);

		this.registerDefaultState(this.defaultBlockState().setValue(NORTH, false).setValue(SOUTH, false).setValue(EAST, false).setValue(WEST, false).setValue(WEST, false));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, SOUTH, WEST, EAST);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos posIn, BlockState stateIn) {
		return new BlockEntitySolarPanel(posIn, stateIn);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level levelIn, BlockState stateIn, BlockEntityType<T> entityTypeIn) {
		return createTicker(levelIn, entityTypeIn, IndustryRegistrationManager.BLOCK_ENTITY_TYPE_SOLAR_PANEL.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level levelIn, BlockEntityType<T> entityTypeIn, BlockEntityType<? extends BlockEntitySolarPanel> entityIn) {
		return createTickerHelper(entityTypeIn, entityIn, BlockEntitySolarPanel::tick);
	}
	
	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		
		if (tileEntity instanceof BlockEntitySolarPanel blockEntity) {
			return blockEntity.useItemOn(stackIn, state, worldIn, pos, playerIn, handIn, hit);
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction directionIn, BlockState facingState, LevelAccessor levelIn, BlockPos currentPos, BlockPos facingPos) {
		return stateIn.setValue(NORTH, this.canSideConnect(levelIn, currentPos, Direction.NORTH))
				.setValue(SOUTH, this.canSideConnect(levelIn, currentPos, Direction.SOUTH))
				.setValue(WEST, this.canSideConnect(levelIn, currentPos, Direction.WEST))
				.setValue(EAST, this.canSideConnect(levelIn, currentPos, Direction.EAST));
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

	@Override
	public VoxelShape getShape(BlockState stateIn, BlockGetter levelIn, BlockPos posIn, CollisionContext context) {
		return BOUNDING_BOX;
	}

	private boolean canSideConnect(LevelAccessor world, BlockPos pos, @Nullable Direction facing) {
		final BlockState blockState = world.getBlockState(pos);
		
		if (facing != null) {
			final BlockState otherState = world.getBlockState(pos.offset(facing.getNormal()));
			return blockState != null && otherState != null && this.canConnect(blockState, otherState);
		}
		return false;
	}
	
	private boolean canConnect(@Nonnull BlockState orig, @Nonnull BlockState conn) {
		return orig.getBlock() == conn.getBlock();
	}

}