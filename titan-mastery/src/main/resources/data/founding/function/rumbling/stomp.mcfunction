# Every half second: footstep, dust, crush everything underfoot

playsound minecraft:entity.ravager.step hostile @a[distance=..128] ~ ~ ~ 6 0.4
particle minecraft:explosion ~ ~ ~ 5 0.5 5 0 6 force
particle minecraft:campfire_cosy_smoke ~ ~1 ~ 5 1 5 0.01 8 force

execute as @e[distance=..9,type=!minecraft:player,type=!#founding:not_commandable,tag=!founding.wall] run damage @s 40 minecraft:falling_block
execute if score #rumbling_spares_players founding.cfg matches 0 as @a[distance=..9,tag=!founding.holder,gamemode=!creative,gamemode=!spectator] run damage @s 40 minecraft:falling_block

execute if score #rumbling_breaks_blocks founding.cfg matches 1 run function founding:rumbling/break
