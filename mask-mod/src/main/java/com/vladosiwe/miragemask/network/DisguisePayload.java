package com.vladosiwe.miragemask.network;

import com.vladosiwe.miragemask.MirageMask;
import com.vladosiwe.miragemask.disguise.Disguise;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

/** Сервер → клиенты: «игрок с этим id теперь выглядит так» (или маскировка снята). */
public record DisguisePayload(int entityId, boolean active, byte kind, ResourceLocation entityType,
                              @Nullable UUID profileId, String profileName, List<ItemStack> equipment,
                              int ticksLeft, int totalTicks, byte flash, boolean paused, boolean locked)
        implements CustomPacketPayload {

    public static final byte FLASH_NONE = 0, FLASH_ACTIVATE = 1, FLASH_CHANGE = 2, FLASH_BREAK = 3, FLASH_END = 4, FLASH_PAUSE = 5, FLASH_RESUME = 6;

    public static final Type<DisguisePayload> TYPE = new Type<>(MirageMask.id("disguise"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DisguisePayload> STREAM_CODEC =
            StreamCodec.of(DisguisePayload::write, DisguisePayload::read);

    public static DisguisePayload of(int entityId, Disguise d, int ticksLeft, int total, byte flash, boolean paused, boolean locked) {
        return new DisguisePayload(entityId, true, (byte) d.kind().ordinal(), d.entityType(), d.profileId(),
                d.profileName(), d.equipment(), ticksLeft, total, flash, paused, locked);
    }

    public static DisguisePayload clear(int entityId, byte flash) {
        return new DisguisePayload(entityId, false, (byte) 0, Disguise.PLAYER_TYPE, null, "", List.of(), 0, 0, flash, false, false);
    }

    public boolean isPlayerCopy() {
        return kind == (byte) Disguise.Kind.PLAYER.ordinal();
    }

    private static void write(RegistryFriendlyByteBuf buf, DisguisePayload p) {
        buf.writeVarInt(p.entityId);
        buf.writeBoolean(p.active);
        buf.writeByte(p.kind);
        buf.writeResourceLocation(p.entityType);
        buf.writeBoolean(p.profileId != null);
        if (p.profileId != null) buf.writeUUID(p.profileId);
        buf.writeUtf(p.profileName, 32);
        buf.writeVarInt(p.equipment.size());
        for (ItemStack stack : p.equipment) ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
        buf.writeVarInt(p.ticksLeft);
        buf.writeVarInt(p.totalTicks);
        buf.writeByte(p.flash);
        buf.writeBoolean(p.paused);
        buf.writeBoolean(p.locked);
    }

    private static DisguisePayload read(RegistryFriendlyByteBuf buf) {
        int id = buf.readVarInt();
        boolean active = buf.readBoolean();
        byte kind = buf.readByte();
        ResourceLocation type = buf.readResourceLocation();
        UUID uuid = buf.readBoolean() ? buf.readUUID() : null;
        String name = buf.readUtf(32);
        int n = Math.min(buf.readVarInt(), 8);
        List<ItemStack> gear = new ArrayList<>(n);
        for (int i = 0; i < n; i++) gear.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
        int left = buf.readVarInt(), total = buf.readVarInt();
        byte flash = buf.readByte();
        return new DisguisePayload(id, active, kind, type, uuid, name, gear, left, total, flash, buf.readBoolean(), buf.readBoolean());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DisguisePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientBridge.handle(payload));
    }
}
