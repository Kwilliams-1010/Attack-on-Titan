package titanmastery;

import daot.ShifterTitan;
import net.minecraft.class_124;
import net.minecraft.class_1282;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_2398;
import net.minecraft.class_2561;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks who is shifted, grants mastery XP, and runs rage (fury), the passive mastery bonuses and active forms.
 * Yarn: class_3222 ServerPlayerEntity, class_3218 ServerWorld, class_1293 StatusEffectInstance, class_1294 StatusEffects.
 */
public final class MasteryManager {
    private MasteryManager() {}

    static final Map<UUID, Session> SESSIONS = new HashMap<>();
    private static final Map<UUID, Map<TitanType, Long>> ABILITY_COOLDOWNS = new HashMap<>();
    private static final Map<UUID, Map<TitanType, Long>> FORM_COOLDOWNS = new HashMap<>();
    private static final List<Frozen> FROZEN = new ArrayList<>();

    record Frozen(class_1308 mob, long until) {}

    public static long now(MinecraftServer server) {
        return server.method_3780(); // getTicks
    }

    public static Session session(class_1657 player) {
        return player == null ? null : SESSIONS.get(player.method_5667());
    }

    /** The server player shifted into this titan, if any. */
    public static class_3222 riderOf(class_1297 titan) {
        if (titan instanceof class_3222 p) { // our Founding Titan is the player's own giant body
            Session s = SESSIONS.get(p.method_5667());
            return (s != null && s.titan == p) ? p : null;
        }
        if (!(titan instanceof ShifterTitan st) || st.getShifterUUID() == null) return null;
        MinecraftServer server = titan.method_5682();
        if (server == null) return null;
        class_3222 p = server.method_3760().method_14602(st.getShifterUUID()); // getPlayerManager().getPlayer(uuid)
        return (p != null && TitanHooks.shiftedTitan(p) == titan) ? p : null;
    }

    // ------------------------------------------------------------------ tick

    public static void tick(MinecraftServer server) {
        long now = now(server);
        for (class_3222 player : server.method_3760().method_14571()) { // getPlayerList
            class_1297 titan = TitanHooks.shiftedTitan(player);
            Session s = SESSIONS.get(player.method_5667());
            TitanType type = titan == null ? null : TitanType.of(titan);
            if (titan == null && TitanHooks.inFoundingForm(player)) {
                titan = player;
                type = TitanType.FOUNDING;
            }

            if (s != null && (titan == null || s.titan != titan)) {
                endSession(player, s);
                s = null;
            }
            if (s == null && titan != null && type != null) {
                s = startSession(player, titan, type, now);
            }
            if (s != null) tickSession(player, s, now);
        }

        Iterator<Frozen> it = FROZEN.iterator();
        while (it.hasNext()) {
            Frozen f = it.next();
            if (now >= f.until() || f.mob().method_31481()) { // isRemoved
                unfreeze(f.mob());
                it.remove();
            }
        }
    }

    private static Session startSession(class_3222 player, class_1297 titan, TitanType type, long now) {
        UUID id = player.method_5667();
        Session s = new Session(type, titan,
                ABILITY_COOLDOWNS.computeIfAbsent(id, k -> Session.newCooldowns()),
                FORM_COOLDOWNS.computeIfAbsent(id, k -> Session.newCooldowns()));
        s.stage = Mastery.stage(player, type);
        SESSIONS.put(id, s);

        player.method_7353(Mastery.progressText(player, type), true);

        float roll = player.method_59922().method_43057(); // getRandom().nextFloat()
        switch (s.stage) {
            case 0 -> startFury(player, s, 30 * 20, true, now);
            case 1 -> { if (roll < 0.40f) startFury(player, s, 12 * 20, false, now); }
            case 2 -> { if (roll < 0.10f) startFury(player, s, 6 * 20, false, now); }
            default -> {}
        }
        return s;
    }

    private static void endSession(class_3222 player, Session s) {
        player.method_5738("tm_raging");
        player.method_6016(TitanMastery.FURY);        // removeStatusEffect
        player.method_6016(TitanMastery.CRYSTALLIZED);
        if (s.formActive) {
            s.formActive = false;
            s.formReadyAt.put(s.type, now(player.method_5682()) + 90 * 20);
        }
        SESSIONS.remove(player.method_5667());
    }

    public static void forget(class_1657 player) {
        SESSIONS.remove(player.method_5667());
    }

    private static void tickSession(class_3222 player, Session s, long now) {
        s.shiftedTicks++;
        class_3218 world = player.method_51469(); // getServerWorld
        class_1309 titan = (class_1309) s.titan;

        if (s.shiftedTicks % 20 == 0) {
            Mastery.addXp(player, s.type, 1);
            s.stage = Mastery.stage(player, s.type);

            if (s.stage >= 2) titan.method_6025(s.stage - 1); // heal
        }

        // ---- Rage
        if (s.inFury) {
            if (now >= s.furyEnd) {
                endFury(player, s);
            } else {
                if (!player.method_6059(TitanMastery.FURY)) { // milk or /effect clear can't end it early
                    player.method_6092(new class_1293(TitanMastery.FURY, (int) (s.furyEnd - now), 0, false, false, true));
                }
                tickFury(player, s, world, now);
            }
        } else if (s.stage >= 1 && s.stage <= 2) {
            float hp = titan.method_6032() / Math.max(1f, titan.method_6063());
            if (!s.lowHealthFuryUsed && hp < 0.35f) {
                s.lowHealthFuryUsed = true;
                player.method_7353(class_2561.method_43470("Pain floods your mind. The Titan takes over!").method_27692(class_124.field_1061), true);
                startFury(player, s, (s.stage == 1 ? 10 : 6) * 20, false, now);
            } else if (s.stage == 1 && s.shiftedTicks % (45 * 20) == 0 && player.method_59922().method_43057() < 0.10f) {
                player.method_7353(class_2561.method_43470("Your grip slips...").method_27692(class_124.field_1061), true);
                startFury(player, s, 6 * 20, false, now);
            }
        }

        // ---- Mastery ability effects that last a few ticks
        MasteryAbilities.tickActive(player, s, world, now);
    }

    // ------------------------------------------------------------------ fury

    public static void startFury(class_3222 player, Session s, int ticks, boolean feral, long now) {
        s.inFury = true;
        s.feralFury = feral;
        s.furyEnd = now + ticks;
        player.method_5780("tm_raging"); // lets the Founding datapack refuse to revert or use the Coordinate
        player.method_6092(new class_1293(TitanMastery.FURY, ticks, 0, false, false, true));
        player.method_6092(new class_1293(class_1294.field_5916, 60, 0, false, false, false)); // brief nausea as the rage hits
        TitanHooks.roar(s.titan);

        class_3218 world = player.method_51469();
        world.method_43128(null, s.titan.method_23317(), s.titan.method_23318(), s.titan.method_23321(),
                class_3417.field_14733, class_3419.field_15251, 4.0f, 0.6f); // ravager roar
        world.method_14199(class_2398.field_11231, s.titan.method_23317(), s.titan.method_23318() + s.titan.method_17682() * 0.85,
                s.titan.method_23321(), 25, 2.0, 1.5, 2.0, 0.1); // angry villager

        if (feral) {
            player.method_7353(class_2561.method_43470("RAGE. You have no control over the " + s.type.display + "!")
                    .method_27692(class_124.field_1079).method_27692(class_124.field_1067), true);
        } else {
            player.method_7353(class_2561.method_43470("You're losing control!").method_27692(class_124.field_1061), true);
        }
    }

    private static void tickFury(class_3222 player, Session s, class_3218 world, long now) {
        class_1297 target = TitanHooks.nearestTarget(s.titan, player, 48);
        if (target != null && s.shiftedTicks % 14 == 0) {
            double reach = s.titan.method_17681() * 0.5 + 9.0; // getWidth
            double dx = target.method_23317() - s.titan.method_23317();
            double dz = target.method_23321() - s.titan.method_23321();
            if (dx * dx + dz * dz <= reach * reach) {
                if (s.titan == player) TitanHooks.playerTitanStrike(player, target);
                else TitanHooks.attack(s.titan);
            }
        }
        if (s.shiftedTicks % 100 == 0 && player.method_59922().method_43057() < 0.35f) {
            TitanHooks.roar(s.titan);
            world.method_43128(null, s.titan.method_23317(), s.titan.method_23318(), s.titan.method_23321(),
                    class_3417.field_14733, class_3419.field_15251, 3.0f, 0.7f);
        }
        if (s.shiftedTicks % 10 == 0) {
            world.method_14199(class_2398.field_11231, s.titan.method_23317(), s.titan.method_23318() + s.titan.method_17682() * 0.9,
                    s.titan.method_23321(), 2, 1.0, 0.5, 1.0, 0.0);
        }
    }

    private static void endFury(class_3222 player, Session s) {
        s.inFury = false;
        player.method_5738("tm_raging");
        player.method_6016(TitanMastery.FURY);
        if (s.feralFury) {
            s.feralFury = false;
            player.method_7353(class_2561.method_43470("The rage burns out. Your Titan body gives way...").method_27692(class_124.field_1080), true);
            TitanHooks.forceRevert(s.titan, player);
            player.method_6092(new class_1293(class_1294.field_5911, 20 * 20, 1, false, true, true)); // weakness
            player.method_6092(new class_1293(class_1294.field_5909, 20 * 20, 1, false, true, true)); // slowness
        } else {
            player.method_7353(class_2561.method_43470("You regain control.").method_27692(class_124.field_1060), true);
        }
    }

    // ------------------------------------------------------------------ freezing (Founding: Coordinate)

    public static void freeze(class_1308 mob, long until) {
        if (mob.method_5987()) return; // already AI-disabled by someone else: leave it alone
        mob.method_5977(true);
        mob.method_5780("tm_frozen"); // addCommandTag, survives a save so it can be undone after a restart
        FROZEN.add(new Frozen(mob, until));
    }

    public static void unfreeze(class_1308 mob) {
        if (mob.method_5752().contains("tm_frozen")) {
            mob.method_5738("tm_frozen");
            mob.method_5977(false);
        }
    }

    public static void unfreezeAll() {
        for (Frozen f : FROZEN) unfreeze(f.mob());
        FROZEN.clear();
    }

    // ------------------------------------------------------------------ kills

    public static void onDeath(class_1309 victim, class_1282 source) {
        if (victim.method_37908().field_9236) return; // isClient
        class_1297 attacker = source.method_5529();
        if (attacker == null) attacker = source.method_5526();
        if (attacker == null) return;

        class_3222 player = null;
        if (attacker instanceof ShifterTitan) {
            player = riderOf(attacker);
        } else if (attacker instanceof class_3222 p && session(p) != null) {
            player = p;
        }
        if (player == null) return;
        Session s = session(player);
        if (s == null) return;

        int xp = (victim instanceof class_1657 || victim instanceof ShifterTitan) ? 40 : 10;
        Mastery.addXp(player, s.type, xp);
    }
}
