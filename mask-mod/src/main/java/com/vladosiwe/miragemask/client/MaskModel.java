package com.vladosiwe.miragemask.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vladosiwe.miragemask.MirageMask;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Маска по референсу: овальная стеклянная пластина из крупных шестиугольников,
 * светящиеся рёбра и обод, тёмные крепления со светящимися полосками.
 * Геометрия — из assets/miragemask/models/mask_mesh.json (её же использует сцена Blender).
 * Координаты — пиксели головы (y вниз, лицо на z = -4, «вперёд» = -z).
 */
public final class MaskModel {
    public enum Mode { FULL, STICK, SHATTER }

    private record Shaded(float[] pts, float shade) {}

    private record Cell(float[] center, float[] normal, float dist, float hash, List<Shaded> glass,
                        List<float[]> edges, List<float[]> glow) {}

    private static List<Cell> cells;
    private static List<float[]> rim, rimGlow, bars;
    private static List<Shaded> clips;
    private static float[] center;

    private MaskModel() {}

    private static boolean load() {
        if (cells != null) return true;
        try (Reader r = Minecraft.getInstance().getResourceManager()
                .getResourceOrThrow(MirageMask.id("models/mask_mesh.json")).openAsReader()) {
            JsonObject o = JsonParser.parseReader(r).getAsJsonObject();
            List<Cell> cs = new ArrayList<>();
            int i = 0;
            for (JsonElement e : o.getAsJsonArray("cells")) {
                JsonObject c = e.getAsJsonObject();
                List<Shaded> glass = new ArrayList<>();
                for (JsonElement g : c.getAsJsonArray("glass")) {
                    glass.add(new Shaded(pts(g.getAsJsonObject().getAsJsonArray("tri")), g.getAsJsonObject().get("shade").getAsFloat()));
                }
                cs.add(new Cell(vec(c.getAsJsonArray("center")), vec(c.getAsJsonArray("normal")), c.get("dist").getAsFloat(),
                        Mth.frac(Mth.sin(i++ * 12.9898F) * 43758.547F), glass, polys(c.getAsJsonArray("edges")),
                        polys(c.getAsJsonArray("glow"))));
            }
            List<Shaded> cl = new ArrayList<>();
            for (JsonElement box : o.getAsJsonArray("clips")) {
                for (JsonElement f : box.getAsJsonArray()) {
                    cl.add(new Shaded(pts(f.getAsJsonObject().getAsJsonArray("quad")), f.getAsJsonObject().get("shade").getAsFloat()));
                }
            }
            rim = polys(o.getAsJsonArray("rim"));
            rimGlow = polys(o.getAsJsonArray("rim_glow"));
            bars = polys(o.getAsJsonArray("bars"));
            clips = cl;
            center = vec(o.getAsJsonArray("center"));
            cells = cs;
            return true;
        } catch (Exception ex) {
            MirageMask.LOGGER.error("Mirage Mask: cannot load mask_mesh.json", ex);
            cells = List.of();
            return false;
        }
    }

    private static float[] vec(JsonArray a) {
        return new float[] {a.get(0).getAsFloat(), a.get(1).getAsFloat(), a.get(2).getAsFloat()};
    }

    private static float[] pts(JsonArray a) {
        float[] out = new float[a.size() * 3];
        for (int i = 0; i < a.size(); i++) {
            JsonArray p = a.get(i).getAsJsonArray();
            out[i * 3] = p.get(0).getAsFloat();
            out[i * 3 + 1] = p.get(1).getAsFloat();
            out[i * 3 + 2] = p.get(2).getAsFloat();
        }
        return out;
    }

    private static List<float[]> polys(JsonArray a) {
        List<float[]> out = new ArrayList<>();
        for (JsonElement e : a) out.add(pts(e.getAsJsonArray()));
        return out;
    }

    /** Центр маски (px) — для предметного рендера и анимаций. */
    public static float[] center() {
        load();
        return center == null ? new float[] {0, -3.35F, -5.2F} : center;
    }

    /**
     * @param progress STICK/SHATTER: 0..1
     * @param time     тики + partialTick (мерцание рёбер)
     */
    public static void render(PoseStack pose, MultiBufferSource buffers, Mode mode, float progress, float time, float alpha) {
        if (!load() || cells.isEmpty()) return;
        pose.pushPose();
        pose.scale(1 / 16F, 1 / 16F, 1 / 16F);
        Matrix4f m = pose.last().pose();

        // ---- состояние сот
        int n = cells.size();
        float[] k = new float[n], off = new float[n], a = new float[n];
        for (int i = 0; i < n; i++) {
            Cell c = cells.get(i);
            switch (mode) {
                case STICK -> {
                    float q = Mth.clamp((progress * 1.7F - c.dist * 0.8F) / 0.45F, 0, 1);
                    k[i] = backOut(q);                 // сота «шлёпается» на лицо с отскоком
                    off[i] = (1 - q) * 3.2F;
                    a[i] = q;
                }
                case SHATTER -> {
                    k[i] = 1 - progress * 0.6F;
                    off[i] = progress * (4 + c.hash * 7);
                    a[i] = 1 - progress;
                }
                default -> { k[i] = 1; off[i] = 0; a[i] = 1; }
            }
            a[i] *= alpha;
        }
        float frameA = alpha * switch (mode) {
            case STICK -> Mth.clamp((progress - 0.45F) / 0.4F, 0, 1);
            case SHATTER -> 1 - progress;
            default -> 1;
        };
        float clipSlide = mode == Mode.STICK ? (1 - Mth.clamp((progress - 0.5F) / 0.4F, 0, 1)) * 2.5F
                : mode == Mode.SHATTER ? -progress * 5 : 0;

        // ---- 1. тёмные крепления (непрозрачные)
        if (frameA > 0.05F) {
            VertexConsumer solid = buffers.getBuffer(MaskRenderTypes.SOLID);
            for (Shaded q : clips) {
                int col = rgb(0x17171E, q.shade());
                for (int v = 0; v < 4; v++) {
                    float x = q.pts()[v * 3], y = q.pts()[v * 3 + 1], z = q.pts()[v * 3 + 2];
                    x += Math.signum(x) * clipSlide;
                    if (mode == Mode.SHATTER) y -= clipSlide;
                    solid.addVertex(m, x, y, z).setColor(0xFF000000 | col);
                }
            }
        }

        // ---- 2. стекло
        VertexConsumer glass = buffers.getBuffer(MaskRenderTypes.GLASS);
        for (int i = 0; i < n; i++) {
            if (a[i] <= 0.01F) continue;
            Cell c = cells.get(i);
            for (Shaded t : c.glass()) {
                int col = rgb(0xA6AEE0, t.shade());
                int argb = ((int) (0.42F * a[i] * 255) << 24) | col;
                for (int v = 0; v < 4; v++) {
                    int vi = Math.min(v, 2);
                    put(glass, m, c, t.pts(), vi, k[i], off[i], argb);
                }
            }
        }

        // ---- 3. свечение: ореол, рёбра, обод, полоски
        VertexConsumer glow = buffers.getBuffer(MaskRenderTypes.ENERGY);
        for (int i = 0; i < n; i++) {
            if (a[i] <= 0.01F) continue;
            Cell c = cells.get(i);
            float pulse = 0.85F + 0.15F * Mth.sin(time * 0.15F + c.hash * 20);
            int halo = argb(0x7F9CFF, 0.20F * a[i] * pulse);
            int core = argb(0xEEF3FF, 0.95F * a[i] * pulse);
            for (float[] q : c.glow()) quad(glow, m, c, q, k[i], off[i], halo);
            for (float[] q : c.edges()) quad(glow, m, c, q, k[i], off[i], core);
        }
        if (frameA > 0.01F) {
            int halo = argb(0x7F9CFF, 0.22F * frameA), core = argb(0xF4F7FF, frameA);
            for (float[] q : rimGlow) quad(glow, m, null, q, 1, 0, halo);
            for (float[] q : rim) quad(glow, m, null, q, 1, 0, core);
            int bar = argb(0xFFFFFF, frameA);
            for (float[] q : bars) {
                for (int v = 0; v < 4; v++) {
                    float x = q[v * 3] + Math.signum(q[v * 3]) * clipSlide;
                    float y = q[v * 3 + 1] - (mode == Mode.SHATTER ? clipSlide : 0);
                    glow.addVertex(m, x, y, q[v * 3 + 2]).setColor(bar);
                }
            }
        }
        pose.popPose();
    }

    private static void quad(VertexConsumer vc, Matrix4f m, Cell c, float[] q, float k, float off, int color) {
        for (int v = 0; v < 4; v++) put(vc, m, c, q, v, k, off, color);
    }

    /** Вершина с учётом «полёта» соты: масштаб k вокруг центра соты и сдвиг off вдоль её нормали. */
    private static void put(VertexConsumer vc, Matrix4f m, Cell c, float[] p, int v, float k, float off, int color) {
        float x = p[v * 3], y = p[v * 3 + 1], z = p[v * 3 + 2];
        if (c != null && (k != 1 || off != 0)) {
            float[] cc = c.center(), nn = c.normal();
            x = cc[0] + (x - cc[0]) * k + nn[0] * off;
            y = cc[1] + (y - cc[1]) * k + nn[1] * off;
            z = cc[2] + (z - cc[2]) * k + nn[2] * off;
        }
        vc.addVertex(m, x, y, z).setColor(color);
    }

    private static float backOut(float x) {
        float c1 = 2.0F, c3 = c1 + 1, y = x - 1;
        return 1 + c3 * y * y * y + c1 * y * y;
    }

    private static int rgb(int rgb, float shade) {
        int r = (int) (((rgb >> 16) & 255) * shade), g = (int) (((rgb >> 8) & 255) * shade), b = (int) ((rgb & 255) * shade);
        return (Mth.clamp(r, 0, 255) << 16) | (Mth.clamp(g, 0, 255) << 8) | Mth.clamp(b, 0, 255);
    }

    static int argb(int rgb, float a) {
        return ((int) (Mth.clamp(a, 0, 1) * 255) << 24) | (rgb & 0xFFFFFF);
    }
}
