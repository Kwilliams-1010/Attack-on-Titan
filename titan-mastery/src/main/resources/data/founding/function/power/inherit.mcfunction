# Runs as the player who inherits the Founding Titan (born with it, drank the serum, or /mastery founding)

advancement revoke @s only founding:drink_serum
execute if entity @s[tag=founding.holder] run return run tellraw @s {"text":"The Founding Titan already dwells within you.","color":"gray","italic":true}

# New generation: any previous Founder loses the power, even if they are offline right now
scoreboard players add #gen founding.g 1
scoreboard players operation @s founding.g = #gen founding.g
scoreboard players set #claimed founding.cfg 1

tag @s add founding.holder
scoreboard players set @s founding.cd_rumble 0
scoreboard players set @s founding.death 0

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
tellraw @s [{"text":"Press ","color":"gray"},{"keybind":"key.titanmastery.founding_shift","color":"gold"},{"text":" to transform. As your mastery grows: ","color":"gray"},{"keybind":"key.titanmastery.ability","color":"gold"},{"text":" Coordinate, ","color":"gray"},{"keybind":"key.titanmastery.form","color":"gold"},{"text":" Paths of Ymir, ","color":"gray"},{"keybind":"key.titanmastery.rumbling","color":"gold"},{"text":" The Rumbling.","color":"gray"}]
