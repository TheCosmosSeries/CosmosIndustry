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
	private final BooleanValue info_message;
	
	IndustryConfigManager(final ModConfigSpec.Builder builder) {
		builder.push("debug");
		{
			info_message = builder
				.comment("Whether cosmosindustry can send system information messages.")
				.define("info_message", true
			);
			debug_message = builder
				.comment("Whether cosmosindustry can send system debug messages.")
				.define("debug_message", false
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

	
	public boolean getInfoMessage() {
		return info_message.get();
	}
	
	public void setInfoMessage(boolean value) {
		this.info_message.set(value);
	}
}