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
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Маска как воксельная модель Blockbench: assets/miragemask/geo/mirage_mask.json (формат Java Block/Item)
 * + пиксель-арт текстура и отдельная текстура свечения. Модель строит tools/mask_blockbench.py.
 * Координаты модели — как в Blockbench (y вверх, голова = куб 4..12); при рендере переводятся
 * в координаты головы ModelPart: (x - 8, -y, z - 8).
 */
public final class MaskModel {
    public enum Mode { FULL, STICK, SHATTER }

    public static final ResourceLocation TEXTURE = MirageMask.id("textures/entity/mirage_mask.png");
    public static final ResourceLocation GLOW = MirageMask.id("textures/entity/mirage_mask_glow.png");

    /** Грань: 4 угла (x,y,z) в координатах модели, uv углов (0..1), нормаль. */
    private record Face(float[] pos, float[] uv, float nx, float ny, float nz) {}

    private record Cube(boolean clip, float cx, float cy, float cz, float dist, float hash, List<Face> faces) {}

    private static List<Cube> cubes;
    private static float[] center = {0, -4, -5.1F};

    private MaskModel() {}

    private static boolean load() {
        if (cubes != null) return !cubes.isEmpty();
        List<Cube> out = new ArrayList<>();
        try (Reader r = Minecraft.getInstance().getResourceManager()
                .getResourceOrThrow(MirageMask.id("geo/mirage_mask.json")).openAsReader()) {
            JsonObject o = JsonParser.parseReader(r).getAsJsonObject();
            JsonArray c = o.getAsJsonObject("mirage_mask").getAsJsonArray("center");
            center = new float[] {c.get(0).getAsFloat() - 8, -c.get(1).getAsFloat(), c.get(2).getAsFloat() - 8};
            int i = 0;
            for (JsonElement e : o.getAsJsonArray("elements")) {
                JsonObject el = e.getAsJsonObject();
                float[] f = vec(el.getAsJsonArray("from")), t = vec(el.getAsJsonArray("to"));
                List<Face> faces = new ArrayList<>();
                for (Map.Entry<String, JsonElement> fe : el.getAsJsonObject("faces").entrySet()) {
                    JsonArray uv = fe.getValue().getAsJsonObject().getAsJsonArray("uv");
                    // UV в модели Minecraft — в единицах 0..16 на всю текстуру
                    faces.add(face(fe.getKey(), f, t, uv.get(0).getAsFloat() / 16F, uv.get(1).getAsFloat() / 16F,
                            uv.get(2).getAsFloat() / 16F, uv.get(3).getAsFloat() / 16F));
                }
                float cx = (f[0] + t[0]) / 2, cy = (f[1] + t[1]) / 2, cz = (f[2] + t[2]) / 2;
                float dist = (float) Math.sqrt((cx - 8) * (cx - 8) + (cy - 4) * (cy - 4)) / 5.5F;
                out.add(new Cube(el.get("name").getAsString().startsWith("clip"), cx, cy, cz, dist,
                        Mth.frac(Mth.sin(i++ * 12.9898F) * 43758.547F), faces));
            }
        } catch (Exception ex) {
            MirageMask.LOGGER.error("Mirage Mask: cannot load geo/mirage_mask.json", ex);
        }
        cubes = out;
        return !cubes.isEmpty();
    }

    private static float[] vec(JsonArray a) {
        return new float[] {a.get(0).getAsFloat(), a.get(1).getAsFloat(), a.get(2).getAsFloat()};
    }

    /**
     * Углы грани в порядке: верх-лево, низ-лево, низ-право, верх-право (как смотрит зритель на грань),
     * по правилам UV моделей Minecraft. u0..u1, v0..v1 — доли текстуры (0..1).
     */
    private static Face face(String dir, float[] f, float[] t, float u0, float v0, float u1, float v1) {
        float x0 = f[0], y0 = f[1], z0 = f[2], x1 = t[0], y1 = t[1], z1 = t[2];
        float[] p;
        float nx = 0, ny = 0, nz = 0;
        switch (dir) {
            case "north" -> { p = new float[] {x1, y1, z0, x1, y0, z0, x0, y0, z0, x0, y1, z0}; nz = -1; }
            case "south" -> { p = new float[] {x0, y1, z1, x0, y0, z1, x1, y0, z1, x1, y1, z1}; nz = 1; }
            case "east" -> { p = new float[] {x1, y1, z1, x1, y0, z1, x1, y0, z0, x1, y1, z0}; nx = 1; }
            case "west" -> { p = new float[] {x0, y1, z0, x0, y0, z0, x0, y0, z1, x0, y1, z1}; nx = -1; }
            case "up" -> { p = new float[] {x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0}; ny = 1; }
            default -> { p = new float[] {x0, y0, z1, x0, y0, z0, x1, y0, z0, x1, y0, z1}; ny = -1; }
        }
        float[] uv = {u0, v0, u0, v1, u1, v1, u1, v0};
        return new Face(p, uv, nx, ny, nz);
    }

    /** Центр маски в координатах головы (px). */
    public static float[] center() {
        load();
        return center;
    }

    /**
     * Рисует маску в координатах головы ModelPart (единицы = пиксели / 16 после внутреннего масштаба).
     * @param progress STICK/SHATTER: 0..1
     */
    public static void render(PoseStack pose, MultiBufferSource buffers, Mode mode, float progress, float time, float alpha) {
        if (!load()) return;
        pose.pushPose();
        pose.scale(1 / 16F, 1 / 16F, 1 / 16F);
        pose.scale(1, -1, 1);           // Blockbench: y вверх -> голова: y вниз
        pose.translate(-8, 0, -8);
        PoseStack.Pose last = pose.last();

        int n = cubes.size();
        float[] k = new float[n], off = new float[n], a = new float[n], side = new float[n];
        for (int i = 0; i < n; i++) {
            Cube c = cubes.get(i);
            k[i] = 1; off[i] = 0; a[i] = alpha;
            switch (mode) {
                case STICK -> {
                    if (c.clip()) {
                        float q = Mth.clamp((progress - 0.5F) / 0.4F, 0, 1);
                        side[i] = (1 - q) * 3F;   // крепления «защёлкиваются» с боков в конце
                        a[i] *= q;
                    } else {
                        float q = Mth.clamp((progress * 1.7F - c.dist() * 0.8F) / 0.45F, 0, 1);
                        k[i] = backOut(q);       // кусочки маски «шлёпаются» на лицо с отскоком
                        off[i] = (1 - q) * 3.5F;
                        a[i] *= q;
                    }
                }
                case SHATTER -> {
                    k[i] = 1 - progress * 0.6F;
                    off[i] = progress * (4 + c.hash() * 7);
                    side[i] = c.clip() ? progress * 3 : 0;
                    a[i] *= 1 - progress;
                }
                default -> {}
            }
        }
        VertexConsumer glass = buffers.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        emit(glass, last, k, off, a, side, 1F, false);
        float pulse = 0.8F + 0.2F * Mth.sin(time * 0.15F);
        VertexConsumer glow = buffers.getBuffer(RenderType.eyes(GLOW));
        emit(glow, last, k, off, a, side, pulse, true);
        pose.popPose();
    }

    private static void emit(VertexConsumer vc, PoseStack.Pose last, float[] k, float[] off, float[] a, float[] side,
                             float bright, boolean bothSides) {
        for (int i = 0; i < cubes.size(); i++) {
            if (a[i] <= 0.01F) continue;
            Cube c = cubes.get(i);
            int alpha = (int) (Mth.clamp(a[i], 0, 1) * 255);
            int col = (int) (Mth.clamp(bright * a[i], 0, 1) * 255);
            for (Face f : c.faces()) {
                for (int pass = 0; pass < (bothSides ? 2 : 1); pass++) {
                    for (int j = 0; j < 4; j++) {
                        int v = pass == 0 ? j : 3 - j;
                        float x = f.pos()[v * 3], y = f.pos()[v * 3 + 1], z = f.pos()[v * 3 + 2];
                        x = c.cx() + (x - c.cx()) * k[i] + Math.signum(c.cx() - 8) * side[i];
                        y = c.cy() + (y - c.cy()) * k[i];
                        z = c.cz() + (z - c.cz()) * k[i] - off[i];      // «вперёд» = -z
                        vc.addVertex(last, x, y, z)
                                .setColor(bothSides ? col : 255, bothSides ? col : 255, bothSides ? col : 255, bothSides ? 255 : alpha)
                                .setUv(f.uv()[v * 2], f.uv()[v * 2 + 1])
                                .setOverlay(OverlayTexture.NO_OVERLAY)
                                .setLight(0xF000F0)
                                .setNormal(last, f.nx(), f.ny(), f.nz());
                    }
                }
            }
        }
    }

    private static float backOut(float x) {
        float c1 = 2.0F, c3 = c1 + 1, y = x - 1;
        return 1 + c3 * y * y * y + c1 * y * y;
    }
}
