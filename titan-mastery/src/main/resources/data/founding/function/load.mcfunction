# Founding Titan datapack - setup (runs on world load and /reload)

scoreboard objectives add founding.use minecraft.used:minecraft.carrot_on_a_stick
scoreboard objectives add founding.death deathCount
scoreboard objectives add founding.item trigger
scoreboard objectives add founding.mode dummy
scoreboard objectives add founding.g dummy
scoreboard objectives add founding.t dummy
scoreboard objectives add founding.tmp dummy
scoreboard objectives add founding.until dummy
scoreboard objectives add founding.form_end dummy
scoreboard objectives add founding.cd_form dummy
scoreboard objectives add founding.cd_kneel dummy
scoreboard objectives add founding.cd_rumble dummy
scoreboard objectives add founding.cfg dummy

# Constants
scoreboard players set #10 founding.t 10
scoreboard players set #20 founding.t 20

# Inheritance generation counter (only the newest Founder keeps the power)
scoreboard players add #gen founding.g 0

# Config defaults (only set if missing, so your choices survive /reload)
execute unless score #rumbling_breaks_blocks founding.cfg matches -2147483648..2147483647 run scoreboard players set #rumbling_breaks_blocks founding.cfg 0
execute unless score #rumbling_spares_players founding.cfg matches -2147483648..2147483647 run scoreboard players set #rumbling_spares_players founding.cfg 1

tellraw @a[tag=!founding.loaded_msg] [{"text":"[Founding Titan] ","color":"gold"},{"text":"Loaded. Ops: run ","color":"gray"},{"text":"/function founding:help","color":"yellow","clickEvent":{"action":"run_command","value":"/function founding:help"}},{"text":" for instructions.","color":"gray"}]
tag @a add founding.loaded_msg
