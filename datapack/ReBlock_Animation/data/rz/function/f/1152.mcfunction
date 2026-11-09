data merge entity @e[type=block_display,tag=rz.heart.p0,limit=1] {transformation:[0.117f,0f,0.1394f,-0.2159f,0f,0.091f,0f,0.455f,-0.1394f,0f,0.117f,0.1158f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p1,limit=1] {transformation:[0.117f,0f,0.1394f,-0.0405f,0f,0.091f,0f,0.455f,-0.1394f,0f,0.117f,-0.0933f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p2,limit=1] {transformation:[0.4095f,0f,0.1394f,-0.2744f,0f,0.091f,0f,0.364f,-0.488f,0f,0.117f,0.1855f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p3,limit=1] {transformation:[0.4095f,0f,0.1394f,-0.2744f,0f,0.091f,0f,0.273f,-0.488f,0f,0.117f,0.1855f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p4,limit=1] {transformation:[0.2925f,0f,0.1394f,-0.2159f,0f,0.091f,0f,0.182f,-0.3486f,0f,0.117f,0.1158f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p5,limit=1] {transformation:[0.1755f,0f,0.1394f,-0.1575f,0f,0.091f,0f,0.091f,-0.2091f,0f,0.117f,0.0461f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p6,limit=1] {transformation:[0.0585f,0f,0.1394f,-0.099f,0f,0.091f,0f,0f,-0.0697f,0f,0.117f,-0.0236f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.warden.heartbeat master @s ~ ~ ~ 1.2 0.9
particle dust{color:[0.75f,0.02f,0.05f],scale:1.3f} ~ ~1.25 ~-2.3 0.15 0.15 0.15 0 6 force @a
particle dust{color:[0.55f,0.15f,0.95f],scale:1f} ~-1.9248 ~1.0345 ~-4.7716 0.15 0.15 0.15 0.01 4 force @a
particle dust{color:[0.55f,0.15f,0.95f],scale:1f} ~1.9248 ~1.0345 ~-4.7716 0.15 0.15 0.15 0.01 4 force @a
particle dust{color:[0.55f,0.15f,0.95f],scale:1f} ~ ~2.1836 ~0.4294 0.15 0.15 0.15 0.01 4 force @a
