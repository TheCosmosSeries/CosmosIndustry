package com.tcn.cosmosindustry.core.management;

import com.tcn.cosmosindustry.CosmosIndustry;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@EventBusSubscriber(modid = CosmosIndustry.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ModGameEventsManager {
	
	@SubscribeEvent
	public static void onServerAboutToStart(final ServerAboutToStartEvent event) {
		CosmosIndustry.CONSOLE.startup("[FMLServerAboutToStartEvent] Server about to start...");
	}

	@SubscribeEvent
	public static void onServerStarting(final ServerStartingEvent event) {
		CosmosIndustry.CONSOLE.startup("[FMLServerStartingEvent] Server starting...");
	}

	@SubscribeEvent
	public static void onServerStarted(final ServerStartedEvent event) {
		CosmosIndustry.CONSOLE.startup("[FMLServerStartedEvent] Server started...");
	}

	@SubscribeEvent
	public static void onServerStopping(final ServerStoppingEvent event) {
		CosmosIndustry.CONSOLE.shutdown("[FMLServerStoppingEvent] Server stopping...");
	}
}