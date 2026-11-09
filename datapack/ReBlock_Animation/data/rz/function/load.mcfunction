scoreboard objectives add rz.t dummy
scoreboard players add #play rz.t 0
tellraw @a ["",{"text":"[Re:Блок] ","color":"dark_purple","bold":true},{"text":"Анимация загружена. ","color":"gray"},{"text":"/function rz:start","color":"light_purple","clickEvent":{"action":"suggest_command","value":"/function rz:start"}},{"text":" — кино с камерой, ","color":"gray"},{"text":"/function rz:play","color":"light_purple","clickEvent":{"action":"suggest_command","value":"/function rz:play"}},{"text":" — свободный обзор.","color":"gray"}]
