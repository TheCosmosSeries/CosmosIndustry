package com.tcn.cosmosindustry.transport.core.util;

import com.tcn.cosmosindustry.IndustryReference.RESOURCE.TRANSPORT;
import com.tcn.cosmoslibrary.common.enums.EnumChannelSideState;
import com.tcn.cosmoslibrary.common.enums.EnumConnectionType;
import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBlockEntityChannelSided;
import com.tcn.cosmoslibrary.common.util.CosmosUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class TransportUtil {
	
	public static EnumChannelSideState getStateForConnection(Direction facing, BlockPos pos, Level levelIn, IBlockEntityChannelSided tile) {
		EnumConnectionType type = tile.getChannelType();
		EnumIndustryTier tier = tile.getChannelTier();
		
		BlockPos posOffset = pos.offset(facing.getNormal());
		BlockEntity entityOffset = levelIn.getBlockEntity(posOffset);
		
		if (tile.getSide(facing).equals(EnumChannelSideState.DISABLED)) {
			return EnumChannelSideState.DISABLED;
		} else if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_INPUT)) {
			return EnumChannelSideState.INTERFACE_INPUT;
		} else if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_OUTPUT)) {
			return EnumChannelSideState.INTERFACE_OUTPUT;
		} 
		
		else if (entityOffset != null) {
			if (entityOffset instanceof IBlockEntityChannelSided channelOffset) {
				EnumConnectionType typeOffset = channelOffset.getChannelType();
				EnumIndustryTier tierOffset = channelOffset.getChannelTier();
				
				if (typeOffset.equals(type)) {
					if (tierOffset.equals(tier)) {
						if (channelOffset.getSide(facing.getOpposite()).equals(EnumChannelSideState.DISABLED)) {
							return EnumChannelSideState.CABLE_NO_CONN;
						} else {
							return EnumChannelSideState.CABLE;
						}
					} else {
						if (channelOffset.getSide(facing.getOpposite()).equals(EnumChannelSideState.DISABLED)) {
							return EnumChannelSideState.CABLE_NO_CONN;
						} else {
							return EnumChannelSideState.CABLE_OTHER;
						}
					}
				}
				
				return EnumChannelSideState.AIR;
			}

			else if (tile.getChannelType().equals(EnumConnectionType.ENERGY)) {
				Object object = levelIn.getCapability(Capabilities.EnergyStorage.BLOCK, posOffset, facing);
				
				if ( object != null) {
					if (object instanceof IEnergyStorage storage) {
						if (storage.canReceive() || storage.canExtract()) {
							if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_NO_CONN)) {
								return EnumChannelSideState.INTERFACE_NO_CONN;
							} else if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_OUTPUT)) {
								return EnumChannelSideState.INTERFACE_OUTPUT;
							} else if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_INPUT)) {
								return EnumChannelSideState.INTERFACE_INPUT;
							} else {
								return EnumChannelSideState.INTERFACE_NO_CONN;
							}
						}
					}
				}
				
				return EnumChannelSideState.AIR;
			} else if (tile.getChannelType().equals(EnumConnectionType.FLUID)) {
				if (tile instanceof IFluidHandler fluidHandler) {
					if (entityOffset != null) {
						Object object = levelIn.getCapability(Capabilities.FluidHandler.BLOCK, posOffset, facing);
						
						if ( object != null) {
							if (object instanceof IFluidHandler fluid) {
								if (fluid.getFluidInTank(0).getAmount() > 0 || fluid.isFluidValid(0, fluidHandler.getFluidInTank(0))) {
									if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_NO_CONN)) {
										return EnumChannelSideState.INTERFACE_NO_CONN;
									} else if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_OUTPUT)) {
										return EnumChannelSideState.INTERFACE_OUTPUT;
									} else if (tile.getSide(facing).equals(EnumChannelSideState.INTERFACE_INPUT)) {
										return EnumChannelSideState.INTERFACE_INPUT;
									} else {
										return EnumChannelSideState.INTERFACE_NO_CONN;
									}
								}
							}
						}
					}
				}
				
				return EnumChannelSideState.AIR;
			} else if (tile.getChannelType().equals(EnumConnectionType.ITEM)) {
				
			}
		}
		
		return EnumChannelSideState.AIR;
	}
	
	public static Direction getDirectionFromHit(BlockPos pos, BlockHitResult hit) {
		for (Direction dir : Direction.values()) {
			if (CosmosUtil.isInBounds(TRANSPORT.BOUNDING_BOXES_INTERFACE[dir.get3DDataValue()], pos, hit.getLocation())) {
				return dir;
			}
		}
		return null;
	}
}