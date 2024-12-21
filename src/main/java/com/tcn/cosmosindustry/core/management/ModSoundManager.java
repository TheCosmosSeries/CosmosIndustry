package com.tcn.cosmosindustry.core.management;

import com.tcn.cosmosindustry.CosmosIndustry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSoundManager {

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, CosmosIndustry.MOD_ID);

	public static final DeferredHolder<SoundEvent, SoundEvent> MACHINE_GRINDER = SOUND_EVENTS.register("grinder", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "grinder")));
	public static final DeferredHolder<SoundEvent, SoundEvent> MACHINE_CRUSHER = SOUND_EVENTS.register("crusher", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "crusher")));
	public static final DeferredHolder<SoundEvent, SoundEvent> MACHINE_COMPRESSOR = SOUND_EVENTS.register("compressor", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "compressor")));
	public static final DeferredHolder<SoundEvent, SoundEvent> MACHINE_EXTRACTOR = SOUND_EVENTS.register("extractor", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "extractor")));
	
	public static final DeferredHolder<SoundEvent, SoundEvent> MACHINE_LASERHUM = SOUND_EVENTS.register("laserhum", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "laserhum")));
	
	public static void register(IEventBus bus) {
		SOUND_EVENTS.register(bus);
	}
}