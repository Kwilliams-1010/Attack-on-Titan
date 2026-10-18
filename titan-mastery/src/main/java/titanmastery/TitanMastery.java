package titanmastery;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_1291;
import net.minecraft.class_1308;
import net.minecraft.class_2378;
import net.minecraft.class_2960;
import net.minecraft.class_4081;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TitanMastery implements ModInitializer {
    public static final String MOD_ID = "titanmastery";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

    public static class_6880<class_1291> FURY;
    public static class_6880<class_1291> CRYSTALLIZED;

    @Override
    public void onInitialize() {
        FURY = class_2378.method_47985(class_7923.field_41174, class_2960.method_60655(MOD_ID, "titan_fury"),
                new TitanStatusEffect(class_4081.field_18272, 0x8B0000));
        CRYSTALLIZED = class_2378.method_47985(class_7923.field_41174, class_2960.method_60655(MOD_ID, "crystallized"),
                new TitanStatusEffect(class_4081.field_18271, 0xBFEFFF));

        PayloadTypeRegistry.playC2S().register(MasteryPayload.ID, MasteryPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(MasteryPayload.ID, (payload, context) ->
                context.server().execute(() -> MasteryAbilities.handle(context.player(), payload.action())));

        ServerTickEvents.END_SERVER_TICK.register(MasteryManager::tick);
        ServerLivingEntityEvents.AFTER_DEATH.register(MasteryManager::onDeath);
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> MasteryManager.forget(handler.method_32311()));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            for (TitanType t : TitanType.values()) Mastery.objective(server, t);
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> MasteryManager.unfreezeAll());
        // A mob frozen by the Coordinate when the world closed wakes up when it loads again.
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof class_1308 mob && mob.method_5752().contains("tm_frozen")) MasteryManager.unfreeze(mob);
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> MasteryCommand.register(dispatcher));
        LOG.info("Titan Mastery loaded: nine Titans, six stages of control.");
    }
}
