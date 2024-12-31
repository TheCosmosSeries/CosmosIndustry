package com.tcn.cosmosindustry.production.core.blockentity;

import java.util.Optional;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.production.client.container.ContainerLiquidFuel;
import com.tcn.cosmosindustry.production.core.block.BlockLiquidFuel;
import com.tcn.cosmoslibrary.client.interfaces.IBEUpdated;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.IEnergyEntity;
import com.tcn.cosmoslibrary.common.interfaces.IFluidStorage;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUIMode;
import com.tcn.cosmoslibrary.common.lib.CompatHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectFluidTankCustom;

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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

@SuppressWarnings("removal")
public class BlockEntityLiquidFuel extends BlockEntity implements IBlockInteract, WorldlyContainer, MenuProvider, IFluidHandler, IFluidStorage, IBEUpdated.Fluid, IBEUpdated.Production, IEnergyEntity, IBEUIMode {

	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(8, ItemStack.EMPTY);
	
	public ObjectFluidTankCustom fluidTank = new ObjectFluidTankCustom(new FluidTank(16000), 0);

	private int produceTimeMax = 200;
	private int produceTime = 0;
	private int burnTime = 0;
	
	private int energy_stored = 0;
	private int energy_capacity = IndustryReference.Resource.Production.CAPACITY[0];
	private int energy_max_extract = IndustryReference.Resource.Production.MAX_OUTPUT[0];
	private int energyMaxProduce = IndustryReference.Resource.Production.RF_TICK_RATE[0];
	
	private int fluidCapacity = IndustryReference.Resource.Production.FLUID_CAPACITY[0];
	private int fluidUsageRate = IndustryReference.Resource.Production.FLUID_USAGE_RATE[0];

	private final int BUCKET_IN_SLOT = 3;
	private final int BUCKET_OUT_SLOT = 4;
	
	private EnumUIMode uiMode = EnumUIMode.DARK;

	public BlockEntityLiquidFuel(BlockPos pos, BlockState blockState) {
		super(IndustryRegistrationManager.BLOCK_ENTITY_TYPE_LIQUID_FUEL.get(), pos, blockState);
		
		fluidTank.getFluidTank().setCapacity(fluidCapacity);
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
		
		compound.putInt("produceTime", this.produceTime);
		compound.putInt("produceTimeMax", this.produceTimeMax);
		compound.putInt("burnTime", this.burnTime);
		compound.putInt("energy", this.energy_stored);

		CompoundTag tank = new CompoundTag();
		this.fluidTank.writeToNBT(tank);
		compound.put("fluidTank", tank);
		
		compound.putInt("fluidCapacity", this.fluidCapacity);
		compound.putInt("fluidUsageRate", this.fluidUsageRate);

		compound.putInt("ui_mode", this.uiMode.getIndex());
	}

	@Override
	public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.loadAdditional(compound, provider);

		this.inventoryItems = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.inventoryItems, provider);
		
		this.produceTime = compound.getInt("produceTime");
		this.produceTimeMax = compound.getInt("produceTimeMax");
		this.burnTime = compound.getInt("burnTime");
		this.energy_stored = compound.getInt("energy");

		this.fluidTank = ObjectFluidTankCustom.readFromNBT(compound.getCompound("fluidTank"));
		
		this.fluidCapacity = compound.getInt("fluidCapacity");
		this.fluidTank.getFluidTank().setCapacity(this.fluidCapacity == 0 ? 16000 : this.fluidCapacity);
		this.fluidUsageRate = compound.getInt("fluidUsageRate");

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
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityLiquidFuel entityIn) {
		entityIn.checkFluidSlots();
		
		if (entityIn.burnTime > 0) {
			entityIn.burnTime--;
		}
		
		if (!(entityIn.burnTime > 0) && entityIn.canProduce()) {
			entityIn.produceTimeMax = entityIn.getProduceTimeMax();
			entityIn.burnTime = entityIn.produceTimeMax;
			entityIn.sendUpdates();
		}
		
		if (entityIn.canProduce()) {
			levelIn.setBlock(posIn, stateIn.setValue(BlockLiquidFuel.ON, true), 3);
			
			if (entityIn.burnTime == entityIn.produceTimeMax) {
				entityIn.drain(entityIn.fluidUsageRate, FluidAction.EXECUTE);
			}
			
			entityIn.produceTime++;
			entityIn.produceEnergy();
			
			if (entityIn.produceTime == 1) {
				entityIn.sendUpdates();
			}
			
			if (entityIn.produceTime == entityIn.produceTimeMax) {
				entityIn.produceTime = 0;
				levelIn.setBlock(posIn, stateIn.setValue(BlockLiquidFuel.ON, false), 3);
			}
		} else {
			entityIn.produceTime = 0;
			entityIn.produceTimeMax = 200;
			levelIn.setBlock(posIn, stateIn.setValue(BlockLiquidFuel.ON, false), 3);
		}
		
		if (!entityIn.getLevel().isClientSide()) {
			entityIn.pushEnergy(levelIn.getBlockState(posIn).getValue(BlockLiquidFuel.FACING));
			entityIn.pushEnergy(levelIn.getBlockState(posIn).getValue(BlockLiquidFuel.FACING).getOpposite());
			entityIn.sendUpdates();
		}

		entityIn.energyMaxProduce = IndustryReference.Resource.Production.RF_TICK_RATE[entityIn.inventoryItems.get(0).getCount()];
		entityIn.energy_capacity = IndustryReference.Resource.Production.CAPACITY[entityIn.inventoryItems.get(1).getCount()];
		entityIn.energy_max_extract = IndustryReference.Resource.Production.MAX_OUTPUT[entityIn.inventoryItems.get(2).getCount()];
		
		entityIn.fluidUsageRate = IndustryReference.Resource.Production.FLUID_USAGE_RATE[entityIn.inventoryItems.get(5).getCount()];
		entityIn.fluidCapacity = IndustryReference.Resource.Production.FLUID_CAPACITY[entityIn.inventoryItems.get(6).getCount()];
		entityIn.updateFluidTankCapacity(entityIn.fluidCapacity);
		entityIn.produceTimeMax = IndustryReference.Resource.Production.FLUID_PRODUCE_TIME[entityIn.inventoryItems.get(7).getCount()];
	}

	public void pushEnergy(Direction directionIn) {
		BlockPos otherPos = this.getBlockPos().offset(directionIn.getNormal());
		BlockEntity entity = this.getLevel().getBlockEntity(otherPos);
		
		if (entity != null && !entity.isRemoved()) {
			if (this.hasEnergy() && this.canExtract(directionIn)) {
				if (this.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, otherPos, directionIn.getOpposite()) instanceof IEnergyStorage storage) {
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

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) { }

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, BlockHitResult hit) {
		return null;
	}

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
			if (CosmosUtil.holdingFluidHandler(stackIn)) {
				if (!this.getLevel().isClientSide()) {					
					if (stackIn.getCapability(Capabilities.FluidHandler.ITEM) instanceof IFluidHandlerItem fluidHandler) {
						if (fluidHandler.getFluidInTank(0).isEmpty()) {
							if (this.getCurrentFluidAmount() > 0) {
								int amount = fluidHandler.fill(this.getFluidInTank(0), FluidAction.SIMULATE);
								
								if (amount > 0) {
									playerIn.setItemInHand(handIn, FluidUtil.tryFillContainer(stackIn, this, amount, null, true).result);
									return ItemInteractionResult.SUCCESS;
								}
							}
						} else {
							FluidStack fluidStack = fluidHandler.drain(fluidHandler.getFluidInTank(0).getAmount(), FluidAction.SIMULATE);
							int amount = this.fill(fluidStack, FluidAction.SIMULATE);
							
							if (amount > 0) {
								if (!playerIn.getAbilities().instabuild) {
									playerIn.setItemInHand(handIn, FluidUtil.tryEmptyContainer(stackIn, this, amount, null, true).result);
								} else {
									this.fill(fluidStack, FluidAction.EXECUTE);
								}
								return ItemInteractionResult.SUCCESS;
							}
						}
					}
				}
			} else {
				if (playerIn instanceof ServerPlayer serverPlayer) {
					serverPlayer.openMenu(this, (packetBuffer) -> packetBuffer.writeBlockPos(this.getBlockPos()));
				}
				return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
			}	
		}
		return ItemInteractionResult.sidedSuccess(levelIn.isClientSide());
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
		return side == Direction.DOWN ? new int[] { 0, 1, 2 } : new int[] {};
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("cosmosindustry.gui.liquidfuel");
	}

	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerLiquidFuel(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}

	public IEnergyStorage createEnergyProxy(@Nullable Direction directionIn) {
        return new IEnergyStorage() {
        	
            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return BlockEntityLiquidFuel.this.extractEnergy(directionIn, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return BlockEntityLiquidFuel.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return BlockEntityLiquidFuel.this.getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return BlockEntityLiquidFuel.this.receiveEnergy(directionIn, maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return BlockEntityLiquidFuel.this.canReceive(directionIn);
            }

            @Override
            public boolean canExtract() {
                return BlockEntityLiquidFuel.this.canExtract(directionIn);
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
		Direction facing = this.getLevel().getBlockState(this.getBlockPos()).getValue(BlockLiquidFuel.FACING);
		
		return directionIn.equals(facing) || directionIn.equals(facing.getOpposite());
	}

	@Override
	public boolean canReceive(Direction directionIn) {
		return false;
	}

	@Override
	public int getProduceTimeMax() {
		return this.produceTimeMax;
	}

	@Override
	public int getProduceProgressScaled(int scale) {
		return this.produceTime * scale / this.produceTimeMax;
	}
	
	@Override
	public boolean canProduce() {
		return (this.burnTime > 0 || !this.fluidTank.getFluidTank().isEmpty()) && this.getEnergyStored() < this.getMaxEnergyStored();
	}

	@Override
	public void produceEnergy() {
		int storedReceived = Math.min(this.getMaxEnergyStored() - this.energy_stored, this.energyMaxProduce);
		this.energy_stored += storedReceived;
		this.sendUpdates();
	}

	@Override
	public boolean isProducing() {
		return this.canProduce() && (this.produceTime > 0 || this.burnTime > 0);
	}

	@Override
	public int getRFTickRate() {
		return this.energyMaxProduce;
	}

	public int getRFOutputRate() {
		return this.energy_max_extract;
	}
	
	

	@Override
	public int getFluidLevelScaled(int one) {
		float scaled = this.getCurrentFluidAmount() * one / this.getFluidCapacity() + 1;
		
		if (scaled == 0 && this.getCurrentFluidAmount() > 0) {
			return 1;
		} else {
			return (int) scaled;
		}
	}

	@Override
	public Fluid getCurrentStoredFluid() {
		this.updateFluidFillLevel();
		
		if (!this.isFluidEmpty()) {
			return this.fluidTank.getFluidTank().getFluid().getFluid();
		}
		return null;
	}

	@Override
	public boolean isFluidEmpty() {
		return this.fluidTank.getFluidTank().getFluidAmount() == 0;
	}

	@Override
	public int getCurrentFluidAmount() {
		return this.fluidTank.getFluidTank().getFluidAmount();
	}
	
	public String getCurrentStoredFluidName() {
		if (this.isFluidEmpty()) {
			return "Empty";
		}
		return this.fluidTank.getFluidTank().getFluid().getTranslationKey();
	}

	@Override
	public int fill(FluidStack resource, FluidAction doFill) {
		if (doFill.equals(FluidAction.EXECUTE)) {
			this.updateFluidFillLevel();
		}
		return this.fluidTank.getFluidTank().fill(resource, doFill);
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction doDrain) {
		FluidStack stack = this.fluidTank.getFluidTank().drain(resource.getAmount(), doDrain);
		this.updateFluidFillLevel();
		return stack;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction doDrain) {
		FluidStack stack = this.fluidTank.getFluidTank().drain(maxDrain, doDrain);
		this.updateFluidFillLevel();
		return stack;
	}
	
	@Override
	public boolean canFill(Direction from, Fluid fluid) {
		return this.canFluidInteract(from) && this.isFluidValid(0, new FluidStack(fluid, 1000));
	}

	@Override
	public boolean canDrain(Direction from, Fluid fluid) {
		return false;
	}
	
	@Override
	public int getTanks() {
		return 1;
	}

	public FluidTank getFluidTank() {
		return this.fluidTank.getFluidTank();
	}
	
	public void setFluidTank(FluidTank tank) {
		this.fluidTank.setFluidTank(tank);
	}
	
	public void emptyFluidTank() {
		this.fluidTank.getFluidTank().setFluid(FluidStack.EMPTY);
	}
	
	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		if (this.isFluidEmpty() || stack.isFluidEqual(this.getFluidInTank(0))) {
			return stack.getFluid().getFluidType().getTemperature() > 1000;
		}
		
		return false;
	}

	@Override
	public FluidTank getTank() {
		return this.fluidTank.getFluidTank();
	}

	@Override
	public int getFluidCapacity() {
		return this.fluidTank.getFluidTank().getCapacity();
	}
	
	public boolean canFluidInteract(Direction fromDirection) {
		Direction dirCheck = this.getLevel().getBlockState(this.getBlockPos()).getValue(BlockLiquidFuel.FACING).getClockWise();
		return fromDirection.equals(dirCheck) || fromDirection.equals(dirCheck.getOpposite());
	}

	public void updateFluidFillLevel() {
		this.sendUpdates();
		if (!this.isFluidEmpty()) {
			if (this.getFluidLevelScaled(6) == 0) {
				this.fluidTank.setFillLevel(1);
			} else {
				this.fluidTank.setFillLevel(Math.clamp(this.getFluidLevelScaled(6), 0, 6));
			}
		} else {
			this.fluidTank.setFillLevel(0);
		}
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return this.fluidTank.getFluidTank().getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		return this.fluidTank.getFluidTank().getCapacity();
	}
	
	@Override
	public int getFluidFillLevel() {
		return this.fluidTank.getFillLevel();
	}
	
	public void updateFluidTankCapacity(int newCapacity) {
		this.fluidTank.getFluidTank().setCapacity(newCapacity);
		if (this.getCurrentFluidAmount() > newCapacity) {
			this.getFluidInTank(0).setAmount(newCapacity);
		}
		this.updateFluidFillLevel();
	}
	
	public IFluidHandler createFluidProxy(@Nullable Direction directionIn) {
		return new IFluidHandler() {

			@Override
			public int getTanks() {
				if (BlockEntityLiquidFuel.this.canFluidInteract(directionIn)) {
					return BlockEntityLiquidFuel.this.getTanks();
				} else {
					return 0;
				}
			}

			@Override
			public FluidStack getFluidInTank(int tank) {
				if (BlockEntityLiquidFuel.this.canFluidInteract(directionIn)) {
					return BlockEntityLiquidFuel.this.getFluidInTank(tank);
				} else {
					return FluidStack.EMPTY;
				}
			}

			@Override
			public int getTankCapacity(int tank) {
				if (BlockEntityLiquidFuel.this.canFluidInteract(directionIn)) {
					return BlockEntityLiquidFuel.this.getTankCapacity(tank);
				} else {
					return 0;
				}
			}

			@Override
			public boolean isFluidValid(int tank, FluidStack stack) {
				if (BlockEntityLiquidFuel.this.canFluidInteract(directionIn)) {
					return BlockEntityLiquidFuel.this.isFluidValid(tank, stack);
				} else {
					return false;
				}
			}

			@Override
			public int fill(FluidStack resource, FluidAction action) {
				if (BlockEntityLiquidFuel.this.canFluidInteract(directionIn)) {
					return BlockEntityLiquidFuel.this.fill(resource, action);
				} else {
					return 0;
				}
			}

			@Override
			public FluidStack drain(FluidStack resource, FluidAction action) {
				if (BlockEntityLiquidFuel.this.canFluidInteract(directionIn)) {
					return BlockEntityLiquidFuel.this.drain(resource, action);
				} else {
					return FluidStack.EMPTY;
				}
			}

			@Override
			public FluidStack drain(int maxDrain, FluidAction action) {
				if (BlockEntityLiquidFuel.this.canFluidInteract(directionIn)) {
					return BlockEntityLiquidFuel.this.drain(maxDrain, action);
				} else {
					return FluidStack.EMPTY;
				}
			}
		};
	}

	public void checkFluidSlots() {
		if (!this.getLevel().isClientSide()) {
			if (!this.getItem(BUCKET_IN_SLOT).isEmpty()) {
				Optional<FluidStack> fluidStack = FluidUtil.getFluidContained(this.getItem(BUCKET_IN_SLOT));
				
				if (fluidStack.isPresent()) {
					FluidStack fluid = fluidStack.get();
					
					if (fluid != null) {
						if (this.isFluidValid(0, fluid)) {
							if (fluid.getAmount() > 0) {
								int amount = this.fill(fluid, FluidAction.SIMULATE);
								if (amount == fluid.getAmount()) {
									if (this.getItem(BUCKET_OUT_SLOT).getItem().equals(FluidUtil.tryEmptyContainer(this.getItem(BUCKET_IN_SLOT), this.getFluidTank(), amount, null, false).result.getItem()) && this.getItem(BUCKET_OUT_SLOT).getCount() < this.getItem(BUCKET_OUT_SLOT).getMaxStackSize()) {
										this.fill(fluid, FluidAction.EXECUTE);
										this.getItem(BUCKET_IN_SLOT).shrink(1);
										this.getItem(BUCKET_OUT_SLOT).grow(1);
									} else if (this.getItem(BUCKET_OUT_SLOT).isEmpty()) {
										this.setItem(BUCKET_OUT_SLOT, FluidUtil.tryEmptyContainer(this.getItem(BUCKET_IN_SLOT), this.getFluidTank(), amount, null, true).result);
										this.getItem(BUCKET_IN_SLOT).shrink(1);
									}
								}
							}
						}
					}
				} else {
					if (this.getCurrentFluidAmount() > 0) {
						if (this.getItem(BUCKET_OUT_SLOT).isEmpty()) {
							ItemStack fillStack = FluidUtil.tryFillContainer(this.getItem(BUCKET_IN_SLOT), this.getTank(), this.getCurrentFluidAmount(), null, true).result;
							
							if (!fillStack.isEmpty()) {
								this.setItem(BUCKET_OUT_SLOT, fillStack);
								this.getItem(BUCKET_IN_SLOT).shrink(1);
								this.updateFluidFillLevel();
							}
						}
					}
				
				}
				this.sendUpdates();
			}
		}
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