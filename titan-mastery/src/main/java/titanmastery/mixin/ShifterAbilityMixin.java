package titanmastery.mixin;

import daot.ArmoredTitanEntity;
import daot.AttackTitanEntity;
import daot.CartShifterTitanEntity;
import daot.FemaleTitanEntity;
import daot.FoundingTitanEntity;
import daot.TestShifterTitanEntity;
import daot.WarhammerTitanEntity;
import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import titanmastery.MasteryCombat;

/** Every class that declares Danny's triggerAbility(int): abilities stay locked until the Awakened stage. */
@Mixin(value = {AttackTitanEntity.class, ArmoredTitanEntity.class, FemaleTitanEntity.class, WarhammerTitanEntity.class,
        TestShifterTitanEntity.class, CartShifterTitanEntity.class, FoundingTitanEntity.class}, remap = false)
public abstract class ShifterAbilityMixin {
    @Inject(method = "triggerAbility(I)V", at = @At("HEAD"), cancellable = true)
    private void titanmastery$gate(int ability, CallbackInfo ci) {
        if (MasteryCombat.blockManualAbility((class_1297) (Object) this)) ci.cancel();
    }
}
