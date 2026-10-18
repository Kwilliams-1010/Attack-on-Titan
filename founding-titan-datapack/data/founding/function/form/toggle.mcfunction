execute if entity @s[tag=founding.titan] run return run function founding:form/revert
execute if score @s founding.cd_form > #now founding.t run return run function founding:use/not_ready
function founding:form/shift
