package com.tcn.cosmosindustry.transport.core.fluid.blockentity;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.transport.core.util.TransportUtil;
import com.tcn.cosmoslibrary.common.capability.IFluidCapBE;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumChannelSideState;
import com.tcn.cosmoslibrary.common.enums.EnumConnectionType;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.enums.EnumRenderType;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEChannelSided;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEChannelType.IChannelFluid;
import com.tcn.cosmoslibrary.common.lib.CompatHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectFluidTankCustom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

@SuppressWarnings("unused")
abstract public class AbstractBlockEntityFluidChannel extends BlockEntity implements IBlockInteract, IBEChannelSided, IChannelFluid, IFluidHandler, IFluidCapBE {
	
	private EnumChannelSideState[] SIDE_STATE_ARRAY = EnumChannelSideState.getStandardArray();
	
	private int fluid_capacity;
	private int fluid_max_receive;
	private int fluid_max_extract;

	private ObjectFluidTankCustom tank;
	
	public Direction last_facing;
	
	private EnumIndustryTier tier;
	
	public AbstractBlockEntityFluidChannel(BlockEntityType<?> typeIn, BlockPos posIn, BlockState stateIn, int[] fluidInfoIn, EnumIndustryTier tierIn) {
		super(typeIn, posIn, stateIn);
		
		this.fluid_capacity = fluidInfoIn[0];
		this.fluid_max_extract = fluidInfoIn[1];
		this.fluid_max_receive = fluidInfoIn[1];
		
		this.tier = tierIn;
		this.tank = new ObjectFluidTankCustom(new FluidTank(fluid_capacity), 0);
	}
	
	@Override 
	public EnumChannelSideState getSide(Direction facing) {
		return this.SIDE_STATE_ARRAY[facing.get3DDataValue()];
	}
	
	@Override
	public void setSide(Direction facing, EnumChannelSideState side_state) {
		this.SIDE_STATE_ARRAY[facing.get3DDataValue()] = side_state;
		this.updateRenders();
	}
	
	@Override
	public EnumChannelSideState[] getSideArray() {
		return this.SIDE_STATE_ARRAY;
	}

	@Override
	public void setSideArray(EnumChannelSideState[] new_array) {
		this.SIDE_STATE_ARRAY = new_array;
	}
	
	public void cycleSide(Direction facing, @Nullable Player playerIn) {
		this.cycleSide(facing);
		
		if (playerIn != null) {
			CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.CYAN, "cosmosindustry.channel.status.cycle_side").append(this.getSide(facing).getColouredComp()));
		}
	}

	@Override
	public void cycleSide(Direction facing) {
		this.setSide(facing, this.getSide(facing).getNextStateUser());
	}

	@Override
	public boolean canConnect(Direction facing) {
		EnumChannelSideState state = this.getSide(facing);
		
		if (state.equals(EnumChannelSideState.DISABLED)) {
			return false;
		}
		
		return true;
	}

	@Override
	public void updateRenders() {
		if (this.getLevel() != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			
			this.getLevel().sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (!this.getLevel().isClientSide()) {
				this.getLevel().setBlockAndUpdate(this.getBlockPos(), state.updateShape(Direction.DOWN, state, level, this.getBlockPos(), this.getBlockPos()));
			}
		}
	}
	
	@Override
	public Direction getLastFacing() {
		return this.last_facing;
	}
	
	@Override
	public void setLastFacing(Direction facing) {
		this.last_facing = facing;
	}

	@Override
	public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.saveAdditional(compound, provider);
		
		if (this.last_facing != null) {
			compound.putInt("last_facing", this.getLastFacing().get3DDataValue());
		}
		
		compound.putInt("down", this.SIDE_STATE_ARRAY[0].getIndex());
		compound.putInt("up", this.SIDE_STATE_ARRAY[1].getIndex());
		compound.putInt("north", this.SIDE_STATE_ARRAY[2].getIndex());
		compound.putInt("south", this.SIDE_STATE_ARRAY[3].getIndex());
		compound.putInt("west", this.SIDE_STATE_ARRAY[4].getIndex());
		compound.putInt("east", this.SIDE_STATE_ARRAY[5].getIndex());

		this.tank.writeToNBT(compound);
	}

	@Override
	public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.loadAdditional(compound, provider);
		
		if (compound.contains("last_facing")) {
			this.setLastFacing(Direction.from3DDataValue(compound.getInt("last_facing")));
		}
		
		this.setSide(Direction.values()[0], EnumChannelSideState.getStateFromIndex(compound.getInt("down")));
		this.setSide(Direction.values()[1], EnumChannelSideState.getStateFromIndex(compound.getInt("up")));
		this.setSide(Direction.values()[2], EnumChannelSideState.getStateFromIndex(compound.getInt("north")));
		this.setSide(Direction.values()[3], EnumChannelSideState.getStateFromIndex(compound.getInt("south")));
		this.setSide(Direction.values()[4], EnumChannelSideState.getStateFromIndex(compound.getInt("west")));
		this.setSide(Direction.values()[5], EnumChannelSideState.getStateFromIndex(compound.getInt("east")));
		
		this.tank = ObjectFluidTankCustom.readFromNBT(compound);
	}

	/**
	 * Set the data once it has been received. [NBT > TE] (READ)
	 */
	@Override
	public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
		super.handleUpdateTag(tag, provider);
	}
	
	/**
	 * Retrieve the data to be stored. [TE > NBT] (WRITE)
	 */
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		this.saveAdditional(tag, provider);
		return tag;
	}
	
	/**
	 * Actually sends the data to the server. [NBT > SER]
	 */
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	/**
	 * Method is called once packet has been received by the client. [SER > CLT]
	 */
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
		super.onDataPacket(net, pkt, provider);
		CompoundTag tag_ = pkt.getTag();
		this.handleUpdateTag(tag_, provider);
	}
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, AbstractBlockEntityFluidChannel entityIn) {
		if (!levelIn.isClientSide()) {
			entityIn.pushFluid(Direction.DOWN);
			entityIn.pushFluid(Direction.UP);
			entityIn.pushFluid(Direction.NORTH);
			entityIn.pushFluid(Direction.SOUTH);
			entityIn.pushFluid(Direction.EAST);
			entityIn.pushFluid(Direction.WEST);
		}
	}

	public void pushFluid(Direction directionIn) {
		BlockPos otherPos = this.getBlockPos().offset(directionIn.getNormal());
		BlockEntity entity = this.getLevel().getBlockEntity(otherPos);

		if (directionIn != this.last_facing) {
			if (entity != null && !entity.isRemoved()) {
				if (!this.getSide(directionIn).equals(EnumChannelSideState.INTERFACE_INPUT) && !this.getSide(directionIn).equals(EnumChannelSideState.DISABLED)) {
					Object object = this.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, otherPos, directionIn);
				
					if (object != null) {
						if (object instanceof IFluidHandler storage) {
							if (storage.isFluidValid(0, this.getFluidInTank(0))) {
								FluidStack stack = this.drain(1000, FluidAction.SIMULATE);
								int lost = storage.fill(stack, FluidAction.SIMULATE);
								
								if (!stack.isEmpty()) {
									if (lost > 0) {
										storage.fill(stack, FluidAction.EXECUTE);
										this.drain(lost, FluidAction.EXECUTE);
									}
								}
							}
						}
					}
				} else {
					return;
				}
			} else {
				return;
			}
		} else {
			return;
		}
	}

	@Override
	public EnumChannelSideState getStateForConnection(Direction facing) {
		return TransportUtil.getStateForConnection(facing, this.getBlockPos(), this.getLevel(), this);
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level levelIn, BlockPos posIn, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (CosmosUtil.holdingWrench(playerIn)) {
			if (!playerIn.isShiftKeyDown()) {
				Direction dir = TransportUtil.getDirectionFromHit(posIn, hit);
				if (dir != null) {
					this.cycleSide(dir, playerIn);
				} else {
					this.cycleSide(hit.getDirection(), playerIn);
				}
				return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
			} else {
				if (!levelIn.isClientSide()) {
					CompatHelper.spawnStack(CompatHelper.generateItemStackFromBlock(state), levelIn, posIn.getX() + 0.5, posIn.getY() + 0.5, posIn.getZ() + 0.5, 0);
					CosmosUtil.setToAir(levelIn, posIn);
				}
				return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
			}
		}
		return ItemInteractionResult.FAIL;
	}

	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player player) { }
	
	@Override
	public IFluidHandler getFluidCapability(@Nullable Direction directionIn) {
		return new IFluidHandler() {

			@Override
			public int getTanks() {
				if (!AbstractBlockEntityFluidChannel.this.getSide(directionIn.getOpposite()).equals(EnumChannelSideState.DISABLED)) {
					return AbstractBlockEntityFluidChannel.this.getTanks();
				}
				return 0;
			}

			@Override
			public FluidStack getFluidInTank(int tank) {
				if (!AbstractBlockEntityFluidChannel.this.getSide(directionIn.getOpposite()).equals(EnumChannelSideState.DISABLED)) {
					return AbstractBlockEntityFluidChannel.this.getFluidInTank(tank);
				}
				return FluidStack.EMPTY;
			}

			@Override
			public int getTankCapacity(int tank) {
				if (!AbstractBlockEntityFluidChannel.this.getSide(directionIn.getOpposite()).equals(EnumChannelSideState.DISABLED)) {
					return AbstractBlockEntityFluidChannel.this.getTankCapacity(tank);
				}
				return 0;
			}

			@Override
			public boolean isFluidValid(int tank, FluidStack stack) {
				if (!AbstractBlockEntityFluidChannel.this.getSide(directionIn.getOpposite()).equals(EnumChannelSideState.DISABLED)) {
					return AbstractBlockEntityFluidChannel.this.isFluidValid(tank, stack);
				}
				return false;
			}

			@Override
			public int fill(FluidStack resource, FluidAction action) {
				if (!AbstractBlockEntityFluidChannel.this.getSide(directionIn.getOpposite()).equals(EnumChannelSideState.DISABLED)) {
					AbstractBlockEntityFluidChannel.this.setLastFacing(directionIn.getOpposite());
					return AbstractBlockEntityFluidChannel.this.fill(resource, action);
				}
				return 0;
			}

			@Override
			public FluidStack drain(FluidStack resource, FluidAction action) {
				if (!AbstractBlockEntityFluidChannel.this.getSide(directionIn.getOpposite()).equals(EnumChannelSideState.DISABLED)) {
					AbstractBlockEntityFluidChannel.this.updateRenders();
					return AbstractBlockEntityFluidChannel.this.drain(resource, action);
				}
				return FluidStack.EMPTY;
			}

			@Override
			public FluidStack drain(int maxDrain, FluidAction action) {
				if (!AbstractBlockEntityFluidChannel.this.getSide(directionIn.getOpposite()).equals(EnumChannelSideState.DISABLED)) {
					AbstractBlockEntityFluidChannel.this.updateRenders();
					return AbstractBlockEntityFluidChannel.this.drain(maxDrain, action);
				}
				return FluidStack.EMPTY;
			}
		};
	}
	
	@Override
	public EnumConnectionType getChannelType() {
		return EnumConnectionType.FLUID;
	}
	
	@Override
	public EnumIndustryTier getChannelTier() {
		return this.tier;
	}
	
	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return this.tank.getFluidTank().getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		return this.tank.getFluidTank().getCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return true;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return this.tank.getFluidTank().fill(resource, action);
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return this.tank.getFluidTank().drain(resource, action);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return this.tank.getFluidTank().drain(maxDrain, action);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, BlockHitResult hit) {
		return null;
	}
}