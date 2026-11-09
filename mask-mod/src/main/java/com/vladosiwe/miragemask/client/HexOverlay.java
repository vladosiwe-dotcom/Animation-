package com.vladosiwe.miragemask.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vladosiwe.miragemask.network.DisguisePayload;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Экранный эффект маски (голубая энергия):
 * прилипание — соты «шлёпаются» на весь экран от центра и стягиваются к краям (с тряской камеры);
 * во время маскировки — выраженные соты по краям, слегка «плывут», как искажённый воздух;
 * лицо открыто — тусклый ободок; последние 10 секунд — красная пульсация; срыв — красная вспышка.
 */
public final class HexOverlay {
    static final int CYAN = 0x1EC8FF;
    static final int ICE = 0xA8F2FF;
    static final int RED = 0xFF3B55;

    private static boolean active, paused, locked;
    private static int ticks;
    private static int waveStart = -1000, stickStart = -1000;
    private static int waveColor = CYAN;
    private static int ticksLeft, total;
    private static Component label = Component.empty();

    private HexOverlay() {}

    static void start(DisguisePayload p, Component what) {
        boolean wasActive = active;
        active = true;
        paused = p.paused();
        locked = p.locked();
        ticksLeft = p.ticksLeft();
        total = Math.max(1, p.totalTicks());
        label = what;
        switch (p.flash()) {
            case DisguisePayload.FLASH_ACTIVATE -> stickStart = ticks;
            case DisguisePayload.FLASH_CHANGE, DisguisePayload.FLASH_RESUME -> wave(CYAN);
            case DisguisePayload.FLASH_PAUSE -> wave(ICE);
            default -> { if (!wasActive) wave(CYAN); }
        }
    }

    static void stop(byte flash) {
        active = false;
        paused = false;
        wave(flash == DisguisePayload.FLASH_BREAK ? RED : ICE);
    }

    static void reset() {
        active = false;
        waveStart = stickStart = -1000;
    }

    private static void wave(int color) {
        waveStart = ticks;
        waveColor = color;
    }

    static void tick() {
        ticks++;
        if (active && ticksLeft > 0) ticksLeft--;
    }

    /** Тряска камеры в момент, когда маска прилипает к лицу. */
    public static float cameraRoll(float pt) {
        float age = ticks - stickStart + pt;
        if (age < 6 || age > 18) return 0;
        float k = 1 - (age - 6) / 12;
        return Mth.sin(age * 2.7F) * 2.2F * k;
    }

    public static void render(GuiGraphics g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;
        float pt = delta.getGameTimeDeltaPartialTick(false);
        float age = ticks - waveStart + pt;
        float stick = ticks - stickStart + pt;
        boolean waving = age >= 0 && age < 30;
        boolean sticking = stick >= 0 && stick < 34;
        if (!active && !waving && !sticking) return;

        int w = g.guiWidth(), h = g.guiHeight();
        float r = h / 9.0F;
        float cx0 = w / 2.0F, cy0 = h / 2.0F;
        float maxD = (float) Math.sqrt(cx0 * cx0 + cy0 * cy0);
        float colStep = (float) (Math.sqrt(3) * r), rowStep = 1.5F * r;
        float time = ticks + pt;
        boolean warning = active && ticksLeft <= 200;
        float pulse = warning ? 0.5F + 0.5F * Mth.cos((ticksLeft % 20 - pt) / 20.0F * Mth.TWO_PI) : 0;

        Matrix4f m = g.pose().last().pose();
        VertexConsumer vc = g.bufferSource().getBuffer(RenderType.gui());
        int rows = (int) (h / rowStep) + 2, cols = (int) (w / colStep) + 2;
        for (int row = -1; row <= rows; row++) {
            for (int col = -1; col <= cols; col++) {
                float hash = Mth.frac(Mth.sin(col * 12.9898F + row * 78.233F) * 43758.547F);
                // «искажение воздуха»: соты слегка плывут
                float cx = col * colStep + ((row & 1) == 1 ? colStep / 2 : 0) + 1.6F * Mth.sin(time * 0.13F + row * 0.9F);
                float cy = row * rowStep + 1.6F * Mth.cos(time * 0.11F + col * 0.7F);
                float d = (float) Math.sqrt((cx - cx0) * (cx - cx0) + (cy - cy0) * (cy - cy0)) / maxD;

                float a = 0, scale = 1;
                int color = CYAN;
                if (active) {
                    float edge = Mth.clamp((d - 0.42F) / 0.58F, 0, 1);
                    float shimmer = 0.7F + 0.3F * Mth.sin(time * 0.09F + hash * 6.28F);
                    a = edge * (0.25F + 0.75F * edge) * 0.8F * shimmer * (paused ? 0.35F : 1);
                    if (warning) {
                        color = lerpColor(CYAN, RED, 0.35F + 0.65F * pulse);
                        a = Math.max(a, edge * (0.35F + 0.55F * pulse));
                    }
                }
                if (sticking) {
                    // 0–8: соты накрывают весь экран от центра; 8–34: «впитываются» к краям
                    float cover = Mth.clamp(stick / 8.0F - d * 0.9F, 0, 1);
                    float release = Mth.clamp((stick - 8) / 26.0F, 0, 1);
                    float keep = Mth.clamp((d - release * 0.9F) * 3.0F, 0, 1);
                    float sa = cover * (1 - release + release * keep) * (0.75F + 0.25F * hash);
                    if (sa > a) {
                        a = sa;
                        color = lerpColor(ICE, CYAN, release);
                        scale = 0.75F + 0.25F * backOut(cover);
                    }
                }
                if (waving) {
                    float front = age / 16.0F;
                    float band = 1.0F - Math.abs(d - front + hash * 0.12F) * 5.0F;
                    float fade = 1.0F - age / 30.0F;
                    if (band > 0 && band * fade > a) { a = band * fade; color = waveColor; }
                }
                if (a < 0.02F) continue;
                a = Math.min(a, 1);
                float rr = r * scale * (0.95F - hash * 0.06F);
                hexFill(vc, m, cx, cy, rr, argb(ICE, a * 0.18F), argb(color, a * 0.07F));
                hexOutline(vc, m, cx, cy, rr * 1.04F, r * 0.16F, argb(color, a * 0.35F));   // свечение
                hexOutline(vc, m, cx, cy, rr, Math.max(1.5F, r * 0.08F), argb(color, a * 0.95F)); // кромка
                hexOutline(vc, m, cx, cy, rr * 0.93F, Math.max(1.0F, r * 0.025F), argb(0xFFFFFF, a * 0.6F));
            }
        }
        g.flush();

        if (active) {
            int sec = (ticksLeft + 19) / 20;
            Component line = Component.literal("⬢ " + sec + "с  ").append(label);
            if (locked) line = line.copy().append(Component.translatable("hud.miragemask.locked"));
            if (paused) line = line.copy().append(Component.translatable("hud.miragemask.paused"));
            int tc = warning ? lerpColor(0xFFD27A, RED, pulse) : ICE;
            g.drawCenteredString(mc.font, line, w / 2, 6, 0xFF000000 | tc);
            int barW = 100, x0 = w / 2 - barW / 2;
            g.fill(x0, 17, x0 + barW, 19, 0x66000000);
            g.fill(x0, 17, x0 + (int) (barW * (ticksLeft / (float) total)), 19, 0xFF000000 | (warning ? RED : CYAN));
        }
    }

    private static float backOut(float x) {
        float c1 = 1.8F, c3 = c1 + 1, y = x - 1;
        return 1 + c3 * y * y * y + c1 * y * y;
    }

    private static float vx(float cx, float r, int i) {
        return cx + r * Mth.cos(Mth.DEG_TO_RAD * (60 * i - 30));
    }

    private static float vy(float cy, float r, int i) {
        return cy + r * Mth.sin(Mth.DEG_TO_RAD * (60 * i - 30));
    }

    private static void hexFill(VertexConsumer vc, Matrix4f m, float cx, float cy, float r, int center, int edge) {
        for (int i = 0; i < 6; i++) {
            int j = (i + 1) % 6;
            vc.addVertex(m, cx, cy, 0).setColor(center);
            vc.addVertex(m, vx(cx, r, j), vy(cy, r, j), 0).setColor(edge);
            vc.addVertex(m, vx(cx, r, i), vy(cy, r, i), 0).setColor(edge);
            vc.addVertex(m, cx, cy, 0).setColor(center);
        }
    }

    private static void hexOutline(VertexConsumer vc, Matrix4f m, float cx, float cy, float r, float t, int color) {
        float ri = r - t;
        for (int i = 0; i < 6; i++) {
            int j = (i + 1) % 6;
            vc.addVertex(m, vx(cx, ri, i), vy(cy, ri, i), 0).setColor(color);
            vc.addVertex(m, vx(cx, ri, j), vy(cy, ri, j), 0).setColor(color);
            vc.addVertex(m, vx(cx, r, j), vy(cy, r, j), 0).setColor(color);
            vc.addVertex(m, vx(cx, r, i), vy(cy, r, i), 0).setColor(color);
        }
    }

    private static int argb(int rgb, float a) {
        return ((int) (Mth.clamp(a, 0, 1) * 255) << 24) | (rgb & 0xFFFFFF);
    }

    static int lerpColor(int a, int b, float t) {
        int r = (int) Mth.lerp(t, (a >> 16) & 255, (b >> 16) & 255);
        int g = (int) Mth.lerp(t, (a >> 8) & 255, (b >> 8) & 255);
        int bl = (int) Mth.lerp(t, a & 255, b & 255);
        return (r << 16) | (g << 8) | bl;
    }
}
