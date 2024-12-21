package com.tcn.cosmosindustry.storage.client.container;

import com.tcn.cosmosindustry.core.management.ModRegistrationManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class ContainerCapacitor extends AbstractContainerCapacitor {

	public ContainerCapacitor(int indexIn, Inventory playerInventoryIn, Container contentsIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(ModRegistrationManager.CONTAINER_TYPE_CAPACITOR.get(), indexIn, playerInventoryIn, contentsIn, accessIn, posIn);
	}
	
	public ContainerCapacitor(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		super(ModRegistrationManager.CONTAINER_TYPE_CAPACITOR.get(), indexIn, playerInventoryIn, extraData);
	}
}