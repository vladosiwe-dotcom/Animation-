particle dust{color:[0.12,0,0.2],scale:1.6} ~-5.8292 ~3.766 ~-4.3246 0.3 0.3 0.3 0 3 force @a
particle dust{color:[0.12,0,0.2],scale:1.6} ~-1.0186 ~3.0592 ~-9.175 0.3 0.3 0.3 0 3 force @a
particle dust{color:[0.12,0,0.2],scale:1.6} ~-5.2362 ~0.1582 ~-2.0613 0.3 0.3 0.3 0 3 force @a
particle squid_ink ~2.16 ~0.05 ~-3.5 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~1.5273 ~0.05 ~-1.9727 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~ ~0.05 ~-1.34 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~-1.5273 ~0.05 ~-1.9727 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~-2.16 ~0.05 ~-3.5 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~-1.5273 ~0.05 ~-5.0273 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~ ~0.05 ~-5.66 0.05 0.02 0.05 0.005 1 force @a
particle squid_ink ~1.5273 ~0.05 ~-5.0273 0.05 0.02 0.05 0.005 1 force @a
particle reverse_portal ~ ~1 ~-3.5 2 1 2 0.02 4 force @a
data merge entity @e[type=block_display,tag=rz.heart.p0,limit=1] {transformation:[0.0122f,0f,-0.1395f,0.0545f,0f,0.07f,0f,0.35f,0.1395f,0f,0.0122f,-0.1804f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p1,limit=1] {transformation:[0.0122f,0f,-0.1395f,0.0728f,0f,0.07f,0f,0.35f,0.1395f,0f,0.0122f,0.0288f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p2,limit=1] {transformation:[0.0427f,0f,-0.1395f,0.0484f,0f,0.07f,0f,0.28f,0.4881f,0f,0.0122f,-0.2502f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p3,limit=1] {transformation:[0.0427f,0f,-0.1395f,0.0484f,0f,0.07f,0f,0.21f,0.4881f,0f,0.0122f,-0.2502f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p4,limit=1] {transformation:[0.0305f,0f,-0.1395f,0.0545f,0f,0.07f,0f,0.14f,0.3487f,0f,0.0122f,-0.1804f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p5,limit=1] {transformation:[0.0183f,0f,-0.1395f,0.0606f,0f,0.07f,0f,0.07f,0.2092f,0f,0.0122f,-0.1107f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p6,limit=1] {transformation:[0.0061f,0f,-0.1395f,0.0667f,0f,0.07f,0f,0f,0.0697f,0f,0.0122f,-0.041f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.warden.heartbeat master @s ~ ~ ~ 1.2 1.3
particle dust{color:[0.75,0.02,0.05],scale:1.3} ~ ~1.25 ~-2.3 0.15 0.15 0.15 0 6 force @a
particle dust{color:[0.75,0.02,0.05],scale:1.3} ~ ~1.3 ~-2.3 0.3 0.3 0.3 0.05 60 force @a
data merge entity @e[type=block_display,tag=rz.hero.leg_r,limit=1] {transformation:[0.209f,0f,0f,-0.2145f,0f,0.55f,0f,0.11f,0f,0f,0.22f,-0.11f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.shoe_r,limit=1] {transformation:[0.22f,0f,0f,-0.22f,0f,0.11f,0f,0f,0f,0f,0.264f,-0.121f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.leg_l,limit=1] {transformation:[0.209f,0f,0f,0.0055f,0f,0.55f,0f,0.11f,0f,0f,0.22f,-0.11f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.shoe_l,limit=1] {transformation:[0.22f,0f,0f,0f,0f,0.11f,0f,0f,0f,0f,0.264f,-0.121f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.torso,limit=1] {transformation:[0.44f,0f,0f,-0.22f,0f,0.5982f,0.093f,0.6135f,0f,-0.2789f,0.1994f,-0.0997f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.arm_r,limit=1] {transformation:[0.1061f,0.4213f,0f,-0.7137f,-0.0725f,0.2028f,0.1352f,0.9797f,0.1035f,-0.2896f,0.0946f,-0.097f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.hand_r,limit=1] {transformation:[0.0955f,0.0843f,0f,-0.7926f,-0.0652f,0.0406f,0.1216f,0.9423f,0.0932f,-0.0579f,0.0852f,-0.0292f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.arm_l,limit=1] {transformation:[0.1061f,-0.4213f,0f,0.6076f,0.0725f,0.2028f,0.1352f,0.9072f,-0.1035f,-0.2896f,0.0946f,0.0065f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.hand_l,limit=1] {transformation:[0.0955f,-0.0843f,0f,0.6972f,0.0652f,0.0406f,0.1216f,0.877f,-0.0932f,-0.0579f,0.0852f,0.064f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.head,limit=1] {transformation:[0.495f,0f,0f,-0.2475f,0f,0.2092f,0.4486f,1.0339f,0f,-0.4486f,0.2092f,-0.3835f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p10,limit=1] {transformation:[0.0385f,0f,0f,-0.0192f,0f,0.5982f,0.0023f,0.7065f,0f,-0.2789f,0.005f,0.0997f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p11,limit=1] {transformation:[0.4455f,0f,0f,-0.2228f,0f,0.0723f,0.0953f,1.1407f,0f,-0.0337f,0.2044f,-0.3486f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p12,limit=1] {transformation:[0.4455f,0f,0f,-0.2228f,0f,0.0498f,0.0953f,0.6123f,0f,-0.0232f,0.2044f,-0.1022f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p13,limit=1] {transformation:[0.0035f,0.4213f,0f,-0.7154f,-0.0024f,0.2028f,0.036f,1.0305f,0.0035f,-0.2896f,0.0252f,-0.064f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p14,limit=1] {transformation:[0.1096f,0.0421f,0f,-0.7154f,-0.0749f,0.0203f,0.1397f,0.9787f,0.107f,-0.029f,0.0978f,-0.1003f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p15,limit=1] {transformation:[0.0035f,-0.4213f,0f,0.7119f,0.0024f,0.2028f,0.036f,1.0281f,-0.0035f,-0.2896f,0.0252f,-0.0606f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p16,limit=1] {transformation:[0.1096f,-0.0421f,0f,0.6058f,0.0749f,0.0203f,0.1397f,0.9037f,-0.107f,-0.029f,0.0978f,0.0067f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p17,limit=1] {transformation:[0.2145f,0f,0f,-0.2172f,0f,0.055f,0f,0.11f,0f,0f,0.2255f,-0.1127f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p18,limit=1] {transformation:[0.2145f,0f,0f,0.0028f,0f,0.055f,0f,0.11f,0f,0f,0.2255f,-0.1127f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p19,limit=1] {transformation:[0.528f,0f,0f,-0.264f,0f,0.0837f,0.4785f,1.1584f,0f,-0.1794f,0.2231f,-0.6896f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p20,limit=1] {transformation:[0.528f,0f,0f,-0.264f,0f,0.186f,0.0648f,1.0372f,0f,-0.3988f,0.0302f,-0.4427f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p21,limit=1] {transformation:[0.0385f,0f,0f,-0.2695f,0f,0.1046f,0.4038f,1.087f,0f,-0.2243f,0.1883f,-0.5105f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p22,limit=1] {transformation:[0.0385f,0f,0f,0.231f,0f,0.1046f,0.4038f,1.087f,0f,-0.2243f,0.1883f,-0.5105f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p23,limit=1] {transformation:[0.154f,0f,0f,-0.2365f,0f,0.0418f,0.0224f,1.5847f,0f,-0.0897f,0.0105f,-0.3937f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p24,limit=1] {transformation:[0.154f,0f,0f,-0.088f,0f,0.0302f,0.0224f,1.5964f,0f,-0.0648f,0.0105f,-0.4186f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p25,limit=1] {transformation:[0.154f,0f,0f,0.066f,0f,0.0465f,0.0224f,1.5801f,0f,-0.0997f,0.0105f,-0.3837f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p26,limit=1] {transformation:[0.121f,0f,0f,-0.165f,0f,0.0325f,0.1097f,1.377f,0f,-0.0698f,0.0511f,-0.794f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p27,limit=1] {transformation:[0.121f,0f,0f,0.0275f,0f,0.0325f,0.1097f,1.4767f,0f,-0.0698f,0.0511f,-0.7475f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p28,limit=1] {transformation:[0.121f,0f,0f,-0.055f,0f,0.0325f,0.1097f,1.5764f,0f,-0.0698f,0.0511f,-0.701f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p29,limit=1] {transformation:[0.121f,0f,0f,-0.187f,0f,0.0465f,0.003f,1.5383f,0f,-0.0997f,0.0014f,-0.294f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p30,limit=1] {transformation:[0.099f,0f,0f,-0.176f,0f,0.0372f,0.005f,1.5439f,0f,-0.0798f,0.0023f,-0.3035f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p31,limit=1] {transformation:[0.121f,0f,0f,0.066f,0f,0.0465f,0.003f,1.5383f,0f,-0.0997f,0.0014f,-0.294f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p32,limit=1] {transformation:[0.099f,0f,0f,0.077f,0f,0.0372f,0.005f,1.5439f,0f,-0.0798f,0.0023f,-0.3035f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
data merge entity @e[type=block_display,tag=rz.hero.p33,limit=1] {transformation:[0.132f,0f,0f,-0.066f,0f,0.007f,0.004f,1.5104f,0f,-0.015f,0.0019f,-0.2341f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:4}
