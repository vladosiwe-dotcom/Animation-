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

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack pose, MultiBufferSource buffers,
                             int light, int overlay) {
        pose.pushPose();
        pose.translate(0.5F, 0.5F, 0.5F);
        pose.mulPose(Axis.YP.rotationDegrees(180));   // лицевая сторона маски — к зрителю (+Z)
        float s = 1.55F;
        pose.scale(s, -s, s);                          // в пикселях головы ось Y смотрит вниз
        pose.translate(0, 4 / 16F, 3.0F / 16F);        // центр лица — в центр предмета
        MaskMesh.render(pose, buffers, MaskMesh.Mode.FULL, 0, ClientDisguises.time(0), 1.0F, ctx == ItemDisplayContext.GUI);
        pose.popPose();
    }
}
