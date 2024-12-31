package com.tcn.cosmosindustry.core.network;

import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.core.network.packet.IndustryPacket;
import com.tcn.cosmosindustry.core.network.packet.PacketEmptyTank;
import com.tcn.cosmosindustry.core.network.packet.PacketPlantMode;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityFluidCrafter;
import com.tcn.cosmosindustry.processing.core.blockentity.BlockEntityOrePlant;
import com.tcn.cosmosindustry.production.core.blockentity.BlockEntityLiquidFuel;
import com.tcn.cosmosindustry.storage.core.blockentity.AbstractBlockEntityFluidTank;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPacketHandler {

	public static void handleDataOnNetwork(final IndustryPacket data, final IPayloadContext context) {
		if (data instanceof PacketEmptyTank packet) {
			context.enqueueWork(() -> {
				ServerLevel world = (ServerLevel) context.player().level();
				BlockEntity entity = world.getBlockEntity(packet.pos());
				
				if (entity instanceof BlockEntityOrePlant blockEntity) {
					blockEntity.emptyFluidTank();
					blockEntity.sendUpdates();
				} else if (entity instanceof BlockEntityFluidCrafter blockEntity) {
					blockEntity.emptyFluidTank();
					blockEntity.sendUpdates();
				} else if (entity instanceof BlockEntityLiquidFuel blockEntity) {
					blockEntity.emptyFluidTank();
					blockEntity.sendUpdates();
				} else if (entity instanceof AbstractBlockEntityFluidTank blockEntity) {
					blockEntity.emptyFluidTank();
					blockEntity.sendUpdates();
				} else {
					CosmosIndustry.CONSOLE.debugWarn("[Packet Delivery Failure] <emptytank> Block Entity not equal to expected.");
				}			
			});
		}
		
		if (data instanceof PacketPlantMode packet) {
			context.enqueueWork(() -> {
				ServerLevel world = (ServerLevel) context.player().level();
				BlockEntity entity = world.getBlockEntity(packet.pos());

				if (entity instanceof BlockEntityOrePlant blockEntity) {
					blockEntity.setMode(packet.index());
					blockEntity.sendUpdates();
				} else if (entity instanceof BlockEntityFluidCrafter blockEntity) {
					blockEntity.setMode(packet.index());
					blockEntity.sendUpdates();
				} else {
					CosmosIndustry.CONSOLE.debugWarn("[Packet Delivery Failure] <plantmode> Block Entity not equal to expected.");
				}			
			});
		}
	}
}
