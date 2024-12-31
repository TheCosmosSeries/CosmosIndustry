package com.tcn.cosmosindustry.core.fluid;

import com.tcn.cosmosindustry.CosmosIndustry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public final class IndustryFluidTags {
    public static final TagKey<Fluid> ENERGIZED_REDSTONE = create(CosmosIndustry.MOD_ID, "energized_redstone");
    public static final TagKey<Fluid> COOLANT = create(CosmosIndustry.MOD_ID, "coolant");

    private IndustryFluidTags() {
    }

    private static TagKey<Fluid> create(String namespace, String name) {
        return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(namespace, name));
    }

    public static TagKey<Fluid> create(ResourceLocation name) {
        return TagKey.create(Registries.FLUID, name);
    }
}
