package com.tcn.cosmosindustry.storage.core.blockentity;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.client.interfaces.IBEUpdated;
import com.tcn.cosmoslibrary.common.capability.IEnergyCapBE;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.enums.EnumSideState;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockNotifier;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBESided;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUIMode;
import com.tcn.cosmoslibrary.common.lib.CompatHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.energy.CosmosEnergyUtil;
import com.tcn.cosmoslibrary.energy.interfaces.IEnergyEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

abstract public class AbstractBlockEntityCapacitor extends BlockEntity implements IBlockInteract, IBlockNotifier, Container, WorldlyContainer, MenuProvider, IEnergyEntity, IBESided, IBEUpdated.Storage, IBEUIMode, IEnergyCapBE {

	private static final int[] SLOTS_TOP = new int[] { 0, 1 };
	private static final int[] SLOTS_BOTTOM = new int[] { 0, 1 };
	private static final int[] SLOTS_SIDES = new int[] { 0, 1 };
	
	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
	
	private int energy_stored = 0;
	private int energy_capacity;
	private int energy_max_receive;
	private int energy_max_extract;
	
	private EnumSideState[] SIDE_STATE_ARRAY = EnumSideState.getStandardArray();
	
	private EnumIndustryTier tier;
	
	private EnumUIMode uiMode = EnumUIMode.DARK;

	public AbstractBlockEntityCapacitor(BlockEntityType<?> typeIn, BlockPos posIn, BlockState stateIn, int[] energyInfoIn, EnumIndustryTier tierIn) {
		super(typeIn, posIn, stateIn);
		
		this.energy_capacity = energyInfoIn[0];
		this.energy_max_receive = energyInfoIn[1];
		this.energy_max_extract = energyInfoIn[1];
		this.tier = tierIn;
	}

	@Override
	public void sendUpdates() {
		if (this.getLevel() != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			
			this.getLevel().sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (!this.getLevel().isClientSide()) {
				this.getLevel().setBlockAndUpdate(this.getBlockPos(), state.updateShape(Direction.DOWN, state, this.getLevel(), this.getBlockPos(), this.getBlockPos()));
			}
		}
	}

	@Override
	public void sendUpdates(boolean update) {
		this.sendUpdates();
	}
	
	@Override
	public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.saveAdditional(compound, provider);
		
		ContainerHelper.saveAllItems(compound, this.inventoryItems, provider);

		compound.putInt("down", this.SIDE_STATE_ARRAY[0].getIndex());
		compound.putInt("up", this.SIDE_STATE_ARRAY[1].getIndex());
		compound.putInt("north", this.SIDE_STATE_ARRAY[2].getIndex());
		compound.putInt("south", this.SIDE_STATE_ARRAY[3].getIndex());
		compound.putInt("west", this.SIDE_STATE_ARRAY[4].getIndex());
		compound.putInt("east", this.SIDE_STATE_ARRAY[5].getIndex());
		
		compound.putInt("energy", this.energy_stored);
		compound.putInt("maxEnergy", this.energy_capacity);

		compound.putInt("ui_mode", this.uiMode.getIndex());
	}

	public static CompoundTag getNBT(String regName) {
		CompoundTag tag = new CompoundTag();
		tag.putString("id", regName);
		
		tag.put("Items", new ListTag());
		
		tag.putInt("down", 0);
		tag.putInt("up", 0);
		tag.putInt("north", 0);
		tag.putInt("south", 0);
		tag.putInt("east", 0);
		tag.putInt("west", 0);
		
		tag.putInt("energy", 0);
		tag.putInt("maxEnergy", 0);
		
		return tag;
	}
	
	@Override
	public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.loadAdditional(compound, provider);
		
		this.inventoryItems = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems, provider);

		this.setSide(Direction.values()[0], EnumSideState.getStateFromIndex(compound.getInt("down")), false);
		this.setSide(Direction.values()[1], EnumSideState.getStateFromIndex(compound.getInt("up")), false);
		this.setSide(Direction.values()[2], EnumSideState.getStateFromIndex(compound.getInt("north")), false);
		this.setSide(Direction.values()[3], EnumSideState.getStateFromIndex(compound.getInt("south")), false);
		this.setSide(Direction.values()[4], EnumSideState.getStateFromIndex(compound.getInt("west")), false);
		this.setSide(Direction.values()[5], EnumSideState.getStateFromIndex(compound.getInt("east")), false);
		
		this.energy_stored = compound.getInt("energy");

		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
		super.handleUpdateTag(tag, provider);
		this.sendUpdates(true);
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
	
	@Override
	public void onLoad() { 
		this.sendUpdates(true);
	}
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, AbstractBlockEntityCapacitor entityIn) {
		entityIn.chargeItem(0);
		entityIn.drainItem(1);
		
		if (!entityIn.getLevel().isClientSide()) {
			entityIn.pushEnergy(Direction.DOWN);
			entityIn.pushEnergy(Direction.UP);
			entityIn.pushEnergy(Direction.NORTH);
			entityIn.pushEnergy(Direction.SOUTH);
			entityIn.pushEnergy(Direction.EAST);
			entityIn.pushEnergy(Direction.WEST);
		}
		
		if (entityIn.tier.creative()) {
			if (entityIn.getEnergyStored() < entityIn.getMaxEnergyStored()) {
				entityIn.setEnergyStored(entityIn.getMaxEnergyStored());
			}
		}
	}
	
	public void pushEnergy(Direction directionIn) {
		BlockPos otherPos = this.getBlockPos().offset(directionIn.getNormal());
		BlockEntity entity = this.getLevel().getBlockEntity(otherPos);
		
		if (entity != null && !entity.isRemoved()) {
			if (this.hasEnergy() && this.canExtract(directionIn)) {
				if (this.getSide(directionIn) == EnumSideState.INTERFACE_OUTPUT) {
					Object object = this.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, otherPos, directionIn.getOpposite());
					
					if (object != null) {
						if (object instanceof IEnergyStorage storage) {
							if (storage.canReceive()) {
								int extract = this.extractEnergy(directionIn, this.getMaxExtract(), true);
								int actualExtract = storage.receiveEnergy(extract, true);
								
								if (actualExtract > 0) {
									this.extractEnergy(directionIn, storage.receiveEnergy(actualExtract, false), false);
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
	}

	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player player) { }

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level levelIn, BlockPos posIn, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (!playerIn.isShiftKeyDown()) {
			if (CosmosUtil.holdingWrench(playerIn)) {
				this.cycleSide(hit.getDirection(), playerIn, true);
				return ItemInteractionResult.SUCCESS;
			}
		
			if (levelIn.isClientSide()) {
				return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
			} else {
				if (playerIn instanceof ServerPlayer serverPlayer) {
					serverPlayer.openMenu(this, (packetBuffer) -> { packetBuffer.writeBlockPos(this.getBlockPos()); });
					return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
				}
			}
		} else {
			if (CosmosUtil.holdingWrench(playerIn)) {
				if (!levelIn.isClientSide()) {
					CompatHelper.spawnStack(CompatHelper.generateItemStackOnRemoval(levelIn, this, posIn), levelIn, posIn.getX() + 0.5, posIn.getY() + 0.5, posIn.getZ() + 0.5, 0);
					CosmosUtil.setToAir(levelIn, posIn);
				}
				ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
			}
		}
		return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
	}
	
	@Override
	public BlockState playerWillDestroy(Level levelIn, BlockPos posIn, BlockState state, Player player) {
		if (!levelIn.isClientSide()) {
			if (!player.getAbilities().instabuild) {
				CompatHelper.spawnStack(CompatHelper.generateItemStackOnRemoval(levelIn, this, posIn), levelIn, posIn.getX() + 0.5, posIn.getY() + 0.5, posIn.getZ() + 0.5, 0);
			}
		}
		return state;
	}

	@Override
	public void setPlacedBy(Level levelIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) { }

	@Override
	public void neighborChanged(BlockState state, Level levelIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) { }

	@Override
	public void onPlace(BlockState state, Level levelIn, BlockPos pos, BlockState oldState, boolean isMoving) { }

	public void chargeItem(int indexIn) {
		if (!this.getItem(indexIn).isEmpty()) {
			if (this.getItem(indexIn).getCapability(Capabilities.EnergyStorage.ITEM) instanceof IEnergyStorage energyItem) {
				if (this.hasEnergy()) {
					if (energyItem.canReceive()) {
						this.extractEnergy(Direction.DOWN, energyItem.receiveEnergy(this.getMaxExtract(), false), false);
					}
				}
			}
		}
	}
	
	public void drainItem(int indexIn) {
		if (!this.getItem(indexIn).isEmpty()) {
			if (this.getItem(indexIn).getCapability(Capabilities.EnergyStorage.ITEM) instanceof IEnergyStorage energyItem) {
				if (!CosmosEnergyUtil.isEnergyFull(this.getEnergyCapability(Direction.DOWN))) {
					if (CosmosEnergyUtil.hasEnergy(energyItem)) {
						if (energyItem.canExtract()) {
							this.receiveEnergy(Direction.DOWN, energyItem.extractEnergy(this.getMaxReceive(), false), false);
						}
					}
				}
			}
		}
	}

	@Override
	public void clearContent() { }

	@Override
	public boolean canPlaceItemThroughFace(int arg0, ItemStack arg1, Direction arg2) {
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int arg0, ItemStack arg1, Direction arg2) {
		return false;
	}

	@Override
	public int getContainerSize() {
		return this.inventoryItems.size();
	}

	@Override
	public ItemStack getItem(int index) {
		return this.inventoryItems.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		this.setChanged();
		return ContainerHelper.removeItem(this.inventoryItems, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		this.setChanged();
		return ContainerHelper.takeItem(this.inventoryItems, index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.inventoryItems.set(index, stack);
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
		
		this.setChanged();
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}
	
	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.inventoryItems) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return side == Direction.DOWN ? SLOTS_BOTTOM : (side == Direction.UP ? SLOTS_TOP : SLOTS_SIDES);
	}
/*
	LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.values());

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove && facing != null && capability == ForgeCapabilities.ITEM_HANDLER) {
			if (facing == Direction.UP)
				return handlers[0].cast();
			else if (facing == Direction.DOWN)
				return handlers[1].cast();
			else
				return handlers[2].cast();
		} else if (capability == ForgeCapabilities.ENERGY) {
			return this.createEnergyProxy(facing).cast();
		}
		
		return super.getCapability(capability, facing);
	}
	*/
	
	@Override
	public IEnergyStorage getEnergyCapability(@Nullable Direction directionIn) {
        return new IEnergyStorage() {
        	
            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return AbstractBlockEntityCapacitor.this.extractEnergy(directionIn, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return AbstractBlockEntityCapacitor.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return AbstractBlockEntityCapacitor.this.getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return AbstractBlockEntityCapacitor.this.receiveEnergy(directionIn, maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return AbstractBlockEntityCapacitor.this.canReceive(directionIn);
            }

            @Override
            public boolean canExtract() {
                return AbstractBlockEntityCapacitor.this.canExtract(directionIn);
            }
        };
    }

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
		int storedReceived = Math.min(this.getMaxEnergyStored() - energy_stored, Math.min(this.energy_max_receive, max_receive));

		if (!simulate) {
			this.energy_stored += storedReceived;
			this.sendUpdates(true);
		}
		
		return storedReceived;
	}

	@Override
	public int extractEnergy(Direction directionIn, int max_extract, boolean simulate) {
		int storedExtracted = Math.min(energy_stored, Math.min(this.energy_max_extract, max_extract));

		if (!simulate) {
			if (this.tier.notCreative()) {
				this.energy_stored -= storedExtracted;
			}
			this.sendUpdates(true);
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
	public int getEnergyScaled(int scale) {
		return ((this.getEnergyStored() / 100) * scale) / (this.getMaxEnergyStored() / 100);
	}
	
	@Override
	public boolean canExtract(Direction directionIn) {
		if (this.getSide(directionIn).equals(EnumSideState.DISABLED) || this.getSide(directionIn).equals(EnumSideState.INTERFACE_INPUT)) {
			return false;
		}
		
		return true;
	}

	@Override
	public boolean canReceive(Direction directionIn) {
		if (this.getSide(directionIn).equals(EnumSideState.DISABLED) || this.getSide(directionIn).equals(EnumSideState.INTERFACE_OUTPUT)) {
			return false;
		}
		
		return true;
	}

	@Override
	public EnumSideState getSide(Direction facing) {
		return this.SIDE_STATE_ARRAY[facing.get3DDataValue()];
	}
	
	@Override
	public void setSide(Direction facing, EnumSideState value, boolean update) {
		this.SIDE_STATE_ARRAY[facing.get3DDataValue()] = value;
		
		if (update) {
			this.sendUpdates(true);
		}
	}

	@Override
	public void cycleSide(Direction facing, boolean update) {
		this.setSide(facing, this.getSide(facing).getNextState(), update);
		
		if (update) {
			this.sendUpdates(true);
		}
	}

	public void cycleSide(Direction facing, @Nullable Player playerIn, boolean update) {
		this.cycleSide(facing, update);
		
		if (playerIn != null) {
			CosmosChatUtil.sendServerPlayerMessage(playerIn, ComponentHelper.style(ComponentColour.CYAN, "cosmosindustry.channel.status.cycle_side").append(this.getSide(facing).getColouredComp()));
		}
	}

	@Override
	public EnumSideState[] getSideArray() {
		return this.SIDE_STATE_ARRAY;
	}

	@Override
	public void setSideArray(EnumSideState[] new_array, boolean update) {
		this.SIDE_STATE_ARRAY = new_array;
	}
	
	public void setStack(Direction facing, ItemStack stack) {
		switch(facing) {
			case UP:
				inventoryItems.set(2, stack);
				break;
			case DOWN:
				inventoryItems.set(3, stack);
				break;
			case NORTH:
				inventoryItems.set(6, stack);
				break;
			case SOUTH:
				inventoryItems.set(7, stack);
				break;
			case EAST:
				inventoryItems.set(4, stack);
				break;
			case WEST:
				inventoryItems.set(5, stack);
				break;
				default:
		}
	}

	@Override
	public boolean canConnect(Direction facing) {
		if (this.getSide(facing).equals(EnumSideState.DISABLED)) {
			return false;
		}
		
		return true;
	}

	public boolean getStateForConnection(Direction facing) {
		BlockPos facingPos = this.getBlockPos().offset(facing.getNormal());
		BlockEntity blockEntity = this.getLevel().getBlockEntity(facingPos);		
		
		if (blockEntity != null && !blockEntity.isRemoved()) {
			Object object = this.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, facingPos, facing);
			
			if (object != null) {
				if (object instanceof IEnergyStorage storage) {
					if (!this.getSide(facing).equals(EnumSideState.DISABLED)) {
						if (storage.canReceive()) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, BlockHitResult hit) {
		return null;
	}

	@Override
	public EnumUIMode getUIMode() {
		return this.uiMode;
	}

	@Override
	public void setUIMode(EnumUIMode modeIn) {
		this.uiMode = modeIn;
	}

	@Override
	public void cycleUIMode() {
		this.uiMode = EnumUIMode.getNextStateFromState(this.uiMode);
	}

	@Override
	public EnumUIHelp getUIHelp() {
		return EnumUIHelp.HIDDEN;
	}

	@Override
	public void setUIHelp(EnumUIHelp modeIn) { }

	@Override
	public void cycleUIHelp() { }
	
}