package com.tcn.cosmosindustry.core.management;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;

public class IndustryConfigManager {

	public static final IndustryConfigManager CONFIG;
	public static final ModConfigSpec SPEC;
	
	static {
		{
			final Pair<IndustryConfigManager, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(IndustryConfigManager::new);
			CONFIG = specPair.getLeft();
			SPEC = specPair.getRight();
		}
	}
	
	public static void save() {
		SPEC.save();
	}
	
	private final BooleanValue debug_message;
	
	IndustryConfigManager(final ModConfigSpec.Builder builder) {
		builder.push("debug");
		{
			debug_message = builder
				.comment("Whether cosmosindustry can send system messages.")
				.define("debug_message", true
			);
		}
		builder.pop();
	}

	public static IndustryConfigManager getInstance() {
		return CONFIG;
	}

	public boolean getDebugMessage() {
		return debug_message.get();
	}
	
	public void setDebugMessage(boolean value) {
		this.debug_message.set(value);
	}
}