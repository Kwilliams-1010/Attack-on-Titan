# Removes the Founding Titan's power from this player

execute if entity @s[tag=founding.titan] run function founding:form/remove_mods
tag @s remove founding.titan
tag @s remove founding.holder
clear @s minecraft:carrot_on_a_stick[minecraft:custom_data~{founding_coord:1b}]
scoreboard players reset @s founding.g
tellraw @s {"text":"The power of the Founding Titan has left you.","color":"gray","italic":true}
