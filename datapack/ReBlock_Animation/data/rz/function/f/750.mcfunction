particle dust_color_transition{from_color:[0.75,0.9,1],to_color:[0.4,0.6,1],scale:1} ~0.017 ~1.3445 ~5.5444 0.05 0.05 0.05 0.01 2 force @a
kill @e[type=block_display,tag=rz.fx6]
particle block{block_state:{Name:"minecraft:packed_ice"}} ~0.017 ~1.3445 ~5.5444 0.3 0.3 0.3 0.1 30 force @a
particle snowflake ~0.017 ~1.3445 ~5.5444 0.2 0.2 0.2 0.15 15 force @a
particle dust{color:[0.12,0,0.2],scale:1.6} ~0.017 ~1.3445 ~5.5444 0.3 0.3 0.3 0 10 force @a
execute as @a[tag=rz.viewer] at @s run playsound minecraft:block.glass.break master @s ~ ~ ~ 1 1.2
