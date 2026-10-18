package titanmastery.mixin.client;

import net.minecraft.class_743;
import net.minecraft.class_744;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import titanmastery.client.TitanMasteryClient;

/** KeyboardInput.tick(boolean slowDown, float slowDownFactor): rewrite the movement keys while the Titan rages. */
@Mixin(value = class_743.class, remap = false)
public abstract class KeyboardInputMixin {
    @Inject(method = "method_3129(ZF)V", at = @At("TAIL"))
    private void titanmastery$rage(boolean slowDown, float factor, CallbackInfo ci) {
        TitanMasteryClient.overrideInput((class_744) (Object) this);
    }
}
