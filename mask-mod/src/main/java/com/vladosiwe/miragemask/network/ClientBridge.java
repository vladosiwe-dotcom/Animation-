package com.vladosiwe.miragemask.network;

import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * Мост общего кода к клиентскому. Сам не импортирует клиентские классы,
 * поэтому безопасно загружается на выделенном сервере.
 */
public final class ClientBridge {
    /** id локального игрока, если на нём маска (выставляет клиент). */
    public static volatile int localDisguisedId = Integer.MIN_VALUE;

    private ClientBridge() {}

    public static boolean isLocalDisguised(Player player) {
        return player.getId() == localDisguisedId;
    }

    static void handle(DisguisePayload payload) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            com.vladosiwe.miragemask.client.ClientDisguises.handle(payload);
        }
    }
}
