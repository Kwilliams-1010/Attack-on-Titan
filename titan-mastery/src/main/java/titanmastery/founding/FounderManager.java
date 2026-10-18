package titanmastery.founding;

import daot.BloodlineData;
import daot.BloodlineType;
import net.minecraft.class_124;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1322;
import net.minecraft.class_1324;
import net.minecraft.class_1538;
import net.minecraft.class_1657;
import net.minecraft.class_1299;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_266;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_5134;
import net.minecraft.class_9013;
import net.minecraft.server.MinecraftServer;
import titanmastery.MasteryManager;
import titanmastery.Session;
import titanmastery.TitanHooks;
import titanmastery.TitanMastery;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The Founding Titan power: who holds it, being born with it, and shifting in and out of the Titan.
 * Holding the power is tracked by the bundled datapack (tag founding.holder, one holder at a time).
 */
public final class FounderManager {
    private FounderManager() {}

    public static final String HOLDER_TAG = "founding.holder";
    public static final String ROLLED_TAG = "tm_bloodroll";
    public static final String SHIFTED_TAG = "tm_founding_shifted";
    private static final class_2960 REACH_ID = class_2960.method_60655(TitanMastery.MOD_ID, "founding_reach");

    private static final Map<UUID, Long> SHIFT_READY = new HashMap<>();

    public static boolean isFounder(class_1657 player) {
        return player.method_5752().contains(HOLDER_TAG);
    }

    /** The Titan this Founder is shifted into: our Founding Titan or SpringDAOT's Ymir. */
    public static class_1297 founderBody(class_1657 player) {
        FoundingTitanEntity ours = titanOf(player);
        if (ours != null) return ours;
        class_1297 v = TitanHooks.shiftedTitan(player);
        return v != null && titanmastery.TitanType.isSpringFounding(v) ? v : null;
    }

    public static final class_2960 SPRING_YMIR = class_2960.method_60655("springdaot", "ymirfounding");

    /** SpringDAOT installed and not switched off with /mastery founding model original. */
    public static boolean useSpringYmir(MinecraftServer server) {
        return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("springdaot")
                && net.minecraft.class_7923.field_41177.method_10250(SPRING_YMIR)
                && cfg(server, "#original_model") == 0;
    }

    public static FoundingTitanEntity titanOf(class_1657 player) {
        if (player.method_5854() instanceof FoundingTitanEntity t && player.method_5667().equals(t.getOwner())) return t;
        return null;
    }

    // ------------------------------------------------------------------ the O key

    public static void toggleShift(class_3222 player) {
        if (founderBody(player) != null) revert(player, false);
        else shift(player);
    }

    public static void shift(class_3222 player) {
        if (!isFounder(player)) {
            player.method_7353(class_2561.method_43470("You don't hold the power of the Founding Titan.").method_27692(class_124.field_1080), true);
            return;
        }
        if (player.method_5854() != null) {
            player.method_7353(class_2561.method_43470("You can't transform while riding something.").method_27692(class_124.field_1080), true);
            return;
        }
        long now = MasteryManager.now(player.method_5682());
        long ready = SHIFT_READY.getOrDefault(player.method_5667(), 0L);
        if (now < ready) {
            player.method_7353(class_2561.method_43470("Your body is still recovering (" + ((ready - now) / 20 + 1) + "s).").method_27692(class_124.field_1080), true);
            return;
        }

        class_3218 world = player.method_51469();
        if (useSpringYmir(player.method_5682()) && shiftIntoSpringYmir(player, world)) return;
        FoundingTitanEntity titan = TitanMastery.FOUNDING_TITAN.method_5883(world); // EntityType.create
        if (titan == null) return;
        titan.method_5808(player.method_23317(), player.method_23318(), player.method_23321(), player.method_36454(), 0f); // refreshPositionAndAngles
        titan.setOwner(player.method_5667());
        world.method_8649(titan); // spawnEntity
        if (!player.method_5873(titan, true)) { // startRiding(force)
            titan.method_31472();
            return;
        }

        player.method_5780(SHIFTED_TAG);
        player.method_6092(new class_1293(class_1294.field_5905, -1, 0, false, false, false)); // invisible inside the Titan
        class_1324 reach = player.method_5996(class_5134.field_47759); // entity interaction range
        if (reach != null) {
            reach.method_6200(REACH_ID);
            reach.method_26835(new class_1322(REACH_ID, 16.0, class_1322.class_1323.field_6328));
        }

        // The transformation: cosmetic lightning, a blast of steam and a shockwave that throws back what's nearby
        class_1538 bolt = class_1299.field_6112.method_5883(world);
        if (bolt != null) {
            bolt.method_29495(player.method_19538());
            bolt.method_29498(true); // cosmetic: no fire, no damage
            world.method_8649(bolt);
        }
        world.method_14199(class_2398.field_11236, titan.method_23317(), titan.method_23318() + 4, titan.method_23321(), 6, 3, 4, 3, 0);
        world.method_14199(class_2398.field_11204, titan.method_23317(), titan.method_23318() + 8, titan.method_23321(), 300, 4, 8, 4, 0.1);
        world.method_14199(class_2398.field_29644, titan.method_23317(), titan.method_23318() + 10, titan.method_23321(), 200, 3, 10, 3, 0.4);
        for (class_1309 e : world.method_8390(class_1309.class, titan.method_5829().method_1014(8.0),
                x -> x != titan && x != player && x.method_5805())) {
            class_243 push = e.method_19538().method_1020(titan.method_19538());
            push = new class_243(push.field_1352, 0, push.field_1350);
            push = (push.method_1027() < 1.0E-4 ? new class_243(0, 0, 1) : push.method_1029()).method_1021(1.8).method_1031(0, 0.6, 0);
            e.method_18799(e.method_18798().method_1019(push));
            e.field_6037 = true;
        }
        titan.roar();
        world.method_43128(null, titan.method_23317(), titan.method_23318(), titan.method_23321(),
                class_3417.field_14865, class_3419.field_15248, 8.0f, 0.7f); // lightning thunder
    }

    /** SpringDAOT's Ymir Founding Titan, spawned the same way Danny's own shifting does it. */
    private static boolean shiftIntoSpringYmir(class_3222 player, class_3218 world) {
        try {
            class_1297 e = net.minecraft.class_7923.field_41177.method_10223(SPRING_YMIR).method_5883(world);
            if (!(e instanceof daot.AttackTitanEntity ymir)) return false;
            ymir.method_5808(player.method_23317(), player.method_23318(), player.method_23321(), player.method_36454(), 0f);
            world.method_8649(ymir);
            ymir.onPlayerShift(player);
            if (TitanHooks.shiftedTitan(player) == ymir) return true;
            ymir.method_31472();
        } catch (Throwable t) {
            TitanMastery.LOG.warn("Couldn't shift into SpringDAOT's Ymir Founding Titan, using the Titan Mastery model instead", t);
        }
        return false;
    }

    /** Leave the Titan. {@code forced} skips the rage check (rage burning out, power lost). */
    public static void revert(class_3222 player, boolean forced) {
        class_1297 body = founderBody(player);
        if (body == null) return;
        if (!forced) {
            Session s = MasteryManager.session(player);
            if (s != null && s.inFury) {
                player.method_7353(class_2561.method_43470("The Titan won't let you go!").method_27692(class_124.field_1061), true);
                return;
            }
            if (titanmastery.MasteryCombat.isCommanded(player)) return;
        }
        if (!(body instanceof FoundingTitanEntity titan)) {
            TitanHooks.forceRevert(body, player); // SpringDAOT's Ymir leaves through Danny's own dismount
            return;
        }
        player.method_5848(); // stopRiding
        cleanupRider(player);
        steamAway(titan);
        titan.method_31472();
        SHIFT_READY.put(player.method_5667(), MasteryManager.now(player.method_5682()) + 10 * 20);
    }

    /** Called when the Titan body is destroyed with the Founder inside. */
    public static void onTitanFallen(class_1657 founder, FoundingTitanEntity titan) {
        if (!(founder instanceof class_3222 player)) return;
        player.method_5848();
        cleanupRider(player);
        player.method_7353(class_2561.method_43470("Your Titan has fallen. You're cut from its nape.").method_27692(class_124.field_1061), false);
        player.method_6092(new class_1293(class_1294.field_5911, 30 * 20, 1));
        player.method_6092(new class_1293(class_1294.field_5909, 30 * 20, 1));
        SHIFT_READY.put(player.method_5667(), MasteryManager.now(player.method_5682()) + 60 * 20);
    }

    static void cleanupRider(class_3222 player) {
        if (!player.method_5752().contains(SHIFTED_TAG)) return;
        player.method_5738(SHIFTED_TAG);
        player.method_6016(class_1294.field_5905);
        class_1324 reach = player.method_5996(class_5134.field_47759);
        if (reach != null) reach.method_6200(REACH_ID);
    }

    static void steamAway(FoundingTitanEntity titan) {
        if (!(titan.method_37908() instanceof class_3218 world)) return;
        world.method_14199(class_2398.field_11204, titan.method_23317(), titan.method_23318() + 8, titan.method_23321(), 400, 3, 9, 3, 0.12);
        world.method_14199(class_2398.field_17430, titan.method_23317(), titan.method_23318() + 10, titan.method_23321(), 60, 3, 8, 3, 0.02);
        world.method_43128(null, titan.method_23317(), titan.method_23318(), titan.method_23321(),
                class_3417.field_15102, class_3419.field_15248, 4.0f, 0.5f); // fire extinguish
    }

    // ------------------------------------------------------------------ every tick

    public static void tick(MinecraftServer server, long now) {
        for (class_3222 player : MasteryManager.players(server)) {
            class_1297 body = founderBody(player);
            if (body != null && !(body instanceof FoundingTitanEntity)) {
                if (!isFounder(player) && !player.method_5752().contains("ymirfounding")) revert(player, true);
            }
            FoundingTitanEntity titan = titanOf(player);
            if (titan != null) {
                if (!isFounder(player)) revert(player, true); // the power passed to someone else
                else if (!player.method_6059(class_1294.field_5905)) {
                    player.method_6092(new class_1293(class_1294.field_5905, -1, 0, false, false, false));
                }
            } else {
                cleanupRider(player); // dismounted some other way (teleport, /ride, death)
            }
            if (now % 100 == 0) rollBloodline(player);
        }
    }

    // ------------------------------------------------------------------ born the Founder

    /** One roll per player, the first time they have royal blood. Only if nobody holds the Founding Titan. */
    public static void rollBloodline(class_3222 player) {
        if (player.method_5752().contains(ROLLED_TAG)) return;
        MinecraftServer server = player.method_5682();
        BloodlineType blood;
        try {
            blood = BloodlineData.get(server).getBloodline(player.method_5667());
        } catch (Throwable t) {
            return; // Danny's AoT changed its bloodline storage; skip rather than crash
        }
        if (blood == null) return;              // not assigned yet: Danny assigns it on first join
        if (blood != BloodlineType.ROYAL) return; // checked again later: a bloodline can still change to royal
        player.method_5780(ROLLED_TAG);
        if (cfg(server, "#claimed") != 0) return;
        int chance = cfg(server, "#chance");
        if (player.method_59922().method_43048(100) >= chance) return;

        TitanHooks.runFunction(player, "founding:power/inherit");
        player.method_7353(class_2561.method_43470("The blood of Ymir stirs in you. You were born the Founder.")
                .method_27692(class_124.field_1065).method_27692(class_124.field_1067), false);
    }

    /** Reads a value kept by the datapack in the founding.cfg scoreboard. */
    public static int cfg(MinecraftServer server, String holder) {
        class_266 obj = server.method_3845().method_1170("founding.cfg");
        if (obj == null) return 0;
        class_9013 score = server.method_3845().method_55430(net.minecraft.class_9015.method_55422(holder), obj);
        return score == null ? 0 : score.method_55397();
    }
}
