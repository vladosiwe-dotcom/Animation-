particle dust{color:[0.12,0,0.2],scale:1.6} ~2.4176 ~2.9008 ~1.0555 0.3 0.3 0.3 0 3 force @a
particle dust{color:[0.12,0,0.2],scale:1.6} ~-4.3518 ~2.0734 ~-5.1125 0.3 0.3 0.3 0 3 force @a
particle dust{color:[0.12,0,0.2],scale:1.6} ~-5.8699 ~0.4244 ~-5.6317 0.3 0.3 0.3 0 3 force @a
particle squid_ink ~2.1565 ~0.05 ~-3.5 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~1.5249 ~0.05 ~-1.9751 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~ ~0.05 ~-1.3435 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~-1.5249 ~0.05 ~-1.9751 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~-2.1565 ~0.05 ~-3.5 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~-1.5249 ~0.05 ~-5.0249 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~ ~0.05 ~-5.6565 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~1.5249 ~0.05 ~-5.0249 0.05 0.02 0.05 0.005 1 force @a
particle reverse_portal ~ ~1 ~-3.5 2 1 2 0.02 4 force @a
data merge entity @e[type=block_display,tag=rz.heart.p0,limit=1] {transformation:[0.182f,0f,0f,-0.2275f,0f,0.091f,0f,0.455f,0f,0f,0.182f,-0.091f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p1,limit=1] {transformation:[0.182f,0f,0f,0.0455f,0f,0.091f,0f,0.455f,0f,0f,0.182f,-0.091f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p2,limit=1] {transformation:[0.637f,0f,0f,-0.3185f,0f,0.091f,0f,0.364f,0f,0f,0.182f,-0.091f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p3,limit=1] {transformation:[0.637f,0f,0f,-0.3185f,0f,0.091f,0f,0.273f,0f,0f,0.182f,-0.091f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p4,limit=1] {transformation:[0.455f,0f,0f,-0.2275f,0f,0.091f,0f,0.182f,0f,0f,0.182f,-0.091f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p5,limit=1] {transformation:[0.273f,0f,0f,-0.1365f,0f,0.091f,0f,0.091f,0f,0f,0.182f,-0.091f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p6,limit=1] {transformation:[0.091f,0f,0f,-0.0455f,0f,0.091f,0f,0f,0f,0f,0.182f,-0.091f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.warden.heartbeat master @s ~ ~ ~ 1.2 0.9
particle dust{color:[0.75,0.02,0.05],scale:1.3} ~ ~1.25 ~-2.3 0.15 0.15 0.15 0 6 force @a
data merge entity @e[type=block_display,tag=rz.witch.p0,limit=1] {transformation:[0.264f,0f,0f,-0.132f,0f,0.264f,0f,-0.132f,0f,0f,0.0792f,-1.5092f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p1,limit=1] {transformation:[0.66f,0f,0f,-0.33f,0f,0.22f,0f,-0.11f,0f,0f,0.0132f,-1.43f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p2,limit=1] {transformation:[0.132f,0f,0f,-0.33f,0f,0.132f,0f,-0.066f,0f,0f,0.0154f,-1.4168f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p3,limit=1] {transformation:[0.132f,0f,0f,-0.154f,0f,0.132f,0f,-0.066f,0f,0f,0.0154f,-1.4168f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p4,limit=1] {transformation:[0.132f,0f,0f,0.022f,0f,0.132f,0f,-0.066f,0f,0f,0.0154f,-1.4168f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p5,limit=1] {transformation:[0.132f,0f,0f,0.198f,0f,0.132f,0f,-0.066f,0f,0f,0.0154f,-1.4168f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p6,limit=1] {transformation:[0.132f,0f,0f,0.33f,0f,0.132f,0f,-0.066f,0f,0f,0.011f,-1.4278f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p7,limit=1] {transformation:[0.264f,0f,0f,-0.132f,0f,0.264f,0f,-0.132f,0f,0f,0.0792f,-1.5092f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p8,limit=1] {transformation:[0.66f,0f,0f,-0.33f,0f,0.22f,0f,-0.11f,0f,0f,0.0132f,-1.43f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p9,limit=1] {transformation:[0.132f,0f,0f,-0.33f,0f,0.132f,0f,-0.066f,0f,0f,0.0154f,-1.4168f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p10,limit=1] {transformation:[0.132f,0f,0f,-0.154f,0f,0.132f,0f,-0.066f,0f,0f,0.0154f,-1.4168f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p11,limit=1] {transformation:[0.132f,0f,0f,0.022f,0f,0.132f,0f,-0.066f,0f,0f,0.0154f,-1.4168f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p12,limit=1] {transformation:[0.132f,0f,0f,0.198f,0f,0.132f,0f,-0.066f,0f,0f,0.0154f,-1.4168f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p13,limit=1] {transformation:[0.132f,0f,0f,0.33f,0f,0.132f,0f,-0.066f,0f,0f,0.011f,-1.4278f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p14,limit=1] {transformation:[0.2112f,0f,0f,-0.1056f,0f,0.2112f,0f,-0.1056f,0f,0f,0.0792f,-3.234f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p15,limit=1] {transformation:[0.528f,0f,0f,-0.264f,0f,0.176f,0f,-0.088f,0f,0f,0.0132f,-3.1548f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p16,limit=1] {transformation:[0.1056f,0f,0f,-0.264f,0f,0.1056f,0f,-0.0528f,0f,0f,0.0154f,-3.1416f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p17,limit=1] {transformation:[0.1056f,0f,0f,-0.1232f,0f,0.1056f,0f,-0.0528f,0f,0f,0.0154f,-3.1416f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p18,limit=1] {transformation:[0.1056f,0f,0f,0.0176f,0f,0.1056f,0f,-0.0528f,0f,0f,0.0154f,-3.1416f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p19,limit=1] {transformation:[0.1056f,0f,0f,0.1584f,0f,0.1056f,0f,-0.0528f,0f,0f,0.0154f,-3.1416f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
data merge entity @e[type=block_display,tag=rz.witch.p20,limit=1] {transformation:[0.1056f,0f,0f,0.264f,0f,0.1056f,0f,-0.0528f,0f,0f,0.011f,-3.1526f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:0}
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.elder_guardian.curse master @s ~ ~ ~ 0.7 0.4
