# Founding Titan (bundled with Titan Mastery): setup on world load and /reload

scoreboard objectives add founding.death deathCount
scoreboard objectives add founding.g dummy
scoreboard objectives add founding.t dummy
scoreboard objectives add founding.until dummy
scoreboard objectives add founding.cd_rumble dummy
scoreboard objectives add founding.cfg dummy

# Constants
scoreboard players set #10 founding.t 10
scoreboard players set #20 founding.t 20

# Inheritance generation counter (only the newest Founder keeps the power)
scoreboard players add #gen founding.g 0

# Settings (only set if missing, so changes survive /reload)
#   #claimed  1 once anyone has held the Founding Titan; royal-blooded newcomers can only be born with it while 0
#   #chance   percent chance for a royal-blooded newcomer to be born the Founder
execute unless score #claimed founding.cfg matches -2147483648..2147483647 run scoreboard players set #claimed founding.cfg 0
execute unless score #chance founding.cfg matches -2147483648..2147483647 run scoreboard players set #chance founding.cfg 25
execute unless score #rumbling_breaks_blocks founding.cfg matches -2147483648..2147483647 run scoreboard players set #rumbling_breaks_blocks founding.cfg 0
execute unless score #rumbling_spares_players founding.cfg matches -2147483648..2147483647 run scoreboard players set #rumbling_spares_players founding.cfg 1
