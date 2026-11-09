package com.vladosiwe.miragemask.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

/** 3D-маска на лице настоящего игрока: прилипание, ободок при открытом лице, рассыпание. */
public class MaskLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public MaskLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int light, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ClientDisguises.Visual v = ClientDisguises.visual(player.getId(), partialTick);
        if (v == null || player.isInvisible()) return;
        pose.pushPose();
        getParentModel().head.translateAndRotate(pose);
        MaskMesh.render(pose, buffers, v.mode(), v.progress(), ClientDisguises.time(partialTick), 1.0F);
        pose.popPose();
    }
}
