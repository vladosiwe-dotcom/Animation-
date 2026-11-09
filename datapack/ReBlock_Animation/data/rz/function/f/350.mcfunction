tp @e[type=item_display,tag=rz.cam,limit=1] ~-2.8 ~2.3 ~0.5 facing ~ ~1.3 ~7
data merge entity @e[type=item_display,tag=rz.cam,limit=1] {teleport_duration:80}
effect give @a[tag=rz.viewer] minecraft:darkness 6 0 true
execute as @a[tag=rz.viewer] at @s run playsound minecraft:ambient.cave master @s ~ ~ ~ 1 0.6
data merge entity @e[type=block_display,tag=rz.hero.leg_r,limit=1] {transformation:[0.207f,0.0765f,0f,-0.29f,-0.0286f,0.5364f,-0.0382f,0.1571f,-0.0051f,0.0946f,0.2167f,-0.2004f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.shoe_r,limit=1] {transformation:[0.2179f,0.0153f,0f,-0.3108f,-0.0302f,0.1073f,-0.0458f,0.0524f,-0.0053f,0.0189f,0.26f,-0.23f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.leg_l,limit=1] {transformation:[0.207f,-0.0765f,0f,0.0831f,0.0286f,0.5364f,0.0382f,0.0902f,-0.0051f,-0.0946f,0.2167f,-0.0112f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.shoe_l,limit=1] {transformation:[0.2179f,-0.0153f,0f,0.0929f,0.0302f,0.1073f,0.0458f,-0.0197f,-0.0053f,-0.0189f,0.26f,-0.003f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.torso,limit=1] {transformation:[0.44f,0f,0f,-0.22f,0f,0.6536f,-0.0306f,0.6753f,0f,0.0919f,0.2179f,-0.1089f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.arm_r,limit=1] {transformation:[0.155f,0.1881f,0f,-0.5399f,-0.0347f,0.3182f,0.13f,0.9138f,0.0445f,-0.4073f,0.1016f,0.3535f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.hand_r,limit=1] {transformation:[0.1395f,0.0376f,0f,-0.5698f,-0.0313f,0.0636f,0.117f,0.8549f,0.04f,-0.0815f,0.0914f,0.4423f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.arm_l,limit=1] {transformation:[0.155f,-0.1881f,0f,0.3849f,0.0347f,0.3182f,0.13f,0.879f,-0.0445f,-0.4073f,0.1016f,0.398f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.hand_l,limit=1] {transformation:[0.1395f,-0.0376f,0f,0.4302f,0.0313f,0.0636f,0.117f,0.8236f,-0.04f,-0.0815f,0.0914f,0.4823f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.head,limit=1] {transformation:[0.495f,0f,0f,-0.2475f,0f,0.4902f,-0.0689f,1.348f,0f,0.0689f,0.4902f,-0.1532f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p10,limit=1] {transformation:[0.0385f,0f,0f,-0.0192f,0f,0.6536f,-0.0008f,0.6447f,0f,0.0919f,0.0054f,0.1089f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p11,limit=1] {transformation:[0.4455f,0f,0f,-0.2228f,0f,0.079f,-0.0314f,1.253f,0f,0.0111f,0.2233f,-0.0305f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p12,limit=1] {transformation:[0.4455f,0f,0f,-0.2228f,0f,0.0545f,-0.0314f,0.6757f,0f,0.0077f,0.2233f,-0.1117f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p13,limit=1] {transformation:[0.0052f,0.1881f,0f,-0.5425f,-0.0012f,0.3182f,0.0347f,0.962f,0.0015f,-0.4073f,0.0271f,0.39f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p14,limit=1] {transformation:[0.1602f,0.0188f,0f,-0.5425f,-0.0359f,0.0318f,0.1344f,0.9122f,0.046f,-0.0407f,0.105f,0.3511f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p15,limit=1] {transformation:[0.0052f,-0.1881f,0f,0.5373f,0.0012f,0.3182f,0.0347f,0.9609f,-0.0015f,-0.4073f,0.0271f,0.3915f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p16,limit=1] {transformation:[0.1602f,-0.0188f,0f,0.3823f,0.0359f,0.0318f,0.1344f,0.8763f,-0.046f,-0.0407f,0.105f,0.397f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p17,limit=1] {transformation:[0.2124f,0.0077f,0f,-0.2928f,-0.0294f,0.0536f,-0.0392f,0.1579f,-0.0052f,0.0095f,0.2221f,-0.203f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p18,limit=1] {transformation:[0.2124f,-0.0077f,0f,0.0803f,0.0294f,0.0536f,0.0392f,0.0893f,-0.0052f,-0.0095f,0.2221f,-0.0139f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p19,limit=1] {transformation:[0.528f,0f,0f,-0.264f,0f,0.1961f,-0.0735f,1.6771f,0f,0.0276f,0.5229f,-0.1236f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p20,limit=1] {transformation:[0.528f,0f,0f,-0.264f,0f,0.4357f,-0.01f,1.4055f,0f,0.0612f,0.0708f,-0.1674f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p21,limit=1] {transformation:[0.0385f,0f,0f,-0.2695f,0f,0.2451f,-0.062f,1.4849f,0f,0.0344f,0.4412f,-0.1395f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p22,limit=1] {transformation:[0.0385f,0f,0f,0.231f,0f,0.2451f,-0.062f,1.4849f,0f,0.0344f,0.4412f,-0.1395f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p23,limit=1] {transformation:[0.154f,0f,0f,-0.2365f,0f,0.098f,-0.0034f,1.5188f,0f,0.0138f,0.0245f,0.3706f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p24,limit=1] {transformation:[0.154f,0f,0f,-0.088f,0f,0.0708f,-0.0034f,1.546f,0f,0.01f,0.0245f,0.3745f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p25,limit=1] {transformation:[0.154f,0f,0f,0.066f,0f,0.1089f,-0.0034f,1.5079f,0f,0.0153f,0.0245f,0.3691f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p26,limit=1] {transformation:[0.121f,0f,0f,-0.165f,0f,0.0763f,-0.0168f,1.8409f,0f,0.0107f,0.1198f,0.0549f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p27,limit=1] {transformation:[0.121f,0f,0f,0.0275f,0f,0.0763f,-0.0168f,1.8255f,0f,0.0107f,0.1198f,0.1638f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p28,limit=1] {transformation:[0.121f,0f,0f,-0.055f,0f,0.0763f,-0.0168f,1.8102f,0f,0.0107f,0.1198f,0.2727f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p29,limit=1] {transformation:[0.121f,0f,0f,-0.187f,0f,0.1089f,-0.0005f,1.4098f,0f,0.0153f,0.0033f,0.3553f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p30,limit=1] {transformation:[0.099f,0f,0f,-0.176f,0f,0.0871f,-0.0008f,1.4206f,0f,0.0122f,0.0054f,0.3579f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p31,limit=1] {transformation:[0.121f,0f,0f,0.066f,0f,0.1089f,-0.0005f,1.4098f,0f,0.0153f,0.0033f,0.3553f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p32,limit=1] {transformation:[0.099f,0f,0f,0.077f,0f,0.0871f,-0.0008f,1.4206f,0f,0.0122f,0.0054f,0.3579f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
data merge entity @e[type=block_display,tag=rz.hero.p33,limit=1] {transformation:[0.132f,0f,0f,-0.066f,0f,0.0163f,-0.0006f,1.3445f,0f,0.0023f,0.0044f,0.3461f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:8}
particle squid_ink ~0.4 ~0.1 ~7 0.05 0.05 0.05 0.01 1 force @a
particle squid_ink ~0.3236 ~0.1 ~7.2351 0.05 0.05 0.05 0.01 1 force @a
particle squid_ink ~0.1236 ~0.1 ~7.3804 0.05 0.05 0.05 0.01 1 force @a
particle squid_ink ~-0.1236 ~0.1 ~7.3804 0.05 0.05 0.05 0.01 1 force @a
particle squid_ink ~-0.3236 ~0.1 ~7.2351 0.05 0.05 0.05 0.01 1 force @a
particle squid_ink ~-0.4 ~0.1 ~7 0.05 0.05 0.05 0.01 1 force @a
particle squid_ink ~-0.3236 ~0.1 ~6.7649 0.05 0.05 0.05 0.01 1 force @a
particle squid_ink ~-0.1236 ~0.1 ~6.6196 0.05 0.05 0.05 0.01 1 force @a
particle squid_ink ~0.1236 ~0.1 ~6.6196 0.05 0.05 0.05 0.01 1 force @a
particle squid_ink ~0.3236 ~0.1 ~6.7649 0.05 0.05 0.05 0.01 1 force @a
particle dust_color_transition{from_color:[0.25,0,0.35],to_color:[0.02,0,0.03],scale:2.2} ~ ~0.6 ~7 1.2 0.6 1.2 0 6 force @a
