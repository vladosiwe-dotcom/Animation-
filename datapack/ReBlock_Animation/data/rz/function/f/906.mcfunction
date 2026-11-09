particle explosion ~ ~1 ~6.6 0.3 0.3 0.3 0 4 force @a
particle sweep_attack ~ ~1.2 ~6.4 0 0 0 0 1 force @a
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.generic.explode master @s ~ ~ ~ 0.8 1.1
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.player.attack.knockback master @s ~ ~ ~ 1 0.6
data merge entity @e[type=block_display,tag=rz.cult.torso,limit=1] {transformation:[-0.44f,0f,0f,0.22f,0f,0.5406f,0.1262f,0.5969f,0f,0.3786f,-0.1802f,0.0901f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.arm_r,limit=1] {transformation:[-0.0935f,-0.4763f,0f,0.7541f,0.1145f,-0.1945f,0.1322f,1.175f,-0.1145f,0.1945f,0.1322f,0.1571f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.hand_r,limit=1] {transformation:[-0.0852f,-0.0953f,0f,0.8453f,0.1044f,-0.0389f,0.1206f,1.2248f,-0.1044f,0.0389f,0.1206f,0.119f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.arm_l,limit=1] {transformation:[-0.0935f,0.4763f,0f,-0.6606f,-0.1145f,-0.1945f,0.1322f,1.2895f,0.1145f,0.1945f,0.1322f,0.0426f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.hand_l,limit=1] {transformation:[-0.0853f,0.0953f,0f,-0.76f,-0.1044f,-0.0389f,0.1206f,1.3292f,0.1044f,0.0389f,0.1206f,0.0146f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.head,limit=1] {transformation:[-0.495f,0f,0f,0.2475f,0f,0.2092f,0.4486f,0.9763f,0f,0.4486f,-0.2092f,0.4832f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.robe,limit=1] {transformation:[-0.55f,0f,0f,0.275f,0f,0.5632f,0.2208f,0.0315f,0f,0.3943f,-0.3154f,-0.2051f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.p11,limit=1] {transformation:[-0.11f,0f,0f,0.055f,0f,1.0588f,0.0047f,0.2507f,0f,0.7413f,-0.0068f,-0.5182f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.p12,limit=1] {transformation:[-0.451f,0f,0f,0.2255f,0f,0.0721f,0.1325f,1.0668f,0f,0.0505f,-0.1892f,0.4259f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.p13,limit=1] {transformation:[-0.528f,0f,0f,0.264f,0f,0.1069f,0.4785f,1.0776f,0f,0.2293f,-0.2231f,0.7394f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.p14,limit=1] {transformation:[-0.539f,0f,0f,0.2695f,0f,0.186f,0.0748f,0.9796f,0f,0.3988f,-0.0349f,0.5423f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.p15,limit=1] {transformation:[-0.033f,0f,0f,0.2695f,0f,0.1278f,0.4536f,1.0112f,0f,0.2742f,-0.2115f,0.5579f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.p16,limit=1] {transformation:[-0.033f,0f,0f,-0.2365f,0f,0.1278f,0.4536f,1.0112f,0f,0.2742f,-0.2115f,0.5579f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.p17,limit=1] {transformation:[-0.495f,0f,0f,0.2475f,0f,0.0186f,0.0199f,1.5784f,0f,0.0399f,-0.0093f,0.603f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.p18,limit=1] {transformation:[-0.143f,0f,0f,0.198f,0f,0.0697f,0.003f,1.4831f,0f,0.1495f,-0.0014f,0.3986f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.p19,limit=1] {transformation:[-0.044f,0f,0f,0.1485f,0f,0.0232f,0.006f,1.505f,0f,0.0498f,-0.0028f,0.443f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.p20,limit=1] {transformation:[-0.143f,0f,0f,-0.055f,0f,0.0697f,0.003f,1.4831f,0f,0.1495f,-0.0014f,0.3986f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.p21,limit=1] {transformation:[-0.044f,0f,0f,-0.1045f,0f,0.0232f,0.006f,1.505f,0f,0.0498f,-0.0028f,0.443f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.p22,limit=1] {transformation:[-0.286f,0f,0f,0.143f,0f,0.0232f,0.004f,1.4435f,0f,0.0498f,-0.0019f,0.3138f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
data merge entity @e[type=block_display,tag=rz.cult.p23,limit=1] {transformation:[-0.242f,0f,0f,0.121f,0f,0.0093f,0.005f,1.4538f,0f,0.0199f,-0.0023f,0.3333f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:5}
tp @e[type=block_display,tag=rz.cult] ~ ~ ~9.5
