# Titan Mastery: a raging Titan won't let its shifter out
execute if entity @s[tag=founding.titan,tag=tm_raging] run return run title @s actionbar {"text":"The Titan won't let you go!","color":"red"}
execute if entity @s[tag=founding.titan] run return run function founding:form/revert
execute if score @s founding.cd_form > #now founding.t run return run function founding:use/not_ready
function founding:form/shift
