package com.tcn.cosmosindustry.processing.core.blockentity;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.processing.client.container.ContainerCharger;
import com.tcn.cosmoslibrary.client.interfaces.IBEUpdated;
import com.tcn.cosmoslibrary.common.capability.IEnergyCapBE;
import com.tcn.cosmoslibrary.common.enums.EnumEnergyState;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockNotifier;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUIMode;
import com.tcn.cosmoslibrary.common.lib.CompatHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.energy.CosmosEnergyUtil;
import com.tcn.cosmoslibrary.energy.interfaces.IEnergyEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class BlockEntityCharger extends BlockEntity implements IBlockInteract, IBlockNotifier, Container, WorldlyContainer, MenuProvider, IBEUpdated.Minimal, IEnergyEntity, IBEUIMode, IEnergyCapBE {
	
	private static final int[] SLOTS_TOP = new int[] { 0 };
	private static final int[] SLOTS_BOTTOM = new int[] { 2, 1 };
	private static final int[] SLOTS_SIDES = new int[] { 1 };
	
	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(12, ItemStack.EMPTY);

	private int update = 0;
	private int energy_stored = 0;
	private int energy_capacity = IndustryReference.Resource.Processing.CAPACITY[0];
	private int energy_max_receive = IndustryReference.Resource.Processing.MAX_INPUT[0];
	private int energy_max_extract = IndustryReference.Resource.Processing.MAX_INPUT[0];
	private EnumEnergyState energy_state = EnumEnergyState.FILL;

	private EnumUIMode uiMode = EnumUIMode.DARK;
	
	public BlockEntityCharger(BlockPos posIn, BlockState stateIn) {
		super(IndustryRegistrationManager.BLOCK_ENTITY_TYPE_CHARGER.get(), posIn, stateIn);
	}

	@Override
	public void sendUpdates() {
		if (this.getLevel() != null) {
			this.setChanged();
			BlockState state = this.getBlockState();

			this.getLevel().sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (!this.getLevel().isClientSide()) {
				this.getLevel().setBlockAndUpdate(this.getBlockPos(), state);
			}
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.saveAdditional(compound, provider);
		
		ContainerHelper.saveAllItems(compound, this.inventoryItems, provider);
		
		compound.putInt("energy_state", this.energy_state.getIndex());
		compound.putInt("energy", this.energy_stored);
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
	}

	@Override
	public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.loadAdditional(compound, provider);
		
		this.inventoryItems = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems, provider);

		this.energy_state = EnumEnergyState.getStateFromIndex(compound.getInt("energy_state"));
		this.energy_stored = compound.getInt("energy");

		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
	}
	
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
		this.sendUpdates();
	}
	
	@Override
	public void onLoad() { }
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityCharger entityIn) {
		if (entityIn.energy_state.equals(EnumEnergyState.FILL)) {
			for (int i = 0; i < entityIn.inventoryItems.size() - 3; i++) {
				entityIn.chargeItem(i);
			}
		} else {
			for (int i = 0; i < entityIn.inventoryItems.size() - 3; i++) {
				entityIn.drainItem(i);
			}
		}
		
		boolean flag = entityIn.update > 0;
		
		if (flag) {
			entityIn.update--;
		} else {
			entityIn.update = 100;
			entityIn.sendUpdates();
		}
	}

	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player player) { }

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level levelIn, BlockPos posIn, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (playerIn.isShiftKeyDown()) {
			if (CosmosUtil.holdingWrench(playerIn)) {
				if (!levelIn.isClientSide()) {
					CompatHelper.spawnStack(CompatHelper.generateItemStackOnRemoval(levelIn, this, posIn), levelIn, posIn.getX() + 0.5, posIn.getY() + 0.5, posIn.getZ() + 0.5, 0);
					CosmosUtil.setToAir(levelIn, posIn);
				}
				ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
			}
		} else {
			if (playerIn instanceof ServerPlayer serverPlayer) {
				serverPlayer.openMenu(this, (packetBuffer) -> packetBuffer.writeBlockPos(this.getBlockPos()));
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
	public int getMaxStackSize() {
		return 64;
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

	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("cosmosindustry.gui.charger");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerCharger(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}
/*
	Optional<? extends ItemStackHandler>[] handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

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
                return BlockEntityCharger.this.extractEnergy(directionIn, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return BlockEntityCharger.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return BlockEntityCharger.this.getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return BlockEntityCharger.this.receiveEnergy(directionIn, maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return BlockEntityCharger.this.canReceive(directionIn);
            }

            @Override
            public boolean canExtract() {
                return BlockEntityCharger.this.canExtract(directionIn);
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
		}

		if (storedReceived > 0) {
			this.sendUpdates();
		}
		
		return storedReceived;
	}

	@Override
	public int extractEnergy(Direction directionIn, int max_extract, boolean simulate) {
		int storedExtracted = Math.min(energy_stored, Math.min(this.energy_max_extract, max_extract));
		
		if (!simulate) {
			this.energy_stored -= storedExtracted;
		}
		
		if (storedExtracted > 0) {
			this.sendUpdates();
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
		return false;
	}

	@Override
	public boolean canReceive(Direction directionIn) {
		if (directionIn.equals(Direction.DOWN)) {
			return true;
		} else {
			return false;
		}
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