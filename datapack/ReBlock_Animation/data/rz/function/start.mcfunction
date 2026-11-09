# Кино-режим: игроки становятся зрителями и смотрят через камеру.
function rz:play
scoreboard players set #cam rz.t 1
tag @a[gamemode=survival] add rz.gm_s
tag @a[gamemode=creative] add rz.gm_c
tag @a[gamemode=adventure] add rz.gm_a
gamemode spectator @a[tag=rz.viewer]
