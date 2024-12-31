package com.tcn.cosmosindustry.core.item;

import java.util.function.UnaryOperator;

import com.tcn.cosmoslibrary.common.enums.EnumIndustryTier;

import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class IndustryItemRarity {
    public static final EnumProxy<Rarity> SURGE = new EnumProxy<>(
        Rarity.class, -1, "cosmosindustry:surge", (UnaryOperator<Style>) style -> style.withColor(TextColor.fromRgb(EnumIndustryTier.SURGE.getColour().dec()))
    );
    
    public static final EnumProxy<Rarity> CREATIVE = new EnumProxy<>(
        Rarity.class, -1, "cosmosindustry:creative", (UnaryOperator<Style>) style -> style.withColor(TextColor.fromRgb(EnumIndustryTier.CREATIVE.getColour().dec()))
    );
}