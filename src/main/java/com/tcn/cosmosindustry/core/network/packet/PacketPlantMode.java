package com.tcn.cosmosindustry.core.network.packet;

import com.tcn.cosmosindustry.CosmosIndustry;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketPlantMode(BlockPos pos, int index) implements CustomPacketPayload, IndustryPacket {

	public static final CustomPacketPayload.Type<PacketPlantMode> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "plant_mode"));

	public static final StreamCodec<ByteBuf, PacketPlantMode> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC,
		PacketPlantMode::pos,
		ByteBufCodecs.VAR_INT,
		PacketPlantMode::index,
		PacketPlantMode::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}