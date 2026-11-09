package com.vladosiwe.miragemask.devtest;

import com.vladosiwe.miragemask.MirageMask;
import com.vladosiwe.miragemask.client.ClientInput;
import com.vladosiwe.miragemask.disguise.DisguiseManager;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.animal.Cow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Сценарий проверки в клиенте: надевание, пауза (H), фиксация (G), срыв, возврат маски, скриншоты. */
@EventBusSubscriber(modid = MirageMask.MOD_ID, value = Dist.CLIENT)
public final class AutoTest {
    private static final boolean ON = Boolean.getBoolean("miragemask.devtest");
    private static int t = -1;

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post e) {
        Minecraft mc = Minecraft.getInstance();
        if (!ON || mc.player == null || mc.level == null || mc.screen != null && t < 0) return;
        t++;
        var c = mc.player.connection;
        switch (t) {
            case 40 -> {
                c.sendCommand("gamemode survival");
                c.sendCommand("time set 6000");
                c.sendCommand("gamerule doDaylightCycle false");
                c.sendCommand("gamerule doMobSpawning false");
                c.sendCommand("weather clear");
                c.sendCommand("kill @e[type=!player]");
                c.sendCommand("tp @s 0 -60 0 180 10");
                c.sendCommand("clear @s");
                c.sendCommand("give @s miragemask:mirage_mask");
                c.sendCommand("give @s diamond_sword");
                c.sendCommand("summon cow 3 -60 -6");
                c.sendCommand("summon cow -2 -60 -7");
                c.sendCommand("summon cow 0 -60 -9");
                mc.player.getInventory().selected = 0;
            }
            case 80 -> shot("01_hand_3d_mask");
            case 90 -> mc.options.keyUse.setDown(true);
            case 97 -> shot("02_put_on_a");
            case 103 -> shot("03_put_on_b");
            case 107 -> shot("04_put_on_press");
            case 112 -> mc.options.keyUse.setDown(false);
            case 113 -> shot("05_stick_overlay");
            case 119 -> shot("06_stick_overlay_late");
            case 150 -> shot("07_hud_hand_empty");
            case 160 -> mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT);
            case 170 -> shot("08_doll_cow");
            case 180 -> ClientInput.send(DisguiseManager.ACTION_REVEAL);
            case 195 -> shot("09_paused_face_ring");
            case 200 -> ClientInput.send(DisguiseManager.ACTION_REVEAL);
            case 215 -> shot("10_resumed");
            case 220 -> ClientInput.send(DisguiseManager.ACTION_LOCK);
            case 230 -> c.sendCommand("time set 18000");
            case 300 -> shot("11_locked_still_cow_at_night");
            case 310 -> ClientInput.send(DisguiseManager.ACTION_LOCK);
            case 380 -> shot("12_unlocked_zombie");
            case 450 -> { c.sendCommand("time set 6000"); c.sendCommand("summon villager 4 -60 -3"); }
            case 530 -> shot("13_village");
            case 540 -> { c.sendCommand("kill @e[type=villager]"); c.sendCommand("summon pig 0 -60 -1.6 {NoAI:1b}"); }
            case 548, 550, 552 -> {
                var pigs = mc.level.getEntitiesOfClass(net.minecraft.world.entity.animal.Pig.class, mc.player.getBoundingBox().inflate(6));
                System.out.println("[AutoTest] attack t=" + t + " pigs=" + pigs.size() + " dist=" + (pigs.isEmpty() ? -1 : pigs.get(0).distanceTo(mc.player)));
                pigs.stream().findFirst().ifPresent(pig -> mc.gameMode.attack(mc.player, pig));
            }
            case 553 -> shot("14_shatter_a");
            case 557 -> shot("15_shatter_b");
            case 562 -> shot("15b_shatter_c");
            case 575 -> mc.options.setCameraType(CameraType.FIRST_PERSON);
            case 580 -> shot("16_mask_back_in_inventory");
            case 590 -> { mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT); c.sendCommand("kill @e[type=pig]"); }
            case 600 -> c.sendCommand("mirage on");
            case 603 -> shot("17_stick_front_a");
            case 606 -> shot("18_stick_front_b");
            case 610 -> shot("19_stick_front_c");
            case 630 -> shot("20_after_stick");
            case 640 -> { mc.options.setCameraType(CameraType.FIRST_PERSON); mc.setScreen(new InventoryScreen(mc.player)); }
            case 650 -> shot("21_inventory_icon");
            case 655 -> mc.setScreen(null);
            case 660 -> c.sendCommand("mirage off");
            case 680 -> mc.stop();
            default -> {}
        }
    }

    private static void shot(String name) {
        Minecraft mc = Minecraft.getInstance();
        mc.gui.getChat().clearMessages(false);
        Screenshot.grab(mc.gameDirectory, name + ".png", mc.getMainRenderTarget(), msg -> {});
        System.out.println("[AutoTest] screenshot " + name + " t=" + t);
    }

    private AutoTest() {}
}
