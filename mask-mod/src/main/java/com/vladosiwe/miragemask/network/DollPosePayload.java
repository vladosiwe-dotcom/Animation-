package com.vladosiwe.miragemask.network;

import com.vladosiwe.miragemask.MirageMask;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Сервер → клиенты: кукла игрока entityId целится из лука/арбалета ticks тиков. */
public record DollPosePayload(int entityId, int ticks) implements CustomPacketPayload {
    public static final Type<DollPosePayload> TYPE = new Type<>(MirageMask.id("doll_pose"));
    public static final StreamCodec<ByteBuf, DollPosePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, DollPosePayload::entityId, ByteBufCodecs.VAR_INT, DollPosePayload::ticks, DollPosePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DollPosePayload p, IPayloadContext context) {
        context.enqueueWork(() -> ClientBridge.handlePose(p));
    }
}
