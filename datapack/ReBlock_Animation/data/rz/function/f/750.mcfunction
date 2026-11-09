particle dust_color_transition{from_color:[0.75f,0.9f,1f],to_color:[0.4f,0.6f,1f],scale:1f} ~0.017 ~1.3445 ~5.5444 0.05 0.05 0.05 0.01 2 force @a
kill @e[type=block_display,tag=rz.fx6]
particle block{block_state:{Name:"minecraft:packed_ice"}} ~0.017 ~1.3445 ~5.5444 0.3 0.3 0.3 0.1 30 force @a
particle snowflake ~0.017 ~1.3445 ~5.5444 0.2 0.2 0.2 0.15 15 force @a
particle dust{color:[0.12f,0f,0.2f],scale:1.6f} ~0.017 ~1.3445 ~5.5444 0.3 0.3 0.3 0 10 force @a
execute as @a[tag=rz.viewer] at @s run playsound minecraft:block.glass.break master @s ~ ~ ~ 1 1.2
