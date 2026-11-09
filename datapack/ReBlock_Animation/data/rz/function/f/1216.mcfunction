particle dust{color:[0.12,0,0.2],scale:1.6} ~-2.5979 ~0.007 ~2.3936 0.3 0.3 0.3 0 3 force @a
particle dust{color:[0.12,0,0.2],scale:1.6} ~0.0297 ~0.4802 ~-9.9368 0.3 0.3 0.3 0 3 force @a
particle dust{color:[0.12,0,0.2],scale:1.6} ~5.3658 ~3.6063 ~-6.1749 0.3 0.3 0.3 0 3 force @a
particle squid_ink ~2.1111 ~0.05 ~-3.5 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~1.4928 ~0.05 ~-2.0072 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~ ~0.05 ~-1.3889 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~-1.4928 ~0.05 ~-2.0072 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~-2.1111 ~0.05 ~-3.5 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~-1.4928 ~0.05 ~-4.9928 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~ ~0.05 ~-5.6111 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~1.4928 ~0.05 ~-4.9928 0.05 0.02 0.05 0.005 1 force @a
particle reverse_portal ~ ~1 ~-3.5 2 1 2 0.02 4 force @a
data merge entity @e[type=block_display,tag=rz.heart.p0,limit=1] {transformation:[-0.1576f,0f,0.091f,0.1515f,0f,0.091f,0f,0.455f,-0.091f,0f,-0.1576f,0.1926f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p1,limit=1] {transformation:[-0.1576f,0f,0.091f,-0.0849f,0f,0.091f,0f,0.455f,-0.091f,0f,-0.1576f,0.0561f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p2,limit=1] {transformation:[-0.5517f,0f,0.091f,0.2303f,0f,0.091f,0f,0.364f,-0.3185f,0f,-0.1576f,0.2381f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p3,limit=1] {transformation:[-0.5517f,0f,0.091f,0.2303f,0f,0.091f,0f,0.273f,-0.3185f,0f,-0.1576f,0.2381f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p4,limit=1] {transformation:[-0.394f,0f,0.091f,0.1515f,0f,0.091f,0f,0.182f,-0.2275f,0f,-0.1576f,0.1926f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p5,limit=1] {transformation:[-0.2364f,0f,0.091f,0.0727f,0f,0.091f,0f,0.091f,-0.1365f,0f,-0.1576f,0.1471f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p6,limit=1] {transformation:[-0.0788f,0f,0.091f,-0.0061f,0f,0.091f,0f,0f,-0.0455f,0f,-0.1576f,0.1016f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.warden.heartbeat master @s ~ ~ ~ 1.2 0.9
particle dust{color:[0.75,0.02,0.05],scale:1.3} ~ ~1.25 ~-2.3 0.15 0.15 0.15 0 6 force @a
