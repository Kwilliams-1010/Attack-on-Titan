scoreboard players add @s founding.mode 1
execute unless score @s founding.mode matches 1..3 run scoreboard players set @s founding.mode 1
playsound minecraft:block.amethyst_block.chime player @s ~ ~ ~ 1 1.2

execute if score @s founding.mode matches 1 run tellraw @s [{"text":"Ability: ","color":"gray"},{"text":"Titan Form","color":"gold","bold":true},{"text":" - become a towering Titan for 2 minutes.","color":"gray"}]
execute if score @s founding.mode matches 2 run tellraw @s [{"text":"Ability: ","color":"gray"},{"text":"Coordinate: Kneel","color":"aqua","bold":true},{"text":" - freeze every mob and Titan within 64 blocks for 30 seconds.","color":"gray"}]
execute if score @s founding.mode matches 3 run tellraw @s [{"text":"Ability: ","color":"gray"},{"text":"The Rumbling","color":"dark_red","bold":true},{"text":" - awaken a line of Wall Titans that march where you face.","color":"gray"}]
