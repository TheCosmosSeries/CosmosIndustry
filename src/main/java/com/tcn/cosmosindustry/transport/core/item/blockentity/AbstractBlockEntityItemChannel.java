package com.tcn.cosmosindustry.transport.core.item.blockentity;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.transport.core.util.TransportUtil;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumChannelSideState;
import com.tcn.cosmoslibrary.common.enums.EnumConnectionType;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.enums.EnumRenderType;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEChannelSided;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEChannelType.IChannelEnergy;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.energy.interfaces.IEnergyEntity;

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
import net.neoforged.neoforge.items.IItemHandler;

abstract public class AbstractBlockEntityItemChannel extends BlockEntity implements IBlockInteract, IBEChannelSided, IChannelEnergy, IEnergyEntity {
	private EnumChannelSideState[] SIDE_STATE_ARRAY = EnumChannelSideState.getStandardArray();
	
	private int energy_stored = 0;
	private int energy_capacity;
	private int energy_max_receive;
	private int energy_max_extract;
	
	public Direction last_facing;
	public int last_rf_rate;
	
	private EnumIndustryTier tier;
	private EnumRenderType transparent;
	
	public AbstractBlockEntityItemChannel(BlockEntityType<?> typeIn, BlockPos posIn, BlockState stateIn, int[] energyInfoIn, EnumIndustryTier tierIn, EnumRenderType transparentIn) {
		super(typeIn, posIn, stateIn);
		
		this.energy_capacity = energyInfoIn[0];
		this.energy_max_extract = energyInfoIn[1];
		this.energy_max_receive = energyInfoIn[1];
		
		this.tier = tierIn;
		this.transparent = transparentIn;
		
		if (this.tier.creative()) {
			this.setEnergyStored(this.energy_capacity);
		}
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
		if (level != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			
			level.sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (!level.isClientSide) {
				level.setBlockAndUpdate(this.getBlockPos(), state.updateShape(Direction.DOWN, state, level, this.getBlockPos(), this.getBlockPos()));
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
	public int getLastRFRate() {
		return this.last_rf_rate;
	}

	@Override
	public void setLastRFRate(int value) { 
		this.last_rf_rate = value;
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
		
		compound.putInt("energy", this.energy_stored);
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
		
		this.energy_stored = compound.getInt("energy");
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
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, AbstractBlockEntityItemChannel entityIn) {
		if (!levelIn.isClientSide()) {
			Arrays.stream(Direction.values()).parallel().forEach((d) -> {
				BlockPos otherPos = entityIn.getBlockPos().offset(d.getNormal());
				BlockEntity tile = entityIn.getLevel().getBlockEntity(otherPos);
				
				if (tile != null && !tile.isRemoved()) {
					EnumChannelSideState state = entityIn.getSide(d);
					
					if (entityIn.hasEnergy()) {
						if (state != EnumChannelSideState.INTERFACE_INPUT && state != EnumChannelSideState.DISABLED) {
							if (!(d.equals(entityIn.last_facing))) {
								Object object = levelIn.getCapability(Capabilities.ItemHandler.BLOCK, otherPos, d);
								
								if (object != null) {
									if (object instanceof IItemHandler itemHandler) {
										
									}
								}
							}
						}
					}
				}
			});
		}
	}
	
	@Override
	public EnumChannelSideState getStateForConnection(Direction facing) {
		//if (this.getChannelTier().equals(EnumIndustryTier.NORMAL)) {
			return TransportUtil.getStateForConnection(facing, this.getBlockPos(), this.getLevel(), this);
		//} else {
		//	return TransportUtil.getStateForConnectionSurge(facing, this.getBlockPos(), this.getLevel(), this);
		//}
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (CosmosUtil.holdingWrench(playerIn) && !playerIn.isShiftKeyDown()) {
			Direction dir = TransportUtil.getDirectionFromHit(pos, hit);
			if (dir != null) {
				this.cycleSide(dir, playerIn);
			} else {
				this.cycleSide(hit.getDirection(), playerIn);
			}
			return ItemInteractionResult.SUCCESS;
		}
		return ItemInteractionResult.FAIL;
	}

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) { }
	
	
	@Override
	public void setMaxTransfer(int maxTransfer) {
		this.setMaxReceive(maxTransfer);
		this.setMaxExtract(maxTransfer);
	}

	@Override
	public void setMaxReceive(int maxReceive) {
		this.energy_max_receive = maxReceive;
	}
	@Override

	public void setMaxExtract(int maxExtract) {
		this.energy_max_extract = maxExtract;
	}

	@Override
	public int getMaxReceive() {
		return this.energy_max_receive;
	}

	@Override
	public int getMaxExtract() {
		return this.energy_max_extract;
	}

	@Override
	public void setEnergyStored(int stored) {
		this.energy_stored = stored;

		if (this.energy_stored > energy_capacity) {
			this.energy_stored = energy_capacity;
		} else if (this.energy_stored < 0) {
			this.energy_stored = 0;
		}
	}

	@Override
	public void modifyEnergyStored(int stored) {
		this.energy_stored += stored;

		if (this.energy_stored > this.energy_capacity) {
			this.energy_stored = energy_capacity;
		} else if (this.energy_stored < 0) {
			this.energy_stored = 0;
		}
	}

	@Override
	public int receiveEnergy(Direction directionIn, int max_receive, boolean simulate) {
		this.last_facing = directionIn.getOpposite();
		
		int storedReceived = Math.min(this.getMaxEnergyStored() - energy_stored, Math.min(this.energy_max_receive, max_receive));

		if (!simulate) {
			this.energy_stored += storedReceived;
		}
		return storedReceived;
	}

	@Override
	public int extractEnergy(Direction directionIn, int max_extract, boolean simulate) {
		int storedExtracted = Math.min(energy_stored, Math.min(this.energy_max_extract, max_extract));
		
		if (!simulate) {
			if (this.getChannelTier().notCreative()) {
				this.energy_stored -= storedExtracted;
			}
		}

		return storedExtracted;
	}

	@Override
	public int getEnergyStored() {
		return this.energy_stored;
	}

	@Override
	public int getMaxEnergyStored() {
		return this.energy_capacity;
	}

	@Override
	public boolean hasEnergy() {
		return this.energy_stored > 0;
	}

	@Override
	public boolean canExtract(Direction directionIn) {
		if (this.getSide(directionIn.getOpposite()).equals(EnumChannelSideState.DISABLED) || this.getSide(directionIn.getOpposite()).equals(EnumChannelSideState.INTERFACE_INPUT)) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean canReceive(Direction directionIn) {
		if (this.getSide(directionIn.getOpposite()).equals(EnumChannelSideState.DISABLED) || this.getSide(directionIn.getOpposite()).equals(EnumChannelSideState.INTERFACE_OUTPUT)) {
			return false;
		}
		
		return true;
	}

	@Override
	public int getEnergyScaled(int scale) {
		return 0;
	}

	@Override
	public EnumConnectionType getChannelType() {
		return EnumConnectionType.ENERGY;
	}
	
	public EnumIndustryTier getChannelTier() {
		return this.tier;
	}
	
	public EnumRenderType getRenderType() {
		return this.transparent;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, BlockHitResult hit) {
		return null;
	}
}