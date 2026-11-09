package com.vladosiwe.miragemask.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * 3D-маска из шестиугольников, облегающая лицо. Координаты — в «пикселях» головы игрока
 * (голова 8×8×8, лицо на z = -4, макушка y = -8, подбородок y = 0), как у ModelPart.
 * Прорези для глаз и рта оставлены пустыми.
 */
public final class MaskMesh {
    public enum Mode { FULL, STICK, RING, SHATTER }

    static final int CORE = 0x0096FF;   // насыщенный голубой
    static final int EDGE = 0x5FD8FF;   // светлая кромка
    static final int HOT = 0xFFFFFF;

    private record Tile(float cx, float cy, float cz, float tx, float tz, float nx, float nz,
                        float u, float v, float dist, float hash, boolean ring) {}

    private static final float R = 1.02F;       // радиус соты (px)
    private static final List<Tile> TILES = build();

    private MaskMesh() {}

    private static List<Tile> build() {
        List<Tile> out = new ArrayList<>();
        float colStep = (float) (Math.sqrt(3) * R), rowStep = 1.5F * R;
        for (int row = 0; row < 8; row++) {
            float v = -8.15F + row * rowStep;
            if (v > 0.4F) break;                                                  // не ниже подбородка
            for (int col = -6; col <= 6; col++) {
                float u = col * colStep + ((row & 1) == 1 ? colStep / 2 : 0);
                float au = Math.abs(u);
                if (au > 1.0F && au < 3.6F && v > -5.3F && v < -2.7F) continue;   // глаза
                if (au < 2.0F && v > -2.0F && v < -0.4F) continue;                // рот
                double theta = u / 4.45;
                if (Math.abs(theta) > 1.2) continue;                               // обхват до висков
                float[] p = shell(theta), p2 = shell(theta + 0.01);
                float tx = p2[0] - p[0], tz = p2[1] - p[1];
                float len = (float) Math.sqrt(tx * tx + tz * tz);
                tx /= len; tz /= len;
                float dist = (float) Math.sqrt(u * u + (v + 4) * (v + 4)) / 6.5F;
                float hash = Mth.frac(Mth.sin(col * 12.9898F + row * 78.233F) * 43758.547F);
                boolean ring = au > 3.3F || v < -7.0F || v > -0.9F;
                out.add(new Tile(p[0], v, p[1], tx, tz, tz, -tx, u, v, dist, hash, ring));
            }
        }
        return out;
    }

    /** Сверхэллипс, плотно обнимающий квадратную голову (полуразмер 4 px + зазор). */
    private static float[] shell(double theta) {
        double s = Math.sin(theta), c = Math.cos(theta);
        double d = Math.pow(Math.pow(Math.abs(s), 4) + Math.pow(Math.abs(c), 4), -0.25);
        double r = 4.32;
        return new float[] {(float) (r * d * s), (float) (-r * d * c)};
    }

    /**
     * @param progress для STICK и SHATTER — 0..1
     * @param time     тики + partialTick (для мерцания и «искажения» ауры)
     */
    public static void render(PoseStack pose, MultiBufferSource buffers, Mode mode, float progress, float time, float alphaMul) {
        render(pose, buffers, mode, progress, time, alphaMul, false);
    }

    /** underlay — тёмно-синяя «основа» под свечением, чтобы иконка читалась на светлом слоте. */
    public static void render(PoseStack pose, MultiBufferSource buffers, Mode mode, float progress, float time,
                              float alphaMul, boolean underlay) {
        pose.pushPose();
        pose.scale(1 / 16F, 1 / 16F, 1 / 16F);
        Matrix4f m = pose.last().pose();
        if (underlay) {
            VertexConsumer base = buffers.getBuffer(net.minecraft.client.renderer.RenderType.gui());
            for (Tile t : TILES) {
                hex(base, m, t, t.cx + t.nx * 0.05F, t.cy, t.cz + t.nz * 0.05F, R * 0.97F, 0, 0xE0083A8C, 0xE0052A6E);
            }
        }
        VertexConsumer vc = buffers.getBuffer(MaskRenderTypes.ENERGY);
        for (Tile t : TILES) {
            float k = 1, off = 0.12F, a = 1;
            float flicker = 0.78F + 0.22F * Mth.sin(time * 0.35F + t.hash * 40);
            switch (mode) {
                case STICK -> {
                    float q = Mth.clamp((progress * 1.6F - t.dist * 0.9F) / 0.45F, 0, 1);
                    if (q <= 0) continue;
                    k = backOut(q);                       // соты «шлёпаются» на лицо с отскоком
                    off = 0.12F + (1 - q) * 3.5F;
                    a = q;
                }
                case RING -> {
                    if (!t.ring) continue;
                    a = 0.55F * flicker * (t.hash > 0.85F && Mth.sin(time * 0.9F + t.hash * 50) > 0.6F ? 0.2F : 1);
                }
                case SHATTER -> {
                    off = 0.12F + progress * (4 + t.hash * 6);
                    k = 1 - progress * 0.7F;
                    a = 1 - progress;
                }
                default -> a = flicker;
            }
            a *= alphaMul;
            if (a <= 0.01F) continue;
            float cx = t.cx + t.nx * off, cy = t.cy, cz = t.cz + t.nz * off;
            // заливка: ярче в центре, к краю соты гаснет
            hex(vc, m, t, cx, cy, cz, R * k * 0.86F, 0, argb(EDGE, a * 0.38F), argb(CORE, a * 0.22F));
            // выраженная кромка соты
            ring(vc, m, t, cx, cy, cz, R * k * 0.70F, R * k * 0.93F, argb(CORE, a * 0.95F));
            ring(vc, m, t, cx, cy, cz, R * k * 0.79F, R * k * 0.86F, argb(HOT, a * 0.35F));
            // аура: второй слой сот чуть дальше от лица, «дрожит» как искажённый воздух
            if (mode != Mode.SHATTER) {
                float wob = 0.55F + 0.35F * Mth.sin(time * 0.22F + t.hash * 6.28F);
                float jx = 0.12F * Mth.sin(time * 0.5F + t.u), jy = 0.12F * Mth.cos(time * 0.43F + t.v * 1.7F);
                ring(vc, m, t, cx + t.nx * wob + jx, cy + jy, cz + t.nz * wob, R * k * 0.95F, R * k * 1.05F,
                        argb(EDGE, a * 0.16F));
            }
        }
        pose.popPose();
    }

    private static float backOut(float x) {
        float c1 = 2.2F, c3 = c1 + 1;
        float y = x - 1;
        return 1 + c3 * y * y * y + c1 * y * y;
    }

    private static float cornerX(Tile t, float cx, float r, int i) {
        return cx + t.tx * r * Mth.cos(Mth.DEG_TO_RAD * (60 * i - 30));
    }

    private static float cornerY(float cy, float r, int i) {
        return cy + r * Mth.sin(Mth.DEG_TO_RAD * (60 * i - 30));
    }

    private static float cornerZ(Tile t, float cz, float r, int i) {
        return cz + t.tz * r * Mth.cos(Mth.DEG_TO_RAD * (60 * i - 30));
    }

    private static void hex(VertexConsumer vc, Matrix4f m, Tile t, float cx, float cy, float cz, float r, float unused,
                            int center, int edge) {
        for (int i = 0; i < 6; i++) {
            int j = (i + 1) % 6;
            vc.addVertex(m, cx, cy, cz).setColor(center);
            vc.addVertex(m, cornerX(t, cx, r, i), cornerY(cy, r, i), cornerZ(t, cz, r, i)).setColor(edge);
            vc.addVertex(m, cornerX(t, cx, r, j), cornerY(cy, r, j), cornerZ(t, cz, r, j)).setColor(edge);
            vc.addVertex(m, cx, cy, cz).setColor(center);
        }
    }

    private static void ring(VertexConsumer vc, Matrix4f m, Tile t, float cx, float cy, float cz, float r0, float r1, int color) {
        for (int i = 0; i < 6; i++) {
            int j = (i + 1) % 6;
            vc.addVertex(m, cornerX(t, cx, r0, i), cornerY(cy, r0, i), cornerZ(t, cz, r0, i)).setColor(color);
            vc.addVertex(m, cornerX(t, cx, r1, i), cornerY(cy, r1, i), cornerZ(t, cz, r1, i)).setColor(color);
            vc.addVertex(m, cornerX(t, cx, r1, j), cornerY(cy, r1, j), cornerZ(t, cz, r1, j)).setColor(color);
            vc.addVertex(m, cornerX(t, cx, r0, j), cornerY(cy, r0, j), cornerZ(t, cz, r0, j)).setColor(color);
        }
    }

    static int argb(int rgb, float a) {
        return ((int) (Mth.clamp(a, 0, 1) * 255) << 24) | (rgb & 0xFFFFFF);
    }
}
