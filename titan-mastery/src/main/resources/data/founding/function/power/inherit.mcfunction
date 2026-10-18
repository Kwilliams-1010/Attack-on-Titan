# Runs as the player who drank the Founding Titan Serum (or via /function founding:give/power)

advancement revoke @s only founding:drink_serum
execute if entity @s[tag=founding.holder] run return run tellraw @s {"text":"The Founding Titan already dwells within you.","color":"gray","italic":true}

# New generation: any previous Founder loses the power, even if they are offline right now
scoreboard players add #gen founding.g 1
scoreboard players operation @s founding.g = #gen founding.g

tag @s add founding.holder
tag @s remove founding.titan
scoreboard players set @s founding.mode 1
scoreboard players set @s founding.cd_form 0
scoreboard players set @s founding.cd_kneel 0
scoreboard players set @s founding.cd_rumble 0
scoreboard players set @s founding.form_end 0
scoreboard players set @s founding.death 0

loot give @s loot founding:coordinate

effect give @s minecraft:darkness 4 0 true
effect give @s minecraft:regeneration 10 2 true
particle minecraft:end_rod ~ ~1 ~ 0.6 1.2 0.6 0.15 200 force
particle minecraft:totem_of_undying ~ ~1 ~ 0.5 1 0.5 0.6 120 force
playsound minecraft:block.beacon.activate master @a ~ ~ ~ 4 0.5
playsound minecraft:entity.lightning_bolt.thunder master @a ~ ~ ~ 4 0.6

title @s times 10 70 20
title @s subtitle {"text":"You can see the Paths. Every Subject of Ymir answers to you.","color":"gray","italic":true}
title @s title {"text":"The Founding Titan","color":"gold","bold":true}
tellraw @a [{"selector":"@s","color":"gold"},{"text":" has inherited the Founding Titan.","color":"yellow"}]
tellraw @s [{"text":"Hold the ","color":"gray"},{"text":"Coordinate","color":"gold"},{"text":". Right-click to use an ability, sneak + right-click to switch. Lost it? Type ","color":"gray"},{"text":"/trigger founding.item","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger founding.item"}}]
