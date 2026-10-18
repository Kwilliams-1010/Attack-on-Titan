# The Rumbling - awaken 7 Wall Titans that march in the direction you face for 60 seconds

execute if score @s founding.cd_rumble > #now founding.t run return run title @s actionbar {"text":"The Rumbling is still recharging.","color":"red"}
execute if entity @e[type=minecraft:giant,tag=founding.wall] run return run tellraw @s {"text":"The Rumbling is already underway.","color":"red"}

execute rotated ~ 0 positioned ^ ^ ^24 run function founding:rumbling/summon_line
execute rotated ~ 0 as @e[type=minecraft:giant,tag=founding.wall_new] positioned as @s run tp @s ~ ~ ~ ~ 0
scoreboard players operation @e[type=minecraft:giant,tag=founding.wall_new] founding.until = #now founding.t
scoreboard players add @e[type=minecraft:giant,tag=founding.wall_new] founding.until 1200
tag @e[type=minecraft:giant,tag=founding.wall_new] remove founding.wall_new

scoreboard players operation @s founding.cd_rumble = #now founding.t
scoreboard players add @s founding.cd_rumble 6000

execute as @a at @s run playsound minecraft:event.raid.horn master @s ~ ~ ~ 1 0.5
execute as @a at @s run playsound minecraft:entity.warden.emerge master @s ~ ~ ~ 1 0.5
title @a times 10 60 20
title @a subtitle {"text":"The walls are walking.","color":"gray","italic":true}
title @a title {"text":"THE RUMBLING","color":"dark_red","bold":true}
tellraw @a [{"selector":"@s","color":"gold"},{"text":" has unleashed the Rumbling.","color":"dark_red"}]
