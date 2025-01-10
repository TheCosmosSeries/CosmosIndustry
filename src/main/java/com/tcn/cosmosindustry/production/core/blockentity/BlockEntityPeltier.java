package com.tcn.cosmosindustry.production.core.blockentity;

import java.util.Optional;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.production.client.container.ContainerPeltier;
import com.tcn.cosmosindustry.production.core.block.BlockPeltier;
import com.tcn.cosmoslibrary.client.interfaces.IBEUpdated;
import com.tcn.cosmoslibrary.common.capability.IEnergyCapBE;
import com.tcn.cosmoslibrary.common.capability.IFluidCapBE;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockNotifier;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUIMode;
import com.tcn.cosmoslibrary.common.lib.CompatHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.energy.interfaces.IEnergyEntity;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

@SuppressWarnings("removal")
public class BlockEntityPeltier extends BlockEntity implements IBlockInteract, IBlockNotifier, WorldlyContainer, MenuProvider, IBEUpdated.FluidDual, IBEUpdated.Production, IEnergyEntity, IBEUIMode, IEnergyCapBE, IFluidCapBE {

	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(8, ItemStack.EMPTY);
	
	public ObjectFluidTankCustom fluidTankCold = new ObjectFluidTankCustom(16000, 0);
	public ObjectFluidTankCustom fluidTankHot = new ObjectFluidTankCustom(16000, 0);

	private int produceTimeMax = 800;
	private int produceTime = 0;
	private int burnTime = 0;
	
	private int energy_stored = 0;
	private int energy_capacity = IndustryReference.Resource.Production.CAPACITY[0];
	private int energy_max_extract = IndustryReference.Resource.Production.MAX_OUTPUT;
	private int energyMaxProduce = IndustryReference.Resource.Production.RF_TICK_RATE[0];
	
	private int fluidCapacity = IndustryReference.Resource.Production.FLUID_CAPACITY[0];
	private int fluidUsageRate = IndustryReference.Resource.Production.FLUID_USAGE_RATE[0];
	
	private int differentialTemp = 0;

	private final int BUCKET_IN_SLOT = 3;
	private final int BUCKET_OUT_SLOT = 4;
	
	private EnumUIMode uiMode = EnumUIMode.DARK;
	private SelectedTank selectedTank = SelectedTank.COLD;

	public BlockEntityPeltier(BlockPos pos, BlockState blockState) {
		super(IndustryRegistrationManager.BLOCK_ENTITY_TYPE_PELTIER.get(), pos, blockState);
		
		fluidTankCold.getFluidTank().setCapacity(fluidCapacity);
		fluidTankHot.getFluidTank().setCapacity(fluidCapacity);
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

		CompoundTag tankCold = new CompoundTag();
		this.fluidTankCold.writeToNBT(tankCold);
		compound.put("fluidTankCold", tankCold);

		CompoundTag tankHot = new CompoundTag();
		this.fluidTankHot.writeToNBT(tankHot);
		compound.put("fluidTankHot", tankHot);
		
		compound.putInt("fluidCapacity", this.fluidCapacity);
		compound.putInt("fluidUsageRate", this.fluidUsageRate);
		
		compound.putInt("deltaTemp", this.differentialTemp);

		compound.putInt("selectedTank", this.selectedTank.getIndex());
		
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

		this.fluidTankCold = ObjectFluidTankCustom.readFromNBT(compound.getCompound("fluidTankCold"));
		this.fluidTankHot = ObjectFluidTankCustom.readFromNBT(compound.getCompound("fluidTankHot"));
		
		this.fluidCapacity = compound.getInt("fluidCapacity");
		this.fluidUsageRate = compound.getInt("fluidUsageRate");
		
		this.differentialTemp = compound.getInt("deltaTemp");		
		
		this.fluidTankCold.getFluidTank().setCapacity(this.fluidCapacity == 0 ? 16000 : this.fluidCapacity);
		this.fluidTankHot.getFluidTank().setCapacity(this.fluidCapacity == 0 ? 16000 : this.fluidCapacity);

		this.selectedTank = SelectedTank.fromIndex(compound.getInt("selectedTank"));
		
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
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntityPeltier entityIn) {
		entityIn.checkFluidSlots();
		
		if (entityIn.burnTime > 0 && entityIn.canProduce()) {
			entityIn.burnTime--;
		}
		
		if (entityIn.burnTime == 0 && entityIn.canProduce()) {
			entityIn.produceTimeMax = entityIn.getProduceTimeMax();
			entityIn.burnTime = entityIn.produceTimeMax;
			entityIn.sendUpdates();
		}
		
		if (entityIn.canProduce()) {
			levelIn.setBlock(posIn, stateIn.setValue(BlockPeltier.ON, true), 3);
			
			if (entityIn.burnTime == entityIn.produceTimeMax) {
				entityIn.differentialTemp = entityIn.getDifferentialFluidTemp();
				entityIn.drainInternal(0, entityIn.fluidUsageRate, FluidAction.EXECUTE);
				entityIn.drainInternal(1, entityIn.fluidUsageRate, FluidAction.EXECUTE);
			}
			
			entityIn.produceTime++;
			entityIn.produceEnergy();
			
			if (entityIn.produceTime == 1) {
				entityIn.sendUpdates();
			}
			
			if (entityIn.produceTime == entityIn.produceTimeMax) {
				entityIn.produceTime = 0;
				entityIn.differentialTemp = 0;
				levelIn.setBlock(posIn, stateIn.setValue(BlockPeltier.ON, false), 3);
			}
		} else {
			entityIn.produceTime = 0;
			entityIn.differentialTemp = 0;
			entityIn.produceTimeMax = 800;
			levelIn.setBlock(posIn, stateIn.setValue(BlockPeltier.ON, false), 3);
		}
		
		if (!entityIn.getLevel().isClientSide()) {
			entityIn.pushEnergy(levelIn.getBlockState(posIn).getValue(BlockPeltier.FACING));
			entityIn.pushEnergy(levelIn.getBlockState(posIn).getValue(BlockPeltier.FACING).getOpposite());
			entityIn.sendUpdates();
		}

		entityIn.energyMaxProduce = (IndustryReference.Resource.Production.RF_TICK_RATE[entityIn.inventoryItems.get(2).getCount()] * (1 + entityIn.inventoryItems.get(0).getCount())) * (entityIn.differentialTemp / 400);
		entityIn.energy_capacity = IndustryReference.Resource.Production.CAPACITY[entityIn.inventoryItems.get(1).getCount()];
		
		entityIn.fluidUsageRate = IndustryReference.Resource.Production.FLUID_USAGE_RATE[entityIn.inventoryItems.get(5).getCount()];
		entityIn.fluidCapacity = IndustryReference.Resource.Production.FLUID_CAPACITY[entityIn.inventoryItems.get(6).getCount()];
		entityIn.updateFluidTankCapacity(0, entityIn.fluidCapacity);
		entityIn.updateFluidTankCapacity(1, entityIn.fluidCapacity);
//		entityIn.produceTimeMax = IndustryReference.Resource.Production.FLUID_PRODUCE_TIME[entityIn.inventoryItems.get(7).getCount()];
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
					Direction direction = hit.getDirection();
					
					if (direction.equals(state.getValue(BlockPeltier.FACING).getClockWise())) { //EAST (COLD)
						if (stackIn.getCapability(Capabilities.FluidHandler.ITEM) instanceof IFluidHandlerItem fluidHandler) {
							if (fluidHandler.getFluidInTank(0).isEmpty()) {
								if (this.getCurrentFluidAmount(0) > 0) {
									int amount = fluidHandler.fill(this.getFluidInTank(0), FluidAction.SIMULATE);
									
									if (amount > 0) {
										playerIn.setItemInHand(handIn, FluidUtil.tryFillContainer(stackIn, this.getFluidCapability(direction), amount, null, true).result);
										return ItemInteractionResult.SUCCESS;
									}
								}
							} else {
								FluidStack fluidStack = fluidHandler.drain(fluidHandler.getFluidInTank(0).getAmount(), FluidAction.SIMULATE);
								if (this.isFluidValid(0, fluidStack)) {
									int amount = this.fillInternal(0, fluidStack, FluidAction.SIMULATE);
									
									if (amount > 0) {
										if (!playerIn.getAbilities().instabuild) {
											playerIn.setItemInHand(handIn, FluidUtil.tryEmptyContainer(stackIn, this.getFluidCapability(direction), amount, null, true).result);
										} else {
											this.fillInternal(0, fluidStack, FluidAction.EXECUTE);
											return ItemInteractionResult.SUCCESS;
										}
									}
								}
							}
						}
						return ItemInteractionResult.FAIL;
					} else if (direction.equals(state.getValue(BlockPeltier.FACING).getCounterClockWise())) { //WEST (HOT)
						if (stackIn.getCapability(Capabilities.FluidHandler.ITEM) instanceof IFluidHandlerItem fluidHandler) {
							if (fluidHandler.getFluidInTank(0).isEmpty()) {
								if (this.getCurrentFluidAmount(1) > 0) {
									int amount = fluidHandler.fill(this.getFluidInTank(1), FluidAction.SIMULATE);
									
									if (amount > 0) {
										playerIn.setItemInHand(handIn, FluidUtil.tryFillContainer(stackIn, this.getFluidCapability(direction), amount, null, true).result);
										return ItemInteractionResult.SUCCESS;
									}
								}
							} else {
								FluidStack fluidStack = fluidHandler.drain(fluidHandler.getFluidInTank(0).getAmount(), FluidAction.SIMULATE);
								if (this.isFluidValid(1, fluidStack)) {
									int amount = this.fillInternal(1, fluidStack, FluidAction.SIMULATE);
									
									if (amount > 0) {
										if (!playerIn.getAbilities().instabuild) {
											playerIn.setItemInHand(handIn, FluidUtil.tryEmptyContainer(stackIn, this.getFluidCapability(direction), amount, null, true).result);
											return ItemInteractionResult.SUCCESS;
										} else {
											this.fillInternal(1, fluidStack, FluidAction.EXECUTE);
											return ItemInteractionResult.SUCCESS;
										}
									}
								}
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
		return ComponentHelper.style(ComponentColour.ORANGE, "", "cosmosindustry.gui.peltier");
	}

	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerPeltier(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}

	@Override
	public IEnergyStorage getEnergyCapability(@Nullable Direction directionIn) {
        return new IEnergyStorage() {
        	
            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return BlockEntityPeltier.this.extractEnergy(directionIn, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return BlockEntityPeltier.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return BlockEntityPeltier.this.getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return BlockEntityPeltier.this.receiveEnergy(directionIn, maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return BlockEntityPeltier.this.canReceive(directionIn);
            }

            @Override
            public boolean canExtract() {
                return BlockEntityPeltier.this.canExtract(directionIn);
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
		Direction facing = this.getLevel().getBlockState(this.getBlockPos()).getValue(BlockPeltier.FACING);
		
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
		if (this.burnTime > 0 && this.getEnergyStored() < this.getMaxEnergyStored()) {
			return true;
		} else {
			boolean flag1 = !this.getCustomTank(0).getFluidTank().isEmpty() && !this.getCustomTank(1).getFluidTank().isEmpty();
			boolean flag2 = flag1 && this.getFluidInTank(0).getFluidType().getTemperature() < this.getFluidInTank(1).getFluidType().getTemperature();
			
			return flag1 && flag2 && this.getEnergyStored() < this.getMaxEnergyStored();
		}
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
	
	public int getDifferentialFluidTemp() {
		if (this.differentialTemp > 0) {
			return this.differentialTemp;
		}
		
		if (!this.isFluidEmpty(0) && !this.isFluidEmpty(1)) {
			int coldTemp = Math.max(this.getFluidTank(0).getFluid().getFluidType().getTemperature(), 1);
			int hotTemp = Math.max(this.getFluidTank(1).getFluid().getFluidType().getTemperature(), 1);
			
//			System.out.println(coldTemp + " | " + hotTemp);
			
			int differential = coldTemp - hotTemp;
			
			if (coldTemp == hotTemp) {
				return 1;
			} else if (hotTemp > coldTemp) {
				return differential * -1;
			} else {
				return differential;
			}
		}
		return 1;
	}
	

	@Override
	public int getFluidLevelScaled(int tank, int one) {
		float scaled = this.getCurrentFluidAmount(tank) * one / this.getFluidCapacity(tank) + 1;
		
		if (scaled == 0 && this.getCurrentFluidAmount(tank) > 0) {
			return 1;
		} else {
			return (int) scaled;
		}
	}

	@Override
	public Fluid getCurrentStoredFluid(int tank) {
		this.updateFluidFillLevel(tank);
		
		if (!this.isFluidEmpty(tank)) {
			return this.getCustomTank(tank).getFluidTank().getFluid().getFluid();
		}
		return null;
	}

	@Override
	public boolean isFluidEmpty(int tank) {
		return this.getCustomTank(tank).getFluidTank().getFluidAmount() == 0;
	}

	@Override
	public int getCurrentFluidAmount(int tank) {
		return this.getCustomTank(tank).getFluidTank().getFluidAmount();
	}
	
	public String getCurrentStoredFluidName(int tank) {
		if (this.isFluidEmpty(tank)) {
			return "Empty";
		}
		return this.getCustomTank(tank).getFluidTank().getFluid().getTranslationKey();
	}

	public int fill(Direction directionIn, FluidStack resource, FluidAction doFill) {
		int tank = this.getTankBasedOnRotation(directionIn);
		
		if (this.canFill(tank, directionIn, resource.getFluid())) {
			if (doFill.equals(FluidAction.EXECUTE)) {
				this.updateFluidFillLevel(tank);
			}
			return this.getCustomTank(tank).getFluidTank().fill(resource, doFill);
		}
		return 0;
	}

	public FluidStack drain(Direction directionIn, FluidStack resource, FluidAction doDrain) {
		int tank = this.getTankBasedOnRotation(directionIn);
		FluidStack stack = this.getCustomTank(tank).getFluidTank().drain(resource.getAmount(), doDrain);
		this.updateFluidFillLevel(tank);
		return stack;
	}

	public FluidStack drain(Direction directionIn, int maxDrain, FluidAction doDrain) {
		int tank = this.getTankBasedOnRotation(directionIn);
		FluidStack stack = this.getCustomTank(tank).getFluidTank().drain(maxDrain, doDrain);
		this.updateFluidFillLevel(tank);
		return stack;
	}

	private int fillInternal(int tank, FluidStack resource, FluidAction doFill) {
		if (doFill.equals(FluidAction.EXECUTE)) {
			this.updateFluidFillLevel(tank);
		}
		return this.getCustomTank(tank).getFluidTank().fill(resource, doFill);
	}

	@SuppressWarnings("unused")
	private FluidStack drainInternal(int tank, FluidStack resource, FluidAction doDrain) {
		FluidStack stack = this.getCustomTank(tank).getFluidTank().drain(resource.getAmount(), doDrain);
		this.updateFluidFillLevel(tank);
		return stack;
	}

	private FluidStack drainInternal(int tank, int maxDrain, FluidAction doDrain) {
		FluidStack stack = this.getCustomTank(tank).getFluidTank().drain(maxDrain, doDrain);
		this.updateFluidFillLevel(tank);
		return stack;
	}
	
	public boolean canFill(int tank, Direction from, Fluid fluid) {
		return this.canFluidInteract(from) && this.isFluidValid(tank, new FluidStack(fluid, 1000));
	}

	public boolean canDrain(int tank, Direction from, Fluid fluid) {
		return false;
	}
	
	public int getTanks() {
		return 2;
	}
	
	public int getTankBasedOnRotation(Direction directionIn) {
		Direction facing = this.getLevel().getBlockState(this.getBlockPos()).getValue(BlockPeltier.FACING);
		
		return directionIn.equals(facing.getClockWise()) ? 1 : directionIn.equals(facing.getCounterClockWise()) ? 0 : -1;
	}
	
	public ObjectFluidTankCustom getCustomTank(int tank) {
		if (tank == 0) {
			return this.fluidTankCold;
		} else {
			return this.fluidTankHot;
		}
	}

	public FluidTank getFluidTank(int tank) {
		return this.getCustomTank(tank).getFluidTank();
	}
	
	public void setFluidTank(int tank, FluidTank tankActual) {
		this.getCustomTank(tank).setFluidTank(tankActual);
	}
	
	public void emptyFluidTank(int tank) {
		this.getCustomTank(tank).getFluidTank().setFluid(FluidStack.EMPTY);
	}
	
	public boolean isFluidValid(int tank, FluidStack stack) {
		if (this.isFluidEmpty(tank) || stack.isFluidEqual(this.getFluidInTank(tank))) {
			return tank == 0 ? stack.getFluid().getFluidType().getTemperature() <= 500 : stack.getFluid().getFluidType().getTemperature() > 500;
		}
		
		return false;
	}

	public FluidTank getTank(int tank) {
		return this.getCustomTank(tank).getFluidTank();
	}

	public int getFluidCapacity(int tank) {
		return this.getCustomTank(tank).getFluidTank().getCapacity();
	}
	
	public boolean canFluidInteract(Direction fromDirection) {
		Direction dirCheck = this.getLevel().getBlockState(this.getBlockPos()).getValue(BlockPeltier.FACING).getClockWise();
		return fromDirection.equals(dirCheck) || fromDirection.equals(dirCheck.getOpposite());
	}

	public void updateFluidFillLevel(int tank) {
		this.sendUpdates();
		if (!this.isFluidEmpty(tank)) {
			if (this.getFluidLevelScaled(tank, 6) == 0) {
				this.getCustomTank(tank).setFillLevel(1);
			} else {
				this.getCustomTank(tank).setFillLevel(Math.clamp(this.getFluidLevelScaled(tank, 6), 0, 6));
			}
		} else {
			this.getCustomTank(tank).setFillLevel(0);
		}
	}

	public FluidStack getFluidInTank(int tank) {
		return this.getCustomTank(tank).getFluidTank().getFluid();
	}

	public int getTankCapacity(int tank) {
		return this.getCustomTank(tank).getFluidTank().getCapacity();
	}

	@Override
	public int getFluidFillLevel(int tank) {
		return this.getCustomTank(tank).getFillLevel();
	}
	
	public void updateFluidTankCapacity(int tank, int newCapacity) {
		this.getCustomTank(tank).getFluidTank().setCapacity(newCapacity);
		if (this.getCurrentFluidAmount(tank) > newCapacity) {
			this.getFluidInTank(tank).setAmount(newCapacity);
		}
		this.updateFluidFillLevel(tank);
	}
	
	public IFluidHandler getFluidCapability(@Nullable Direction directionIn) {
		return new IFluidHandler() {
			
			@Override
			public int getTanks() {
				if (BlockEntityPeltier.this.canFluidInteract(directionIn)) {
					return BlockEntityPeltier.this.getTanks();
				} else {
					return 0;
				}
			}

			@Override
			public FluidStack getFluidInTank(int tank) {
				if (BlockEntityPeltier.this.canFluidInteract(directionIn)) {
					return BlockEntityPeltier.this.getFluidInTank(BlockEntityPeltier.this.getTankBasedOnRotation(directionIn));
				} else {
					return FluidStack.EMPTY;
				}
			}

			@Override
			public int getTankCapacity(int tank) {
				if (BlockEntityPeltier.this.canFluidInteract(directionIn)) {
					return BlockEntityPeltier.this.getTankCapacity(BlockEntityPeltier.this.getTankBasedOnRotation(directionIn));
				} else {
					return 0;
				}
			}

			@Override
			public boolean isFluidValid(int tank, FluidStack stack) {
				if (BlockEntityPeltier.this.canFluidInteract(directionIn)) {
					return BlockEntityPeltier.this.isFluidValid(BlockEntityPeltier.this.getTankBasedOnRotation(directionIn), stack);
				} else {
					return false;
				}
			}

			@Override
			public int fill(FluidStack resource, FluidAction action) {
				if (BlockEntityPeltier.this.canFluidInteract(directionIn)) {
					return BlockEntityPeltier.this.fill(directionIn, resource, action);
				} else {
					return 0;
				}
			}

			@Override
			public FluidStack drain(FluidStack resource, FluidAction action) {
				if (BlockEntityPeltier.this.canFluidInteract(directionIn)) {
					return BlockEntityPeltier.this.drain(directionIn, resource, action);
				} else {
					return FluidStack.EMPTY;
				}
			}

			@Override
			public FluidStack drain(int maxDrain, FluidAction action) {
				if (BlockEntityPeltier.this.canFluidInteract(directionIn)) {
					return BlockEntityPeltier.this.drain(directionIn, maxDrain, action);
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
						if (this.isFluidValid(this.getSelectedTank().getIndex(), fluid)) {
							if (fluid.getAmount() > 0) {
								int amount = this.fillInternal(this.getSelectedTank().getIndex(), fluid, FluidAction.SIMULATE);
								if (amount == fluid.getAmount()) {
									if (this.getItem(BUCKET_OUT_SLOT).getItem().equals(FluidUtil.tryEmptyContainer(this.getItem(BUCKET_IN_SLOT), this.getFluidTank(this.getSelectedTank().getIndex()), amount, null, false).result.getItem()) && this.getItem(BUCKET_OUT_SLOT).getCount() < this.getItem(BUCKET_OUT_SLOT).getMaxStackSize()) {
										this.fillInternal(this.getSelectedTank().getIndex(), fluid, FluidAction.EXECUTE);
										this.getItem(BUCKET_IN_SLOT).shrink(1);
										this.getItem(BUCKET_OUT_SLOT).grow(1);
									} else if (this.getItem(BUCKET_OUT_SLOT).isEmpty()) {
										this.setItem(BUCKET_OUT_SLOT, FluidUtil.tryEmptyContainer(this.getItem(BUCKET_IN_SLOT), this.getFluidTank(this.getSelectedTank().getIndex()), amount, null, true).result);
										this.getItem(BUCKET_IN_SLOT).shrink(1);
									}
								}
							}
						}
					}
				} else {
					if (this.getCurrentFluidAmount(this.getSelectedTank().getIndex()) > 0) {
						if (this.getItem(BUCKET_OUT_SLOT).isEmpty()) {
							ItemStack fillStack = FluidUtil.tryFillContainer(this.getItem(BUCKET_IN_SLOT), this.getTank(this.getSelectedTank().getIndex()), this.getCurrentFluidAmount(this.getSelectedTank().getIndex()), null, true).result;
							
							if (!fillStack.isEmpty()) {
								this.setItem(BUCKET_OUT_SLOT, fillStack);
								this.getItem(BUCKET_IN_SLOT).shrink(1);
								this.updateFluidFillLevel(this.getSelectedTank().getIndex());
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
	
	public SelectedTank getSelectedTank() {
		return this.selectedTank;
	}
	
	public void cycleSelectedTank() {
		this.selectedTank = this.selectedTank.getNextSelected();
	}
	
	public enum SelectedTank {
		COLD (0, "Cold", ComponentColour.LIGHT_BLUE),
		HOT (1, "Hot", ComponentColour.ORANGE);
		
		int index;
		String name;
		ComponentColour textColour;
		
		SelectedTank(int indexIn, String nameIn, ComponentColour textColourIn) {
			this.index = indexIn;
			this.name = nameIn;
			this.textColour = textColourIn;
		}
		
		public int getIndex() {
			return this.index;
		}
		
		public String getName() {
			return this.name;
		}
		
		public ComponentColour getTextColour() {
			return this.textColour;
		}
		
		public static SelectedTank fromIndex(int index) {
			switch (index) {
				case 0:
					return COLD;
				case 1:
					return HOT;
				default:
					return COLD;
			}
		}
		
		public SelectedTank getNextSelected() {
			switch(this) {
				case COLD:
					return HOT;
				case HOT:
					return COLD;
				default:
					return COLD;
			}
		}
	}
}