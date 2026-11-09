package com.vladosiwe.miragemask.devtest;

import com.vladosiwe.miragemask.MirageMask;
import java.io.File;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.world.entity.animal.Cow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Сценарий проверки в клиенте: команды, надевание маски, смена обстановки, скриншоты. */
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
            case 80 -> shot("01_inventory_hand");
            case 90 -> mc.options.keyUse.setDown(true);
            case 96 -> mc.gui.getChat().clearMessages(false);
            case 98 -> shot("02_putting_on_mid");
            case 102 -> shot("02b_putting_on");
            case 106 -> shot("03_putting_on_face");
            case 112 -> mc.options.keyUse.setDown(false);
            case 114 -> shot("04_hex_wave");
            case 130 -> shot("05_hud_active");
            case 140 -> mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT);
            case 150 -> shot("06_field_disguise_front");
            case 155 -> mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
            case 165 -> shot("07_field_disguise_back");
            case 170 -> { c.sendCommand("time set 18000"); c.sendCommand("kill @e[type=cow]"); }
            case 200 -> c.sendCommand("summon zombie 3 -60 -4 {PersistenceRequired:1b}");
            case 255 -> logZombieTarget("night, look=zombie (expect: ignores player)");
            case 260 -> { mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT); }
            case 270 -> shot("08_night_disguise");
            case 280 -> { c.sendCommand("time set 6000"); c.sendCommand("summon villager 4 -60 -3"); c.sendCommand("summon villager -4 -60 -2"); }
            case 380 -> shot("09_village_disguise");
            case 400 -> logZombieTarget("day, look=villager (expect: targets player)");
            case 460 -> shot("10_idle_behaviour");
            case 1100 -> mc.options.setCameraType(CameraType.FIRST_PERSON);
            case 1150 -> shot("11_last_10_seconds");
            case 1160 -> {
                c.sendCommand("summon cow 0 -60 -2");
                c.sendCommand("tp @s 0 -60 0 180 30");
            }
            case 1170 -> mc.level.getEntitiesOfClass(Cow.class, mc.player.getBoundingBox().inflate(5)).stream().findFirst()
                    .ifPresent(cow -> mc.gameMode.attack(mc.player, cow));
            case 1173 -> shot("12_mask_broken");
            case 1200 -> { mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT); }
            case 1210 -> shot("13_after_break");
            case 1230 -> mc.stop();
            default -> {}
        }
    }

    private static void logZombieTarget(String label) {
        Minecraft mc = Minecraft.getInstance();
        var server = mc.getSingleplayerServer();
        server.execute(() -> {
            var level = server.overworld();
            for (var z : level.getEntitiesOfClass(net.minecraft.world.entity.monster.Zombie.class,
                    new net.minecraft.world.phys.AABB(-30, -70, -30, 30, -40, 30))) {
                var target = z.getTarget();
                System.out.println("[AutoTest] zombie " + label + " -> target=" + (target == null ? "none" : target.getName().getString()));
            }
        });
    }

    private static void shot(String name) {
        Minecraft mc = Minecraft.getInstance();
        mc.gui.getChat().clearMessages(false);
        Screenshot.grab(mc.gameDirectory, name + ".png", mc.getMainRenderTarget(), msg -> {});
        System.out.println("[AutoTest] screenshot " + name + " t=" + t + " gameTime=" + mc.level.getGameTime());
    }

    private AutoTest() {}
}
