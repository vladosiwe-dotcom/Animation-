package com.vladosiwe.miragemask.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.vladosiwe.miragemask.MirageMask;
import com.vladosiwe.miragemask.disguise.DisguiseManager;
import com.vladosiwe.miragemask.network.ClientBridge;
import com.vladosiwe.miragemask.network.MaskActionPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/** Клавиши функций маски (переназначаются в «Управлении») и тряска камеры при прилипании. */
@EventBusSubscriber(modid = MirageMask.MOD_ID, value = Dist.CLIENT)
public final class ClientInput {
    static final String CATEGORY = "key.categories.miragemask";
    static final KeyMapping LOCK = new KeyMapping("key.miragemask.lock", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);
    static final KeyMapping REVEAL = new KeyMapping("key.miragemask.reveal", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, CATEGORY);

    private ClientInput() {}

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        while (LOCK.consumeClick()) send(DisguiseManager.ACTION_LOCK);
        while (REVEAL.consumeClick()) send(DisguiseManager.ACTION_REVEAL);
    }

    public static void send(byte action) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && ClientBridge.isLocalDisguised(mc.player)) {
            PacketDistributor.sendToServer(new MaskActionPayload(action));
        }
    }

    @SubscribeEvent
    public static void onCamera(ViewportEvent.ComputeCameraAngles event) {
        float roll = HexOverlay.cameraRoll((float) event.getPartialTick());
        if (roll != 0) event.setRoll(event.getRoll() + roll);
    }
}
