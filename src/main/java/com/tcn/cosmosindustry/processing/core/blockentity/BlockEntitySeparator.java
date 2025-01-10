package com.tcn.cosmosindustry.processing.core.blockentity;

import java.util.Random;

import javax.annotation.Nullable;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRecipeManager;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.core.recipe.SeparatorRecipe;
import com.tcn.cosmosindustry.processing.client.container.ContainerSeparator;
import com.tcn.cosmosindustry.processing.core.block.BlockSeparator;
import com.tcn.cosmoslibrary.client.interfaces.IBEUpdated.ProcessingRecipe;
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

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class BlockEntitySeparator extends BlockEntity implements IBlockInteract, IBlockNotifier, WorldlyContainer, MenuProvider, ProcessingRecipe, IEnergyEntity, RecipeCraftingHolder, IBEUIMode, IEnergyCapBE {

	private static final int[] SLOTS_TOP = new int[] { 0 };
	private static final int[] SLOTS_BOTTOM = new int[] { 2, 1 };
	private static final int[] SLOTS_SIDES = new int[] { 1 };
	
	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(6, ItemStack.EMPTY);

	private int update = 0;
	private int process_time;
	private int process_speed = IndustryReference.Resource.Processing.SPEED_RATE[0];
	
	private int energy_stored = 0;
	private int energy_capacity = IndustryReference.Resource.Processing.CAPACITY[0];
	private int energy_max_receive = IndustryReference.Resource.Processing.MAX_INPUT[0];
	private int rf_tick_rate = IndustryReference.Resource.Processing.RF_TICK_RATE[0];

	private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
	protected final RecipeType<SeparatorRecipe> recipeType;
	
	private EnumUIMode uiMode = EnumUIMode.DARK;
	
	public BlockEntitySeparator(BlockPos posIn, BlockState stateIn) {
		super(IndustryRegistrationManager.BLOCK_ENTITY_TYPE_SEPARATOR.get(), posIn, stateIn);

		this.recipeType = IndustryRecipeManager.RECIPE_TYPE_SEPARATING.get();
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
		
		compound.putInt("process_time", this.process_time);
		compound.putInt("process_speed", this.process_speed);

		compound.putInt("energy", this.energy_stored);
		compound.putInt("rf_rate", this.rf_tick_rate);

		CompoundTag compoundnbt = new CompoundTag();
		this.recipesUsed.forEach((location, inte) -> { 
			compoundnbt.putInt(location.toString(), inte); 
		});
		compound.put("RecipesUsed", compoundnbt);

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
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntitySeparator entityIn) {
		if (!entityIn.getItem(0).isEmpty()) {
			RecipeHolder<?> recipe = entityIn.getRecipeUsed();
			
			HolderLookup.Provider provider = levelIn.registryAccess();
			
			if (entityIn.canProcess(recipe, provider) && entityIn.hasEnergy()) {
				entityIn.extractEnergy(Direction.DOWN, entityIn.rf_tick_rate, false);
				
				entityIn.process_time++;
				entityIn.setChanged();
				
				if (entityIn.process_time == entityIn.process_speed) {
					entityIn.process_time = 0;
					if (!levelIn.isClientSide) {
						entityIn.processItem(recipe, provider);
					}
				}
			} else {
				entityIn.process_time = 0;
			}
			
			if (entityIn.canProcess(recipe, provider) && entityIn.hasEnergy()) {
				BlockPos pos = entityIn.getBlockPos();
				Random rand = new Random();
				
				if (rand.nextDouble() < 0.1D) {
					//entityIn.world.playSound(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, SoundHandler.MACHINE.COMPRESSOR, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				}

				if (rand.nextDouble() < 0.6) {
					levelIn.addParticle(ParticleTypes.DUST_PLUME, pos.getX() + 0.3, pos.getY() + 0.4, pos.getZ() + 0.3, 0.0F, 0.0F, 0.0F);
					levelIn.addParticle(ParticleTypes.DUST_PLUME, pos.getX() + 0.3, pos.getY() + 0.4, pos.getZ() + 0.7, 0.0F, 0.0F, 0.0F);
					levelIn.addParticle(ParticleTypes.DUST_PLUME, pos.getX() + 0.7, pos.getY() + 0.4, pos.getZ() + 0.3, 0.0F, 0.0F, 0.0F);
					levelIn.addParticle(ParticleTypes.DUST_PLUME, pos.getX() + 0.7, pos.getY() + 0.4, pos.getZ() + 0.7, 0.0F, 0.0F, 0.0F);
				}
			}
		} else {
			if (entityIn.process_time > 0) {
				entityIn.process_time = 0;
			}
		}
		
		int i = entityIn.getItem(3).getCount();
		entityIn.process_speed = IndustryReference.Resource.Processing.SPEED_RATE[i];
		
		int j = entityIn.getItem(4).getCount();
		entityIn.energy_capacity = IndustryReference.Resource.Processing.CAPACITY[j];

		int k = entityIn.getItem(5).getCount();
		entityIn.rf_tick_rate = IndustryReference.Resource.Processing.RF_TICK_RATE[i] - IndustryReference.Resource.Processing.RF_EFF_RATE[k];

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

	@Override
	public boolean isProcessing() {
		return this.hasEnergy() && this.canProcess(this.getRecipeUsed(), this.getLevel().registryAccess()) && this.process_time > 0;
	}
	
	@Override
	public RecipeHolder<?> getRecipeUsed() {
		return this.getLevel().getRecipeManager().getRecipeFor(this.recipeType, new SingleRecipeInput(this.getItem(0)), this.getLevel()).orElse(null);
	}
	
	@Override
	public boolean canProcess(@Nullable RecipeHolder<?> recipeIn, HolderLookup.Provider provider) {
		if (!this.getItem(0).isEmpty() && recipeIn != null) {
			ItemStack resultItem = recipeIn.value().getResultItem(provider);
			ItemStack secondaryResultItem = ((SeparatorRecipe) recipeIn.value()).getSecondaryResultItem();
			
			if (resultItem.isEmpty()) {
				return false;
			} else {
				ItemStack output = this.getItem(1);
				ItemStack second = this.getItem(2);
				
				int resultCount = 0;
				int secondaryCount = 0;
				int secondaryMaxCount = 0;
				
				if (!output.isEmpty()) {
					resultCount = output.getCount() + resultItem.getCount();
				}
				
				if (!secondaryResultItem.isEmpty() && !second.isEmpty()) {
					secondaryCount = second.getCount() + secondaryResultItem.getCount();
					secondaryMaxCount = secondaryResultItem.getMaxStackSize();
				}
				
				if (!output.isEmpty() && output.getItem() != resultItem.getItem()) {
					return false;
				}
				
				if (!second.isEmpty() && second.getItem() != secondaryResultItem.getItem()) {
					return false;
				}
				
				return resultCount <= resultItem.getMaxStackSize() && secondaryCount <= secondaryMaxCount;
			}
		} else {
			return false;
		}
	}

	@Override
	public void processItem(@Nullable RecipeHolder<?> recipeIn, HolderLookup.Provider provider) {
		if (this.canProcess(recipeIn, provider) && recipeIn != null) {
			ItemStack input = this.getItem(0);
			ItemStack output = this.getItem(1);
			ItemStack second = this.getItem(2);

			ItemStack resultItem = recipeIn.value().getResultItem(provider);
			ItemStack secondaryResultItem = ((SeparatorRecipe) recipeIn.value()).getSecondaryResultItem();

			if (output.isEmpty()) {
				this.inventoryItems.set(1, resultItem.copy());
			} else if (output.getItem() == resultItem.getItem()) {
				output.grow(resultItem.getCount());
			} if (second.isEmpty()) {
				this.inventoryItems.set(2, secondaryResultItem.copy());
			} else if (second.getItem().equals(secondaryResultItem.getItem())) {
				second.grow(secondaryResultItem.getCount());
			}
			
			input.shrink(1);
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
		return ComponentHelper.title("cosmosindustry.gui.separator");
	}

	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerSeparator(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
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
                return BlockEntitySeparator.this.extractEnergy(directionIn, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return BlockEntitySeparator.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return BlockEntitySeparator.this.getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return BlockEntitySeparator.this.receiveEnergy(directionIn, maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return BlockEntitySeparator.this.canReceive(directionIn);
            }

            @Override
            public boolean canExtract() {
                return BlockEntitySeparator.this.canExtract(directionIn);
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
		BlockState state = this.level.getBlockState(this.getBlockPos());
		
		if (state.getBlock() instanceof BlockSeparator) {
			Direction facing = state.getValue(BlockSeparator.FACING);

			if (directionIn.equals(Direction.UP) || directionIn.equals(facing)) {
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