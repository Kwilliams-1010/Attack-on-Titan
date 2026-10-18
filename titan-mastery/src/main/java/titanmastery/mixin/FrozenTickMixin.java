package titanmastery.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_3218;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ServerWorld.tickEntity: a creature stopped by the Founder's Coordinate doesn't tick at all until the command
 * wears off. (Danny's pure titans switch their own AI on and off to sleep, so turning their AI off isn't enough.)
 */
@Mixin(value = class_3218.class, remap = false)
public abstract class FrozenTickMixin {
    @Inject(method = "method_18762(Lnet/minecraft/class_1297;)V", at = @At("HEAD"), cancellable = true)
    private void titanmastery$stopped(class_1297 entity, CallbackInfo ci) {
        if (entity.method_5752().contains("tm_frozen")) ci.cancel();
    }
}
