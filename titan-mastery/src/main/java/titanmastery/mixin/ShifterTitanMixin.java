package titanmastery.mixin;

import daot.ArmoredTitanEntity;
import daot.AttackTitanEntity;
import daot.BeastTitanEntity;
import daot.ColossalTitanEntity;
import daot.FemaleTitanEntity;
import daot.WarhammerTitanEntity;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import titanmastery.MasteryCombat;

/**
 * The six base shifter titan classes in Danny's AOT (Jaw, Cart, Founding and Ogre extend AttackTitanEntity).
 * - damage: mastery reduces what the Titan takes (and adds the attacker's bonus)
 * - startDismounting: no reverting while raging
 * - triggerAttack: your punches don't fire while raging
 */
@Mixin(value = {AttackTitanEntity.class, ColossalTitanEntity.class, ArmoredTitanEntity.class,
        FemaleTitanEntity.class, BeastTitanEntity.class, WarhammerTitanEntity.class}, remap = false)
public abstract class ShifterTitanMixin {

    @ModifyVariable(method = "method_5643(Lnet/minecraft/class_1282;F)Z", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float titanmastery$scale(float amount, @Local(argsOnly = true) class_1282 source) {
        return MasteryCombat.modify((class_1309) (Object) this, source, amount);
    }

    @Inject(method = "startDismounting(Lnet/minecraft/class_1657;)V", at = @At("HEAD"), cancellable = true)
    private void titanmastery$holdRage(class_1657 player, CallbackInfo ci) {
        if (MasteryCombat.blockRevert((class_1297) (Object) this, player)) ci.cancel();
    }

    @Inject(method = "triggerAttack()V", at = @At("HEAD"), cancellable = true)
    private void titanmastery$noManualAttack(CallbackInfo ci) {
        if (MasteryCombat.blockManualAttack((class_1297) (Object) this)) ci.cancel();
    }
}
