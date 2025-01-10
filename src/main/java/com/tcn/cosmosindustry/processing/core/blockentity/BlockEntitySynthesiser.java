package com.tcn.cosmosindustry.processing.core.blockentity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRecipeManager;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.core.recipe.SynthesiserRecipe;
import com.tcn.cosmosindustry.core.recipe.SynthesiserRecipeInput;
import com.tcn.cosmosindustry.processing.client.container.ContainerSynthesiser;
import com.tcn.cosmosindustry.processing.core.block.BlockSynthesiserStand;
import com.tcn.cosmoslibrary.client.interfaces.IBEUpdated.Processing;
import com.tcn.cosmoslibrary.common.capability.IEnergyCapBE;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

//@SuppressWarnings("unused")
public class BlockEntitySynthesiser extends BlockEntity implements IBlockInteract, IBlockNotifier, WorldlyContainer, MenuProvider, Processing, IEnergyEntity, RecipeCraftingHolder, IBEUIMode, IEnergyCapBE {

	private static final int[] SLOTS_BOTTOM = new int[] { 0 };
	
	private NonNullList<ItemStack> inventoryItems = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);

//	private int update = 0;
	private int sound_timer = 0;
	private int process_time;
	private int process_speed = IndustryReference.Resource.Processing.SPEED_RATE[0];
	
	private int energy_stored = 0;
	private int energy_capacity = IndustryReference.Resource.Processing.CAPACITY_U[0];
	private int energy_max_receive = IndustryReference.Resource.Processing.MAX_INPUT_U[0];
	private int energy_max_extract = IndustryReference.Resource.Processing.MAX_INPUT_U[0];
	private int rf_tick_rate = IndustryReference.Resource.Processing.RF_TICK_RATE_U[4];
	
	private boolean hasCompleted;

	private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
	protected final RecipeType<SynthesiserRecipe> recipeType;
	
	private EnumUIMode uiMode = EnumUIMode.DARK;
	
	public BlockEntitySynthesiser(BlockPos posIn, BlockState stateIn) {
		super(IndustryRegistrationManager.BLOCK_ENTITY_TYPE_SYNTHESISER.get(), posIn, stateIn);

		this.recipeType = IndustryRecipeManager.RECIPE_TYPE_SYNTHESISING.get();
	}

	public void sendUpdates() {
		if (this.getLevel() != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
//			BlockSynthesiser block = (BlockSynthesiser) state.getBlock();
			
			this.getLevel().sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (!this.getLevel().isClientSide()) {
				this.getLevel().setBlockAndUpdate(this.getBlockPos(), state.updateShape(Direction.DOWN, state, this.getLevel(), this.getBlockPos(), this.getBlockPos()));
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
		
		compound.putBoolean("completed", this.hasCompleted);

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
		
		this.hasCompleted = compound.getBoolean("completed");

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
	}

	@Override
	public void onLoad() { }
	
	public static void tick(Level levelIn, BlockPos posIn, BlockState stateIn, BlockEntitySynthesiser entityIn) {
		if (!entityIn.getItem(0).isEmpty()) {
			RecipeHolder<?> recipe = entityIn.getRecipeUsed();
			
			HolderLookup.Provider provider = levelIn.registryAccess();
			
			if (entityIn.canProcessEightWay(recipe, provider) && entityIn.hasEnergy()) {
				entityIn.sound_timer++;
				
				if (entityIn.sound_timer > 13) {
//					levelIn.playLocalSound(posIn.getX(), posIn.getY(), posIn.getZ(), ModSoundManager.MACHINE_LASERHUM.get(), SoundSource.BLOCKS, 1F, 1F, false);
					
					entityIn.sound_timer = 0;
				}
				
				entityIn.extractEnergy(Direction.DOWN, entityIn.rf_tick_rate, false);
				entityIn.process_time++;
				entityIn.setChanged();
				
				if (entityIn.process_time == entityIn.getProcessTimeEightWay(recipe, provider)) {
					entityIn.process_time = 0;
					entityIn.hasCompleted = true;
					entityIn.processEightWay(recipe, provider);
					levelIn.addParticle(ParticleTypes.EXPLOSION, posIn.getX() + 0.5, posIn.getY() + 1.5D, posIn.getZ() + 0.5, 1.0D, 1.0D, 1.0D);
				}
				
			} else if (entityIn.canProcessFourWay(recipe, provider) && entityIn.hasEnergy()) {
				entityIn.sound_timer++;
				
				if (entityIn.sound_timer > 13) {
//					levelIn.playLocalSound(posIn.getX(), posIn.getY(), posIn.getZ(), ModSoundManager.MACHINE_LASERHUM.get(), SoundSource.BLOCKS, 1F, 1F, false);
					
					entityIn.sound_timer = 0;
				}

				entityIn.extractEnergy(Direction.DOWN, entityIn.rf_tick_rate, false);
				entityIn.process_time++;
				entityIn.setChanged();
				
				if (entityIn.process_time == entityIn.getProcessTimeFourWay(recipe, provider)) {
					entityIn.process_time = 0;
					entityIn.hasCompleted = true;
					entityIn.processFourWay(recipe, provider);
					levelIn.addParticle(ParticleTypes.EXPLOSION, posIn.getX() + 0.5, posIn.getY() + 1.5D, posIn.getZ() + 0.5, 1.0D, 1.0D, 1.0D);
				}
			} else {
				entityIn.sound_timer = 0;
				entityIn.process_time = 0;
			}
		} else {
			if (entityIn.process_time > 0) {
				entityIn.process_time = 0;
			}
			
			if (entityIn.hasCompleted) {
				entityIn.hasCompleted = false;
			}
		}
	}

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) { }

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand handIn, BlockHitResult hit) {
		if (!playerIn.isShiftKeyDown()) {
			if ((!CosmosUtil.handEmpty(playerIn)) && this.getItem(0).getCount() < 1) {
				ItemStack stack = playerIn.getItemInHand(handIn);
				
				ItemStack copyStack = stack.copy();
				copyStack.setCount(1);
				
				this.setItem(0, copyStack);
				stack.shrink(1);
				worldIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.6F, 2F, false);
				
				return ItemInteractionResult.SUCCESS;
			} else if (this.getItem(0).getCount() > 0) {
				playerIn.addItem(this.getItem(0));
				worldIn.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.6F, 2F, false);
				
				return ItemInteractionResult.SUCCESS;
			}
		} else if (!CosmosUtil.holdingWrench(playerIn)) {
			if (playerIn instanceof ServerPlayer serverPlayer) {
				serverPlayer.openMenu(this, (packetBuffer) -> { packetBuffer.writeBlockPos( this.getBlockPos()); });
			}
			return ItemInteractionResult.SUCCESS;
		}
		return ItemInteractionResult.FAIL;
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
		return this.hasEnergy() && this.process_time > 0;
	}
	
	public boolean canProcessFourWay(@Nullable RecipeHolder<?> recipeIn, HolderLookup.Provider provider) {
		if (this.isSetupFourWay() && recipeIn != null) {
			SynthesiserRecipe synRecipe = (SynthesiserRecipe) recipeIn.value();
			
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesFourWay();
			
			if (!tiles.isEmpty()) {
				if (synRecipe.matches(this.getRecipeInput(), this.getLevel())) {
					return !this.getItem(0).isEmpty();
				}
				return false;
			}	
		}
		return false;
	}
	
	public boolean canProcessEightWay(@Nullable RecipeHolder<?> recipeIn, HolderLookup.Provider provider) {
		if (this.isSetupEightWay() && recipeIn != null) {
			SynthesiserRecipe synRecipe = (SynthesiserRecipe) recipeIn.value();
			
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesEightWay();
			
			if (!tiles.isEmpty()) {				
				if (synRecipe.matches(this.getRecipeInput(), this.getLevel())) {
					return !this.getItem(0).isEmpty();
				}
				return false;
			}	
		}
		return false;
	}
	
	public void processFourWay(@Nullable RecipeHolder<?> recipeIn, HolderLookup.Provider provider) {
		if (this.isSetupFourWay() && this.canProcessFourWay(recipeIn, provider)) {
			SynthesiserRecipe synRecipe = (SynthesiserRecipe) recipeIn.value();
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesFourWay();
			
			if (!(tiles.isEmpty())) {
				ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
				
				for (int x = 0; x < tiles.size(); x++) {
					if (tiles.get(x) instanceof BlockEntitySynthesiserStand) {
						stacks.add(((BlockEntitySynthesiserStand) tiles.get(x)).getItem(0));
					}
				}
				
				if (synRecipe.matches(this.getRecipeInput(), this.getLevel())) {
					this.inventoryItems.set(0, synRecipe.getResultItem(provider).copy());
					this.sendUpdates();
					
					for (int i = 0; i < stacks.size(); i++) {
						stacks.get(i).shrink(1);
					}
				}
			}
		}
		
		this.sendUpdates();
	}
	
	public void processEightWay(@Nullable RecipeHolder<?> recipeIn, HolderLookup.Provider provider) {
		if (this.isSetupEightWay() && this.canProcessEightWay(recipeIn, provider)) {
			SynthesiserRecipe synRecipe = (SynthesiserRecipe) recipeIn.value();
			
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesEightWay();
			
			if (!(tiles.isEmpty())) {
				ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
				
				for (int x = 0; x < tiles.size(); x++) {
					if (tiles.get(x) instanceof BlockEntitySynthesiserStand) {
						stacks.add(((BlockEntitySynthesiserStand) tiles.get(x)).getItem(0));
					}
				}
				
				if (synRecipe.matches(this.getRecipeInput(), this.getLevel())) {
					this.inventoryItems.set(0, synRecipe.getResultItem(provider).copy());
					this.sendUpdates();

					for (int i = 0; i < stacks.size(); i++) {
						stacks.get(i).shrink(1);
					}
				}
			}
		}
		
		this.sendUpdates();
	}
	
	public Integer getProcessTimeFourWay(@Nullable RecipeHolder<?> recipeIn, HolderLookup.Provider provider) {
		if (this.isSetupFourWay() && recipeIn != null) {
			SynthesiserRecipe synRecipe = (SynthesiserRecipe) recipeIn.value();
			
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesFourWay();
			
			if (!tiles.isEmpty()) {
				if (synRecipe.matches(this.getRecipeInput(), this.getLevel())) {
					if (!this.getItem(0).isEmpty()) {
						return synRecipe.getProcessTime();
					}
				}
				return 0;
			}
		}
		return 0;
	}
	
	public Integer getProcessTimeEightWay(@Nullable RecipeHolder<?> recipeIn, HolderLookup.Provider provider) {
		if (this.isSetupEightWay() && recipeIn != null) {
			SynthesiserRecipe synRecipe = (SynthesiserRecipe) recipeIn.value();
			
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesEightWay();
			
			if (!tiles.isEmpty()) {				
				if (synRecipe.matches(this.getRecipeInput(), this.getLevel())) {
					if (!this.getItem(0).isEmpty()) {
						return synRecipe.getProcessTime();
					}
				}
				return 0;
			}
		}
		return 0;
	}
	
	public boolean isSetupFourWay() {
		ArrayList<BlockEntity> tiles = this.getBlockEntitiesFourWay();
		ArrayList<Block> blocks = this.getBlocksFourWay();
		
		if (tiles.get(0) instanceof BlockEntitySynthesiserStand && tiles.get(1) instanceof BlockEntitySynthesiserStand &&
				tiles.get(2) instanceof BlockEntitySynthesiserStand && tiles.get(3) instanceof BlockEntitySynthesiserStand) {
			if (blocks.get(0) instanceof BlockSynthesiserStand && blocks.get(1) instanceof BlockSynthesiserStand &&
					blocks.get(2) instanceof BlockSynthesiserStand && blocks.get(3) instanceof BlockSynthesiserStand) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public boolean isSetupEightWay() {
		ArrayList<BlockEntity> tiles = this.getBlockEntitiesEightWay();
		ArrayList<Block> blocks = this.getBlocksEightWay();
		
		if (tiles.get(0) instanceof BlockEntitySynthesiserStand && tiles.get(1) instanceof BlockEntitySynthesiserStand &&
				tiles.get(2) instanceof BlockEntitySynthesiserStand && tiles.get(3) instanceof BlockEntitySynthesiserStand &&
					tiles.get(4) instanceof BlockEntitySynthesiserStand && tiles.get(5) instanceof BlockEntitySynthesiserStand &&
						tiles.get(6) instanceof BlockEntitySynthesiserStand && tiles.get(7) instanceof BlockEntitySynthesiserStand) {
			if (blocks.get(0) instanceof BlockSynthesiserStand && blocks.get(1) instanceof BlockSynthesiserStand &&
					blocks.get(2) instanceof BlockSynthesiserStand && blocks.get(3) instanceof BlockSynthesiserStand && 
						blocks.get(4) instanceof BlockSynthesiserStand && blocks.get(5) instanceof BlockSynthesiserStand &&
							blocks.get(6) instanceof BlockSynthesiserStand && blocks.get(7) instanceof BlockSynthesiserStand) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public ArrayList<BlockEntity> getBlockEntitiesFourWay() {
		ArrayList<BlockEntity> tiles = new ArrayList<BlockEntity>();
		
		for (Direction c : Direction.values()) {
			if (c != Direction.UP && c != Direction.DOWN) {
				tiles.add(this.level.getBlockEntity(this.getBlockPos().relative(c, 3)));
			}
		}
		return tiles;
	}

	public ArrayList<BlockEntitySynthesiserStand> getBlockEntitiesFourWayItem() {
		ArrayList<BlockEntitySynthesiserStand> tiles = new ArrayList<BlockEntitySynthesiserStand>();
		
		for (Direction c : Direction.values()) {
			if (c != Direction.UP && c != Direction.DOWN) {
				BlockEntity first = this.level.getBlockEntity(this.getBlockPos().relative(c, 3));

				if (first instanceof BlockEntitySynthesiserStand stand) {
					if (!stand.getItem(0).isEmpty()) {
						tiles.add(stand);
					}
				}
			}
		}
		return tiles;
	}
	
	public ArrayList<Block> getBlocksFourWay() {
		ArrayList<Block> blocks = new ArrayList<Block>();
		
		for (Direction c : Direction.values()) {
			if (c != Direction.UP && c != Direction.DOWN) {
				blocks.add(this.level.getBlockState(this.getBlockPos().relative(c, 3)).getBlock());
			}
		}
		return blocks;
	}
	
	public ArrayList<BlockEntity> getBlockEntitiesEightWay() {
		ArrayList<BlockEntity> tiles = new ArrayList<BlockEntity>();
		
		for (Direction c : Direction.values()) {
			if (c != Direction.UP && c != Direction.DOWN) {
				tiles.add(this.level.getBlockEntity(this.getBlockPos().relative(c, 3)));
				
				if (c.equals(Direction.NORTH) || c.equals(Direction.SOUTH)) {
					tiles.add(this.level.getBlockEntity(this.getBlockPos().relative(c, 2).relative(Direction.WEST, 2)));
					tiles.add(this.level.getBlockEntity(this.getBlockPos().relative(c, 2).relative(Direction.EAST, 2)));
				}
			}
		}
		return tiles;
	}

	public ArrayList<BlockEntitySynthesiserStand> getBlockEntitiesEightWayItem() {
		ArrayList<BlockEntitySynthesiserStand> tiles = new ArrayList<BlockEntitySynthesiserStand>();
		
		for (Direction c : Direction.values()) {
			if (c != Direction.UP && c != Direction.DOWN) {
				BlockEntity initial = this.level.getBlockEntity(this.getBlockPos().relative(c, 3));

				if (initial instanceof BlockEntitySynthesiserStand stand) {
					if (!stand.getItem(0).isEmpty()) {
						tiles.add(stand);
					}
				}
				
				if (c.equals(Direction.NORTH) || c.equals(Direction.SOUTH)) {
					BlockEntity first = this.level.getBlockEntity(this.getBlockPos().relative(c, 2).relative(Direction.WEST, 2));
					BlockEntity second = this.level.getBlockEntity(this.getBlockPos().relative(c, 2).relative(Direction.EAST, 2));
					
					if (first instanceof BlockEntitySynthesiserStand stand) {
						if (!stand.getItem(0).isEmpty()) {
							tiles.add(stand);
						}
					}

					if (second instanceof BlockEntitySynthesiserStand stand) {
						if (!stand.getItem(0).isEmpty()) {
							tiles.add(stand);
						}
					}
				}
			}
		}
		return tiles;
	}
	
	public ArrayList<Block> getBlocksEightWay() {
		ArrayList<Block> blocks = new ArrayList<Block>();
		
		for (Direction c : Direction.values()) {
			if (c != Direction.UP && c != Direction.DOWN) {
				blocks.add(this.level.getBlockState(this.getBlockPos().relative(c, 3)).getBlock());
				
				if (c.equals(Direction.NORTH) || c.equals(Direction.SOUTH)) {
					blocks.add(this.level.getBlockState(this.getBlockPos().relative(c, 2).relative(Direction.WEST, 2)).getBlock());
					blocks.add(this.level.getBlockState(this.getBlockPos().relative(c, 2).relative(Direction.EAST, 2)).getBlock());
				}
			}
		}
		return blocks;
	}
	
	public ComponentColour getColour(@Nullable RecipeHolder<?> recipeIn, HolderLookup.Provider provider) {
		if (recipeIn != null) {
			SynthesiserRecipe synRecipe = (SynthesiserRecipe) recipeIn.value();
			
			if (this.isSetupEightWay() && recipeIn != null) {
				ArrayList<BlockEntity> tiles = this.getBlockEntitiesEightWay();
				
				if (!tiles.isEmpty()) {					
					if (synRecipe.matches(this.getRecipeInput(), this.getLevel())) {
						if (!this.getItem(0).isEmpty()) {
							return synRecipe.getRecipeColour();
						}
					}
					return ComponentColour.WHITE;
				}
			} else if (this.isSetupFourWay()) {
				ArrayList<BlockEntity> tiles = this.getBlockEntitiesFourWay();
				
				if (!tiles.isEmpty()) {
					if (synRecipe.matches(this.getRecipeInput(), this.getLevel())) {
						if (!this.getItem(0).isEmpty()) {
							return synRecipe.getRecipeColour();
						}
					}
					return ComponentColour.WHITE;
				}
			}
		}
		return ComponentColour.WHITE;
	}

	public SynthesiserRecipeInput getRecipeInput() {
		if (this.isSetupEightWay()) {
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesEightWay();
			
			if (!tiles.isEmpty()) {
				ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
				List<ItemStack> list = Lists.newArrayList();
				
				for (int x = 0; x < tiles.size(); x++) {
					if (tiles.get(x) instanceof BlockEntitySynthesiserStand) {
						stacks.add(((BlockEntitySynthesiserStand) tiles.get(x)).getItem(0));
					}
				}
				
				for (int j = 0; j < stacks.size(); j++) {
					if (!(stacks.get(j).isEmpty())) {
						list.add(stacks.get(j));
					}
				}

				return new SynthesiserRecipeInput(this.getItem(0), list);
			}
		} else if (this.isSetupFourWay()) {
			ArrayList<BlockEntity> tiles = this.getBlockEntitiesFourWay();
			
			if (!tiles.isEmpty()) {
				ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
				List<ItemStack> list = Lists.newArrayList();
				
				for (int x = 0; x < tiles.size(); x++) {
					if (tiles.get(x) instanceof BlockEntitySynthesiserStand) {
						stacks.add(((BlockEntitySynthesiserStand) tiles.get(x)).getItem(0));
					}
				}
				
				for (int j = 0; j < stacks.size(); j++) {
					if (!(stacks.get(j).isEmpty())) {
						list.add(stacks.get(j));
					}
				}
				
				return new SynthesiserRecipeInput(this.getItem(0), list);
			}
		} else {
			return new SynthesiserRecipeInput(ItemStack.EMPTY);
		}
		return new SynthesiserRecipeInput(ItemStack.EMPTY);
	}
	
	@Override
	public void clearContent() { }

	@Override
	public boolean canPlaceItemThroughFace(int indexIn, ItemStack stackIn, Direction directionIn) {
		if (!directionIn.equals(Direction.UP) && this.getItem(0).isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int indexIn, ItemStack stackIn, Direction directionIn) {
		ItemStack stack = this.getItem(0);
		
		if (!directionIn.equals(Direction.UP)) {
			if (this.hasCompleted) {
				return true;
			}
		}
		
		if (stack.isEmpty()) {
			this.hasCompleted = false;
		}
		
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
		this.sendUpdates();
		return ContainerHelper.removeItem(this.inventoryItems, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		this.sendUpdates();
		return ContainerHelper.takeItem(this.inventoryItems, index);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
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
		return side == Direction.DOWN ? SLOTS_BOTTOM : new int[] { 0 };
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.style(ComponentColour.ORANGE, "", "cosmosindustry.gui.synthesiser");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerSynthesiser(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
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
                return BlockEntitySynthesiser.this.extractEnergy(directionIn, maxExtract, simulate);
            }

            @Override
            public int getEnergyStored() {
                return BlockEntitySynthesiser.this.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return BlockEntitySynthesiser.this.getMaxEnergyStored();
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return BlockEntitySynthesiser.this.receiveEnergy(directionIn, maxReceive, simulate);
            }

            @Override
            public boolean canReceive() {
                return BlockEntitySynthesiser.this.canReceive(directionIn);
            }

            @Override
            public boolean canExtract() {
                return BlockEntitySynthesiser.this.canExtract(directionIn);
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
		
		this.sendUpdates();
		return storedReceived;
	}

	@Override
	public int extractEnergy(Direction directionIn, int max_extract, boolean simulate) {
		int storedExtracted = Math.min(energy_stored, Math.min(this.energy_max_extract, max_extract));

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
		return !directionIn.equals(Direction.UP);
	}

	@Override
	public int getProcessSpeed() {
		return 0;
	}

	@Override
	public int getProcessTime(int i) {
		return 0;
	}

	@Override
	public int getProcessProgressScaled(int scale) {
		return 0;
	}

	@Override
	public boolean canProcess() {
		return false;
	}

	@Override
	public void processItem() { }

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
	
	public boolean getStateForConnection(Direction facing) {
		BlockPos facingPos = this.getBlockPos().offset(facing.getNormal());
		BlockEntity blockEntity = this.getLevel().getBlockEntity(facingPos);		
		
		if (blockEntity != null && !blockEntity.isRemoved()) {
			
			Object object = this.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, facingPos, facing);
			if (object != null) {
				if (object instanceof IEnergyStorage storage) {
					if (facing != Direction.DOWN && facing != Direction.UP) {
						return true;
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