package titanmastery.mixin;

import net.minecraft.class_1657;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import titanmastery.founding.FoundingTitanEntity;

/** PlayerEntity.shouldDismount: sneaking doesn't drop you out of the Founding Titan; the O key reverts. */
@Mixin(value = class_1657.class, remap = false)
public abstract class FounderDismountMixin {
    @Inject(method = "method_21824()Z", at = @At("HEAD"), cancellable = true)
    private void titanmastery$stayInside(CallbackInfoReturnable<Boolean> cir) {
        if (((class_1657) (Object) this).method_5854() instanceof FoundingTitanEntity) cir.setReturnValue(false);
    }
}
