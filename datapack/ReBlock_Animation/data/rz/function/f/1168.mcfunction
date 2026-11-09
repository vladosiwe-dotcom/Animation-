particle dust{color:[0.12f,0f,0.2f],scale:1.6f} ~-0.1431 ~0.3379 ~0.5688 0.3 0.3 0.3 0 3 force @a
particle dust{color:[0.12f,0f,0.2f],scale:1.6f} ~3.552 ~2.6822 ~-8.9996 0.3 0.3 0.3 0 3 force @a
particle dust{color:[0.12f,0f,0.2f],scale:1.6f} ~-0.8665 ~1.1722 ~0.7605 0.3 0.3 0.3 0 3 force @a
particle squid_ink ~2.6703 ~0.05 ~-3.5 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~1.8882 ~0.05 ~-1.6118 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~ ~0.05 ~-0.8297 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~-1.8882 ~0.05 ~-1.6118 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~-2.6703 ~0.05 ~-3.5 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~-1.8882 ~0.05 ~-5.3882 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~ ~0.05 ~-6.1703 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~1.8882 ~0.05 ~-5.3882 0.05 0.02 0.05 0.005 1 force @a
particle reverse_portal ~ ~1 ~-3.5 2 1 2 0.02 4 force @a
data merge entity @e[type=block_display,tag=rz.heart.p0,limit=1] {transformation:[0.0471f,0f,0.1758f,-0.1468f,0f,0.091f,0f,0.455f,-0.1758f,0f,0.0471f,0.1962f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p1,limit=1] {transformation:[0.0471f,0f,0.1758f,-0.0761f,0f,0.091f,0f,0.455f,-0.1758f,0f,0.0471f,-0.0675f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p2,limit=1] {transformation:[0.1649f,0f,0.1758f,-0.1703f,0f,0.091f,0f,0.364f,-0.6153f,0f,0.0471f,0.2841f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p3,limit=1] {transformation:[0.1649f,0f,0.1758f,-0.1703f,0f,0.091f,0f,0.273f,-0.6153f,0f,0.0471f,0.2841f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p4,limit=1] {transformation:[0.1178f,0f,0.1758f,-0.1468f,0f,0.091f,0f,0.182f,-0.4395f,0f,0.0471f,0.1962f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p5,limit=1] {transformation:[0.0707f,0f,0.1758f,-0.1232f,0f,0.091f,0f,0.091f,-0.2637f,0f,0.0471f,0.1083f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p6,limit=1] {transformation:[0.0236f,0f,0.1758f,-0.0997f,0f,0.091f,0f,0f,-0.0879f,0f,0.0471f,0.0204f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.warden.heartbeat master @s ~ ~ ~ 1.2 0.9
particle dust{color:[0.75f,0.02f,0.05f],scale:1.3f} ~ ~1.25 ~-2.3 0.15 0.15 0.15 0 6 force @a
particle dust{color:[0.55f,0.15f,0.95f],scale:1f} ~-1.9248 ~1.0345 ~-4.7716 0.15 0.15 0.15 0.01 4 force @a
particle dust{color:[0.55f,0.15f,0.95f],scale:1f} ~1.9248 ~1.0345 ~-4.7716 0.15 0.15 0.15 0.01 4 force @a
particle dust{color:[0.55f,0.15f,0.95f],scale:1f} ~ ~2.1836 ~0.4294 0.15 0.15 0.15 0.01 4 force @a
