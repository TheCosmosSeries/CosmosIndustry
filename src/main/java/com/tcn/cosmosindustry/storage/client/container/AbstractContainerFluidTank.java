package com.tcn.cosmosindustry.storage.client.container;

import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;
import com.tcn.cosmoslibrary.client.container.slot.SlotBucket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

abstract public class AbstractContainerFluidTank extends CosmosContainerMenuBlockEntity {

	public AbstractContainerFluidTank(MenuType<?> typeIn, int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(typeIn, indexIn, playerInventoryIn, new SimpleContainer(2), ContainerLevelAccess.NULL, extraData.readBlockPos());
	}
	public AbstractContainerFluidTank(MenuType<?> typeIn, int indexIn, Inventory playerInventoryIn, Container contentsIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(typeIn, indexIn, playerInventoryIn, accessIn, posIn);
		
		/** @Drain Item */
		this.addSlot(new SlotBucket(contentsIn, 0, 93, 19));

		/** @Fill Item */
		this.addSlot(new SlotBucket(contentsIn, 1, 93, 63) {
			@Override
			public boolean mayPlace(ItemStack stackIn) {
				return false;
			}
		});

		//Player Inventory
		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInventoryIn, i1 + k * 9 + 9, 8 + i1 * 18, 95 + k * 18));
			}
		}

		//Player Hotbar
		for (int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventoryIn, l, 8 + l * 18, 153));
		}
	}
		
	@Override
	public void addSlotListener(ContainerListener listenerIn) {
		super.addSlotListener(listenerIn);
	}

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
		return true;
	}
	
	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(indexIn);
		boolean flag = slot.mayPickup(playerIn);
		
		if (flag && slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if (indexIn >= 0 && indexIn < 2) {
				if (!this.moveItemStackTo(itemstack1, 2, this.slots.size() - 9, false)) {
					if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (indexIn >= 3 && indexIn < this.slots.size()) {
				if (itemstack.getCapability(Capabilities.FluidHandler.ITEM) instanceof IFluidHandler) {
					if (!this.moveItemStackTo(itemstack, 0, 2, false)) {
						return ItemStack.EMPTY;
					}
				
					slot.set(ItemStack.EMPTY);
				} else {
					if (indexIn >= this.slots.size() - 9 && indexIn < this.slots.size()) {
						if (!this.moveItemStackTo(itemstack1, 2, this.slots.size() - 9, false)) {
							return ItemStack.EMPTY;
						}
					}
					
					if (indexIn >= 2 && indexIn < this.slots.size() - 9) {
						if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
							return ItemStack.EMPTY;
						}
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