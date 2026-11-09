data merge entity @e[type=block_display,tag=rz.heart.p0,limit=1] {transformation:[0.1649f,0f,0.0769f,-0.2446f,0f,0.091f,0f,0.455f,-0.0769f,0f,0.1649f,0.0137f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p1,limit=1] {transformation:[0.1649f,0f,0.0769f,0.0028f,0f,0.091f,0f,0.455f,-0.0769f,0f,0.1649f,-0.1017f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p2,limit=1] {transformation:[0.5773f,0f,0.0769f,-0.3271f,0f,0.091f,0f,0.364f,-0.2692f,0f,0.1649f,0.0521f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p3,limit=1] {transformation:[0.5773f,0f,0.0769f,-0.3271f,0f,0.091f,0f,0.273f,-0.2692f,0f,0.1649f,0.0521f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p4,limit=1] {transformation:[0.4124f,0f,0.0769f,-0.2446f,0f,0.091f,0f,0.182f,-0.1923f,0f,0.1649f,0.0137f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p5,limit=1] {transformation:[0.2474f,0f,0.0769f,-0.1622f,0f,0.091f,0f,0.091f,-0.1154f,0f,0.1649f,-0.0248f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p6,limit=1] {transformation:[0.0825f,0f,0.0769f,-0.0797f,0f,0.091f,0f,0f,-0.0385f,0f,0.1649f,-0.0632f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.warden.heartbeat master @s ~ ~ ~ 1.2 0.9
particle dust{color:[0.75,0.02,0.05],scale:1.3} ~ ~1.25 ~-2.3 0.15 0.15 0.15 0 6 force @a
particle dust{color:[0.55,0.15,0.95],scale:1} ~-1.9248 ~1.0345 ~-4.7716 0.15 0.15 0.15 0.01 4 force @a
particle dust{color:[0.55,0.15,0.95],scale:1} ~1.9248 ~1.0345 ~-4.7716 0.15 0.15 0.15 0.01 4 force @a
particle dust{color:[0.55,0.15,0.95],scale:1} ~ ~2.1836 ~0.4294 0.15 0.15 0.15 0.01 4 force @a
