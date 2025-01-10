package com.tcn.cosmosindustry.processing.client.container;

import com.tcn.cosmosindustry.core.management.IndustryRecipeManager;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.processing.client.container.slot.SlotOrePlant;
import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.cosmoslibrary.client.container.slot.SlotBucket;
import com.tcn.cosmoslibrary.client.container.slot.SlotUpgrade;
import com.tcn.cosmoslibrary.common.item.CosmosItemUpgradeEnergy;
import com.tcn.cosmoslibrary.common.item.CosmosItemUpgradeFluid;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.neoforge.capabilities.Capabilities;

public class ContainerFluidCrafter extends CosmosContainerMenuBlockEntity {
	
	public ContainerFluidCrafter(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, new SimpleContainer(10), ContainerLevelAccess.NULL, extraData.readBlockPos());
	}

	public ContainerFluidCrafter(int indexIn, Inventory playerInventoryIn, Container tile, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(IndustryRegistrationManager.CONTAINER_TYPE_FLUID_CRAFTER.get(), indexIn, playerInventoryIn, accessIn, posIn);

		/** @EnergyUpgrade Slots */
		this.addSlot(new SlotUpgrade(tile, 0, 17, 19, IndustryRegistrationManager.UPGRADE_SPEED.get()));
		this.addSlot(new SlotUpgrade(tile, 1, 17, 39, IndustryRegistrationManager.UPGRADE_CAPACITY.get()));
		this.addSlot(new SlotUpgrade(tile, 2, 17, 59, IndustryRegistrationManager.UPGRADE_EFFICIENCY.get()));
		
		/**@Inputslot*/
		this.addSlot(new Slot(tile, 3, 70, 19));
		
		/**@OutputSlot*/
		this.addSlot(new SlotOrePlant(tile, 4, 60, 59));
		
		/** @Bucket Slots */
		this.addSlot(new SlotBucket(tile, 5, 100, 19));
		this.addSlot(new SlotBucket(tile, 6, 100, 59) {
			@Override
			public boolean mayPlace(ItemStack stackIn) {
				return false;
			}
		});

		/** @FluidUpgrade Slots */
		this.addSlot(new SlotUpgrade(tile, 7, 143, 19, IndustryRegistrationManager.UPGRADE_FLUID_USAGE.get()));
		this.addSlot(new SlotUpgrade(tile, 8, 143, 39, IndustryRegistrationManager.UPGRADE_FLUID_CAPACITY.get()));
		this.addSlot(new SlotUpgrade(tile, 9, 143, 59, IndustryRegistrationManager.UPGRADE_FLUID_EFFICIENCY.get()));
		
		/**@Inventory*/
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlot(new Slot(playerInventoryIn, 9 + x + y * 9, 8 + x * 18, 95 + y * 18));
			}
		}
		
		/**@Actionbar*/
		for (int x = 0; x < 9; x++) {
			this.addSlot(new Slot(playerInventoryIn, x, 8 + x * 18, 153));
		}
	}

	@Override
	public void addSlotListener(ContainerListener listenerIn) {
		super.addSlotListener(listenerIn);
	}

	@Override
	public void removeSlotListener(ContainerListener listenerIn) {
		super.removeSlotListener(listenerIn);
	}

	@Override
	public void slotsChanged(Container inventoryIn) {
		super.slotsChanged(inventoryIn);
		this.broadcastChanges();
	}
	
	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return stillValid(this.access, playerIn, IndustryRegistrationManager.BLOCK_FLUID_CRAFTER.get());
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(indexIn);
		boolean flag = slot.mayPickup(playerIn);
		
		if (flag && slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if (indexIn >= 0 && indexIn < 10 ) {
				if (!this.moveItemStackTo(itemstack1, 10, this.slots.size() - 9, false)) {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (indexIn > 9 && indexIn < this.slots.size()) {
				if (itemstack.getItem() instanceof CosmosItemUpgradeEnergy) {
					if (!this.moveItemStackTo(itemstack1, 0, 2, false)) {
						if (indexIn < this.slots.size() - 9) {
							if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
								return ItemStack.EMPTY;
							}
						} else if (!this.moveItemStackTo(itemstack1, 10, this.slots.size() - 9, false)) {
							return ItemStack.EMPTY;
						}
					}
				}
				
				if (itemstack.getItem() instanceof CosmosItemUpgradeFluid) {
					if (!this.moveItemStackTo(itemstack1, 7, 10, false)) {
						if (indexIn < this.slots.size() - 9) {
							if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
								return ItemStack.EMPTY;
							}
						} else if (!this.moveItemStackTo(itemstack1, 10, this.slots.size() - 9, false)) {
							return ItemStack.EMPTY;
						}
					}
				}

				else if (this.getLevel().getRecipeManager().getRecipeFor(IndustryRecipeManager.RECIPE_TYPE_FLUID_CRAFTER.get(), new SingleRecipeInput(itemstack), getLevel()).isPresent()) {
					if (!this.moveItemStackTo(itemstack1, 3, 4, false)) {
						return ItemStack.EMPTY;
					}
				}
				
				else if (itemstack.getCapability(Capabilities.FluidHandler.ITEM) != null) {
					if (!this.moveItemStackTo(itemstack1, 5, 6, false)) {
						return ItemStack.EMPTY;
					}
				}
				
				else if (indexIn < this.slots.size() - 9) {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
						return ItemStack.EMPTY;
					}
				} else {
					if (!this.moveItemStackTo(itemstack1, 4, this.slots.size() - 9, false)) {
						return ItemStack.EMPTY;
					}
				}
			}
			
			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, itemstack1);
			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		return itemstack != null ? itemstack : ItemStack.EMPTY;
	}
}