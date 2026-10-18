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
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.class_1269;
import net.minecraft.class_1299;
import net.minecraft.class_1311;
import org.slf4j.Logger;
import titanmastery.founding.FounderManager;
import titanmastery.founding.FoundingTitanEntity;
import org.slf4j.LoggerFactory;

public class TitanMastery implements ModInitializer {
    public static final String MOD_ID = "titanmastery";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

    public static class_6880<class_1291> FURY;
    public static class_6880<class_1291> CRYSTALLIZED;
    /** Stopped by the Founder's Coordinate: a shifter can't move, punch, use abilities or revert. */
    public static class_6880<class_1291> COMMANDED;
    public static class_1299<FoundingTitanEntity> FOUNDING_TITAN;

    @Override
    public void onInitialize() {
        FURY = class_2378.method_47985(class_7923.field_41174, class_2960.method_60655(MOD_ID, "titan_fury"),
                new TitanStatusEffect(class_4081.field_18272, 0x8B0000));
        CRYSTALLIZED = class_2378.method_47985(class_7923.field_41174, class_2960.method_60655(MOD_ID, "crystallized"),
                new TitanStatusEffect(class_4081.field_18271, 0xBFEFFF));

        FOUNDING_TITAN = class_2378.method_10230(class_7923.field_41177, class_2960.method_60655(MOD_ID, "founding_titan"),
                class_1299.class_1300.<FoundingTitanEntity>method_5903(FoundingTitanEntity::new, class_1311.field_17715)
                        .method_17687(FoundingTitanEntity.WIDTH, FoundingTitanEntity.HEIGHT)
                        .method_27299(10)       // tracking range (chunks): visible from far away
                        .method_27300(2)
                        .method_5905("founding_titan"));
        FabricDefaultAttributeRegistry.register(FOUNDING_TITAN, FoundingTitanEntity.createAttributes());

        // Punching from inside the Founding Titan: the Titan's fist lands, not the Founder's.
        AttackEntityCallback.EVENT.register((player, world, hand, target, hit) -> {
            if (!(player.method_5854() instanceof FoundingTitanEntity titan) || !player.method_5667().equals(titan.getOwner())) {
                return class_1269.field_5811; // PASS
            }
            if (world.field_9236) return class_1269.field_5811;  // let the click reach the server
            if (target == titan) return class_1269.field_5814;
            if (!MasteryCombat.blockManualAttack(titan) && titan.canStrike()) titan.strike(target);
            return class_1269.field_5814; // FAIL: cancels the Founder's own tiny punch
        });

        COMMANDED = class_2378.method_47985(class_7923.field_41174, class_2960.method_60655(MOD_ID, "commanded"),
                new TitanStatusEffect(class_4081.field_18272, 0xFFD54F));

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
