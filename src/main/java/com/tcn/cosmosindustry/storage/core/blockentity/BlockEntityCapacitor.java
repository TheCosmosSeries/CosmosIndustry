package com.tcn.cosmosindustry.storage.core.blockentity;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.storage.client.container.ContainerCapacitor;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityCapacitor extends AbstractBlockEntityCapacitor {


	public BlockEntityCapacitor(BlockPos posIn, BlockState stateIn) {
		super(IndustryRegistrationManager.BLOCK_ENTITY_TYPE_CAPACITOR.get(), posIn, stateIn, IndustryReference.Resource.Storage.ENERGY, EnumIndustryTier.NORMAL);
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("cosmosindustry.gui.capacitor");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerCapacitor(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}
}