package com.vladosiwe.miragemask.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Маска как предмет: объёмная модель Blockbench в руке и на земле, пиксельная иконка в инвентаре. */
public class MaskItemRenderer extends BlockEntityWithoutLevelRenderer {
    public MaskItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    private static final net.minecraft.resources.ResourceLocation ICON =
            com.vladosiwe.miragemask.MirageMask.id("textures/item/mirage_mask.png");

    /** Игрок, который сейчас рисуется (выставляет ClientDisguises): во время надевания маску рисует MaskLayer. */
    static net.minecraft.world.entity.player.Player renderingPlayer;

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack pose, MultiBufferSource buffers,
                             int light, int overlay) {
        if ((ctx == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || ctx == ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                && renderingPlayer != null && renderingPlayer.isUsingItem() && renderingPlayer.getUseItem() == stack) {
            return;
        }
        if (ctx == ItemDisplayContext.GUI) {
            // в инвентаре — пиксельная иконка (маска анфас), как на референсе
            pose.pushPose();
            PoseStack.Pose p = pose.last();
            com.mojang.blaze3d.vertex.VertexConsumer vc = buffers.getBuffer(
                    net.minecraft.client.renderer.RenderType.entityTranslucent(ICON));
            float[][] q = {{0, 1, 0, 0}, {0, 0, 0, 1}, {1, 0, 1, 1}, {1, 1, 1, 0}};
            for (float[] v : q) {
                vc.addVertex(p, v[0], v[1], 0.5F).setColor(255, 255, 255, 255).setUv(v[2], v[3])
                        .setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
                        .setLight(0xF000F0).setNormal(p, 0, 0, 1);
            }
            pose.popPose();
            return;
        }
        pose.pushPose();
        pose.translate(0.5F, 0.5F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(180));   // лицевая сторона маски — к зрителю (+Z)
        float s = 1.9F;
        pose.scale(s, -s, s);                          // в пикселях головы ось Y смотрит вниз
        float[] c = MaskModel.center();
        pose.translate(-c[0] / 16F, -c[1] / 16F, -c[2] / 16F);   // центр маски — в центр предмета
        MaskModel.render(pose, buffers, MaskModel.Mode.FULL, 0, ClientDisguises.time(0), 1.0F);
        pose.popPose();
    }
}
