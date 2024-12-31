package com.tcn.cosmosindustry.storage.client.screen;

import com.tcn.cosmosindustry.storage.client.container.AbstractContainerCapacitor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenCapacitor extends AbstractScreenCapacitor {
	public ScreenCapacitor(AbstractContainerCapacitor containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);
	}
}