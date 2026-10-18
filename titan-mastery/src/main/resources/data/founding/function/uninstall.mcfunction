# Cleans up the Founding Titan's world data.

kill @e[type=minecraft:giant,tag=founding.wall]
tag @a remove founding.holder

scoreboard objectives remove founding.death
scoreboard objectives remove founding.g
scoreboard objectives remove founding.t
scoreboard objectives remove founding.until
scoreboard objectives remove founding.cd_rumble
scoreboard objectives remove founding.cfg

tellraw @s {"text":"Founding Titan data removed.","color":"gold"}
