tp @e[type=item_display,tag=rz.cam,limit=1] ~-3 ~2 ~-2.5 facing ~2.5 ~1.3 ~-0.8
execute as @e[type=block_display,tag=rz.mage] run data merge entity @s {teleport_duration:40}
