# Cleans up everything this pack added. Run it, then remove the datapack and /reload.

execute as @e[tag=founding.frozen] run function founding:kneel/release
kill @e[type=minecraft:giant,tag=founding.wall]
execute as @a[tag=founding.titan] run function founding:form/remove_mods
tag @a remove founding.titan
tag @a remove founding.holder
tag @a remove founding.loaded_msg

scoreboard objectives remove founding.use
scoreboard objectives remove founding.death
scoreboard objectives remove founding.item
scoreboard objectives remove founding.mode
scoreboard objectives remove founding.g
scoreboard objectives remove founding.t
scoreboard objectives remove founding.tmp
scoreboard objectives remove founding.until
scoreboard objectives remove founding.form_end
scoreboard objectives remove founding.cd_form
scoreboard objectives remove founding.cd_kneel
scoreboard objectives remove founding.cd_rumble
scoreboard objectives remove founding.cfg

tellraw @s {"text":"Founding Titan data removed. Now disable the datapack (/datapack disable) or delete it, then /reload.","color":"gold"}
