data merge entity @e[type=block_display,tag=rz.heart.p0,limit=1] {transformation:[-0.0479f,0f,-0.1316f,0.1256f,0f,0.07f,0f,0.35f,0.1316f,0f,-0.0479f,-0.1405f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p1,limit=1] {transformation:[-0.0479f,0f,-0.1316f,0.0538f,0f,0.07f,0f,0.35f,0.1316f,0f,-0.0479f,0.0568f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p2,limit=1] {transformation:[-0.1676f,0f,-0.1316f,0.1496f,0f,0.07f,0f,0.28f,0.4604f,0f,-0.0479f,-0.2063f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p3,limit=1] {transformation:[-0.1676f,0f,-0.1316f,0.1496f,0f,0.07f,0f,0.21f,0.4604f,0f,-0.0479f,-0.2063f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p4,limit=1] {transformation:[-0.1197f,0f,-0.1316f,0.1256f,0f,0.07f,0f,0.14f,0.3289f,0f,-0.0479f,-0.1405f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p5,limit=1] {transformation:[-0.0718f,0f,-0.1316f,0.1017f,0f,0.07f,0f,0.07f,0.1973f,0f,-0.0479f,-0.0747f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p6,limit=1] {transformation:[-0.0239f,0f,-0.1316f,0.0777f,0f,0.07f,0f,0f,0.0658f,0f,-0.0479f,-0.0089f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.warden.heartbeat master @s ~ ~ ~ 1.2 1.3
particle dust{color:[0.75,0.02,0.05],scale:1.3} ~ ~1.25 ~-2.3 0.15 0.15 0.15 0 6 force @a
