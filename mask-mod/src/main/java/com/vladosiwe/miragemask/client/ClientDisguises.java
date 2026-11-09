package com.vladosiwe.miragemask.client;

import com.mojang.authlib.GameProfile;
import com.vladosiwe.miragemask.MirageMask;
import com.vladosiwe.miragemask.network.ClientBridge;
import com.vladosiwe.miragemask.network.DisguisePayload;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

/**
 * Клиент: для каждого замаскированного игрока держим «куклу» — моба или копию игрока,
 * не добавленную в мир. Вместо игрока рисуем куклу, синхронизируя положение, повороты и шаг.
 */
@EventBusSubscriber(modid = MirageMask.MOD_ID, value = Dist.CLIENT)
public final class ClientDisguises {
    public static final int STICK_TICKS = 14;
    static final int SHATTER_TICKS = 16;
    static int clientTicks;

    /** Что рисовать на лице настоящего игрока (3D-маска) — для MaskLayer. */
    public record Visual(MaskModel.Mode mode, float progress) {}

    private static final Map<Integer, Integer> SHATTER = new HashMap<>();

    static final class Doll {
        DisguisePayload info;
        int stickStart = -1000;
        final LivingEntity entity;
        boolean fresh = true;
        int stillTicks;
        // «мобье» поведение на месте
        float idleYaw, idlePitch, bodyYaw;
        int idleTimer;
        Doll(DisguisePayload info, LivingEntity entity) {
            this.info = info;
            this.entity = entity;
        }
    }

    private static final Map<Integer, Doll> DOLLS = new HashMap<>();
    private static final RandomSource RANDOM = RandomSource.create();

    private ClientDisguises() {}

    public static void handle(DisguisePayload p) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;
        boolean local = mc.player != null && mc.player.getId() == p.entityId();
        if (!p.active()) {
            DOLLS.remove(p.entityId());
            if (p.flash() == DisguisePayload.FLASH_BREAK || p.flash() == DisguisePayload.FLASH_END) {
                SHATTER.put(p.entityId(), clientTicks);
            }
            if (local) {
                ClientBridge.localDisguisedId = Integer.MIN_VALUE;
                HexOverlay.stop(p.flash());
            }
            return;
        }
        Doll old = DOLLS.get(p.entityId());
        if (old != null && sameLook(old.info, p)) {
            old.info = p;                                   // пауза/фиксация — кукла та же
        } else {
            LivingEntity doll = createDoll(level, p);
            if (doll == null) return;
            Doll fresh = new Doll(p, doll);
            if (old != null) fresh.stickStart = old.stickStart;
            DOLLS.put(p.entityId(), fresh);
        }
        Doll d = DOLLS.get(p.entityId());
        if (p.flash() == DisguisePayload.FLASH_ACTIVATE) d.stickStart = clientTicks;
        SHATTER.remove(p.entityId());
        if (local) {
            ClientBridge.localDisguisedId = p.entityId();
            HexOverlay.start(p, describe(p));
        }
    }

    private static boolean sameLook(DisguisePayload a, DisguisePayload b) {
        return a.kind() == b.kind() && a.entityType().equals(b.entityType())
                && java.util.Objects.equals(a.profileId(), b.profileId());
    }

    /** Кукла рисуется вместо игрока, только когда маска уже прилипла и лицо не открыто. */
    private static boolean showsDoll(Doll d) {
        return !d.info.paused() && clientTicks - d.stickStart >= STICK_TICKS;
    }

    public static Visual visual(int entityId, float partialTick) {
        Doll d = DOLLS.get(entityId);
        if (d != null) {
            float age = clientTicks - d.stickStart + partialTick;
            if (age < STICK_TICKS) return new Visual(MaskModel.Mode.STICK, age / STICK_TICKS);
            if (d.info.paused()) return new Visual(MaskModel.Mode.FULL, 0);   // маскировка неактивна — маска на лице
            return null;
        }
        Integer s = SHATTER.get(entityId);
        if (s != null) {
            float age = clientTicks - s + partialTick;
            if (age < SHATTER_TICKS) return new Visual(MaskModel.Mode.SHATTER, age / SHATTER_TICKS);
        }
        return null;
    }

    /** Кукла локального игрока — для портрета в HUD. */
    public static LivingEntity localDoll() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return null;
        Doll d = DOLLS.get(mc.player.getId());
        return d == null ? null : d.entity;
    }

    public static float time(float partialTick) {
        return clientTicks + partialTick;
    }

    public static Component describe(DisguisePayload p) {
        if (p.isPlayerCopy()) return Component.literal(p.profileName());
        return BuiltInRegistries.ENTITY_TYPE.get(p.entityType()).getDescription();
    }

    private static LivingEntity createDoll(ClientLevel level, DisguisePayload p) {
        if (p.isPlayerCopy() && p.profileId() != null) {
            RemotePlayer copy = new RemotePlayer(level, new GameProfile(p.profileId(), p.profileName())) {
                @Override
                public boolean isModelPartShown(PlayerModelPart part) {
                    return true; // второй слой скина (куртка, рукава, шляпа)
                }
            };
            EquipmentSlot[] slots = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.FEET,
                    EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
            for (int i = 0; i < slots.length && i < p.equipment().size(); i++) {
                copy.setItemSlot(slots[i], p.equipment().get(i).copy());
            }
            return copy;
        }
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(p.entityType());
        Entity e = type.create(level);
        if (!(e instanceof LivingEntity living)) return null;
        if (living instanceof Villager villager && Minecraft.getInstance().player != null) {
            Entity owner = level.getEntity(p.entityId());
            if (owner != null) {
                villager.setVillagerData(villager.getVillagerData()
                        .setType(VillagerType.byBiome(level.getBiome(owner.blockPosition()))));
            }
        }
        if (living instanceof Mob mob) mob.setNoAi(true);
        return living;
    }

    // ------------------------------------------------------------------ рендер
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        MaskItemRenderer.renderingPlayer = event.getEntity();
        Doll doll = DOLLS.get(event.getEntity().getId());
        if (doll == null || !showsDoll(doll)) return;
        event.setCanceled(true); // настоящий игрок (с мечом, бронёй, ником) не рисуется вообще
        LivingEntity e = doll.entity;
        float pt = event.getPartialTick();
        EntityRenderer<? super LivingEntity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(e);
        renderer.render(e, Mth.lerp(pt, e.yRotO, e.getYRot()), pt, event.getPoseStack(),
                event.getMultiBufferSource(), event.getPackedLight());
    }

    // ------------------------------------------------------------------ синхронизация куклы
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.isPaused()) return;
        clientTicks++;
        HexOverlay.tick();
        SHATTER.values().removeIf(start -> clientTicks - start > SHATTER_TICKS + 2);
        Iterator<Map.Entry<Integer, Doll>> it = DOLLS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Doll> entry = it.next();
            Entity owner = mc.level.getEntity(entry.getKey());
            if (!(owner instanceof Player player)) {
                continue; // игрок вне зоны видимости — кукла ждёт
            }
            Doll d = entry.getValue();
            follow(d, player, mc.level);
        }
    }

    private static void follow(Doll d, Player p, ClientLevel level) {
        LivingEntity e = d.entity;
        if (d.fresh) {
            e.setPos(p.getX(), p.getY(), p.getZ());
            e.xo = p.getX(); e.yo = p.getY(); e.zo = p.getZ();
            e.yBodyRot = e.yBodyRotO = p.yBodyRot;
            e.yHeadRot = e.yHeadRotO = p.yHeadRot;
            e.setYRot(p.yBodyRot); e.yRotO = p.yBodyRot;
            e.setXRot(p.getXRot()); e.xRotO = p.getXRot();
            d.bodyYaw = p.yBodyRot;
            d.fresh = false;
        }
        double dx = p.getX() - e.getX(), dz = p.getZ() - e.getZ();
        float moved = (float) Math.sqrt(dx * dx + dz * dz);
        e.xo = e.getX(); e.yo = e.getY(); e.zo = e.getZ();
        e.xOld = e.xo; e.yOld = e.yo; e.zOld = e.zo;
        e.setPos(p.getX(), p.getY(), p.getZ());
        e.yBodyRotO = e.yBodyRot;
        e.yHeadRotO = e.yHeadRot;
        e.yRotO = e.getYRot();
        e.xRotO = e.getXRot();
        e.tickCount = p.tickCount;
        e.setOnGround(p.onGround());
        e.wasTouchingWater = p.isInWater();     // рыбы и аксолотли «плывут», а не бьются на суше
        e.setSprinting(p.isSprinting());
        e.walkAnimation.update(Math.min(moved * 4.0F, 1.0F), 0.4F);
        if (e instanceof Player copy) {
            copy.setPose(p.getPose());
            copy.setShiftKeyDown(p.isShiftKeyDown());
            copy.setSwimming(p.isSwimming());
        }

        boolean turning = Math.abs(Mth.wrapDegrees(p.yHeadRot - p.yHeadRotO)) > 2.0F;
        d.stillTicks = (moved < 0.01F && !turning && !d.info.isPlayerCopy()) ? d.stillTicks + 1 : 0;
        if (d.stillTicks > 40) {
            idle(d, p, level);
        } else {
            d.bodyYaw = p.yBodyRot;
            d.idleTimer = 0;
            e.yBodyRot = p.yBodyRot;
            e.setYRot(p.yBodyRot);
            e.yHeadRot = p.yHeadRot;
            e.setXRot(p.getXRot());
        }
    }

    /**
     * Игрок стоит — кукла ведёт себя как моб: озирается, смотрит на проходящих игроков,
     * животные «щиплют траву», тело иногда разворачивается вслед за головой.
     */
    private static void idle(Doll d, Player p, ClientLevel level) {
        LivingEntity e = d.entity;
        if (--d.idleTimer <= 0) {
            d.idleTimer = 40 + RANDOM.nextInt(60);
            Player watcher = level.getNearestPlayer(p.getX(), p.getY(), p.getZ(), 8.0,
                    other -> other != p && !other.isSpectator());
            int roll = RANDOM.nextInt(10);
            if (watcher != null && roll < 4) {
                double wx = watcher.getX() - p.getX(), wz = watcher.getZ() - p.getZ();
                double wy = watcher.getEyeY() - p.getEyeY();
                d.idleYaw = (float) (Mth.atan2(wz, wx) * Mth.RAD_TO_DEG) - 90.0F;
                d.idlePitch = (float) -(Mth.atan2(wy, Math.sqrt(wx * wx + wz * wz)) * Mth.RAD_TO_DEG);
            } else if (e instanceof Animal && roll < 7) {
                d.idleYaw = d.bodyYaw + RANDOM.nextInt(30) - 15;
                d.idlePitch = 55.0F; // голова к земле — пасётся
            } else {
                d.idleYaw = d.bodyYaw + RANDOM.nextInt(140) - 70;
                d.idlePitch = RANDOM.nextInt(40) - 15;
            }
        }
        e.yHeadRot = approach(e.yHeadRot, d.idleYaw, 9.0F);
        e.setXRot(approachLinear(e.getXRot(), d.idlePitch, 6.0F));
        // как у мобов: если голова отвернулась слишком сильно — тело догоняет
        float diff = Mth.wrapDegrees(e.yHeadRot - d.bodyYaw);
        if (Math.abs(diff) > 50.0F) d.bodyYaw += diff - Math.signum(diff) * 50.0F;
        e.yBodyRot = approach(e.yBodyRot, d.bodyYaw, 6.0F);
        e.setYRot(e.yBodyRot);
    }

    private static float approach(float from, float to, float step) {
        return from + Mth.clamp(Mth.wrapDegrees(to - from), -step, step);
    }

    private static float approachLinear(float from, float to, float step) {
        return from + Mth.clamp(to - from, -step, step);
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        DOLLS.clear();
        SHATTER.clear();
        ClientBridge.localDisguisedId = Integer.MIN_VALUE;
        HexOverlay.reset();
    }

}
