package titanmastery.mixin;

import daot.ShifterTitan;
import net.minecraft.class_1282;
import net.minecraft.class_1309;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import titanmastery.MasteryCombat;

/** LivingEntity.damage(DamageSource, float): mastery bonus for hits that a shifted Titan lands on anything that isn't a Titan. */
@Mixin(value = class_1309.class, remap = false)
public abstract class LivingEntityDamageMixin {
    @ModifyVariable(method = "method_5643(Lnet/minecraft/class_1282;F)Z", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float titanmastery$scale(float amount, @Local(argsOnly = true) class_1282 source) {
        if ((Object) this instanceof ShifterTitan) return amount; // titans are handled in ShifterTitanMixin
        return MasteryCombat.modify((class_1309) (Object) this, source, amount);
    }
}
