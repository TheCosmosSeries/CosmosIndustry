package com.tcn.cosmosindustry.processing.client.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotOrePlant extends Slot {

	public SlotOrePlant(Container inventory, int index, int x_pos, int y_pos) {
		super(inventory, index, x_pos, y_pos);
	}

	@Override
	public boolean mayPlace(ItemStack par1ItemStack) {
		return false;
	}

	@Override
	public ItemStack remove(int par1) {
		return super.remove(par1);
	}

	@Override
	public void onTake(Player par1EntityPlayer, ItemStack par2ItemStack) {
		this.checkTakeAchievements(par2ItemStack);
		super.onTake(par1EntityPlayer, par2ItemStack);
	}
}