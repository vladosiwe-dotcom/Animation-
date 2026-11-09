particle dust{color:[0.12f,0f,0.2f],scale:1.6f} ~-0.5619 ~3.0107 ~-8.5468 0.3 0.3 0.3 0 3 force @a
particle dust{color:[0.12f,0f,0.2f],scale:1.6f} ~-2.7705 ~0.1959 ~-7.0482 0.3 0.3 0.3 0 3 force @a
particle dust{color:[0.12f,0f,0.2f],scale:1.6f} ~3.5353 ~1.8887 ~-5.2519 0.3 0.3 0.3 0 3 force @a
particle squid_ink ~2.5583 ~0.05 ~-3.5 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~1.809 ~0.05 ~-1.691 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~ ~0.05 ~-0.9417 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~-1.809 ~0.05 ~-1.691 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~-2.5583 ~0.05 ~-3.5 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~-1.809 ~0.05 ~-5.309 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~ ~0.05 ~-6.0583 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~1.809 ~0.05 ~-5.309 0.05 0.02 0.05 0.005 1 force @a
particle reverse_portal ~ ~1 ~-3.5 2 1 2 0.02 4 force @a
data merge entity @e[type=block_display,tag=rz.heart.p0,limit=1] {transformation:[-0.1316f,0f,-0.0479f,0.1884f,0f,0.07f,0f,0.35f,0.0479f,0f,-0.1316f,0.0059f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p1,limit=1] {transformation:[-0.1316f,0f,-0.0479f,-0.0089f,0f,0.07f,0f,0.35f,0.0479f,0f,-0.1316f,0.0777f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p2,limit=1] {transformation:[-0.4604f,0f,-0.0479f,0.2542f,0f,0.07f,0f,0.28f,0.1676f,0f,-0.1316f,-0.018f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p3,limit=1] {transformation:[-0.4604f,0f,-0.0479f,0.2542f,0f,0.07f,0f,0.21f,0.1676f,0f,-0.1316f,-0.018f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p4,limit=1] {transformation:[-0.3289f,0f,-0.0479f,0.1884f,0f,0.07f,0f,0.14f,0.1197f,0f,-0.1316f,0.0059f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p5,limit=1] {transformation:[-0.1973f,0f,-0.0479f,0.1226f,0f,0.07f,0f,0.07f,0.0718f,0f,-0.1316f,0.0299f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p6,limit=1] {transformation:[-0.0658f,0f,-0.0479f,0.0568f,0f,0.07f,0f,0f,0.0239f,0f,-0.1316f,0.0538f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.warden.heartbeat master @s ~ ~ ~ 1.2 1.3
particle dust{color:[0.75f,0.02f,0.05f],scale:1.3f} ~ ~1.25 ~-2.3 0.15 0.15 0.15 0 6 force @a
