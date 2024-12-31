package com.tcn.cosmosindustry.core.network.packet;

import com.tcn.cosmosindustry.CosmosIndustry;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketEmptyTank(BlockPos pos) implements CustomPacketPayload, IndustryPacket {

	public static final CustomPacketPayload.Type<PacketEmptyTank> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "empty_tank"));

	public static final StreamCodec<ByteBuf, PacketEmptyTank> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC,
		PacketEmptyTank::pos,
		PacketEmptyTank::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}