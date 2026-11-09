# Конец: вернуть игроков и убрать сущности.
execute as @a[tag=rz.viewer,gamemode=spectator] run spectate
execute at @e[type=marker,tag=rz.stage,limit=1] run tp @a[tag=rz.viewer] ~ ~ ~-8 facing ~ ~1 ~
gamemode survival @a[tag=rz.gm_s]
gamemode creative @a[tag=rz.gm_c]
gamemode adventure @a[tag=rz.gm_a]
effect clear @a[tag=rz.viewer] minecraft:darkness
effect clear @a[tag=rz.viewer] minecraft:blindness
time set 13000
function rz:cleanup
tellraw @a {"text":"[Re:Блок] Конец. Перезапуск: /function rz:start","color":"dark_purple"}
