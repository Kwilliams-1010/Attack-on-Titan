package titanmastery.mixin;

import daot.CartShifterTitanEntity;
import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import titanmastery.MasteryCombat;

/** The Cart Titan overrides triggerAttack, so it gets the same rage gate. */
@Mixin(value = CartShifterTitanEntity.class, remap = false)
public abstract class CartAttackMixin {
    @Inject(method = "triggerAttack()V", at = @At("HEAD"), cancellable = true)
    private void titanmastery$noManualAttack(CallbackInfo ci) {
        if (MasteryCombat.blockManualAttack((class_1297) (Object) this)) ci.cancel();
    }
}
