package com.vladosiwe.miragemask.disguise;

import com.vladosiwe.miragemask.network.DisguisePayload;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** Серверная логика: таймер 60 с, адаптация облика, реакция мобов, срыв маски при атаке. */
public final class DisguiseManager {
    static final int CONTEXT_CHECK_TICKS = 60;
    static final int WARN_TICKS = 200;

    private static final class State {
        Disguise disguise;
        long endTick;
        int total;
        Mob soundMob;           // временный моб (не в мире) — только чтобы издавать его звуки
        int nextAmbient = 80;
        boolean paused;         // «отключить образ»: лицо открыто, маскировка на паузе
        boolean locked;         // «оставить образ»: облик не меняется по обстановке
        net.minecraft.world.item.ItemStack mask = net.minecraft.world.item.ItemStack.EMPTY;
    }

    private static final Map<UUID, State> ACTIVE = new HashMap<>();

    private DisguiseManager() {}

    public static boolean isDisguised(Player player) {
        if (player.level().isClientSide) {
            return com.vladosiwe.miragemask.network.ClientBridge.isLocalDisguised(player);
        }
        return ACTIVE.containsKey(player.getUUID());
    }

    // ------------------------------------------------------------------ включение / выключение
    public static boolean activate(ServerPlayer player, net.minecraft.world.item.ItemStack mask) {
        return activate(player, mask, null);
    }

    public static boolean activate(ServerPlayer player, net.minecraft.world.item.ItemStack mask, Disguise forced) {
        if (ACTIVE.containsKey(player.getUUID())) return false;
        State s = new State();
        s.mask = mask;
        s.total = com.vladosiwe.miragemask.item.MirageMaskItem.DURATION_TICKS;
        s.endTick = player.serverLevel().getGameTime() + s.total;
        ACTIVE.put(player.getUUID(), s);
        if (forced != null) s.locked = true;
        apply(player, s, forced != null ? forced
                : DisguiseContext.choose(player, DisguiseContext.contextKey(player), player.getRandom()),
                DisguisePayload.FLASH_ACTIVATE);
        play(player, SoundEvents.ILLUSIONER_MIRROR_MOVE, 1.0F, 0.8F);
        play(player, SoundEvents.AMETHYST_BLOCK_RESONATE, 1.0F, 1.2F);
        return true;
    }

    public static void end(ServerPlayer player, boolean broken) {
        State s = ACTIVE.remove(player.getUUID());
        if (s == null) return;
        // маска «отклеивается» и возвращается в инвентарь (или падает рядом, если места нет)
        if (!s.mask.isEmpty() && !player.getInventory().add(s.mask)) {
            player.drop(s.mask, false);
        }
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                DisguisePayload.clear(player.getId(), broken ? DisguisePayload.FLASH_BREAK : DisguisePayload.FLASH_END));
        if (broken) {
            play(player, SoundEvents.GLASS_BREAK, 1.0F, 0.7F);
            play(player, SoundEvents.AMETHYST_CLUSTER_BREAK, 1.0F, 0.6F);
        } else {
            play(player, SoundEvents.BEACON_DEACTIVATE, 0.8F, 1.6F);
        }
    }

    private static void apply(ServerPlayer player, State s, Disguise d, byte flash) {
        s.disguise = d;
        s.soundMob = null;
        if (d.kind() == Disguise.Kind.MOB) {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(d.entityType());
            if (type.create(player.serverLevel()) instanceof Mob mob) {
                mob.setSilent(false);
                s.soundMob = mob;
            }
        }
        sync(player, s, flash);
        calmTargeters(player, d);
    }

    private static void sync(ServerPlayer player, State s, byte flash) {
        int left = (int) Math.max(0, s.endTick - player.serverLevel().getGameTime());
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                DisguisePayload.of(player.getId(), s.disguise, left, s.total, flash, s.paused, s.locked));
    }

    // ------------------------------------------------------------------ две функции маски
    public static final byte ACTION_LOCK = 1, ACTION_REVEAL = 2;

    /** Клавиши: G — оставить образ (не перемаскироваться), H — открыть лицо / вернуть образ. */
    public static void action(ServerPlayer player, byte action) {
        State s = ACTIVE.get(player.getUUID());
        if (s == null) return;
        if (action == ACTION_LOCK) {
            s.locked = !s.locked;
            play(player, SoundEvents.AMETHYST_BLOCK_CHIME, 0.8F, s.locked ? 0.7F : 1.3F);
            sync(player, s, DisguisePayload.FLASH_NONE);
        } else if (action == ACTION_REVEAL) {
            s.paused = !s.paused;
            if (s.paused) {
                play(player, SoundEvents.AMETHYST_BLOCK_RESONATE, 1.0F, 0.6F);
                sync(player, s, DisguisePayload.FLASH_PAUSE);
            } else {
                play(player, SoundEvents.ILLUSIONER_MIRROR_MOVE, 0.9F, 1.1F);
                String key = DisguiseContext.contextKey(player);
                if (!s.locked && !key.equals(s.disguise.context())) {
                    apply(player, s, DisguiseContext.choose(player, key, player.getRandom()), DisguisePayload.FLASH_RESUME);
                } else {
                    sync(player, s, DisguisePayload.FLASH_RESUME);
                    calmTargeters(player, s.disguise);
                }
            }
        }
    }

    // ------------------------------------------------------------------ тик игрока
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        State s = ACTIVE.get(player.getUUID());
        if (s == null) return;
        ServerLevel level = player.serverLevel();
        long left = s.endTick - level.getGameTime();
        if (left <= 0) {
            end(player, false);
            return;
        }
        if (left <= WARN_TICKS && left % 20 == 0) {
            int sec = (int) (left / 20);
            player.playNotifySound(SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.PLAYERS, 0.6F, sec == 10 ? 0.7F : 1.0F + (10 - sec) * 0.05F);
        }
        if (s.paused) return; // лицо открыто: облик, звуки и реакция мобов на паузе
        // смена обстановки → новый облик (если образ не закреплён)
        if (!s.locked && left % CONTEXT_CHECK_TICKS == 0) {
            String key = DisguiseContext.contextKey(player);
            if (!key.equals(s.disguise.context())) {
                apply(player, s, DisguiseContext.choose(player, key, player.getRandom()), DisguisePayload.FLASH_CHANGE);
                play(player, SoundEvents.ILLUSIONER_MIRROR_MOVE, 0.7F, 1.3F);
            }
        }
        // голоса моба: замаскированный игрок «мычит», «хрюкает» и т.д.
        if (s.soundMob != null && --s.nextAmbient <= 0) {
            s.nextAmbient = 140 + player.getRandom().nextInt(200);
            s.soundMob.moveTo(player.getX(), player.getY(), player.getZ());
            s.soundMob.playAmbientSound();
        }
        // голем видит «монстра»
        if (level.getGameTime() % 20 == 0 && isHostileLook(s.disguise)) {
            for (IronGolem golem : level.getEntitiesOfClass(IronGolem.class, player.getBoundingBox().inflate(16),
                    g -> g.getTarget() == null && g.hasLineOfSight(player))) {
                golem.setTarget(player);
            }
        }
    }

    // ------------------------------------------------------------------ мобы реагируют «по облику»
    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        if (!(event.getNewAboutToBeSetTarget() instanceof ServerPlayer player)) return;
        State s = ACTIVE.get(player.getUUID());
        if (s == null || s.paused) return;
        if (!mayTarget(event.getEntity(), s.disguise)) {
            event.setCanceled(true);
        }
    }

    static boolean mayTarget(LivingEntity attacker, Disguise d) {
        if (d.kind() == Disguise.Kind.PLAYER) return true;            // копия игрока — для мобов обычный игрок
        if (attacker instanceof IronGolem || attacker instanceof SnowGolem) return isHostileLook(d);
        if (attacker instanceof Enemy) {
            if (isVillagerLook(d)) return attacker instanceof Zombie || attacker instanceof Raider || attacker instanceof Vex;
            return false;                                             // монстр не трогает «своего» и «скотину»
        }
        return true;
    }

    private static void calmTargeters(ServerPlayer player, Disguise d) {
        for (Mob mob : player.serverLevel().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(48))) {
            if (mayTarget(mob, d)) continue;
            if (mob.getTarget() == player) mob.setTarget(null);
            if (mob.getBrain().hasMemoryValue(MemoryModuleType.ATTACK_TARGET)
                    && mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null) == player) {
                mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            }
        }
    }

    static boolean isHostileLook(Disguise d) {
        if (d.kind() != Disguise.Kind.MOB) return false;
        return BuiltInRegistries.ENTITY_TYPE.get(d.entityType()).getCategory() == MobCategory.MONSTER;
    }

    static boolean isVillagerLook(Disguise d) {
        return d.kind() == Disguise.Kind.MOB && (d.entityType().equals(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.VILLAGER))
                || d.entityType().equals(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WANDERING_TRADER)));
    }

    // ------------------------------------------------------------------ срыв маски
    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && isActiveUnpaused(player)) {
            end(player, true);
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingIncomingDamageEvent event) {
        Entity src = event.getSource().getEntity();
        if (src instanceof ServerPlayer player && src != event.getEntity() && isActiveUnpaused(player)) {
            end(player, true);
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) end(player, true);
    }

    private static boolean isActiveUnpaused(ServerPlayer player) {
        State s = ACTIVE.get(player.getUUID());
        return s != null && !s.paused;
    }

    /** Выход с сервера: маска возвращается в инвентарь до сохранения игрока. */
    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) end(player, false);
    }

    @SubscribeEvent
    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            State s = ACTIVE.get(player.getUUID());
            if (s != null) {
                // в новом измерении игровое время может отличаться — сохраняем остаток
                long left = s.endTick - player.server.overworld().getGameTime();
                s.endTick = player.serverLevel().getGameTime() + Math.max(20, left);
                apply(player, s, DisguiseContext.choose(player, DisguiseContext.contextKey(player), player.getRandom()),
                        DisguisePayload.FLASH_CHANGE);
            }
        }
    }

    /** Новый наблюдатель должен сразу видеть маскировку. */
    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer target && event.getEntity() instanceof ServerPlayer viewer) {
            State s = ACTIVE.get(target.getUUID());
            if (s != null) {
                int left = (int) Math.max(0, s.endTick - target.serverLevel().getGameTime());
                PacketDistributor.sendToPlayer(viewer, DisguisePayload.of(target.getId(), s.disguise, left, s.total,
                        DisguisePayload.FLASH_NONE, s.paused, s.locked));
            }
        }
    }

    /** /mirage on|off [игроки] — для админов и тестов (уровень OP 2). */
    @SubscribeEvent
    public static void onCommands(net.neoforged.neoforge.event.RegisterCommandsEvent event) {
        event.getDispatcher().register(net.minecraft.commands.Commands.literal("mirage")
                .requires(src -> src.hasPermission(2))
                .then(net.minecraft.commands.Commands.literal("on")
                        .executes(ctx -> mirage(java.util.List.of(ctx.getSource().getPlayerOrException()), true))
                        .then(net.minecraft.commands.Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                                .executes(ctx -> mirage(net.minecraft.commands.arguments.EntityArgument.getPlayers(ctx, "targets"), true))))
                .then(net.minecraft.commands.Commands.literal("reset")
                        .executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            p.getCooldowns().removeCooldown(com.vladosiwe.miragemask.registry.ModItems.MIRAGE_MASK.get());
                            return 1;
                        }))
                .then(net.minecraft.commands.Commands.literal("as")
                        .then(net.minecraft.commands.Commands.argument("mob", net.minecraft.commands.arguments.ResourceLocationArgument.id())
                                .suggests(net.minecraft.commands.synchronization.SuggestionProviders.SUMMONABLE_ENTITIES)
                                .executes(DisguiseManager::mirageAs)))
                .then(net.minecraft.commands.Commands.literal("copy")
                        .then(net.minecraft.commands.Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                                .executes(DisguiseManager::mirageCopy)))
                .then(net.minecraft.commands.Commands.literal("off")
                        .executes(ctx -> mirage(java.util.List.of(ctx.getSource().getPlayerOrException()), false))
                        .then(net.minecraft.commands.Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
                                .executes(ctx -> mirage(net.minecraft.commands.arguments.EntityArgument.getPlayers(ctx, "targets"), false)))));
    }

    /** Принудительный облик (команда): облик закрепляется, таймер начинается заново. */
    public static void force(ServerPlayer player, Disguise d) {
        State s = ACTIVE.get(player.getUUID());
        if (s == null) {
            activate(player, net.minecraft.world.item.ItemStack.EMPTY, d);
            return;
        }
        s.locked = true;
        s.paused = false;
        s.endTick = player.serverLevel().getGameTime() + s.total;
        apply(player, s, d, DisguisePayload.FLASH_CHANGE);
        play(player, SoundEvents.ILLUSIONER_MIRROR_MOVE, 0.7F, 1.3F);
    }

    private static int mirageAs(com.mojang.brigadier.context.CommandContext<net.minecraft.commands.CommandSourceStack> ctx)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        net.minecraft.resources.ResourceLocation id = net.minecraft.commands.arguments.ResourceLocationArgument.getId(ctx, "mob");
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
        if (type == null || !(type.create(player.serverLevel()) instanceof LivingEntity) || type == EntityType.PLAYER) {
            ctx.getSource().sendFailure(Component.translatable("command.miragemask.bad_mob", id.toString()));
            return 0;
        }
        force(player, Disguise.mob(id, "forced"));
        return 1;
    }

    private static int mirageCopy(com.mojang.brigadier.context.CommandContext<net.minecraft.commands.CommandSourceStack> ctx)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ServerPlayer target = net.minecraft.commands.arguments.EntityArgument.getPlayer(ctx, "target");
        force(player, DisguiseContext.copyOf(target, "forced"));
        return 1;
    }

    private static int mirage(java.util.Collection<ServerPlayer> players, boolean on) {
        int n = 0;
        for (ServerPlayer p : players) {
            if (on ? activate(p, net.minecraft.world.item.ItemStack.EMPTY) : ACTIVE.containsKey(p.getUUID())) {
                if (!on) end(p, false);
                n++;
            }
        }
        return n;
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ACTIVE.clear();
    }

    // ------------------------------------------------------------------ эффекты

    private static void play(ServerPlayer player, SoundEvent sound, float volume, float pitch) {
        player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, volume, pitch);
    }
}
