# Runs as each commanded mob

data merge entity @s {NoAI:1b}
tag @s add founding.frozen
scoreboard players operation @s founding.until = #now founding.t
scoreboard players add @s founding.until 600
# Backup in case a modded mob ignores NoAI: no movement, no damage
effect give @s minecraft:slowness 30 9 true
effect give @s minecraft:weakness 30 9 true
effect give @s minecraft:glowing 30 0 true
particle minecraft:enchant ~ ~1 ~ 0.5 1 0.5 1 30
