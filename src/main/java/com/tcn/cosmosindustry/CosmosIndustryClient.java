package com.tcn.cosmosindustry;

import com.tcn.cosmosindustry.client.screen.ScreenConfiguration;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = CosmosIndustry.MOD_ID, dist = Dist.CLIENT)
public class CosmosIndustryClient {

	public CosmosIndustryClient(ModContainer container) {
		container.registerExtensionPoint(IConfigScreenFactory.class, ScreenConfiguration::new);
	}
}