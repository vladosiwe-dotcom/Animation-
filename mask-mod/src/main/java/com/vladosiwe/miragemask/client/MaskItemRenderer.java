package com.vladosiwe.miragemask.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Маска как предмет — тоже объёмная из светящихся сот (в руке, в инвентаре, на земле). */
public class MaskItemRenderer extends BlockEntityWithoutLevelRenderer {
    public MaskItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    /** Игрок, который сейчас рисуется (выставляет ClientDisguises): во время надевания маску рисует MaskLayer. */
    static net.minecraft.world.entity.player.Player renderingPlayer;

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack pose, MultiBufferSource buffers,
                             int light, int overlay) {
        if ((ctx == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || ctx == ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                && renderingPlayer != null && renderingPlayer.isUsingItem() && renderingPlayer.getUseItem() == stack) {
            return;
        }
        pose.pushPose();
        pose.translate(0.5F, 0.5F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(180));   // лицевая сторона маски — к зрителю (+Z)
        float s = 1.75F;
        pose.scale(s, -s, s);                          // в пикселях головы ось Y смотрит вниз
        float[] c = MaskModel.center();
        pose.translate(-c[0] / 16F, -c[1] / 16F, -c[2] / 16F);   // центр маски — в центр предмета
        MaskModel.render(pose, buffers, MaskModel.Mode.FULL, 0, ClientDisguises.time(0), 1.0F);
        pose.popPose();
    }
}
