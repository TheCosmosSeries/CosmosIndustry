package com.tcn.cosmosindustry.storage.core.blockentity;

import java.util.Optional;

import javax.annotation.Nullable;

import com.tcn.cosmoslibrary.client.interfaces.IBEUpdated;
import com.tcn.cosmoslibrary.common.capability.IFluidCapBE;
import com.tcn.cosmoslibrary.common.chat.CosmosChatUtil;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.enums.EnumSideState;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.IFluidStorage;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockNotifier;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBESided;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUIMode;
import com.tcn.cosmoslibrary.common.lib.CompatHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectFluidTankCustom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

@SuppressWarnings("removal")
abstract public class AbstractBlockEntityFluidTank extends BlockEntity implements IBlockInteract, IBlockNotifier, WorldlyContainer, MenuProvider, IFluidHandler, IFluidStorage, IBESided, IBEUpdated.Fluid, IBEUIMode, IFluidCapBE {

	private static final int[] SLOTS_TOP = new int[] { 0, 1 };
	private static final int[] SLOTS_BOTTOM = new int[] { 0, 1 };
	private static final int[] SLOTS_SIDES = new int[] { 0, 1 };
	
	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);

	public ObjectFluidTankCustom fluidTank;
	private int fluidCapacity;
	
	private EnumSideState[] SIDE_STATE_ARRAY = EnumSideState.getStandardArray();
	private EnumIndustryTier tier;
	
	private final int BUCKET_IN_SLOT = 0;
	private final int BUCKET_OUT_SLOT = 1;
	
	private EnumUIMode uiMode = EnumUIMode.DARK;

	public AbstractBlockEntityFluidTank(BlockEntityType<?> typeIn, BlockPos posIn, BlockState stateIn, EnumIndustryTier tierIn, int capacity) {
		super(typeIn, posIn, stateIn);
		this.tier = tierIn;
		
		this.fluidCapacity = capacity;
		
		this.fluidTank = new ObjectFluidTankCustom(new FluidTank(this.fluidCapacity), 0);
		this.fluidTank.getFluidTank().setCapacity(this.fluidCapacity);
	}

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
		sendUpdates();
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

		CompoundTag tank = new CompoundTag();
		this.fluidTank.writeToNBT(tank);
		compound.put("fluidTank", tank);

		compound.putInt("ui_mode", this.uiMode.getIndex());
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

		this.fluidTank = ObjectFluidTankCustom.readFromNBT(compound.getCompound("fluidTank"));
		this.fluidTank.getFluidTank().setCapacity(this.fluidCapacity);
		this.updateFluidFillLevel();

		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
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
		
		return tag;
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
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, AbstractBlockEntityFluidTank entityIn) {
		entityIn.checkFluidSlots();
		
		if (!entityIn.getLevel().isClientSide()) {
			entityIn.pushFluid(Direction.DOWN);
			entityIn.pushFluid(Direction.UP);
			entityIn.pushFluid(Direction.NORTH);
			entityIn.pushFluid(Direction.SOUTH);
			entityIn.pushFluid(Direction.EAST);
			entityIn.pushFluid(Direction.WEST);

			if (entityIn.tier.creative()) {
				if (!entityIn.isFluidEmpty() && (entityIn.getCurrentFluidAmount() < entityIn.getFluidCapacity())) {
					int amount = entityIn.getFluidCapacity() - entityIn.getCurrentFluidAmount();
					FluidStack fluid = entityIn.getFluidInTank(0);
					FluidStack newFluid = fluid.copy();
					newFluid.setAmount(amount);
					
					entityIn.fill(newFluid, FluidAction.EXECUTE);
					entityIn.fluidTank.setFillLevel(16);
				}
			}
		}
	}

	public void pushFluid(Direction directionIn) {
		BlockPos otherPos = this.getBlockPos().offset(directionIn.getNormal());
		BlockEntity entity = this.getLevel().getBlockEntity(otherPos);

		if (entity != null && !entity.isRemoved()) {
			if (this.getSide(directionIn).equals(EnumSideState.INTERFACE_OUTPUT)) {
				Object object = this.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, otherPos, directionIn);
			
				if (object != null) {
					if (object instanceof IFluidHandler storage) {
						if (storage.isFluidValid(0, this.getFluidInTank(0))) {
							FluidStack stack = this.drain(FluidType.BUCKET_VOLUME, FluidAction.SIMULATE);
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
	}

	@Override
	public void attack(BlockState state, Level levelIn, BlockPos pos, Player player) { }

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level levelIn, BlockPos posIn, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (playerIn.isShiftKeyDown()) {
			if (CosmosUtil.holdingWrench(playerIn)) {
				if (!levelIn.isClientSide()) {
					CompatHelper.spawnStack(CompatHelper.generateFluidItemStackOnRemoval(levelIn, this, this.getFluidTank(), posIn), levelIn, posIn.getX() + 0.5, posIn.getY() + 0.5, posIn.getZ() + 0.5, 0);
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
			} else if (CosmosUtil.holdingWrench(playerIn)) {
				if (CosmosUtil.holdingWrench(playerIn)) {
					this.cycleSide(hit.getDirection(), playerIn, true);
					return ItemInteractionResult.SUCCESS;
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
		return this.fluidTank.getFluidTank().isEmpty();
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
		int amount = this.fluidTank.getFluidTank().fill(resource, doFill);
		if (doFill.equals(FluidAction.EXECUTE)) {
			this.updateFluidFillLevel();
		}
		return amount;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction doDrain) {
		FluidStack stack = this.fluidTank.getFluidTank().drain(resource.getAmount(), !this.tier.creative() ? doDrain : FluidAction.SIMULATE);
		this.updateFluidFillLevel();
		return stack;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction doDrain) {
		FluidStack stack = this.fluidTank.getFluidTank().drain(maxDrain, !this.tier.creative() ? doDrain : FluidAction.SIMULATE);
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
		return stack.isEmpty() || this.fluidTank.getFluidTank().isEmpty() || stack.getFluid() == this.getCurrentStoredFluid();
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
		return !this.getSide(fromDirection.getOpposite()).equals(EnumSideState.DISABLED);
	}

	public void updateFluidFillLevel() {
		if (!this.isFluidEmpty()) {
			if (this.getCurrentFluidAmount() == this.getFluidCapacity()) {
				this.fluidTank.setFillLevel(16);
			} else if (this.getFluidLevelScaled(16) == 0 && this.getCurrentFluidAmount() < this.getFluidCapacity()) {
				this.fluidTank.setFillLevel(1);
			} else {
				this.fluidTank.setFillLevel(Math.clamp(this.getFluidLevelScaled(16), 0, 16));
			}
		} else {
			this.fluidTank.setFillLevel(0);
		}
		this.sendUpdates();
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
	
	@Override
	public IFluidHandler getFluidCapability(@Nullable Direction directionIn) {
		return new IFluidHandler() {

			@Override
			public int getTanks() {
				if (AbstractBlockEntityFluidTank.this.canFluidInteract(directionIn)) {
					return AbstractBlockEntityFluidTank.this.getTanks();
				} else {
					return 0;
				}
			}

			@Override
			public FluidStack getFluidInTank(int tank) {
				if (AbstractBlockEntityFluidTank.this.canFluidInteract(directionIn)) {
					return AbstractBlockEntityFluidTank.this.getFluidInTank(tank);
				} else {
					return FluidStack.EMPTY;
				}
			}

			@Override
			public int getTankCapacity(int tank) {
				if (AbstractBlockEntityFluidTank.this.canFluidInteract(directionIn)) {
					return AbstractBlockEntityFluidTank.this.getTankCapacity(tank);
				} else {
					return 0;
				}
			}

			@Override
			public boolean isFluidValid(int tank, FluidStack stack) {
				if (AbstractBlockEntityFluidTank.this.canFluidInteract(directionIn)) {
					return AbstractBlockEntityFluidTank.this.isFluidValid(tank, stack);
				} else {
					return false;
				}
			}

			@Override
			public int fill(FluidStack resource, FluidAction action) {
				if (AbstractBlockEntityFluidTank.this.canFluidInteract(directionIn)) {
					return AbstractBlockEntityFluidTank.this.fill(resource, action);
				} else {
					return 0;
				}
			}

			@Override
			public FluidStack drain(FluidStack resource, FluidAction action) {
				if (AbstractBlockEntityFluidTank.this.canFluidInteract(directionIn)) {
					return AbstractBlockEntityFluidTank.this.drain(resource, action);
				} else {
					return FluidStack.EMPTY;
				}
			}

			@Override
			public FluidStack drain(int maxDrain, FluidAction action) {
				if (AbstractBlockEntityFluidTank.this.canFluidInteract(directionIn)) {
					return AbstractBlockEntityFluidTank.this.drain(maxDrain, action);
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
					
					if (!fluid.isEmpty()) {
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
	public int[] getSlotsForFace(Direction side) {
		return side == Direction.DOWN ? SLOTS_BOTTOM : (side == Direction.UP ? SLOTS_TOP : SLOTS_SIDES);
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