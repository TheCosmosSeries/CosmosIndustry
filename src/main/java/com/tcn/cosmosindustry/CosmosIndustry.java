package com.tcn.cosmosindustry;

import com.tcn.cosmosindustry.core.management.IndustryConfigManager;
import com.tcn.cosmosindustry.core.management.IndustryRecipeManager;
import com.tcn.cosmosindustry.core.management.IndustryRegistrationManager;
import com.tcn.cosmosindustry.core.management.IndustrySoundManager;
import com.tcn.cosmosindustry.core.management.IndustryWorldgenManager;
import com.tcn.cosmoslibrary.runtime.common.CosmosConsoleManager;

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
		container.registerConfig(ModConfig.Type.COMMON, IndustryConfigManager.SPEC, MOD_ID + "-common.toml");
		
		IndustryRegistrationManager.register(bus);
		IndustryWorldgenManager.register(bus);
		IndustryRecipeManager.register(bus);
		IndustrySoundManager.register(bus);
		
		bus.addListener(this::onFMLCommonSetup);
		bus.addListener(this::onFMLClientSetup);
	}

	public void onFMLCommonSetup(final FMLCommonSetupEvent event) {
		CONSOLE = new CosmosConsoleManager(CosmosIndustry.MOD_ID, IndustryConfigManager.getInstance().getDebugMessage(), true);
		
		CONSOLE.startup("CommonSetup complete.");
	}

	public void onFMLClientSetup(final FMLClientSetupEvent event) {
		IndustryRegistrationManager.onFMLClientSetup(event);
	}
}