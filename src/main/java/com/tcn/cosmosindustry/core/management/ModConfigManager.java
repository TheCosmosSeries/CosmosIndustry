package com.tcn.cosmosindustry.core.management;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;

public class ModConfigManager {

	public static final ModConfigManager CONFIG;
	public static final ModConfigSpec SPEC;
	
	static {
		{
			final Pair<ModConfigManager, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ModConfigManager::new);
			CONFIG = specPair.getLeft();
			SPEC = specPair.getRight();
		}
	}
	
	public static void save() {
		SPEC.save();
	}
	
	private final BooleanValue debug_message;
	
	ModConfigManager(final ModConfigSpec.Builder builder) {
		builder.push("debug");
		{
			debug_message = builder
						.comment("Whether cosmosindustry can send system messages.")
						.define("debug_message", true);
		}
		builder.pop();
	}

	public static ModConfigManager getInstance() {
		return CONFIG;
	}

	public boolean getDebugMessage() {
		return debug_message.get();
	}
	
	public void setDebugMessage(boolean value) {
		this.debug_message.set(value);
	}
}