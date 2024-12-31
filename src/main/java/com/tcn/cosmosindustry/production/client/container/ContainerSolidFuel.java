package com.tcn.cosmosindustry.production.client.container;

import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.production.client.container.slot.SlotSolidFuel;
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
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class ContainerSolidFuel extends CosmosContainerMenuBlockEntity {
	
	public ContainerSolidFuel(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, new SimpleContainer(4), ContainerLevelAccess.NULL, extraData.readBlockPos());
	}

	public ContainerSolidFuel(int indexIn, Inventory playerInventoryIn, Container tile, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(IndustryRegistrationManager.CONTAINER_TYPE_SOLID_FUEL.get(), indexIn, playerInventoryIn, accessIn, posIn);
		
		/** @Input Slot */
		this.addSlot(new SlotSolidFuel(tile, 0, 102, 30));
		
		/** @Upgrade Slots */
		this.addSlot(new SlotUpgrade(tile, 1, 54, 17, IndustryRegistrationManager.UPGRADE_SPEED.get()));
		this.addSlot(new SlotUpgrade(tile, 2, 54, 39, IndustryRegistrationManager.UPGRADE_CAPACITY.get()));
		this.addSlot(new SlotUpgrade(tile, 3, 54, 61, IndustryRegistrationManager.UPGRADE_EFFICIENCY.get()));
		
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
		return stillValid(this.access, playerIn, IndustryRegistrationManager.BLOCK_SOLID_FUEL.get());
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(indexIn);
		boolean flag = slot.mayPickup(playerIn);
		
		if (flag && slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if (indexIn >= 0 && indexIn < 4 ) {
				if (!this.moveItemStackTo(itemstack1, 4, this.slots.size() - 9, false)) {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (indexIn > 4 && indexIn < this.slots.size()) {
				if (itemstack.getItem() instanceof CosmosItemUpgradeEnergy) {
					if (!this.moveItemStackTo(itemstack1, 1, 4, false)) {
						if (indexIn < this.slots.size() - 9) {
							if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
								return ItemStack.EMPTY;
							}
						} else if (!this.moveItemStackTo(itemstack1, 4, this.slots.size() - 9, false)) {
							return ItemStack.EMPTY;
						}
					}
				}

				else if (itemstack.getBurnTime(RecipeType.SMELTING) > 0) {
					if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
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