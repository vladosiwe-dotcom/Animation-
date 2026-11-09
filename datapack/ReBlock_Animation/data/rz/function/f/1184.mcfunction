data merge entity @e[type=block_display,tag=rz.heart.p0,limit=1] {transformation:[-0.0316f,0f,0.1792f,-0.0501f,0f,0.091f,0f,0.455f,-0.1792f,0f,-0.0316f,0.2398f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p1,limit=1] {transformation:[-0.0316f,0f,0.1792f,-0.0975f,0f,0.091f,0f,0.455f,-0.1792f,0f,-0.0316f,-0.029f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p2,limit=1] {transformation:[-0.1106f,0f,0.1792f,-0.0343f,0f,0.091f,0f,0.364f,-0.6273f,0f,-0.0316f,0.3295f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p3,limit=1] {transformation:[-0.1106f,0f,0.1792f,-0.0343f,0f,0.091f,0f,0.273f,-0.6273f,0f,-0.0316f,0.3295f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p4,limit=1] {transformation:[-0.079f,0f,0.1792f,-0.0501f,0f,0.091f,0f,0.182f,-0.4481f,0f,-0.0316f,0.2398f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p5,limit=1] {transformation:[-0.0474f,0f,0.1792f,-0.0659f,0f,0.091f,0f,0.091f,-0.2689f,0f,-0.0316f,0.1502f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
data merge entity @e[type=block_display,tag=rz.heart.p6,limit=1] {transformation:[-0.0158f,0f,0.1792f,-0.0817f,0f,0.091f,0f,0f,-0.0896f,0f,-0.0316f,0.0606f,0f,0f,0f,1f],start_interpolation:0,interpolation_duration:2}
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.warden.heartbeat master @s ~ ~ ~ 1.2 0.9
particle dust{color:[0.75,0.02,0.05],scale:1.3} ~ ~1.25 ~-2.3 0.15 0.15 0.15 0 6 force @a
