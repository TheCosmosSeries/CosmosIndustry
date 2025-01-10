package com.tcn.cosmosindustry;

import com.tcn.cosmosindustry.client.screen.ScreenConfiguration;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.production.client.renderer.LiquidFuelBEWLR;
import com.tcn.cosmosindustry.production.client.renderer.PeltierBEWLR;
import com.tcn.cosmosindustry.storage.client.renderer.FluidTankBEWLR;
import com.tcn.cosmoslibrary.runtime.common.CosmosRuntime;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@Mod(value = CosmosIndustry.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CosmosIndustry.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CosmosIndustryClient {

	public CosmosIndustryClient(ModContainer container) {		
		CosmosRuntime.Client.regiserConfigScreen(container, ScreenConfiguration::new);
	}

	@SubscribeEvent
	public static void registerItemRenderers(RegisterClientExtensionsEvent event) {
		CosmosRuntime.Client.registerBEWLRToItems(event, FluidTankBEWLR.INSTANCE,
			IndustryRegistrationManager.ITEMBLOCK_FLUID_TANK.get(),
			IndustryRegistrationManager.ITEMBLOCK_FLUID_TANK_SURGE.get(),
			IndustryRegistrationManager.ITEMBLOCK_FLUID_TANK_CREATIVE.get()
		);
		
		CosmosRuntime.Client.registerBEWLRToItem(event, LiquidFuelBEWLR.INSTANCE, IndustryRegistrationManager.ITEMBLOCK_LIQUID_FUEL.get());

		CosmosRuntime.Client.registerBEWLRToItem(event, PeltierBEWLR.INSTANCE, IndustryRegistrationManager.ITEMBLOCK_PELTIER.get());
	}
}