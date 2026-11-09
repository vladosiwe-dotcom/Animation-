package com.vladosiwe.miragemask.item;

import com.vladosiwe.miragemask.disguise.DisguiseManager;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * Маска Миражей. ПКМ и удерживать — игрок одной рукой подносит маску к лицу (1 сек),
 * после чего на 60 секунд превращается для всех в моба / жителя / копию другого игрока.
 */
public class MirageMaskItem extends Item {
    public static final int PUT_ON_TICKS = 20;
    public static final int DURATION_TICKS = 60 * 20;
    public static final int RECHARGE_TICKS = 120 * 20;

    public MirageMaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (DisguiseManager.isDisguised(player)) {
            return InteractionResultHolder.fail(stack);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 0.6F);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            // маска уходит из руки на лицо; вернётся в инвентарь, когда действие закончится
            if (DisguiseManager.activate(player, stack.copy())) {
                player.getCooldowns().addCooldown(this, DURATION_TICKS + RECHARGE_TICKS);
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return PUT_ON_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE; // своя анимация руки — в клиентском расширении предмета
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.miragemask.mirage_mask.desc1").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.miragemask.mirage_mask.desc2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.miragemask.mirage_mask.desc3").withStyle(ChatFormatting.DARK_GRAY));
    }
}
