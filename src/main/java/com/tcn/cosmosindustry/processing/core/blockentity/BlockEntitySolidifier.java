package com.tcn.cosmosindustry.processing.core.blockentity;

import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRecipeManager;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.core.recipe.SolidifierRecipe;
import com.tcn.cosmosindustry.core.recipe.SolidifierRecipeInput;
import com.tcn.cosmosindustry.processing.client.container.ContainerSolidifier;
import com.tcn.cosmosindustry.processing.core.block.BlockSolidifier;
import com.tcn.cosmoslibrary.client.interfaces.IBEUpdated;
import com.tcn.cosmoslibrary.client.interfaces.IBEUpdated.ProcessingRecipe;
import com.tcn.cosmoslibrary.common.capability.IEnergyCapBE;
import com.tcn.cosmoslibrary.common.capability.IFluidCapBE;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.IFluidStorage;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockNotifier;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUIMode;
import com.tcn.cosmoslibrary.common.lib.CompatHelper;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;
import com.tcn.cosmoslibrary.energy.interfaces.IEnergyEntity;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectFluidTankCustom;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
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
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

@SuppressWarnings("removal")
public class BlockEntitySolidifier extends BlockEntity implements IBlockInteract, IBlockNotifier, ProcessingRecipe, WorldlyContainer, MenuProvider, IFluidHandler, IFluidStorage, IBEUpdated.Fluid, IEnergyEntity, RecipeCraftingHolder, IBEUIMode, IEnergyCapBE, IFluidCapBE {

	private static final int[] SLOTS_TOP = new int[] { 0 };
	private static final int[] SLOTS_BOTTOM = new int[] { 2, 1 };
	private static final int[] SLOTS_SIDES = new int[] { 1 };
	
	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);

	public ObjectFluidTankCustom fluidTank = new ObjectFluidTankCustom(16000, 0);

	private int update = 0;
	private int process_time;
	private int process_speed = IndustryReference.Resource.Processing.SPEED_RATE[0];
	
	private int energy_stored = 0;
	private int energy_capacity = IndustryReference.Resource.Processing.CAPACITY[0];
	private int energy_max_receive = IndustryReference.Resource.Processing.MAX_INPUT[0];
	private int rf_tick_rate = IndustryReference.Resource.Processing.RF_TICK_RATE[0];

	private int fluidCapacity = IndustryReference.Resource.Processing.FLUID_CAPACITY[0];
	private int fluidUsageRate = IndustryReference.Resource.Processing.FLUID_USAGE_RATE[0];
	
	private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
	protected final RecipeType<SolidifierRecipe> recipeType;
	
	private final int BUCKET_IN_SLOT = 4;
	private final int BUCKET_OUT_SLOT = 5;
	
	private EnumUIMode uiMode = EnumUIMode.DARK;

	public BlockEntitySolidifier(BlockPos posIn, BlockState stateIn) {
		super(IndustryRegistrationManager.BLOCK_ENTITY_TYPE_SOLIDIFIER.get(), posIn, stateIn);

		this.recipeType = IndustryRecipeManager.RECIPE_TYPE_SOLIDIFIER.get();
		fluidTank.getFluidTank().setCapacity(fluidCapacity);
	}
	
	public void sendUpdates() {
		if (this.getLevel() != null) {
			this.setChanged();
			
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2 | 4);
			
			if (!this.getLevel().isClientSide()) {
				this.getLevel().setBlockAndUpdate(this.getBlockPos(), this.getBlockState());
			}
		}
	}

	@Override
	public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.saveAdditional(compound, provider);
		
		ContainerHelper.saveAllItems(compound, this.inventoryItems, provider);
		
		compound.putInt("process_time", this.process_time);
		compound.putInt("process_speed", this.process_speed);

		compound.putInt("energy", this.energy_stored);
		compound.putInt("rf_rate", this.rf_tick_rate);

		CompoundTag compoundnbt = new CompoundTag();
		this.recipesUsed.forEach((location, inte) -> { 
			compoundnbt.putInt(location.toString(), inte);
		});
		compound.put("RecipesUsed", compoundnbt);
		
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
		
		this.process_time = compound.getInt("process_time");
		this.process_speed = compound.getInt("process_speed");
		
		this.energy_stored = compound.getInt("energy");
		this.rf_tick_rate = compound.getInt("rf_rate");

		CompoundTag compoundnbt = compound.getCompound("RecipesUsed");

		for (String s : compoundnbt.getAllKeys()) {
			this.recipesUsed.put(ResourceLocation.parse(s), compoundnbt.getInt(s));
		}
		
		CompoundTag tankTag = compound.getCompound("fluidTank");
		this.fluidTank = ObjectFluidTankCustom.readFromNBT(tankTag);
		
		this.fluidCapacity = compound.getInt("fluidCapacity");
		this.fluidTank.getFluidTank().setCapacity(this.fluidCapacity == 0 ? 16000 : this.fluidCapacity);
		this.fluidUsageRate = compound.getInt("fluidUsageRate");

		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
	}

	@Override
	public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
		super.handleUpdateTag(tag, provider);
		this.setChanged();
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
	public void onLoad() { }
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntitySolidifier entityIn) {
		entityIn.checkFluidSlots();
		
		if (!entityIn.isFluidEmpty()) {
			RecipeHolder<?> recipe = entityIn.getCurrentRecipe();
			HolderLookup.Provider provider = levelIn.registryAccess();
			
			if (entityIn.canProcess(recipe, provider) && entityIn.hasEnergy()) {
				levelIn.setBlock(posIn, stateIn.setValue(BlockSolidifier.ON, true), 3);
				entityIn.extractEnergy(Direction.DOWN, entityIn.rf_tick_rate, false);
				
				entityIn.process_time++;
				entityIn.setChanged();
				
				if (entityIn.process_time == entityIn.process_speed) {
					entityIn.process_time = 0;
					if (!levelIn.isClientSide()) {
						entityIn.processItem(recipe, provider);
					}
				}
				
			} else {
				entityIn.process_time = 0;
				levelIn.setBlock(posIn, stateIn.setValue(BlockSolidifier.ON, false), 3);
			}
			
			if (entityIn.process_time > 0) {
				entityIn.sendUpdates();
			}
			
			if (entityIn.canProcess(recipe, provider) && entityIn.hasEnergy()) {
				Random rand = new Random();
				
				if (rand.nextDouble() < 0.1D) {
//					levelIn.playLocalSound(posIn.getX() + 0.5D, posIn.getY(), posIn.getZ() + 0.5D, ModSoundManager.MACHINE_CRUSHER.get(), SoundSource.BLOCKS, 0.1F, 1.0F, false);
				}
	
//				levelIn.addParticle(ParticleTypes.BUBBLE, posIn.getX() + 0.2, posIn.getY() + 0.5, posIn.getZ() + 0.2, 0.0F, 0.0F, 0.0F);
//				levelIn.addParticle(ParticleTypes.BUBBLE, posIn.getX() + 0.2, posIn.getY() + 0.5, posIn.getZ() + 0.4, 0.0F, 0.0F, 0.0F);
//				levelIn.addParticle(ParticleTypes.BUBBLE, posIn.getX() + 0.75, posIn.getY() + 0.5, posIn.getZ() + 0.2, 0.0F, 0.0F, 0.0F);
//				levelIn.addParticle(ParticleTypes.BUBBLE, posIn.getX() + 0.75, posIn.getY() + 0.5, posIn.getZ() + 0.75, 0.0F, 0.0F, 0.0F);
			}
		} else {
			if (entityIn.process_time > 0) {
				entityIn.process_time = 0;
			}
			levelIn.setBlock(posIn, stateIn.setValue(BlockSolidifier.ON, false), 3);
		}
		
		entityIn.process_speed = IndustryReference.Resource.Processing.SPEED_RATE[entityIn.inventoryItems.get(0).getCount()];
		entityIn.energy_capacity = IndustryReference.Resource.Processing.CAPACITY[entityIn.inventoryItems.get(1).getCount()];
		entityIn.rf_tick_rate = IndustryReference.Resource.Processing.RF_TICK_RATE[entityIn.inventoryItems.get(0).getCount()] - IndustryReference.Resource.Processing.RF_EFF_RATE[entityIn.inventoryItems.get(2).getCount()];

		entityIn.fluidUsageRate = IndustryReference.Resource.Processing.FLUID_USAGE_RATE[entityIn.inventoryItems.get(7).getCount()];
		entityIn.fluidCapacity = IndustryReference.Resource.Processing.FLUID_CAPACITY[entityIn.inventoryItems.get(8).getCount()];
		entityIn.updateFluidTankCapacity(entityIn.fluidCapacity);
//		entityIn.produceTimeMax = IndustryReference.RESOURCE.PRODUCTION.FLUID_PRODUCE_TIME[entityIn.inventoryItems.get(9).getCount()];
		
		if (entityIn.update > 0) {
			entityIn.update--;
		} else {
			entityIn.update = 40;
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
	public boolean isProcessing() {
		return this.hasEnergy() && this.canProcess(this.getCurrentRecipe(), this.getLevel().registryAccess()) && this.process_time > 0;
	}
	
	public RecipeHolder<?> getCurrentRecipe() {
		return this.getLevel().getRecipeManager().getRecipeFor(this.recipeType, this.getRecipeInput(), this.getLevel()).orElse(null);
	}
	
	@Override
	public boolean canProcess(@Nullable RecipeHolder<?> recipeIn, HolderLookup.Provider provider) {
		if (!this.isFluidEmpty() && recipeIn != null) {
			SolidifierRecipe recipe = (SolidifierRecipe)recipeIn.value();
			
			ItemStack resultItem = recipeIn.value().getResultItem(provider);
			ItemStack outputStack = this.getItem(3);
			
			if (outputStack.isEmpty()) {
				return true;
			}
			
			if (outputStack.getItem() != resultItem.getItem()) {
				return false;
			}
			
			int outputCheck = outputStack.getCount() + resultItem.getCount();
			int fluidCheck = this.getCurrentFluidAmount() - recipe.getFluidStack().getAmount();
			return outputCheck <= this.getMaxStackSize() && outputCheck <= outputStack.getMaxStackSize() && (fluidCheck >= 0);
		} else {
			return false;
		}
	}

	@Override
	public void processItem(@Nullable RecipeHolder<?> recipeIn, HolderLookup.Provider provider) {
		if (this.canProcess(recipeIn, provider) && recipeIn != null) {
			SolidifierRecipe recipe = (SolidifierRecipe) recipeIn.value();
			
			ItemStack inputStack = this.getItem(3);
			ItemStack resultItem = recipeIn.value().getResultItem(provider);

			if (inputStack.isEmpty()) {
				this.setItem(3, resultItem.copy());
			} else if (inputStack.getItem() == resultItem.getItem()) {
				inputStack.grow(resultItem.getCount());
			}
			
			this.drain(recipe.getFluidStack(), FluidAction.EXECUTE);
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
		this.sendUpdates();
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
		return ComponentHelper.style(ComponentColour.ORANGE, "", "cosmosindustry.gui.solidifier");
	}

	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerSolidifier(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}
/*
	LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

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
				return BlockEntitySolidifier.this.extractEnergy(directionIn, maxExtract, simulate);
			}
	
			@Override
			public int getEnergyStored() {
				return BlockEntitySolidifier.this.getEnergyStored();
			}
	
			@Override
			public int getMaxEnergyStored() {
				return BlockEntitySolidifier.this.getMaxEnergyStored();
			}
	
			@Override
			public int receiveEnergy(int maxReceive, boolean simulate) {
				return BlockEntitySolidifier.this.receiveEnergy(directionIn, maxReceive, simulate);
			}
	
			@Override
			public boolean canReceive() {
				return BlockEntitySolidifier.this.canReceive(directionIn);
			}
	
			@Override
			public boolean canExtract() {
				return BlockEntitySolidifier.this.canExtract(directionIn);
			}
		};
	}

	@Override
	public void setMaxTransfer(int maxTransfer) {
		this.setMaxReceive(maxTransfer);
	}

	@Override
	public void setMaxReceive(int max_receive) {
		this.energy_max_receive = max_receive;
	}

	@Override
	public int getMaxReceive() {
		return this.energy_max_receive;
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

		this.sendUpdates();
		return storedReceived;
	}

	@Override
	public int extractEnergy(Direction directionIn, int max_extract, boolean simulate) {
		int storedExtracted = Math.min(energy_stored, Math.min(this.rf_tick_rate, max_extract));

		if (!simulate) {
			this.energy_stored -= storedExtracted;
		}

		this.sendUpdates();
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
		BlockState state = this.getLevel().getBlockState(this.getBlockPos());
		
		if (state.getBlock() instanceof BlockSolidifier) {
			Direction facing = state.getValue(BlockSolidifier.FACING);
			Direction dirCheck = this.getLevel().getBlockState(this.getBlockPos()).getValue(BlockSolidifier.FACING).getClockWise();
			
			if (directionIn.equals(Direction.UP) || directionIn.equals(facing)) {
				return false;
			} else if (directionIn.equals(dirCheck) || directionIn.equals(dirCheck.getOpposite())) {
				return false;
			} else {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public int getProcessSpeed() {
		return this.process_speed;
	}

	@Override
	public int getProcessTime(int i) {
		if (i == 0) {
			return this.process_time;
		}
		return -1;
	}
	
	@Override
	public int getProcessProgressScaled(int scale) {
		return this.process_time * scale / this.process_speed;
	}

	@Override
	public void setMaxExtract(int maxExtract) { }

	@Override
	public int getMaxExtract() {
		return 0;
	}

	@Override
	public int getRFTickRate() {
		return this.rf_tick_rate;
	}

	@Override
	public void setRecipeUsed(RecipeHolder<?> recipeIn) {
		if (recipeIn != null) {
			ResourceLocation resourcelocation = recipeIn.id();
			this.recipesUsed.addTo(resourcelocation, 1);
		}
	}

	@Override
	public RecipeHolder<?> getRecipeUsed() {
		return this.getLevel().getRecipeManager().getRecipeFor(this.recipeType, this.getRecipeInput(), this.getLevel()).orElse(null);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, BlockHitResult hit) {
		return null;
	}
	
	public SolidifierRecipeInput getRecipeInput() {
		return new SolidifierRecipeInput(this.getFluidInTank(0));
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
	
	@Override
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
		Direction dirCheck = this.getLevel().getBlockState(this.getBlockPos()).getValue(BlockSolidifier.FACING).getClockWise();
		return fromDirection.equals(dirCheck) || fromDirection.equals(dirCheck.getOpposite());
	}

	public void updateFluidFillLevel() {
		this.sendUpdates();
		if (!this.isFluidEmpty()) {
			if (this.getFluidLevelScaled(9) == 0) {
				this.fluidTank.setFillLevel(1);
			} else {
				this.fluidTank.setFillLevel(Math.clamp(this.getFluidLevelScaled(9), 0, 9));
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
	
	public void updateFluidTankCapacity(int newCapacity) {
		this.fluidTank.getFluidTank().setCapacity(newCapacity);
		if (this.getCurrentFluidAmount() > newCapacity) {
			this.getFluidInTank(0).setAmount(newCapacity);
		}
		this.updateFluidFillLevel();
	}

	@Override
	public int getFluidFillLevel() {
		return this.fluidTank.getFillLevel();
	}
	
	@Override
	public IFluidHandler getFluidCapability(@Nullable Direction directionIn) {
		return new IFluidHandler() {

			@Override
			public int getTanks() {
				if (BlockEntitySolidifier.this.canFluidInteract(directionIn)) {
					return BlockEntitySolidifier.this.getTanks();
				} else {
					return 0;
				}
			}

			@Override
			public FluidStack getFluidInTank(int tank) {
				if (BlockEntitySolidifier.this.canFluidInteract(directionIn)) {
					return BlockEntitySolidifier.this.getFluidInTank(tank);
				} else {
					return FluidStack.EMPTY;
				}
			}

			@Override
			public int getTankCapacity(int tank) {
				if (BlockEntitySolidifier.this.canFluidInteract(directionIn)) {
					return BlockEntitySolidifier.this.getTankCapacity(tank);
				} else {
					return 0;
				}
			}

			@Override
			public boolean isFluidValid(int tank, FluidStack stack) {
				if (BlockEntitySolidifier.this.canFluidInteract(directionIn)) {
					return BlockEntitySolidifier.this.isFluidValid(tank, stack);
				} else {
					return false;
				}
			}

			@Override
			public int fill(FluidStack resource, FluidAction action) {
				if (BlockEntitySolidifier.this.canFluidInteract(directionIn)) {
					return BlockEntitySolidifier.this.fill(resource, action);
				} else {
					return 0;
				}
			}

			@Override
			public FluidStack drain(FluidStack resource, FluidAction action) {
				if (BlockEntitySolidifier.this.canFluidInteract(directionIn)) {
					return BlockEntitySolidifier.this.drain(resource, action);
				} else {
					return FluidStack.EMPTY;
				}
			}

			@Override
			public FluidStack drain(int maxDrain, FluidAction action) {
				if (BlockEntitySolidifier.this.canFluidInteract(directionIn)) {
					return BlockEntitySolidifier.this.drain(maxDrain, action);
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