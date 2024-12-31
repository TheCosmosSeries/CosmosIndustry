package com.tcn.cosmosindustry.production.client.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class SlotSolidFuel extends Slot {

    public SlotSolidFuel(Container furnaceContainer, int slot, int xPosition, int yPosition) {
        super(furnaceContainer, slot, xPosition, yPosition);
    }
    
    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.isFuel(stack);
    }

    protected boolean isFuel(ItemStack stack) {
        return stack.getBurnTime(RecipeType.SMELTING) > 0;
    }
}