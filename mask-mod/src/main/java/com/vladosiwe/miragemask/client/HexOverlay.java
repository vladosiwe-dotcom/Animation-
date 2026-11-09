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
 * Экранный эффект маски: соты-шестиугольники (фиолетовые, с розовым стеклом как у маски).
 * Надевание — волна сот от центра экрана; во время маскировки — соты по краям экрана;
 * последние 10 секунд — края краснеют и пульсируют; срыв — красная вспышка.
 */
public final class HexOverlay {
    private static final int VIOLET = 0x7A5CFF;
    private static final int PINK = 0xFFC9DC;
    private static final int RED = 0xFF3B55;

    private static boolean active;
    private static int ticks;           // тиков с начала эффекта
    private static int waveStart = -1000;
    private static int waveColor = VIOLET;
    private static int ticksLeft, total;
    private static Component label = Component.empty();

    private HexOverlay() {}

    static void start(DisguisePayload p, Component what) {
        boolean wasActive = active;
        active = true;
        ticksLeft = p.ticksLeft();
        total = Math.max(1, p.totalTicks());
        label = what;
        if (p.flash() != DisguisePayload.FLASH_NONE || !wasActive) wave(VIOLET);
    }

    static void stop(byte flash) {
        active = false;
        wave(flash == DisguisePayload.FLASH_BREAK ? RED : PINK);
    }

    static void reset() {
        active = false;
        waveStart = -1000;
    }

    private static void wave(int color) {
        waveStart = ticks;
        waveColor = color;
    }

    static void tick() {
        ticks++;
        if (active && ticksLeft > 0) ticksLeft--;
    }

    public static void render(GuiGraphics g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;
        float pt = delta.getGameTimeDeltaPartialTick(false);
        float age = ticks - waveStart + pt;
        boolean waving = age >= 0 && age < 34;
        if (!active && !waving) return;

        int w = g.guiWidth(), h = g.guiHeight();
        float r = h / 11.0F;
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
                float cx = col * colStep + ((row & 1) == 1 ? colStep / 2 : 0);
                float cy = row * rowStep;
                float d = (float) Math.sqrt((cx - cx0) * (cx - cx0) + (cy - cy0) * (cy - cy0)) / maxD;
                float hash = Mth.frac(Mth.sin(col * 12.9898F + row * 78.233F) * 43758.547F);

                float a = 0;
                int color = VIOLET;
                if (active) {
                    float edge = Mth.clamp((d - 0.5F) / 0.5F, 0, 1);
                    float shimmer = 0.75F + 0.25F * Mth.sin(time * 0.08F + hash * 6.28F);
                    a = edge * edge * 0.55F * shimmer;
                    if (warning) {
                        color = lerpColor(VIOLET, RED, 0.35F + 0.65F * pulse);
                        a = Math.max(a, edge * (0.3F + 0.5F * pulse));
                    }
                }
                if (waving) {
                    float front = age / 20.0F;                    // радиус волны (0..1.7)
                    float band = 1.0F - Math.abs(d - front + hash * 0.12F) * 5.0F;
                    float fade = 1.0F - age / 34.0F;
                    if (band > 0) {
                        float wa = band * fade;
                        if (wa > a) { a = wa; color = waveColor; }
                    }
                }
                if (a < 0.02F) continue;
                a = Math.min(a, 1);
                float rr = r * (0.94F - hash * 0.08F);
                hexFill(vc, m, cx, cy, rr, argb(PINK, a * 0.16F), argb(color, a * 0.10F));
                hexOutline(vc, m, cx, cy, rr, Math.max(1.0F, r * 0.07F), argb(color, a * 0.85F));
            }
        }
        g.flush();

        if (active) {
            int sec = (ticksLeft + 19) / 20;
            String text = "⬢ " + sec + "с";
            int tc = warning ? lerpColor(0xFFD27A, RED, pulse) : 0xD9CCFF;
            g.drawCenteredString(mc.font, Component.literal(text).append("  ").append(label), w / 2, 6, 0xFF000000 | tc);
            int barW = 90, x0 = w / 2 - barW / 2;
            g.fill(x0, 17, x0 + barW, 19, 0x66000000);
            g.fill(x0, 17, x0 + (int) (barW * (ticksLeft / (float) total)), 19, 0xFF000000 | (warning ? RED : VIOLET));
        }
    }

    // ---------------------------------------------------------------- геометрия (pointy-top гекс)
    private static float vx(float cx, float r, int i) {
        return cx + r * Mth.cos(Mth.DEG_TO_RAD * (60 * i - 30));
    }

    private static float vy(float cy, float r, int i) {
        return cy + r * Mth.sin(Mth.DEG_TO_RAD * (60 * i - 30));
    }

    private static void hexFill(VertexConsumer vc, Matrix4f m, float cx, float cy, float r, int centerColor, int edgeColor) {
        for (int i = 0; i < 6; i++) {
            int j = (i + 1) % 6;
            vc.addVertex(m, cx, cy, 0).setColor(centerColor);
            vc.addVertex(m, vx(cx, r, j), vy(cy, r, j), 0).setColor(edgeColor);
            vc.addVertex(m, vx(cx, r, i), vy(cy, r, i), 0).setColor(edgeColor);
            vc.addVertex(m, cx, cy, 0).setColor(centerColor);
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

    private static int lerpColor(int a, int b, float t) {
        int r = (int) Mth.lerp(t, (a >> 16) & 255, (b >> 16) & 255);
        int g = (int) Mth.lerp(t, (a >> 8) & 255, (b >> 8) & 255);
        int bl = (int) Mth.lerp(t, a & 255, b & 255);
        return (r << 16) | (g << 8) | bl;
    }
}
