package com.tcn.cosmosindustry.core.management;

import com.tcn.cosmosindustry.CosmosIndustry;
import com.tcn.cosmosindustry.core.worldgen.OverworldOreFeature;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class IndustryWorldgenManager {
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, CosmosIndustry.MOD_ID);
	
	public static final DeferredHolder<Feature<?>, Feature<?>> TIN_ORE = FEATURES.register("tin_ore", OverworldOreFeature::new);
	public static final DeferredHolder<Feature<?>, Feature<?>> SILVER_ORE = FEATURES.register("silver_ore", OverworldOreFeature::new);
	public static final DeferredHolder<Feature<?>, Feature<?>> ZINC_ORE = FEATURES.register("zinc_ore", OverworldOreFeature::new);
	public static final DeferredHolder<Feature<?>, Feature<?>> SILICON_ORE = FEATURES.register("silicon_ore", OverworldOreFeature::new);
	
	public static void register(IEventBus bus) {
		FEATURES.register(bus);
	}
}