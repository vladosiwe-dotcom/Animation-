# Площадь в средневековом городе (около 25x27 блоков) вокруг тебя.
# ВНИМАНИЕ: заменяет блоки в этой области! Запускай на ровном пустом месте.
execute align xz run fill ~-12 ~ ~-14 ~12 ~12 ~12 air
execute align xz run fill ~-12 ~-1 ~-14 ~12 ~-1 ~12 stone_bricks
execute align xz run fill ~-11 ~-1 ~-13 ~11 ~-1 ~11 polished_andesite
execute align xz run fill ~-1 ~-1 ~-13 ~1 ~-1 ~11 smooth_stone
execute align xz run fill ~-11 ~-1 ~-1 ~11 ~-1 ~1 smooth_stone
execute align xz run fill ~-11 ~-1 ~-11 ~-11 ~-1 ~9 cracked_stone_bricks
execute align xz run fill ~11 ~-1 ~-11 ~11 ~-1 ~9 cracked_stone_bricks
# фонари
execute align xz run fill ~-6 ~ ~-6 ~-6 ~2 ~-6 dark_oak_fence
execute align xz run setblock ~-6 ~3 ~-6 lantern
execute align xz run fill ~6 ~ ~-6 ~6 ~2 ~-6 dark_oak_fence
execute align xz run setblock ~6 ~3 ~-6 lantern
execute align xz run fill ~-6 ~ ~6 ~-6 ~2 ~6 dark_oak_fence
execute align xz run setblock ~-6 ~3 ~6 lantern
execute align xz run fill ~6 ~ ~6 ~6 ~2 ~6 dark_oak_fence
execute align xz run setblock ~6 ~3 ~6 lantern
# торговые лавки
execute align xz run fill ~-10 ~ ~-9 ~-10 ~2 ~-9 spruce_fence
execute align xz run fill ~-10 ~ ~-5 ~-10 ~2 ~-5 spruce_fence
execute align xz run fill ~-11 ~3 ~-9 ~-9 ~3 ~-5 red_wool
execute align xz run fill ~-10 ~ ~-8 ~-10 ~ ~-6 barrel
execute align xz run fill ~-10 ~ ~4 ~-10 ~2 ~4 spruce_fence
execute align xz run fill ~-10 ~ ~8 ~-10 ~2 ~8 spruce_fence
execute align xz run fill ~-11 ~3 ~4 ~-9 ~3 ~8 yellow_wool
execute align xz run fill ~-10 ~ ~5 ~-10 ~ ~7 hay_block
execute align xz run fill ~10 ~ ~-12 ~10 ~2 ~-12 spruce_fence
execute align xz run fill ~10 ~ ~-8 ~10 ~2 ~-8 spruce_fence
execute align xz run fill ~9 ~3 ~-12 ~11 ~3 ~-8 blue_wool
execute align xz run fill ~10 ~ ~-11 ~10 ~ ~-9 composter
# стены домов по краям
execute align xz run fill ~-12 ~ ~12 ~12 ~5 ~12 white_terracotta
execute align xz run fill ~-12 ~ ~12 ~12 ~0 ~12 cobblestone
execute align xz run fill ~-12 ~6 ~12 ~12 ~6 ~12 dark_oak_planks
execute align xz run fill ~-12 ~ ~-14 ~-12 ~5 ~12 white_terracotta
execute align xz run fill ~12 ~ ~-14 ~12 ~5 ~12 white_terracotta
execute align xz run fill ~-12 ~0 ~-14 ~-12 ~0 ~12 cobblestone
execute align xz run fill ~12 ~0 ~-14 ~12 ~0 ~12 cobblestone
tellraw @s {"text":"[Re:Блок] Площадь построена. Встань в центр и запусти /function rz:start","color":"dark_purple"}
