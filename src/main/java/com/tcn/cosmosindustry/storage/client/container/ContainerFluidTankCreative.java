package com.tcn.cosmosindustry.storage.client.container;

import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class ContainerFluidTankCreative extends AbstractContainerFluidTank {

	public ContainerFluidTankCreative(int indexIn, Inventory playerInventoryIn, Container contentsIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(IndustryRegistrationManager.CONTAINER_TYPE_FLUID_TANK_CREATIVE.get(), indexIn, playerInventoryIn, contentsIn, accessIn, posIn);
	}
	
	public ContainerFluidTankCreative(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		super(IndustryRegistrationManager.CONTAINER_TYPE_FLUID_TANK_CREATIVE.get(), indexIn, playerInventoryIn, extraData);
	}
}