# Founding Titan: runs every tick

execute store result score #now founding.t run time query gametime
scoreboard players operation #m10 founding.t = #now founding.t
scoreboard players operation #m10 founding.t %= #10 founding.t
scoreboard players operation #m20 founding.t = #now founding.t
scoreboard players operation #m20 founding.t %= #20 founding.t

# Only the newest inheritor keeps the power (handles players who were offline when it passed on)
execute as @a[tag=founding.holder] unless score @s founding.g = #gen founding.g run function founding:power/strip

# Death of the Founder releases the power as a serum where they fell
execute as @a[tag=founding.holder,scores={founding.death=1..}] at @s run function founding:power/on_death
scoreboard players reset @a[scores={founding.death=1..}] founding.death

# Founder upkeep
execute as @a[tag=founding.holder] run function founding:power/upkeep

# The Rumbling
execute as @e[type=minecraft:giant,tag=founding.wall] at @s run function founding:rumbling/wall_tick
