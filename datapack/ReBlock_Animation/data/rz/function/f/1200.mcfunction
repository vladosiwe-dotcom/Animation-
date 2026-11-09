data merge entity @e[type=block_display,tag=rz.heart.p0,limit=1] {transformation:[-0.1044f,0f,0.1491f,0.0559f,0f,0.091f,0f,0.455f,-0.1491f,0f,-0.1044f,0.2386f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p1,limit=1] {transformation:[-0.1044f,0f,0.1491f,-0.1006f,0f,0.091f,0f,0.455f,-0.1491f,0f,-0.1044f,0.0149f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p2,limit=1] {transformation:[-0.3654f,0f,0.1491f,0.1081f,0f,0.091f,0f,0.364f,-0.5218f,0f,-0.1044f,0.3131f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p3,limit=1] {transformation:[-0.3654f,0f,0.1491f,0.1081f,0f,0.091f,0f,0.273f,-0.5218f,0f,-0.1044f,0.3131f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p4,limit=1] {transformation:[-0.261f,0f,0.1491f,0.0559f,0f,0.091f,0f,0.182f,-0.3727f,0f,-0.1044f,0.2386f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p5,limit=1] {transformation:[-0.1566f,0f,0.1491f,0.0038f,0f,0.091f,0f,0.091f,-0.2236f,0f,-0.1044f,0.164f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p6,limit=1] {transformation:[-0.0522f,0f,0.1491f,-0.0484f,0f,0.091f,0f,0f,-0.0745f,0f,-0.1044f,0.0895f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.warden.heartbeat master @s ~ ~ ~ 1.2 0.9
particle dust{color:[0.75,0.02,0.05],scale:1.3} ~ ~1.25 ~-2.3 0.15 0.15 0.15 0 6 force @a
