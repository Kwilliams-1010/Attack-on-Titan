# Leave Titan Form (manually or when the 2 minutes run out); 30 second recharge

function founding:form/remove_mods
tag @s remove founding.titan
scoreboard players operation @s founding.cd_form = #now founding.t
scoreboard players add @s founding.cd_form 600

particle minecraft:cloud ~ ~3 ~ 3 5 3 0.15 400 force
particle minecraft:campfire_cosy_smoke ~ ~4 ~ 3 5 3 0.02 80 force
playsound minecraft:block.fire.extinguish master @a ~ ~ ~ 3 0.5
