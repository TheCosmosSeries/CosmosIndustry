package com.tcn.cosmosindustry;

import com.tcn.cosmosindustry.core.management.ModConfigManager;
import com.tcn.cosmosindustry.core.management.ModRecipeManager;
import com.tcn.cosmosindustry.core.management.ModRegistrationManager;
import com.tcn.cosmosindustry.core.management.ModSoundManager;
import com.tcn.cosmoslibrary.common.runtime.CosmosConsoleManager;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(CosmosIndustry.MOD_ID)
public class CosmosIndustry {
	
	//This must NEVER EVER CHANGE!
	public static final String MOD_ID = "cosmosindustry";

	public static CosmosConsoleManager CONSOLE = new CosmosConsoleManager(CosmosIndustry.MOD_ID, true, true);
	
	public CosmosIndustry(ModContainer container, IEventBus bus) {
		container.registerConfig(ModConfig.Type.COMMON, ModConfigManager.SPEC, "cosmosindustry-common.toml");
		
		ModRegistrationManager.register(bus);
//		ModWorldgenManager.register(bus);
		ModRecipeManager.register(bus);
		ModSoundManager.register(bus);
		
		bus.addListener(this::onFMLCommonSetup);
		bus.addListener(this::onFMLClientSetup);
	}

	public void onFMLCommonSetup(final FMLCommonSetupEvent event) {
		CONSOLE = new CosmosConsoleManager(CosmosIndustry.MOD_ID, ModConfigManager.getInstance().getDebugMessage(), true);
		
		CONSOLE.startup("CommonSetup complete.");
	}

	public void onFMLClientSetup(final FMLClientSetupEvent event) {
		ModRegistrationManager.onFMLClientSetup(event);
		
		CONSOLE.startup("ClientSetup complete.");
	}
}