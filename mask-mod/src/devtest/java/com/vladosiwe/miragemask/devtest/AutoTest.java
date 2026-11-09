package com.vladosiwe.miragemask.devtest;

import com.vladosiwe.miragemask.MirageMask;
import com.vladosiwe.miragemask.client.ClientInput;
import com.vladosiwe.miragemask.disguise.DisguiseManager;
import java.io.File;
import java.util.List;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.world.entity.animal.Pig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Сценарий для проверки и демонстрации: видео превращения со стороны (F5), пауза, срыв,
 * затем галерея всех обликов маски. Видео пишется ffmpeg'ом с виртуального дисплея.
 */
@EventBusSubscriber(modid = MirageMask.MOD_ID, value = Dist.CLIENT)
public final class AutoTest {
    private static final boolean ON = Boolean.getBoolean("miragemask.devtest");
    private static int t = -1;

    /** Все облики, которые маска умеет выбирать (по категориям обстановки). */
    static final List<String[]> GALLERY = List.of(
            g("field", "cow"), g("field", "pig"), g("field", "sheep"), g("field", "chicken"), g("field", "horse"),
            g("field", "donkey"), g("field", "mule"), g("field", "goat"), g("field", "rabbit"), g("field", "fox"),
            g("field", "llama"), g("field", "camel"), g("field", "frog"), g("field", "mooshroom"), g("field", "wolf"),
            g("field", "cat"), g("field", "ocelot"), g("field", "panda"), g("field", "polar_bear"), g("field", "armadillo"),
            g("field", "turtle"), g("field", "parrot"),
            g("village", "villager"), g("village", "wandering_trader"),
            g("hostile", "zombie"), g("hostile", "skeleton"), g("hostile", "creeper"), g("hostile", "spider"),
            g("hostile", "husk"), g("hostile", "stray"), g("hostile", "drowned"), g("hostile", "enderman"),
            g("hostile", "witch"), g("hostile", "bogged"), g("hostile", "zombie_villager"), g("hostile", "cave_spider"),
            g("hostile", "pillager"),
            g("nether", "zombified_piglin"), g("nether", "piglin"), g("nether", "piglin_brute"), g("nether", "hoglin"),
            g("nether", "wither_skeleton"), g("nether", "strider"), g("nether", "blaze"),
            g("water", "squid"), g("water", "glow_squid"), g("water", "cod"), g("water", "salmon"),
            g("water", "dolphin"), g("water", "tropical_fish"), g("water", "axolotl"),
            g("player", "copy"));
    private static final int GALLERY_START = 520, STEP = 30;

    private static String[] g(String cat, String id) {
        return new String[] {cat, id};
    }

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
                c.sendCommand("tp @s 0.5 -60 0.5 180 10");
                c.sendCommand("clear @s");
                c.sendCommand("give @s miragemask:mirage_mask");
                c.sendCommand("give @s diamond_sword");
                c.sendCommand("item replace entity @s armor.head with iron_helmet");
                c.sendCommand("item replace entity @s armor.chest with iron_chestplate");
                c.sendCommand("recipe give @s *");
                mc.player.getInventory().selected = 0;
                c.sendCommand("gamerule sendCommandFeedback false");
                c.sendCommand("gamerule announceAdvancements false");
            }
            case 85 -> clean();
            case 80 -> mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT);
            // --- видео 1: надевание со стороны, прилипание, превращение, шаги, «мобье» поведение на месте
            case 90 -> record("01_transform_from_outside", 11);
            case 104 -> mc.options.keyUse.setDown(true);
            case 127 -> mc.options.keyUse.setDown(false);
            case 175 -> mc.options.keyUp.setDown(true);
            case 200 -> mc.options.keyUp.setDown(false);
            case 128 -> shot("00_stick_helmet_a");
            case 131 -> shot("00_stick_helmet_b");
            case 134 -> shot("00_stick_helmet_c");
            case 140 -> shot("00_hud_panel");
            case 300 -> c.sendCommand("item replace entity @s armor.head with air");
            // --- видео 2: H — лицо открыто (ободок сот), H — образ возвращается
            case 320 -> record("02_reveal_and_resume", 6);
            case 330 -> ClientInput.send(DisguiseManager.ACTION_REVEAL);
            case 345 -> shot("00_hud_paused");
            case 380 -> ClientInput.send(DisguiseManager.ACTION_REVEAL);
            // --- видео 3: удар — маска рассыпается и возвращается в инвентарь
            case 430 -> c.sendCommand("summon pig 0.5 -60 -1.4 {NoAI:1b}");
            case 440 -> record("03_break", 4);
            case 450 -> mc.level.getEntitiesOfClass(Pig.class, mc.player.getBoundingBox().inflate(4)).stream()
                    .findFirst().ifPresent(pig -> mc.gameMode.attack(mc.player, pig));
            case 500 -> { c.sendCommand("kill @e[type=pig]"); c.sendCommand("tp @s 10.5 -60 0.5 180 10"); }
            default -> {}
        }
        // --- галерея всех обликов (вид со стороны, F5)
        if (t >= GALLERY_START) {
            int i = (t - GALLERY_START) / STEP, phase = (t - GALLERY_START) % STEP;
            if (i >= GALLERY.size()) {
                if (phase == 5) c.sendCommand("mirage off");
                if (phase == 20) mc.stop();
                return;
            }
            String[] entry = GALLERY.get(i);
            if (phase == 0) mc.getToasts().clear();
            if (phase == 0) {
                if (entry[0].equals("water") && !GALLERY.get(i - 1)[0].equals("water")) {
                    c.sendCommand("fill 3 -61 -9 18 -61 9 water");
                    c.sendCommand("tp @s 10.5 -61 0.5 180 -50");   // камера сверху — видно мобов в воде
                }
                if (entry[0].equals("player")) {
                    c.sendCommand("tp @s 30.5 -60 0.5 180 10");
                    c.sendCommand("mirage copy @s");
                } else {
                    c.sendCommand("mirage as minecraft:" + entry[1]);
                }
            }
            if (i == 0 && (phase == 3 || phase == 6 || phase == 9 || phase == 14)) shot("00_stick_nohelmet_" + phase);
            if (phase == STEP - 4) shot(String.format("g%02d_%s_%s", i + 1, entry[0], entry[1]));
        }
    }

    private static void clean() {
        Minecraft mc = Minecraft.getInstance();
        mc.gui.getChat().clearMessages(false);
        mc.getToasts().clear();
    }

    private static void shot(String name) {
        Minecraft mc = Minecraft.getInstance();
        mc.gui.getChat().clearMessages(false);
        Screenshot.grab(mc.gameDirectory, name + ".png", mc.getMainRenderTarget(), msg -> {});
        System.out.println("[AutoTest] screenshot " + name + " t=" + t);
    }

    /** Запись экрана виртуального дисплея (ffmpeg x11grab) — только для демонстрационных видео. */
    private static void record(String name, int seconds) {
        clean();
        try {
            File dir = new File(Minecraft.getInstance().gameDirectory, "videos");
            dir.mkdirs();
            new ProcessBuilder("ffmpeg", "-y", "-loglevel", "error", "-f", "x11grab", "-video_size", "1280x720",
                    "-framerate", "20", "-i", System.getenv("DISPLAY"), "-t", String.valueOf(seconds),
                    "-c:v", "libx264", "-preset", "veryfast", "-pix_fmt", "yuv420p",
                    new File(dir, name + ".mp4").getAbsolutePath())
                    .redirectErrorStream(true).redirectOutput(new File(dir, name + ".log")).start();
            System.out.println("[AutoTest] recording " + name);
        } catch (Exception ex) {
            System.out.println("[AutoTest] recording failed: " + ex);
        }
    }

    private AutoTest() {}
}
