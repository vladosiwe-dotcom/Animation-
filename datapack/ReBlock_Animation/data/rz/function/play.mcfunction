# Свободный режим: сцена разворачивается вокруг точки запуска, смотри как хочешь.
function rz:cleanup
tag @a add rz.viewer
scoreboard players set #cam rz.t 0
scoreboard players set #t rz.t 0
execute align xz positioned ~0.5 ~ ~0.5 run summon marker ~ ~ ~ {Tags:["rz.e","rz.stage"]}
execute align xz positioned ~0.5 ~ ~0.5 run summon item_display ~ ~1.6 ~-10 {Tags:["rz.e","rz.cam"],teleport_duration:0}
scoreboard players set #play rz.t 1
tellraw @a[tag=rz.viewer] {"text":"[Re:Блок] Мотор!","color":"dark_purple"}
