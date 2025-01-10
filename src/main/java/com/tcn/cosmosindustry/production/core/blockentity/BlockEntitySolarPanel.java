package com.tcn.cosmosindustry.production.core.blockentity;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.production.client.container.ContainerSolarPanel;
import com.tcn.cosmosindustry.production.core.block.BlockSolarPanel;
import com.tcn.cosmoslibrary.client.interfaces.IBEUpdated;
import com.tcn.cosmoslibrary.common.capability.IEnergyCapBE;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockNotifier;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUIMode;
import com.tcn.cosmoslibrary.common.lib.CompatHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
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

public class BlockEntitySolarPanel extends BlockEntity implements IBlockInteract, IBlockNotifier, WorldlyContainer, MenuProvider, IBEUpdated.Production, IEnergyEntity, IBEUIMode, IEnergyCapBE {

	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(3, ItemStack.EMPTY);

	private boolean isProducing = false;
	private int energy_stored = 0;
	private int energy_capacity = IndustryReference.Resource.Production.CAPACITY[0];
	private int energy_max_extract = IndustryReference.Resource.Production.MAX_OUTPUT;
	private int energyMaxProduce = IndustryReference.Resource.Production.RF_TICK_RATE[0];
	
	private EnumUIMode uiMode = EnumUIMode.DARK;
	
	public BlockEntitySolarPanel(BlockPos pos, BlockState blockState) {
		super(IndustryRegistrationManager.BLOCK_ENTITY_TYPE_SOLAR_PANEL.get(), pos, blockState);
	}

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
		
		compound.putInt("energy", this.energy_stored);

		compound.putInt("ui_mode", this.uiMode.getIndex());
	}

	@Override
	public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.loadAdditional(compound, provider);

		this.inventoryItems = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems, provider);
		
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
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntitySolarPanel entityIn) {
		if (entityIn.canProduce()) {
			entityIn.isProducing = true;
		}

		if (!entityIn.canProduce()) {
			entityIn.isProducing = false;
		}
		
		if (entityIn.isProducing()) {
			entityIn.produceEnergy();
		}

		if (!entityIn.getLevel().isClientSide()) {
			entityIn.pushEnergy(Direction.DOWN);
			entityIn.pushEnergy(Direction.NORTH);
			entityIn.pushEnergy(Direction.SOUTH);
			entityIn.pushEnergy(Direction.EAST);
			entityIn.pushEnergy(Direction.WEST);
			entityIn.sendUpdates();
		}

		entityIn.energyMaxProduce = IndustryReference.Resource.Production.RF_TICK_RATE[entityIn.inventoryItems.get(2).getCount()] * (1 + entityIn.inventoryItems.get(0).getCount());
		entityIn.energy_capacity = IndustryReference.Resource.Production.CAPACITY[entityIn.inventoryItems.get(1).getCount()];
	}

	public void pushEnergy(Direction directionIn) {
		BlockPos otherPos = this.getBlockPos().offset(directionIn.getNormal());
		BlockEntity entity = this.getLevel().getBlockEntity(otherPos);
		
		if (entity != null && !entity.isRemoved()) {
			if (this.hasEnergy() && this.canExtract(directionIn)) {
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
		}
	}

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) { }

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, BlockHitResult hit) {
		return null;
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level levelIn, BlockPos posIn, Player playerIn, InteractionHand hand, BlockHitResult hit) {
		if (playerIn.isShiftKeyDown()) {
			if (CosmosUtil.holdingWrench(playerIn)) {
				if (!levelIn.isClientSide) {
					CompatHelper.spawnStack(CompatHelper.generateItemStackOnRemoval(levelIn, this, posIn), levelIn, posIn.getX() + 0.5, posIn.getY() + 0.5, posIn.getZ() + 0.5, 0);
					CosmosUtil.setToAir(levelIn, posIn);
				}
				ItemInteractionResult.sidedSuccess(levelIn.isClientSide);
			}
		} else {
			if (playerIn instanceof ServerPlayer serverPlayer) {
				serverPlayer.openMenu(this, (packetBuffer) -> packetBuffer.writeBlockPos(this.getBlockPos()));
			}
		}
		return ItemInteractionResult.sidedSuccess(levelIn.isClientSide);
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

	public boolean renderBit(Direction dirOne, Direction dirTwo) {
		if (dirOne.equals(Direction.SOUTH) && dirTwo.equals(Direction.NORTH) || dirOne.equals(Direction.EAST) && dirTwo.equals(Direction.WEST)) {
			return false; // return false if directions are opposed to each other
		}
		
		BlockState stateOne = this.getLevel().getBlockState(this.getBlockPos().offset(dirOne.getNormal()));
		BlockState stateTwo = this.getLevel().getBlockState(this.getBlockPos().offset(dirTwo.getNormal()));
		BlockState stateThr = this.getLevel().getBlockState(this.getBlockPos().offset(dirOne.getNormal()).offset(dirTwo.getNormal()));
		
		return stateOne.getBlock() instanceof BlockSolarPanel && stateTwo.getBlock() instanceof BlockSolarPanel && !(stateThr.getBlock() instanceof BlockSolarPanel);
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("cosmosindustry.gui.solarpanel");
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
		return 4;
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
		return side == Direction.DOWN ? new int[] { 0, 1, 2 } : new int[] {};
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerSolarPanel(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}

	@Override
	public IEnergyStorage getEnergyCapability(@Nullable Direction directionIn) {
        return new IEnergyStorage() {
        	
            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return BlockEntitySolarPanel.this.extractEnergy(directionIn, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return BlockEntitySolarPanel.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return BlockEntitySolarPanel.this.getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return BlockEntitySolarPanel.this.receiveEnergy(directionIn, maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return BlockEntitySolarPanel.this.canReceive(directionIn);
            }

            @Override
            public boolean canExtract() {
                return BlockEntitySolarPanel.this.canExtract(directionIn);
            }
        };
    }

	@Override
	public void setMaxTransfer(int maxTransfer) {
		this.setMaxReceive(maxTransfer);
		this.setMaxExtract(maxTransfer);
	}

	@Override
	public void setMaxReceive(int maxReceive) { }
	
	@Override
	public void setMaxExtract(int maxExtract) {
		this.energy_max_extract = maxExtract;
	}

	@Override
	public int getMaxReceive() {
		return 0;
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
		return 0;
	}

	@Override
	public int extractEnergy(Direction directionIn, int max_extract, boolean simulate) {
		int storedExtracted = Math.min(energy_stored, Math.min(this.energy_max_extract, max_extract));
		
		if (!simulate) {
			this.energy_stored -= storedExtracted;
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
		return directionIn != Direction.UP;
	}

	@Override
	public boolean canReceive(Direction directionIn) {
		return false;
	}

	@Override
	public int getProduceTimeMax() {
		return 0;
	}

	@Override
	public int getProduceProgressScaled(int scale) {
		return 0;
	}
	
	@Override
	public boolean canProduce() {
		return this.getLevel().canSeeSky(this.getBlockPos()) && this.getLevel().getDayTime() < 13000 && this.getEnergyStored() < this.getMaxEnergyStored();
	}

	@Override
	public void produceEnergy() {
		int storedReceived = Math.min(this.getMaxEnergyStored() - this.energy_stored, this.energyMaxProduce);
		this.energy_stored += storedReceived;
		this.sendUpdates();
	}

	@Override
	public boolean isProducing() {
		return this.isProducing;
	}

	@Override
	public int getRFTickRate() {
		return this.energyMaxProduce;
	}

	public int getRFOutputRate() {
		return this.energy_max_extract;
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