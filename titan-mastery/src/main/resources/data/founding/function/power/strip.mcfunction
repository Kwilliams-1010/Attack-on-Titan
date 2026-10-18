# Removes the Founding Titan's power from this player (Titan Mastery reverts them if they are transformed)
tag @s remove founding.holder
scoreboard players reset @s founding.g
tellraw @s {"text":"The power of the Founding Titan has left you.","color":"gray","italic":true}
