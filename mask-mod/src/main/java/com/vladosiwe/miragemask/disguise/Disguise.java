package com.vladosiwe.miragemask.disguise;

import java.util.List;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Текущий облик. kind=MOB: entityType — моб. kind=PLAYER: копия игрока (uuid, ник, снаряжение).
 */
public record Disguise(Kind kind, ResourceLocation entityType, @Nullable UUID profileId, String profileName,
                       List<ItemStack> equipment, String context) {
    public enum Kind { MOB, PLAYER }

    public static final ResourceLocation PLAYER_TYPE = ResourceLocation.withDefaultNamespace("player");

    public static Disguise mob(ResourceLocation type, String context) {
        return new Disguise(Kind.MOB, type, null, "", List.of(), context);
    }
}
