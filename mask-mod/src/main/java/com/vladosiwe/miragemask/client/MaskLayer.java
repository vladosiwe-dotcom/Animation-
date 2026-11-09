package com.vladosiwe.miragemask.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

/** 3D-маска на лице настоящего игрока: прилипание, маска при открытом лице (H), рассыпание. */
public class MaskLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public MaskLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffers, int light, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (player.isInvisible()) return;
        // надевание (вид со стороны): маска уходит из поднятой к глазу руки на центр лица
        if (player.isUsingItem() && player.getUseItem().getItem() instanceof com.vladosiwe.miragemask.item.MirageMaskItem) {
            float p = (com.vladosiwe.miragemask.item.MirageMaskItem.PUT_ON_TICKS - player.getUseItemRemainingTicks() + partialTick)
                    / com.vladosiwe.miragemask.item.MirageMaskItem.PUT_ON_TICKS;
            p = net.minecraft.util.Mth.clamp(p, 0, 1);
            float e = p * p * (3 - 2 * p);
            boolean right = (player.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND)
                    == (player.getMainArm() == net.minecraft.world.entity.HumanoidArm.RIGHT);
            float side = right ? -1 : 1;                      // правая рука модели — на -x
            pose.pushPose();
            getParentModel().head.translateAndRotate(pose);
            helmet(player, pose);
            float[] c = MaskModel.center();
            pose.translate((side * 7.5F * (1 - e)) / 16F, (2.0F * (1 - e)) / 16F, (-4.0F * (1 - e)) / 16F);
            pose.translate(c[0] / 16F, c[1] / 16F, c[2] / 16F);
            pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(side * 40F * (1 - e)));
            pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(side * 25F * (1 - e)));
            float s = 0.8F + 0.2F * e;
            pose.scale(s, s, s);
            pose.translate(-c[0] / 16F, -c[1] / 16F, -c[2] / 16F);
            MaskModel.render(pose, buffers, MaskModel.Mode.FULL, 0, ClientDisguises.time(partialTick), 1.0F);
            pose.popPose();
            return;
        }
        ClientDisguises.Visual v = ClientDisguises.visual(player.getId(), partialTick);
        if (v == null) return;
        pose.pushPose();
        getParentModel().head.translateAndRotate(pose);
        helmet(player, pose);
        MaskModel.render(pose, buffers, v.mode(), v.progress(), ClientDisguises.time(partialTick), 1.0F);
        pose.popPose();
    }

    /** Маска надевается поверх шлема: чуть крупнее, вокруг центра головы. */
    private static void helmet(AbstractClientPlayer player, PoseStack pose) {
        if (!player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).isEmpty()) {
            pose.translate(0, -4 / 16F, 0);
            pose.scale(1.14F, 1.14F, 1.14F);
            pose.translate(0, 4 / 16F, 0);
        }
    }
}
