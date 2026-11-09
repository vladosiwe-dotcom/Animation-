data merge entity @e[type=block_display,tag=rz.heart.p0,limit=1] {transformation:[-0.099f,0f,-0.099f,0.1732f,0f,0.07f,0f,0.35f,0.099f,0f,-0.099f,-0.0742f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p1,limit=1] {transformation:[-0.099f,0f,-0.099f,0.0247f,0f,0.07f,0f,0.35f,0.099f,0f,-0.099f,0.0742f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p2,limit=1] {transformation:[-0.3465f,0f,-0.099f,0.2227f,0f,0.07f,0f,0.28f,0.3465f,0f,-0.099f,-0.1237f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p3,limit=1] {transformation:[-0.3465f,0f,-0.099f,0.2227f,0f,0.07f,0f,0.21f,0.3465f,0f,-0.099f,-0.1237f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p4,limit=1] {transformation:[-0.2475f,0f,-0.099f,0.1732f,0f,0.07f,0f,0.14f,0.2475f,0f,-0.099f,-0.0742f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p5,limit=1] {transformation:[-0.1485f,0f,-0.099f,0.1237f,0f,0.07f,0f,0.07f,0.1485f,0f,-0.099f,-0.0247f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p6,limit=1] {transformation:[-0.0495f,0f,-0.099f,0.0742f,0f,0.07f,0f,0f,0.0495f,0f,-0.099f,0.0247f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.warden.heartbeat master @s ~ ~ ~ 1.2 1.3
particle dust{color:[0.75f,0.02f,0.05f],scale:1.3f} ~ ~1.25 ~-2.3 0.15 0.15 0.15 0 6 force @a
