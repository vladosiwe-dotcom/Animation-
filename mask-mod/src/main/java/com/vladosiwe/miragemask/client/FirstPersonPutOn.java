package com.vladosiwe.miragemask.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vladosiwe.miragemask.MirageMask;
import com.vladosiwe.miragemask.item.MirageMaskItem;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import org.joml.Matrix4f;

/**
 * Анимация надевания от первого лица, сделанная в Blender (tools/blender/put_on_animation.py):
 * на каждый тик — матрица руки и матрица маски в пространстве камеры. Здесь она проигрывается
 * с интерполяцией между тиками: рисуем руку игрока (его скин) и маску по этим матрицам.
 */
@EventBusSubscriber(modid = MirageMask.MOD_ID, value = Dist.CLIENT)
public final class FirstPersonPutOn {
    private static List<float[]> arm, mask;   // column-major, готово для JOML
    private static boolean loaded;

    private FirstPersonPutOn() {}

    private static boolean load() {
        if (loaded) return arm != null;
        loaded = true;
        try (Reader r = Minecraft.getInstance().getResourceManager()
                .getResourceOrThrow(MirageMask.id("animations/put_on_first_person.json")).openAsReader()) {
            JsonObject o = JsonParser.parseReader(r).getAsJsonObject();
            List<float[]> a = new ArrayList<>(), m = new ArrayList<>();
            for (JsonElement f : o.getAsJsonArray("frames")) {
                a.add(colMajor(f.getAsJsonObject().getAsJsonArray("arm")));
                m.add(colMajor(f.getAsJsonObject().getAsJsonArray("mask")));
            }
            arm = a;
            mask = m;
        } catch (Exception ex) {
            MirageMask.LOGGER.error("Mirage Mask: cannot load put_on_first_person.json", ex);
        }
        return arm != null;
    }

    private static float[] colMajor(JsonArray rowMajor) {
        float[] out = new float[16];
        for (int r = 0; r < 4; r++) for (int c = 0; c < 4; c++) out[c * 4 + r] = rowMajor.get(r * 4 + c).getAsFloat();
        return out;
    }

    private static Matrix4f sample(List<float[]> frames, float t, boolean mirror) {
        float f = Mth.clamp(t, 0, frames.size() - 1);
        int i = Math.min((int) f, frames.size() - 2);
        float k = f - i;
        float[] a = frames.get(i), b = frames.get(i + 1), out = new float[16];
        for (int j = 0; j < 16; j++) out[j] = a[j] + (b[j] - a[j]) * k;
        Matrix4f m = new Matrix4f().set(out);
        if (mirror) {   // левая рука: зеркалим пространство камеры по x
            Matrix4f s = new Matrix4f().scaling(-1, 1, 1);
            m = new Matrix4f(s).mul(m).mul(s);
        }
        return m;
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || !player.isUsingItem() || player.getUsedItemHand() != event.getHand()
                || !(player.getUseItem().getItem() instanceof MirageMaskItem) || !load()) {
            return;
        }
        event.setCanceled(true);
        float used = MirageMaskItem.PUT_ON_TICKS - (player.getUseItemRemainingTicks() - event.getPartialTick() + 1.0F);
        HumanoidArm side = event.getHand() == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        boolean left = side == HumanoidArm.LEFT;

        PoseStack pose = event.getPoseStack();
        PlayerRenderer renderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(player);
        pose.pushPose();
        pose.mulPose(sample(arm, used, left));
        if (left) renderer.renderLeftHand(pose, event.getMultiBufferSource(), event.getPackedLight(), player);
        else renderer.renderRightHand(pose, event.getMultiBufferSource(), event.getPackedLight(), player);
        pose.popPose();

        pose.pushPose();
        pose.mulPose(sample(mask, used, left));
        MaskModel.render(pose, event.getMultiBufferSource(), MaskModel.Mode.FULL, 0, ClientDisguises.time(event.getPartialTick()), 1.0F);
        pose.popPose();
    }
}
