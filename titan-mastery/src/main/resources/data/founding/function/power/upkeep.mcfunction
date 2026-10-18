# Runs every tick as and at the Founder

# Titan healing
execute if score #m20 founding.t matches 0 run effect give @s minecraft:regeneration 3 0 true

# Titan Form: steam, and automatic revert when time runs out
execute if entity @s[tag=founding.titan] run particle minecraft:cloud ~ ~ ~ 1.5 3 1.5 0.02 3
execute if entity @s[tag=founding.titan] if score @s founding.form_end <= #now founding.t run function founding:form/revert

# HUD while holding the Coordinate
execute if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{founding_coord:1b}] run function founding:power/hud
