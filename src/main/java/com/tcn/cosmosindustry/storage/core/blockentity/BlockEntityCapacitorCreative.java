package com.tcn.cosmosindustry.storage.core.blockentity;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.storage.client.container.ContainerCapacitorCreative;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityCapacitorCreative extends AbstractBlockEntityCapacitor {

	public BlockEntityCapacitorCreative(BlockPos posIn, BlockState stateIn) {
		super(IndustryRegistrationManager.BLOCK_ENTITY_TYPE_CAPACITOR_CREATIVE.get(), posIn, stateIn, IndustryReference.Resource.Storage.ENERGY_CREATIVE, EnumIndustryTier.CREATIVE);
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.style(ComponentColour.MAGENTA, "", "cosmosindustry.gui.capacitor_creative");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player playerIn) {
		return new ContainerCapacitorCreative(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}
}