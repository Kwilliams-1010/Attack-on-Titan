tellraw @s [{"text":"\nRumbling settings","color":"gold","bold":true}]

execute if score #rumbling_breaks_blocks founding.cfg matches 1 run tellraw @s [{"text":"Wall Titans destroy terrain: ","color":"gray"},{"text":"ON ","color":"green"},{"text":"[turn off]","color":"aqua","clickEvent":{"action":"run_command","value":"/function founding:config/breaks_blocks_off"}}]
execute if score #rumbling_breaks_blocks founding.cfg matches 0 run tellraw @s [{"text":"Wall Titans destroy terrain: ","color":"gray"},{"text":"OFF ","color":"red"},{"text":"[turn on]","color":"aqua","clickEvent":{"action":"run_command","value":"/function founding:config/breaks_blocks_on"}}]

execute if score #rumbling_spares_players founding.cfg matches 1 run tellraw @s [{"text":"Wall Titans spare other players: ","color":"gray"},{"text":"ON ","color":"green"},{"text":"[turn off]","color":"aqua","clickEvent":{"action":"run_command","value":"/function founding:config/spare_players_off"}}]
execute if score #rumbling_spares_players founding.cfg matches 0 run tellraw @s [{"text":"Wall Titans spare other players: ","color":"gray"},{"text":"OFF ","color":"red"},{"text":"[turn on]","color":"aqua","clickEvent":{"action":"run_command","value":"/function founding:config/spare_players_on"}}]
