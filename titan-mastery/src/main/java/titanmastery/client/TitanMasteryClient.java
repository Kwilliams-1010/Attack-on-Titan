package titanmastery.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.class_1297;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_744;
import net.minecraft.class_746;
import titanmastery.MasteryAbilities;
import titanmastery.MasteryPayload;
import titanmastery.TitanHooks;
import titanmastery.TitanMastery;

/**
 * Client side: the G / H keys, and the rage that takes your movement and camera.
 * Yarn: class_310 MinecraftClient, class_746 ClientPlayerEntity, class_744 Input, class_304 KeyBinding.
 */
public class TitanMasteryClient implements ClientModInitializer {
    private static class_304 abilityKey;
    private static class_304 formKey;
    private static float wanderYaw = 0f;
    private static int wanderTicks = 0;

    @Override
    public void onInitializeClient() {
        abilityKey = KeyBindingHelper.registerKeyBinding(new class_304("key.titanmastery.ability",
                class_3675.class_307.field_1668, 71, "category.titanmastery")); // G
        formKey = KeyBindingHelper.registerKeyBinding(new class_304("key.titanmastery.form",
                class_3675.class_307.field_1668, 72, "category.titanmastery")); // H

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            class_746 player = client.field_1724;
            while (abilityKey.method_1436()) send(player, MasteryAbilities.ACTION_ABILITY);
            while (formKey.method_1436()) send(player, MasteryAbilities.ACTION_FORM);
            if (player != null) steerRage(player);
        });
    }

    private static void send(class_746 player, int action) {
        if (player == null || TitanHooks.shiftedTitan(player) == null) return;
        if (ClientPlayNetworking.canSend(MasteryPayload.ID)) ClientPlayNetworking.send(new MasteryPayload(action));
    }

    private static boolean raging(class_746 player) {
        return player.method_6059(TitanMastery.FURY) && TitanHooks.shiftedTitan(player) != null;
    }

    private static boolean crystallized(class_746 player) {
        return player.method_6059(TitanMastery.CRYSTALLIZED) && TitanHooks.shiftedTitan(player) != null;
    }

    /** While raging, the Titan turns toward whatever is nearest; your mouse can't hold it. */
    private static void steerRage(class_746 player) {
        if (!raging(player)) return;
        class_1297 titan = TitanHooks.shiftedTitan(player);
        class_1297 target = TitanHooks.nearestTarget(titan, player, 48);
        float desired;
        if (target != null) {
            desired = TitanHooks.yawToward(titan, target);
        } else {
            if (--wanderTicks <= 0) {
                wanderTicks = 40 + player.method_59922().method_43048(60);
                wanderYaw = player.method_36454() + (player.method_59922().method_43057() - 0.5f) * 140f;
            }
            desired = wanderYaw;
        }
        float current = player.method_36454();
        float delta = TitanHooks.wrap(desired - current);
        float step = Math.max(-14f, Math.min(14f, delta));
        player.method_36456(current + step);       // setYaw
        player.method_5847(current + step);        // setHeadYaw
        player.method_36457(Math.max(-10f, Math.min(25f, player.method_36455()))); // keep pitch sane
    }

    /** Called from the KeyboardInput mixin after the keys are read. */
    public static void overrideInput(class_744 input) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) return;
        if (raging(player)) {
            input.field_3905 = 1.0f;   // movementForward: always charging
            input.field_3907 = 0.0f;   // movementSideways
            input.field_3910 = true;   // pressingForward
            input.field_3909 = false;
            input.field_3908 = false;
            input.field_3906 = false;
            input.field_3903 = false;  // sneaking can't be used to bail out
            class_1297 titan = TitanHooks.shiftedTitan(player);
            input.field_3904 = titan != null && titan.field_5976; // jump when blocked by a wall
        } else if (crystallized(player)) {
            input.field_3905 = 0f;
            input.field_3907 = 0f;
            input.field_3910 = false;
            input.field_3909 = false;
            input.field_3908 = false;
            input.field_3906 = false;
            input.field_3904 = false;
            input.field_3903 = false;
        }
    }
}
