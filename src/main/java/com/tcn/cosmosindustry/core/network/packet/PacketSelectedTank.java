package com.tcn.cosmosindustry.core.network.packet;

import com.tcn.cosmosindustry.CosmosIndustry;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketSelectedTank(BlockPos pos) implements CustomPacketPayload, IndustryPacket {

	public static final CustomPacketPayload.Type<PacketSelectedTank> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "selected_tank"));

	public static final StreamCodec<ByteBuf, PacketSelectedTank> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC,
		PacketSelectedTank::pos,
		PacketSelectedTank::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}