effect give @a[tag=rz.viewer] minecraft:blindness 3 0 true
effect give @a[tag=rz.viewer] minecraft:darkness 14 0 true
execute as @a[tag=rz.viewer] at @s run playsound minecraft:block.beacon.deactivate master @s ~ ~ ~ 1 0.5
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.warden.heartbeat master @s ~ ~ ~ 1 0.6
