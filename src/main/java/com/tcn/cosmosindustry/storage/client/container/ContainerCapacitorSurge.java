package com.tcn.cosmosindustry.storage.client.container;

import com.tcn.cosmosindustry.core.management.ModRegistrationManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class ContainerCapacitorSurge extends AbstractContainerCapacitor {

	public ContainerCapacitorSurge(int indexIn, Inventory playerInventoryIn, Container contentsIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(ModRegistrationManager.CONTAINER_TYPE_CAPACITOR_SURGE.get(), indexIn, playerInventoryIn, contentsIn, accessIn, posIn);
	}
	
	public ContainerCapacitorSurge(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		super(ModRegistrationManager.CONTAINER_TYPE_CAPACITOR_SURGE.get(), indexIn, playerInventoryIn, extraData);
	}
}