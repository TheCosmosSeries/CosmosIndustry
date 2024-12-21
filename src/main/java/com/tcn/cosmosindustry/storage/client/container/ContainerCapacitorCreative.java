package com.tcn.cosmosindustry.storage.client.container;

import com.tcn.cosmosindustry.core.management.ModRegistrationManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class ContainerCapacitorCreative extends AbstractContainerCapacitor {

	public ContainerCapacitorCreative(int indexIn, Inventory playerInventoryIn, Container contentsIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(ModRegistrationManager.CONTAINER_TYPE_CAPACITOR_CREATIVE.get(), indexIn, playerInventoryIn, contentsIn, accessIn, posIn);
	}
	
	public ContainerCapacitorCreative(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		super(ModRegistrationManager.CONTAINER_TYPE_CAPACITOR_CREATIVE.get(), indexIn, playerInventoryIn, extraData);
	}
}