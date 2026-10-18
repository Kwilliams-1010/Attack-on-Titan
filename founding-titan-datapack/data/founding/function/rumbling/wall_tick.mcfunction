# Runs every tick as and at each Wall Titan

execute if score @s founding.until <= #now founding.t run return run function founding:rumbling/end

# Climb over terrain, otherwise march forward
execute unless block ~ ~ ~ #founding:passable run return run tp @s ^ ^1 ^0.2
tp @s ^ ^ ^0.35

execute if score #m10 founding.t matches 0 run function founding:rumbling/stomp
