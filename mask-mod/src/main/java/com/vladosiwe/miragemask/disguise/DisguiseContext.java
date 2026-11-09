package com.vladosiwe.miragemask.disguise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.AABB;

/**
 * Решает, в кого превратить игрока, глядя на обстановку:
 * рядом игроки → копия случайного другого игрока; деревня/жители → житель;
 * ночь/пещера → монстр; вода → водный моб; иначе → животное, которое здесь водится.
 */
public final class DisguiseContext {
    static final double PLAYER_RADIUS = 40;
    static final double MOB_RADIUS = 32;

    private static final Set<EntityType<?>> HOSTILE = Set.of(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
            EntityType.SPIDER, EntityType.HUSK, EntityType.STRAY, EntityType.DROWNED, EntityType.ENDERMAN,
            EntityType.WITCH, EntityType.BOGGED, EntityType.ZOMBIE_VILLAGER, EntityType.CAVE_SPIDER, EntityType.PILLAGER);
    private static final Set<EntityType<?>> PASSIVE = Set.of(EntityType.COW, EntityType.PIG, EntityType.SHEEP,
            EntityType.CHICKEN, EntityType.HORSE, EntityType.DONKEY, EntityType.GOAT, EntityType.RABBIT, EntityType.FOX,
            EntityType.LLAMA, EntityType.CAMEL, EntityType.FROG, EntityType.MOOSHROOM, EntityType.WOLF, EntityType.CAT,
            EntityType.OCELOT, EntityType.PANDA, EntityType.POLAR_BEAR, EntityType.ARMADILLO, EntityType.TURTLE,
            EntityType.MULE, EntityType.PARROT);
    private static final Set<EntityType<?>> WATER = Set.of(EntityType.SQUID, EntityType.GLOW_SQUID, EntityType.COD,
            EntityType.SALMON, EntityType.DOLPHIN, EntityType.TROPICAL_FISH, EntityType.DROWNED, EntityType.AXOLOTL,
            EntityType.TURTLE);
    private static final Set<EntityType<?>> NETHER = Set.of(EntityType.ZOMBIFIED_PIGLIN, EntityType.PIGLIN,
            EntityType.PIGLIN_BRUTE, EntityType.HOGLIN, EntityType.WITHER_SKELETON, EntityType.SKELETON,
            EntityType.ENDERMAN, EntityType.STRIDER, EntityType.BLAZE);

    private DisguiseContext() {}

    /** Ключ обстановки: облик меняется только когда ключ меняется. */
    public static String contextKey(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        if (!otherPlayerCandidates(player).isEmpty() && !nearbyPlayers(player).isEmpty()) return "players";
        if (level.dimension() == Level.NETHER) return "nether";
        if (level.dimension() == Level.END) return "end";
        if (player.isInWater() || player.isUnderWater()) return "water";
        BlockPos pos = player.blockPosition();
        boolean villagers = !level.getEntitiesOfClass(net.minecraft.world.entity.npc.AbstractVillager.class,
                player.getBoundingBox().inflate(MOB_RADIUS)).isEmpty();
        if (villagers || level.isCloseToVillage(pos, 2)) return "village";
        boolean cave = !level.canSeeSky(pos) && pos.getY() < level.getSeaLevel() - 4;
        if (cave || level.isNight()) return "hostile";
        return "field";
    }

    public static Disguise choose(ServerPlayer player, String key, RandomSource random) {
        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition();
        Holder<Biome> biome = level.getBiome(pos);
        switch (key) {
            case "players" -> {
                List<ServerPlayer> candidates = otherPlayerCandidates(player);
                ServerPlayer target = candidates.get(random.nextInt(candidates.size()));
                List<ItemStack> gear = new ArrayList<>();
                for (EquipmentSlot slot : SLOTS) gear.add(target.getItemBySlot(slot).copy());
                return new Disguise(Disguise.Kind.PLAYER, Disguise.PLAYER_TYPE, target.getUUID(),
                        target.getGameProfile().getName(), gear, key);
            }
            case "nether" -> {
                return mob(pickNearby(player, NETHER, random,
                        List.of(EntityType.ZOMBIFIED_PIGLIN, EntityType.PIGLIN, EntityType.WITHER_SKELETON)), key);
            }
            case "end" -> {
                return mob(EntityType.ENDERMAN, key);
            }
            case "water" -> {
                return mob(pickNearby(player, WATER, random,
                        level.isNight() ? List.of(EntityType.DROWNED, EntityType.GLOW_SQUID) : List.of(EntityType.SQUID, EntityType.COD, EntityType.SALMON)), key);
            }
            case "village" -> {
                return mob(random.nextInt(8) == 0 ? EntityType.WANDERING_TRADER : EntityType.VILLAGER, key);
            }
            case "hostile" -> {
                List<EntityType<?>> fallback;
                if (biome.is(Biomes.DESERT) || biome.is(BiomeTags.IS_BADLANDS)) fallback = List.of(EntityType.HUSK, EntityType.SKELETON, EntityType.SPIDER);
                else if (biome.value().coldEnoughToSnow(pos)) fallback = List.of(EntityType.STRAY, EntityType.ZOMBIE, EntityType.CREEPER);
                else if (biome.is(Biomes.SWAMP) || biome.is(Biomes.MANGROVE_SWAMP)) fallback = List.of(EntityType.BOGGED, EntityType.WITCH, EntityType.ZOMBIE);
                else fallback = List.of(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER, EntityType.SPIDER);
                return mob(pickNearby(player, HOSTILE, random, fallback), key);
            }
            default -> {
                List<EntityType<?>> fallback;
                if (biome.is(Biomes.DESERT)) fallback = List.of(EntityType.CAMEL, EntityType.RABBIT);
                else if (biome.is(Biomes.MUSHROOM_FIELDS)) fallback = List.of(EntityType.MOOSHROOM);
                else if (biome.is(BiomeTags.IS_SAVANNA)) fallback = List.of(EntityType.HORSE, EntityType.DONKEY, EntityType.LLAMA);
                else if (biome.is(BiomeTags.IS_JUNGLE)) fallback = List.of(EntityType.OCELOT, EntityType.PANDA, EntityType.CHICKEN);
                else if (biome.is(BiomeTags.IS_MOUNTAIN)) fallback = List.of(EntityType.GOAT, EntityType.SHEEP);
                else if (biome.is(Biomes.SWAMP) || biome.is(Biomes.MANGROVE_SWAMP)) fallback = List.of(EntityType.FROG);
                else if (biome.is(BiomeTags.IS_TAIGA) || biome.value().coldEnoughToSnow(pos)) fallback = List.of(EntityType.FOX, EntityType.RABBIT, EntityType.WOLF);
                else if (biome.is(BiomeTags.IS_BEACH)) fallback = List.of(EntityType.TURTLE);
                else fallback = List.of(EntityType.COW, EntityType.PIG, EntityType.SHEEP, EntityType.CHICKEN, EntityType.HORSE);
                return mob(pickNearby(player, PASSIVE, random, fallback), key);
            }
        }
    }

    static final EquipmentSlot[] SLOTS = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND, EquipmentSlot.FEET,
            EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

    private static Disguise mob(EntityType<?> type, String key) {
        return Disguise.mob(BuiltInRegistries.ENTITY_TYPE.getKey(type), key);
    }

    /** Самый частый подходящий тип мобов вокруг — чтобы слиться со стадом. */
    private static EntityType<?> pickNearby(ServerPlayer player, Set<EntityType<?>> allowed, RandomSource random,
                                            List<EntityType<?>> fallback) {
        Map<EntityType<?>, Integer> counts = new HashMap<>();
        AABB box = player.getBoundingBox().inflate(MOB_RADIUS);
        for (Mob mob : player.level().getEntitiesOfClass(Mob.class, box, m -> allowed.contains(m.getType()) && !m.isBaby())) {
            counts.merge(mob.getType(), 1, Integer::sum);
        }
        if (!counts.isEmpty()) {
            int best = counts.values().stream().max(Integer::compare).orElse(0);
            List<EntityType<?>> top = counts.entrySet().stream().filter(e -> e.getValue() == best).map(Map.Entry::getKey).toList();
            return top.get(random.nextInt(top.size()));
        }
        return fallback.get(random.nextInt(fallback.size()));
    }

    static List<ServerPlayer> nearbyPlayers(ServerPlayer player) {
        return player.serverLevel().getPlayers(p -> p != player && !p.isSpectator()
                && p.distanceToSqr(player) < PLAYER_RADIUS * PLAYER_RADIUS);
    }

    /** Игроки сервера, которых нет рядом: их копией можно стать, не столкнувшись с «оригиналом». */
    static List<ServerPlayer> otherPlayerCandidates(ServerPlayer player) {
        List<ServerPlayer> near = nearbyPlayers(player);
        List<ServerPlayer> out = new ArrayList<>();
        for (ServerPlayer p : player.server.getPlayerList().getPlayers()) {
            if (p != player && !near.contains(p) && !p.isSpectator()) out.add(p);
        }
        return out;
    }
}
