package com.vladosiwe.miragemask.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.vladosiwe.miragemask.MirageMask;
import com.vladosiwe.miragemask.item.MirageMaskItem;
import com.vladosiwe.miragemask.registry.ModItems;
import com.vladosiwe.miragemask.registry.ModParticles;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = MirageMask.MOD_ID, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    @SubscribeEvent
    public static void particles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.HEX.get(), HexParticle.Provider::new);
    }

    @SubscribeEvent
    public static void guiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(MirageMask.id("hex_overlay"), HexOverlay::render);
    }

    @SubscribeEvent
    public static void itemExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new MaskHand(), ModItems.MIRAGE_MASK.get());
    }

    /** Анимация надевания: одна рука подносит маску к лицу. */
    static final class MaskHand implements IClientItemExtensions {
        @Override
        public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
            if (entity.isUsingItem() && entity.getUsedItemHand() == hand) {
                return HumanoidModel.ArmPose.SPYGLASS; // вид от третьего лица: рука у лица
            }
            return null;
        }

        @Override
        public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack,
                                               float partialTick, float equipProcess, float swingProcess) {
            if (!player.isUsingItem() || player.getUseItem() != stack) return false;
            int side = arm == HumanoidArm.RIGHT ? 1 : -1;
            float used = MirageMaskItem.PUT_ON_TICKS - (player.getUseItemRemainingTicks() - partialTick + 1.0F);
            float t = Mth.clamp(used / MirageMaskItem.PUT_ON_TICKS, 0, 1);
            float e = t * t * (3 - 2 * t); // плавный старт и финиш
            // из обычного положения руки — к центру лица, маска разворачивается к камере и «наплывает»
            pose.translate(Mth.lerp(e, side * 0.56F, 0.0F), Mth.lerp(e, -0.52F, -0.12F), Mth.lerp(e, -0.72F, -0.30F));
            // модель в руке развёрнута боком (display -70°) — доворачиваем маску лицом к камере
            pose.mulPose(Axis.YP.rotationDegrees(side * 70.0F * e));
            pose.mulPose(Axis.ZP.rotationDegrees(-side * 8.0F * e));
            float s = 1.0F + 1.6F * e * e;
            pose.scale(s, s, s);
            if (t > 0.92F) {
                float shake = (t - 0.92F) * 0.08F;
                pose.translate(Mth.sin(player.tickCount * 3.1F) * shake, Mth.cos(player.tickCount * 2.7F) * shake, 0);
            }
            return true;
        }
    }
}
