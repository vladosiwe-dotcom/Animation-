particle snowflake ~1.4899 ~1.1906 ~1.8015 0 0 0 0 1 force @a
particle snowflake ~1.4008 ~1.5811 ~1.8015 0 0 0 0 1 force @a
particle snowflake ~1.151 ~1.8943 ~1.8015 0 0 0 0 1 force @a
particle snowflake ~0.7902 ~2.0681 ~1.8015 0 0 0 0 1 force @a
particle snowflake ~0.3896 ~2.0681 ~1.8015 0 0 0 0 1 force @a
particle snowflake ~0.0287 ~1.8943 ~1.8015 0 0 0 0 1 force @a
particle snowflake ~-0.221 ~1.5811 ~1.8015 0 0 0 0 1 force @a
particle snowflake ~-0.3101 ~1.1906 ~1.8015 0 0 0 0 1 force @a
particle snowflake ~-0.221 ~0.8001 ~1.8015 0 0 0 0 1 force @a
particle snowflake ~0.0287 ~0.487 ~1.8015 0 0 0 0 1 force @a
particle snowflake ~0.3896 ~0.3132 ~1.8015 0 0 0 0 1 force @a
particle snowflake ~0.7902 ~0.3132 ~1.8015 0 0 0 0 1 force @a
particle snowflake ~1.151 ~0.487 ~1.8015 0 0 0 0 1 force @a
particle snowflake ~1.4008 ~0.8001 ~1.8015 0 0 0 0 1 force @a
particle end_rod ~1.1399 ~1.1906 ~1.8015 0 0 0 0 1 force @a
particle end_rod ~0.9788 ~1.5796 ~1.8015 0 0 0 0 1 force @a
particle end_rod ~0.5899 ~1.7406 ~1.8015 0 0 0 0 1 force @a
particle end_rod ~0.201 ~1.5796 ~1.8015 0 0 0 0 1 force @a
particle end_rod ~0.0399 ~1.1906 ~1.8015 0 0 0 0 1 force @a
particle end_rod ~0.201 ~0.8017 ~1.8015 0 0 0 0 1 force @a
particle end_rod ~0.5899 ~0.6406 ~1.8015 0 0 0 0 1 force @a
particle end_rod ~0.9788 ~0.8017 ~1.8015 0 0 0 0 1 force @a
particle dust_color_transition{from_color:[0.75f,0.9f,1f],to_color:[0.4f,0.6f,1f],scale:1f} ~-0.1691 ~1.0255 ~3.6729 0.05 0.05 0.05 0.01 2 force @a
execute as @a[tag=rz.viewer] at @s run playsound minecraft:entity.player.attack.sweep master @s ~ ~ ~ 0.6 1.8
summon block_display ~0.1915 ~0.8077 ~1.8015 {Tags:["rz.e","rz.fx5"],block_state:{Name:"minecraft:packed_ice"},transformation:[0.1524f,-0.1578f,0.1069f,-0.0507f,0.1517f,0.1517f,0.312f,-0.3077f,-0.0468f,-0.0224f,1.3606f,-0.6457f,0f,0f,0f,1f],brightness:{sky:15,block:15},teleport_duration:8,Glowing:1b,glow_color_override:10475775}
