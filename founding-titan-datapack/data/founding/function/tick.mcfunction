# Founding Titan datapack - runs every tick

execute store result score #now founding.t run time query gametime
scoreboard players operation #m10 founding.t = #now founding.t
scoreboard players operation #m10 founding.t %= #10 founding.t
scoreboard players operation #m20 founding.t = #now founding.t
scoreboard players operation #m20 founding.t %= #20 founding.t

# Only the newest inheritor keeps the power (handles players who were offline when it passed on)
execute as @a[tag=founding.holder] unless score @s founding.g = #gen founding.g run function founding:power/strip

# Death of the Founder releases the power as a serum at the spot they died
execute as @a[tag=founding.holder,scores={founding.death=1..}] at @s run function founding:power/on_death
scoreboard players reset @a[scores={founding.death=1..}] founding.death

# Lost your Coordinate? /trigger founding.item gives a new one (Founder only)
scoreboard players enable @a[tag=founding.holder] founding.item
execute as @a[tag=founding.holder,scores={founding.item=1..}] run loot give @s loot founding:coordinate
scoreboard players set @a[scores={founding.item=1..}] founding.item 0

# Right-clicking the Coordinate
execute as @a[scores={founding.use=1..}] at @s run function founding:use/check
scoreboard players reset @a[scores={founding.use=1..}] founding.use

# Founder upkeep: regeneration, Titan Form timer, HUD
execute as @a[tag=founding.holder] at @s run function founding:power/upkeep

# Release mobs whose Kneel has worn off
execute as @e[tag=founding.frozen] if score @s founding.until <= #now founding.t run function founding:kneel/release

# The Rumbling
execute as @e[type=minecraft:giant,tag=founding.wall] at @s run function founding:rumbling/wall_tick
