data merge entity @e[type=block_display,tag=rz.heart.p0,limit=1] {transformation:[-0.1395f,0f,0.0122f,0.1682f,0f,0.07f,0f,0.35f,-0.0122f,0f,-0.1395f,0.085f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p1,limit=1] {transformation:[-0.1395f,0f,0.0122f,-0.041f,0f,0.07f,0f,0.35f,-0.0122f,0f,-0.1395f,0.0667f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p2,limit=1] {transformation:[-0.4881f,0f,0.0122f,0.238f,0f,0.07f,0f,0.28f,-0.0427f,0f,-0.1395f,0.0911f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p3,limit=1] {transformation:[-0.4881f,0f,0.0122f,0.238f,0f,0.07f,0f,0.21f,-0.0427f,0f,-0.1395f,0.0911f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p4,limit=1] {transformation:[-0.3487f,0f,0.0122f,0.1682f,0f,0.07f,0f,0.14f,-0.0305f,0f,-0.1395f,0.085f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p5,limit=1] {transformation:[-0.2092f,0f,0.0122f,0.0985f,0f,0.07f,0f,0.07f,-0.0183f,0f,-0.1395f,0.0789f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p6,limit=1] {transformation:[-0.0697f,0f,0.0122f,0.0288f,0f,0.07f,0f,0f,-0.0061f,0f,-0.1395f,0.0728f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.warden.heartbeat master @s ~ ~ ~ 1.2 1.3
particle dust{color:[0.75,0.02,0.05],scale:1.3} ~ ~1.25 ~-2.3 0.15 0.15 0.15 0 6 force @a
title @a[tag=rz.viewer] times 3 30 3
title @a[tag=rz.viewer] subtitle {"text":"Я люблю тебя. Я люблю тебя. Я люблю тебя. ","color":"light_purple","italic":true}
title @a[tag=rz.viewer] title {"text":"","color":"dark_purple","bold":true}
