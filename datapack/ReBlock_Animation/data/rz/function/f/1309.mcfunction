particle reverse_portal ~0.3291 ~3.32 ~-2.9279 0 0 0 0 3 force @a
particle enchant ~0.3291 ~0.66 ~-4.0721 0 0 0 0 2 force @a
particle reverse_portal ~-0.66 ~3.32 ~-3.5007 0 0 0 0 3 force @a
particle enchant ~-0.66 ~0.66 ~-3.4993 0 0 0 0 2 force @a
particle reverse_portal ~0.3304 ~3.32 ~-4.0713 0 0 0 0 3 force @a
particle enchant ~0.3304 ~0.66 ~-2.9287 0 0 0 0 2 force @a
execute as @e[type=block_display,tag=rz.mage] run data merge entity @s {teleport_duration:0}
execute as @e[type=block_display,tag=rz.maid] run data merge entity @s {teleport_duration:0}
execute as @e[type=block_display,tag=rz.cult] run data merge entity @s {teleport_duration:0}
data merge entity @e[type=item_display,tag=rz.cam,limit=1] {teleport_duration:0}
