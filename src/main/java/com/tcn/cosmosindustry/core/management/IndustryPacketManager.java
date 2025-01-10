package com.tcn.cosmosindustry.core.management;

import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.core.network.ServerPacketHandler;
import com.tcn.cosmosindustry.core.network.packet.PacketEmptyTank;
import com.tcn.cosmosindustry.core.network.packet.PacketEmptyTankDual;
import com.tcn.cosmosindustry.core.network.packet.PacketPlantMode;
import com.tcn.cosmosindustry.core.network.packet.PacketSelectedTank;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CosmosIndustry.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class IndustryPacketManager {
	
	@SubscribeEvent
	public static void register(final RegisterPayloadHandlersEvent event) {
	    final PayloadRegistrar registrar = event.registrar("1");
	    registrar.playToServer(PacketEmptyTank.TYPE, PacketEmptyTank.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);
	    registrar.playToServer(PacketEmptyTankDual.TYPE, PacketEmptyTankDual.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);
	    
	    registrar.playToServer(PacketPlantMode.TYPE, PacketPlantMode.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);
	    registrar.playToServer(PacketSelectedTank.TYPE, PacketSelectedTank.STREAM_CODEC, ServerPacketHandler::handleDataOnNetwork);
	}
}