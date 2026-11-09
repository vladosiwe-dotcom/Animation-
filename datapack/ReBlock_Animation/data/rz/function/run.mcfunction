scoreboard players add #t rz.t 1
function rz:d/root
execute if score #cam rz.t matches 1 if score #t rz.t matches 3.. as @a[tag=rz.viewer,gamemode=spectator] run spectate @e[type=item_display,tag=rz.cam,limit=1] @s
