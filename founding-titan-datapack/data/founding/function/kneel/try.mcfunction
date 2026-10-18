# Coordinate: Kneel - freeze all mobs (including Danny's AOT pure titans) within 64 blocks for 30 seconds

execute if score @s founding.cd_kneel > #now founding.t run return run function founding:use/not_ready

execute as @e[distance=..64,type=!minecraft:player,type=!#founding:not_commandable,tag=!founding.frozen,tag=!founding.wall,nbt=!{NoAI:1b}] at @s run function founding:kneel/freeze

# Other Subjects of Ymir (players) are briefly stunned by the scream
effect give @a[distance=0.1..64,tag=!founding.holder] minecraft:slowness 4 3 true
effect give @a[distance=0.1..64,tag=!founding.holder] minecraft:nausea 6 0 true

scoreboard players operation @s founding.cd_kneel = #now founding.t
scoreboard players add @s founding.cd_kneel 900

particle minecraft:sonic_boom ^ ^1.6 ^2 0 0 0 0 1 force
particle minecraft:end_rod ~ ~1 ~ 8 2 8 0.3 400 force
playsound minecraft:entity.elder_guardian.curse master @a ~ ~ ~ 6 0.6
playsound minecraft:event.raid.horn master @a ~ ~ ~ 8 0.8
title @s actionbar {"text":"KNEEL.","color":"aqua","bold":true}
