package com.tcn.cosmosindustry.processing.client.container;

import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.cosmoslibrary.client.container.slot.SlotUpgrade;
import com.tcn.cosmoslibrary.common.item.CosmosItemUpgradeEnergy;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public class ContainerKiln extends CosmosContainerMenuBlockEntity {

	public ContainerKiln(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, new SimpleContainer(5), ContainerLevelAccess.NULL, extraData.readBlockPos());
	}
	
	public ContainerKiln(int indexIn, Inventory playerInventoryIn, Container contentsIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(IndustryRegistrationManager.CONTAINER_TYPE_KILN.get(), indexIn, playerInventoryIn, accessIn, posIn);
		
		/**@Inputslot*/
		this.addSlot(new Slot(contentsIn, 0, 78, 39));

		/**@Powerslot*/
		//this.addSlot(new SlotEnergyItem(contentsIn, 1, 77, 61));

		/**@OutputSlot*/
		this.addSlot(new FurnaceResultSlot(player, contentsIn, 1, 124, 39));

		/**@Upgradeslots*/
		this.addSlot(new SlotUpgrade(contentsIn, 2, 32, 17, IndustryRegistrationManager.UPGRADE_SPEED.get()));
		this.addSlot(new SlotUpgrade(contentsIn, 3, 32, 39, IndustryRegistrationManager.UPGRADE_CAPACITY.get()));
		this.addSlot(new SlotUpgrade(contentsIn, 4, 32, 61, IndustryRegistrationManager.UPGRADE_EFFICIENCY.get()));
		
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
		return stillValid(this.access, playerIn, IndustryRegistrationManager.BLOCK_KILN.get());
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(indexIn);
		boolean flag = slot.mayPickup(playerIn);
		
		if (flag && slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if (indexIn >= 0 && indexIn < 6 ) {
				if (!this.moveItemStackTo(itemstack1, 5, this.slots.size() - 9, false)) {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (indexIn >= 6 && indexIn < this.slots.size()) {
				if (itemstack.getItem() instanceof CosmosItemUpgradeEnergy) {
					if (!this.moveItemStackTo(itemstack1, 2, 6, false)) {
						if (indexIn < this.slots.size() - 9) {
							if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
								return ItemStack.EMPTY;
							}
						} else if (!this.moveItemStackTo(itemstack1, 5, this.slots.size() - 9, false)) {
							return ItemStack.EMPTY;
						}
					}
				}
				
				else if (this.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(itemstack), getLevel()).isPresent()) {
					if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				}
				
				else if (indexIn < this.slots.size() - 9) {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
						return ItemStack.EMPTY;
					}
				} else {
					if (!this.moveItemStackTo(itemstack1, 6, this.slots.size() - 9, false)) {
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