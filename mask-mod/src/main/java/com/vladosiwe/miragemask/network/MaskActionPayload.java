package com.vladosiwe.miragemask.network;

import com.vladosiwe.miragemask.MirageMask;
import com.vladosiwe.miragemask.disguise.DisguiseManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Клиент → сервер: нажата клавиша функции маски (1 — оставить образ, 2 — отключить/вернуть образ). */
public record MaskActionPayload(byte action) implements CustomPacketPayload {
    public static final Type<MaskActionPayload> TYPE = new Type<>(MirageMask.id("action"));
    public static final StreamCodec<ByteBuf, MaskActionPayload> STREAM_CODEC =
            ByteBufCodecs.BYTE.map(MaskActionPayload::new, MaskActionPayload::action);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MaskActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) DisguiseManager.action(player, payload.action());
        });
    }
}
