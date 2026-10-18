# Action bar: current ability and cooldown

# Titan Form active: show time remaining
execute if score @s founding.mode matches 1 if entity @s[tag=founding.titan] run scoreboard players operation @s founding.tmp = @s founding.form_end
execute if score @s founding.mode matches 1 if entity @s[tag=founding.titan] run scoreboard players operation @s founding.tmp -= #now founding.t
execute if score @s founding.mode matches 1 if entity @s[tag=founding.titan] run scoreboard players operation @s founding.tmp /= #20 founding.t
execute if score @s founding.mode matches 1 if entity @s[tag=founding.titan] run return run title @s actionbar [{"text":"Titan Form ","color":"gold","bold":true},{"score":{"name":"@s","objective":"founding.tmp"},"color":"yellow"},{"text":"s left - right-click to return to human form","color":"gray","bold":false}]

# Otherwise: seconds until the selected ability is ready
execute if score @s founding.mode matches 1 run scoreboard players operation @s founding.tmp = @s founding.cd_form
execute if score @s founding.mode matches 2 run scoreboard players operation @s founding.tmp = @s founding.cd_kneel
execute if score @s founding.mode matches 3 run scoreboard players operation @s founding.tmp = @s founding.cd_rumble
scoreboard players operation @s founding.tmp -= #now founding.t

execute if score @s founding.tmp matches ..0 if score @s founding.mode matches 1 run title @s actionbar [{"text":"Titan Form","color":"gold","bold":true},{"text":"  READY","color":"green"}]
execute if score @s founding.tmp matches ..0 if score @s founding.mode matches 2 run title @s actionbar [{"text":"Coordinate: Kneel","color":"aqua","bold":true},{"text":"  READY","color":"green"}]
execute if score @s founding.tmp matches ..0 if score @s founding.mode matches 3 run title @s actionbar [{"text":"The Rumbling","color":"dark_red","bold":true},{"text":"  READY","color":"green"}]
execute if score @s founding.tmp matches ..0 run return 0

scoreboard players operation @s founding.tmp /= #20 founding.t
scoreboard players add @s founding.tmp 1
execute if score @s founding.mode matches 1 run title @s actionbar [{"text":"Titan Form","color":"gold","bold":true},{"text":"  recharging: ","color":"gray","bold":false},{"score":{"name":"@s","objective":"founding.tmp"},"color":"red"},{"text":"s","color":"red"}]
execute if score @s founding.mode matches 2 run title @s actionbar [{"text":"Coordinate: Kneel","color":"aqua","bold":true},{"text":"  recharging: ","color":"gray","bold":false},{"score":{"name":"@s","objective":"founding.tmp"},"color":"red"},{"text":"s","color":"red"}]
execute if score @s founding.mode matches 3 run title @s actionbar [{"text":"The Rumbling","color":"dark_red","bold":true},{"text":"  recharging: ","color":"gray","bold":false},{"score":{"name":"@s","objective":"founding.tmp"},"color":"red"},{"text":"s","color":"red"}]
