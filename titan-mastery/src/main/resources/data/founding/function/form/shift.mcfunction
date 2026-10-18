# Transform into the Founding Titan (about 12x human size) for 2 minutes

tag @s add founding.titan
attribute @s minecraft:generic.scale modifier add founding:titan_scale 11 add_value
attribute @s minecraft:generic.max_health modifier add founding:titan_health 180 add_value
attribute @s minecraft:generic.attack_damage modifier add founding:titan_damage 18 add_value
attribute @s minecraft:generic.armor modifier add founding:titan_armor 12 add_value
attribute @s minecraft:generic.knockback_resistance modifier add founding:titan_kb 1 add_value
attribute @s minecraft:generic.step_height modifier add founding:titan_step 6 add_value
attribute @s minecraft:generic.safe_fall_distance modifier add founding:titan_fall 60 add_value
attribute @s minecraft:generic.movement_speed modifier add founding:titan_speed 0.25 add_multiplied_base
attribute @s minecraft:player.entity_interaction_range modifier add founding:titan_reach 12 add_value
attribute @s minecraft:player.block_interaction_range modifier add founding:titan_breach 12 add_value
effect give @s minecraft:instant_health 1 10 true

scoreboard players operation @s founding.form_end = #now founding.t
scoreboard players add @s founding.form_end 2400

# Shift lightning (visual only, so nothing catches fire)
particle minecraft:flash ~ ~2 ~ 0 0 0 0 1 force
particle minecraft:explosion_emitter ~ ~ ~ 0 0 0 0 1 force
particle minecraft:electric_spark ~ ~6 ~ 2 8 2 0.5 300 force
particle minecraft:cloud ~ ~4 ~ 4 6 4 0.1 300 force
playsound minecraft:entity.lightning_bolt.thunder master @a ~ ~ ~ 6 0.7
playsound minecraft:entity.lightning_bolt.impact master @a ~ ~ ~ 4 0.6
playsound minecraft:entity.ravager.roar master @a ~ ~ ~ 4 0.5
