package titanmastery.mixin;

import net.minecraft.class_1282;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import titanmastery.founding.FounderManager;
import titanmastery.founding.FoundingTitanEntity;

/**
 * LivingEntity.damage: a Founder inside the Titan can't be hurt directly. A hit that reaches them is a
 * strike on the Titan's nape and lands on the Titan at triple strength.
 */
@Mixin(value = class_1309.class, remap = false)
public abstract class FounderRiderMixin {
    @Inject(method = "method_5643(Lnet/minecraft/class_1282;F)Z", at = @At("HEAD"), cancellable = true)
    private void titanmastery$napeHit(class_1282 source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof class_1657 player) || player.method_37908().field_9236) return;
        FoundingTitanEntity titan = FounderManager.titanOf(player);
        if (titan == null) return;
        if (source.method_48789(net.minecraft.class_8103.field_42242)) return; // /kill and the void still apply
        if (source.method_5529() != titan) titan.method_5643(source, amount * 3.0f);
        cir.setReturnValue(false);
    }
}
