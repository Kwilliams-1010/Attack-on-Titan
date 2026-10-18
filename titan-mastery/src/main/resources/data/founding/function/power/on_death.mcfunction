# Runs as and at the Founder when they die: the power is released as a serum where they fell

function founding:power/strip
execute as @e[type=minecraft:item,distance=..6] if items entity @s contents minecraft:carrot_on_a_stick[minecraft:custom_data~{founding_coord:1b}] run kill @s
loot spawn ~ ~1 ~ loot founding:serum
execute as @e[type=minecraft:item,distance=..3] if items entity @s contents minecraft:honey_bottle[minecraft:custom_data~{founding_serum:1b}] run data merge entity @s {Age:-32768,Invulnerable:1b,Glowing:1b,PickupDelay:40}

particle minecraft:end_rod ~ ~1 ~ 1 2 1 0.1 150 force
playsound minecraft:block.beacon.deactivate master @a ~ ~ ~ 4 0.5
tellraw @a [{"text":"The Founder has fallen. ","color":"gold"},{"text":"A serum of royal spinal fluid lies where they died...","color":"yellow","italic":true}]
