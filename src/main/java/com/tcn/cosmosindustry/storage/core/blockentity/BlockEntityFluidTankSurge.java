package com.tcn.cosmosindustry.storage.core.blockentity;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.storage.client.container.ContainerFluidTankSurge;
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

public class BlockEntityFluidTankSurge extends AbstractBlockEntityFluidTank {

	public BlockEntityFluidTankSurge(BlockPos posIn, BlockState stateIn) {
		super(IndustryRegistrationManager.BLOCK_ENTITY_TYPE_FLUID_TANK_SURGE.get(), posIn, stateIn, EnumIndustryTier.SURGE, IndustryReference.Resource.Storage.FLUID_CAPACITY_S);
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.style(ComponentColour.ORANGE, "", "cosmosindustry.gui.fluidtank_surge");
	}

	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player player) {
		return new ContainerFluidTankSurge(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}

}