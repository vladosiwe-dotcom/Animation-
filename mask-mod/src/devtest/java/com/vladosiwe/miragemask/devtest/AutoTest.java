package com.vladosiwe.miragemask.devtest;

import com.vladosiwe.miragemask.MirageMask;
import com.vladosiwe.miragemask.client.ClientInput;
import com.vladosiwe.miragemask.disguise.DisguiseManager;
import java.io.File;
import java.util.List;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
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
    private static final int GALLERY_START = 100, STEP = 30;

    private static String[] g(String cat, String id) {
        return new String[] {cat, id};
    }

    private static final String MODE = System.getProperty("miragemask.devtest.mode", "look");

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post e) {
        Minecraft mc = Minecraft.getInstance();
        if (!ON || mc.player == null || mc.level == null || mc.screen != null && t < 0) return;
        t++;
        if (MODE.equals("gallery")) gallery(mc);
        else if (MODE.equals("gait")) gait(mc);
        else look(mc);
    }

    /** Новая маска: надевание от первого лица (анимация из Blender), вид со стороны, иконка. */
    private static void look(Minecraft mc) {
        var c = mc.player.connection;
        switch (t) {
            case 40 -> {
                c.sendCommand("gamemode survival");
                c.sendCommand("time set 13500");
                c.sendCommand("gamerule doDaylightCycle false");
                c.sendCommand("gamerule doMobSpawning false");
                c.sendCommand("weather clear");
                c.sendCommand("kill @e[type=!player]");
                c.sendCommand("tp @s 0.5 -60 0.5 180 0");
                c.sendCommand("clear @s");
                c.sendCommand("recipe give @s *");
                c.sendCommand("give @s miragemask:mirage_mask");
                c.sendCommand("give @s diamond_sword");
                mc.player.getInventory().selected = 0;
                c.sendCommand("gamerule sendCommandFeedback false");
                c.sendCommand("gamerule announceAdvancements false");
            }
            case 70 -> { clean(); shot("a00_holding"); }
            // --- от первого лица: кадры анимации из Blender + видео
            case 78 -> record("fp_put_on_first_person", 3);
            case 80 -> mc.options.keyUse.setDown(true);
            case 81 -> shot("a01_fp_t01");
            case 85 -> shot("a02_fp_t05");
            case 90 -> shot("a03_fp_t10");
            case 94 -> shot("a04_fp_t14");
            case 97 -> shot("a05_fp_t17");
            case 99 -> shot("a06_fp_t19");
            case 102 -> mc.options.keyUse.setDown(false);
            case 104 -> shot("a07_fp_stuck");
            // --- со стороны: маска на лице, когда маскировка неактивна (H)
            case 140 -> mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT);
            case 150 -> ClientInput.send(DisguiseManager.ACTION_REVEAL);
            case 165 -> shot("b01_paused_front_nohelmet");
            case 170 -> mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
            case 180 -> shot("b02_paused_back_nohelmet");
            case 185 -> { mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT); c.sendCommand("tp @s 0.5 -60 0.5 120 0"); }
            case 195 -> shot("b03_paused_angle_nohelmet");
            case 200 -> { c.sendCommand("tp @s 0.5 -60 0.5 180 0"); c.sendCommand("item replace entity @s armor.head with iron_helmet"); }
            case 210 -> shot("b04_paused_front_helmet");
            case 215 -> ClientInput.send(DisguiseManager.ACTION_REVEAL);
            case 245 -> shot("b05_doll_no_aura");
            case 250 -> { c.sendCommand("mirage off"); c.sendCommand("item replace entity @s armor.head with air"); }
            // --- со стороны: прилипание (команда, без руки) + видео
            case 270 -> record("tp_stick_front", 3);
            case 275 -> c.sendCommand("mirage on");
            case 279 -> shot("c01_stick_a");
            case 282 -> shot("c02_stick_b");
            case 285 -> shot("c03_stick_c");
            case 289 -> shot("c04_stick_d");
            case 300 -> c.sendCommand("mirage off");
            // --- со стороны: надевание рукой (видео)
            case 320 -> { c.sendCommand("mirage reset"); mc.player.getInventory().selected = 0; }
            case 330 -> record("tp_put_on_front", 4);
            case 334 -> mc.options.keyUse.setDown(true);
            case 340 -> shot("c05_tp_hand_raise_a");
            case 346 -> shot("c06_tp_hand_raise_b");
            case 352 -> shot("c07_tp_hand_raise_c");
            case 358 -> mc.options.keyUse.setDown(false);
            case 380 -> { c.sendCommand("mirage off"); mc.options.setCameraType(CameraType.FIRST_PERSON); }
            case 395 -> mc.setScreen(new net.minecraft.client.gui.screens.inventory.InventoryScreen(mc.player));
            case 405 -> shot("d01_inventory");
            case 410 -> { mc.setScreen(null); }
            case 430 -> mc.stop();
            default -> {}
        }
    }

    /** Походка, оружие облика, фантомная стрела. */
    private static void gait(Minecraft mc) {
        var c = mc.player.connection;
        switch (t) {
            case 40 -> {
                c.sendCommand("gamemode survival"); c.sendCommand("time set 6000");
                c.sendCommand("gamerule doDaylightCycle false"); c.sendCommand("gamerule doMobSpawning false");
                c.sendCommand("weather clear"); c.sendCommand("kill @e[type=!player]");
                c.sendCommand("tp @s 0.5 -60 0.5 180 0"); c.sendCommand("clear @s");
                c.sendCommand("recipe give @s *"); c.sendCommand("gamerule sendCommandFeedback false");
            }
            case 60 -> { c.sendCommand("mirage as minecraft:skeleton"); mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT); }
            case 80 -> { clean(); shot("g01_skeleton_bow"); }
            // выстрел: корова в 7 блоках впереди
            case 85 -> c.sendCommand("summon cow 0.5 -60 -7.5 {NoAI:1b}");
            case 95 -> record("g_phantom_arrow", 3);
            case 100 -> ClientInput.send(DisguiseManager.ACTION_ARROW);
            case 106 -> shot("g02_aim_a");
            case 110 -> shot("g03_aim_b");
            case 114 -> shot("g04_arrow_flying");
            case 135 -> logState("after phantom arrow");
            // ходьба вперёд (к камере) и вбок — видео
            case 150 -> { c.sendCommand("kill @e[type=cow]"); c.sendCommand("kill @e[type=arrow]"); }
            case 160 -> record("g_walk_forward_skeleton", 4);
            case 165 -> mc.options.keyUp.setDown(true);
            case 175 -> shot("g05_walk_a");
            case 179 -> shot("g06_walk_b");
            case 205 -> mc.options.keyUp.setDown(false);
            case 215 -> { c.sendCommand("tp @s 0.5 -60 0.5 180 0"); c.sendCommand("mirage as minecraft:cow"); }
            case 235 -> record("g_walk_sideways_cow", 4);
            case 240 -> mc.options.keyLeft.setDown(true);
            case 255 -> shot("g07_cow_strafe_body_turns");
            case 280 -> mc.options.keyLeft.setDown(false);
            case 290 -> { c.sendCommand("tp @s 0.5 -60 0.5 180 0"); c.sendCommand("mirage as minecraft:pillager"); }
            case 310 -> shot("g08_pillager_crossbow");
            case 315 -> c.sendCommand("mirage as minecraft:piglin_brute");
            case 335 -> shot("g09_piglin_brute_axe");
            case 340 -> c.sendCommand("mirage as minecraft:zombified_piglin");
            case 360 -> shot("g10_zombified_piglin_sword");
            case 365 -> c.sendCommand("mirage as minecraft:wither_skeleton");
            case 385 -> shot("g11_wither_skeleton_sword");
            case 390 -> { c.sendCommand("mirage as minecraft:stray"); }
            case 400 -> ClientInput.send(DisguiseManager.ACTION_ARROW);
            case 408 -> shot("g12_stray_aim");
            case 430 -> mc.stop();
            default -> {}
        }
    }

    private static void logState(String label) {
        Minecraft mc = Minecraft.getInstance();
        var server = mc.getSingleplayerServer();
        var uuid = mc.player.getUUID();
        server.execute(() -> {
            var sp = server.getPlayerList().getPlayer(uuid);
            boolean disguised = DisguiseManager.isDisguised(sp);
            var cows = server.overworld().getEntitiesOfClass(net.minecraft.world.entity.animal.Cow.class,
                    new net.minecraft.world.phys.AABB(-20, -70, -20, 20, -40, 20));
            String hp = cows.isEmpty() ? "no cow" : ("cow hp=" + cows.get(0).getHealth() + "/" + cows.get(0).getMaxHealth());
            System.out.println("[AutoTest] " + label + ": disguised=" + disguised + " " + hp);
        });
    }

    private static void gallery(Minecraft mc) {
        var c = mc.player.connection;
        if (t == 40) {
            c.sendCommand("time set 6000"); c.sendCommand("gamerule doDaylightCycle false");
            c.sendCommand("kill @e[type=!player]"); c.sendCommand("tp @s 10.5 -60 0.5 180 10");
            c.sendCommand("recipe give @s *"); c.sendCommand("gamerule sendCommandFeedback false");
        }
        if (t == 60) mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT);
        if (t >= GALLERY_START) {
            int i = (t - GALLERY_START) / STEP, phase = (t - GALLERY_START) % STEP;
            if (i >= GALLERY.size()) {
                if (phase == 5) c.sendCommand("mirage off");
                if (phase == 20) mc.stop();
                return;
            }
            String[] entry = GALLERY.get(i);
            if (phase == 0) {
                mc.getToasts().clear();
                if (entry[0].equals("water") && !GALLERY.get(i - 1)[0].equals("water")) {
                    c.sendCommand("fill 3 -61 -9 18 -61 9 water");
                    c.sendCommand("tp @s 10.5 -61 0.5 180 -50");
                }
                if (entry[0].equals("player")) {
                    c.sendCommand("tp @s 30.5 -60 0.5 180 10");
                    c.sendCommand("mirage copy @s");
                } else {
                    c.sendCommand("mirage as minecraft:" + entry[1]);
                }
            }
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
