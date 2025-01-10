package com.tcn.cosmosindustry.core.network.packet;

import com.tcn.cosmosindustry.CosmosIndustry;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketEmptyTankDual(BlockPos pos, int tank) implements CustomPacketPayload, IndustryPacket {

	public static final CustomPacketPayload.Type<PacketEmptyTankDual> TYPE = 
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CosmosIndustry.MOD_ID, "empty_tank_dual"));

	public static final StreamCodec<ByteBuf, PacketEmptyTankDual> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC,
		PacketEmptyTankDual::pos,
		ByteBufCodecs.INT,
		PacketEmptyTankDual::tank,
		PacketEmptyTankDual::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}