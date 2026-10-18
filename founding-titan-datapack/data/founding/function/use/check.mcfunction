# Runs as a player who right-clicked a carrot on a stick this tick

execute unless items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data~{founding_coord:1b}] run return 0
execute unless entity @s[tag=founding.holder] run return run title @s actionbar {"text":"The Coordinate is silent. Only the Founder can wield it.","color":"gray","italic":true}

# Sneak + right-click: switch ability
execute if predicate founding:sneaking run return run function founding:use/cycle

execute if score @s founding.mode matches 1 run return run function founding:form/toggle
execute if score @s founding.mode matches 2 run return run function founding:kneel/try
execute if score @s founding.mode matches 3 run return run function founding:rumbling/try
