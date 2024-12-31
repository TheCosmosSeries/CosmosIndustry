package com.tcn.cosmosindustry.storage.core.blockentity;

import com.tcn.cosmosindustry.IndustryReference;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.storage.client.container.ContainerFluidTank;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityFluidTank extends AbstractBlockEntityFluidTank {

	public BlockEntityFluidTank(BlockPos posIn, BlockState stateIn) {
		super(IndustryRegistrationManager.BLOCK_ENTITY_TYPE_FLUID_TANK.get(), posIn, stateIn, EnumIndustryTier.NORMAL, IndustryReference.Resource.Storage.FLUID_CAPACITY);
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("cosmosindustry.gui.fluidtank");
	}

	@Override
	public AbstractContainerMenu createMenu(int idIn, Inventory playerInventoryIn, Player player) {
		return new ContainerFluidTank(idIn, playerInventoryIn, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}

}