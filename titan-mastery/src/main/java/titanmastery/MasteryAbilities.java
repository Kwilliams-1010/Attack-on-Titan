package titanmastery;

import daot.ShifterTitan;
import net.minecraft.class_124;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_2561;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3419;

import java.util.List;

/**
 * Mastery abilities (J) and forms (U) for each of the nine Titans.
 * Yarn: class_243 Vec3d, class_238 Box, class_2398 ParticleTypes, class_3417 SoundEvents, class_1308 MobEntity.
 */
public final class MasteryAbilities {
    private MasteryAbilities() {}

    public static final int ACTION_ABILITY = 0;
    public static final int ACTION_FORM = 1;
    public static final int ACTION_FOUNDING_SHIFT = 2;
    public static final int ACTION_RUMBLING = 3;

    // ------------------------------------------------------------------ input from the client

    public static void handle(class_3222 player, int action) {
        if (MasteryCombat.isCommanded(player)) {
            player.method_7353(class_2561.method_43470("The Founder's command holds you in place.").method_27692(class_124.field_1065), true);
            return;
        }
        if (action == ACTION_FOUNDING_SHIFT) {
            titanmastery.founding.FounderManager.toggleShift(player);
            return;
        }
        Session s = MasteryManager.session(player);
        if (s == null) return;
        long now = MasteryManager.now(player.method_5682());

        if (s.inFury) {
            deny(player, s, now, "The rage won't listen.");
            return;
        }
        if (s.type == TitanType.FOUNDING && !titanmastery.founding.FounderManager.isFounder(player)) {
            deny(player, s, now, "Only the one true Founder can command the Paths.");
            return;
        }
        if (action == ACTION_RUMBLING) rumbling(player, s, now);
        else if (action == ACTION_ABILITY) useAbility(player, s, now);
        else if (action == ACTION_FORM) toggleForm(player, s, now);
    }

    private static void useAbility(class_3222 player, Session s, long now) {
        if (s.stage < 3) {
            deny(player, s, now, s.type.abilityName + " unlocks at Disciplined mastery (" + Mastery.THRESHOLDS[3] + " XP).");
            return;
        }
        long ready = s.abilityReadyAt.getOrDefault(s.type, 0L);
        if (now < ready) {
            deny(player, s, now, s.type.abilityName + " ready in " + ((ready - now) / 20 + 1) + "s");
            return;
        }
        int cooldown = s.type.abilityCooldownSeconds;
        if (s.formActive && s.type == TitanType.BEAST) cooldown = 6;
        if (s.formActive && s.type == TitanType.CART) cooldown /= 2;
        s.abilityReadyAt.put(s.type, now + cooldown * 20L);

        class_3218 world = player.method_51469();
        class_1297 titan = s.titan;
        switch (s.type) {
            case ATTACK -> warCry(player, s, world, titan, now);
            case COLOSSAL -> steamBurst(player, s, world, titan, now);
            case ARMORED -> batteringRam(player, world, titan);
            case FEMALE -> crystalStrike(player, s, world, titan, now);
            case BEAST -> rockBarrage(player, world, titan);
            case WARHAMMER -> spikeEruption(player, world, titan);
            case JAW -> lungeBite(player, world, titan);
            case CART -> { s.burstShotsLeft = 15; sound(world, titan, class_3417.field_14908, 3f, 1.4f); }
            case FOUNDING -> coordinate(player, world, titan, now);
        }
        player.method_7353(class_2561.method_43470(s.type.abilityName + "!").method_54663(s.type.color).method_27692(class_124.field_1067), true);
    }

    /** P: the Founding Titan's ultimate. Runs the bundled datapack's Rumbling (which keeps its own 5-minute recharge). */
    private static void rumbling(class_3222 player, Session s, long now) {
        if (s.type != TitanType.FOUNDING) return;
        if (s.stage < 4) {
            deny(player, s, now, "The Rumbling unlocks at Mastered (" + Mastery.THRESHOLDS[4] + " XP).");
            return;
        }
        TitanHooks.runFunction(player, "founding:rumbling/try");
    }

    private static void toggleForm(class_3222 player, Session s, long now) {
        if (s.formActive) {
            endForm(player, s, now);
            return;
        }
        if (s.stage < 4) {
            deny(player, s, now, s.type.formName + " unlocks at Mastered (" + Mastery.THRESHOLDS[4] + " XP).");
            return;
        }
        long ready = s.formReadyAt.getOrDefault(s.type, 0L);
        if (now < ready) {
            deny(player, s, now, s.type.formName + " ready in " + ((ready - now) / 20 + 1) + "s");
            return;
        }
        int seconds = s.stage >= 5 ? 60 : 30;
        if (s.type == TitanType.FEMALE) seconds = s.stage >= 5 ? 15 : 10;
        s.formActive = true;
        s.formEnd = now + seconds * 20L;

        class_3218 world = player.method_51469();
        if (s.type == TitanType.FEMALE) {
            player.method_6092(new class_1293(TitanMastery.CRYSTALLIZED, seconds * 20, 0, false, false, true));
        }
        burst(world, s.titan, class_2398.field_11220, 120); // totem
        sound(world, s.titan, class_3417.field_14703, 4f, 0.7f); // beacon activate
        player.method_7353(class_2561.method_43470(s.type.formName + " (" + seconds + "s)").method_54663(s.type.color).method_27692(class_124.field_1067), true);
    }

    private static void endForm(class_3222 player, Session s, long now) {
        s.formActive = false;
        s.formReadyAt.put(s.type, now + 90 * 20L);
        player.method_6016(TitanMastery.CRYSTALLIZED);
        sound(player.method_51469(), s.titan, class_3417.field_15102, 3f, 0.6f);
        player.method_7353(class_2561.method_43470(s.type.formName + " fades.").method_27692(class_124.field_1080), true);
    }

    private static void deny(class_3222 player, Session s, long now, String msg) {
        if (now - s.lastDeniedMessage < 10) return;
        s.lastDeniedMessage = now;
        player.method_7353(class_2561.method_43470(msg).method_27692(class_124.field_1080), true);
    }

    // ------------------------------------------------------------------ per-tick upkeep

    public static void tickActive(class_3222 player, Session s, class_3218 world, long now) {
        class_1297 titan = s.titan;
        class_1309 living = (class_1309) titan;

        if (s.buffEnd > 0 && now >= s.buffEnd) {
            s.buffEnd = 0;
            s.buffMultiplier = 1f;
            s.launchHits = false;
        }

        if (s.burstShotsLeft > 0 && s.shiftedTicks % 4 == 0) {
            s.burstShotsLeft--;
            hitscan(player, world, titan, aim(player, titan, 0.03), 70, 7f, class_2398.field_11251, true);
            sound(world, titan, class_3417.field_14917, 2.5f, 1.6f); // firework blast
        }

        if (!s.formActive) return;
        if (now >= s.formEnd) {
            endForm(player, s, now);
            return;
        }
        if (s.shiftedTicks % 20 != 0) {
            if (s.shiftedTicks % 5 == 0) aura(world, titan, s.type);
            return;
        }

        switch (s.type) {
            case COLOSSAL -> {
                for (class_1309 v : victims(world, titan, player, 12)) {
                    hurt(titan, v, 5f);
                    push(v, away(titan, v).method_1021(1.4).method_1031(0, 0.5, 0));
                }
                living.method_5643(world.method_48963().method_48830(), living.method_6063() * 0.01f); // the body burns away
                burst(world, titan, class_2398.field_11204, 80);
                sound(world, titan, class_3417.field_15102, 2f, 0.5f);
            }
            case FEMALE -> living.method_6025(living.method_6063() * 0.04f);
            case WARHAMMER -> living.method_6025(3f);
            case CART -> living.method_6025(2f);
            case FOUNDING -> {
                living.method_6025(3f);
                for (class_3222 ally : world.method_8390(class_3222.class, titan.method_5829().method_1014(32), p -> true)) {
                    ally.method_6092(new class_1293(class_1294.field_5924, 60, 1, true, true, true)); // regeneration II
                    ally.method_6092(new class_1293(class_1294.field_5907, 60, 0, true, true, true)); // resistance
                    ally.method_6092(new class_1293(class_1294.field_5910, 60, 0, true, true, true)); // strength
                }
            }
            default -> {}
        }
    }

    // ------------------------------------------------------------------ abilities

    private static void warCry(class_3222 player, Session s, class_3218 world, class_1297 titan, long now) {
        TitanHooks.roar(titan);
        sound(world, titan, class_3417.field_14733, 5f, 0.5f);
        sound(world, titan, class_3417.field_38075, 4f, 0.8f); // warden roar
        for (class_1309 v : victims(world, titan, player, 20)) {
            v.method_6092(new class_1293(class_1294.field_5909, 100, 2));
            v.method_6092(new class_1293(class_1294.field_5911, 100, 1));
            push(v, away(titan, v).method_1021(1.2).method_1031(0, 0.4, 0));
        }
        s.buffMultiplier = 1.4f;
        s.buffEnd = now + 200;
        burst(world, titan, class_2398.field_38908, 6); // sonic boom
    }

    private static void steamBurst(class_3222 player, Session s, class_3218 world, class_1297 titan, long now) {
        for (class_1309 v : victims(world, titan, player, 18)) {
            hurt(titan, v, 14f);
            v.method_5639(5f); // setOnFireFor
            push(v, away(titan, v).method_1021(2.5).method_1031(0, 0.8, 0));
        }
        s.immuneEnd = now + 60;
        burst(world, titan, class_2398.field_11204, 500);
        burst(world, titan, class_2398.field_17430, 60);
        sound(world, titan, class_3417.field_15102, 5f, 0.4f);
        sound(world, titan, class_3417.field_15222, 5f, 0.5f);
    }

    private static void batteringRam(class_3222 player, class_3218 world, class_1297 titan) {
        class_243 dir = flatLook(player);
        double half = titan.method_17681() * 0.5 + 3.0;
        class_243 origin = titan.method_19538();
        for (class_1309 v : victims(world, titan, player, 26)) {
            class_243 rel = v.method_19538().method_1020(origin);
            double along = rel.field_1352 * dir.field_1352 + rel.field_1350 * dir.field_1350;
            double side = Math.abs(rel.field_1352 * dir.field_1350 - rel.field_1350 * dir.field_1352);
            if (along < -2 || along > 26 || side > half) continue;
            hurt(titan, v, 22f);
            push(v, dir.method_1021(2.2).method_1031(0, 0.5, 0));
        }
        for (int i = 2; i <= 26; i += 3) {
            class_243 p = origin.method_1019(dir.method_1021(i));
            world.method_14199(class_2398.field_11236, p.field_1352, p.field_1351 + 1, p.field_1350, 1, 1, 0.5, 1, 0);
        }
        sound(world, titan, class_3417.field_14649, 5f, 0.5f); // iron golem attack
        sound(world, titan, class_3417.field_15152.comp_349(), 3f, 0.7f); // explode
    }

    private static void crystalStrike(class_3222 player, Session s, class_3218 world, class_1297 titan, long now) {
        s.buffMultiplier = 1.8f;
        s.launchHits = true;
        s.buffEnd = now + 160;
        burst(world, titan, class_2398.field_11207, 120); // end rod
        sound(world, titan, class_3417.field_26980, 4f, 0.6f); // amethyst chime
    }

    private static void rockBarrage(class_3222 player, class_3218 world, class_1297 titan) {
        for (int i = 0; i < 10; i++) {
            hitscan(player, world, titan, aim(player, titan, 0.09), 80, 16f, class_2398.field_11237, false);
        }
        sound(world, titan, class_3417.field_14917, 4f, 0.5f);
        sound(world, titan, class_3417.field_15152.comp_349(), 3f, 0.9f);
    }

    private static void spikeEruption(class_3222 player, class_3218 world, class_1297 titan) {
        for (class_1309 v : victims(world, titan, player, 16)) {
            hurt(titan, v, 18f);
            push(v, new class_243(0, 1.6, 0));
            world.method_14199(class_2398.field_11207, v.method_23317(), v.method_23318(), v.method_23321(), 40, 0.3, 2.5, 0.3, 0.02);
        }
        burst(world, titan, class_2398.field_11207, 150);
        sound(world, titan, class_3417.field_28033, 5f, 0.5f); // dripstone land
        sound(world, titan, class_3417.field_38830, 2f, 1.2f); // sonic boom sound
    }

    private static void lungeBite(class_3222 player, class_3218 world, class_1297 titan) {
        class_1309 best = null;
        double bestD = Double.MAX_VALUE;
        for (class_1309 v : victims(world, titan, player, 28)) {
            double d = v.method_5858(titan);
            if (d < bestD) { bestD = d; best = v; }
        }
        if (best == null) {
            player.method_7353(class_2561.method_43470("Nothing to bite.").method_27692(class_124.field_1080), true);
            return;
        }
        hurt(titan, best, 32f);
        push(best, titan.method_19538().method_1020(best.method_19538()).method_1029().method_1021(0.8).method_1031(0, 0.3, 0));
        world.method_14199(class_2398.field_11205, best.method_23317(), best.method_23318() + 1, best.method_23321(), 40, 1, 1, 1, 0.3);
        world.method_14199(class_2398.field_11227, best.method_23317(), best.method_23318() + 1, best.method_23321(), 3, 0.5, 0.5, 0.5, 0);
        sound(world, titan, class_3417.field_14840, 4f, 0.5f);
        sound(world, titan, class_3417.field_14733, 3f, 1.3f);
    }

    /**
     * J for the Founder. Every Subject of Ymir obeys: within 96 blocks all titans stop where they stand.
     * Pure titans and other mobs freeze; shifters inside their Titans are Commanded (can't move, punch,
     * use abilities or revert). 10 seconds, 15 at Perfected.
     */
    private static void coordinate(class_3222 player, class_3218 world, class_1297 titan, long now) {
        Session s = MasteryManager.session(player);
        int ticks = (s != null && s.stage >= 5) ? 300 : 200;
        int stopped = 0;
        for (class_1297 e : world.method_8335(titan, titan.method_5829().method_1014(96))) { // getOtherEntities
            class_3222 rider = MasteryManager.riderOf(e);
            if (rider != null && rider != player) {
                rider.method_6092(new class_1293(TitanMastery.COMMANDED, ticks, 0, false, false, true));
                rider.method_7353(class_2561.method_43470("The Founder commands you. Your Titan won't move.")
                        .method_27692(class_124.field_1065).method_27692(class_124.field_1067), true);
                if (e instanceof class_1309 l) l.method_6092(new class_1293(class_1294.field_5912, ticks, 0, false, false));
                world.method_14199(class_2398.field_11207, e.method_23317(), e.method_23318() + e.method_17682() * 0.6, e.method_23321(),
                        60, e.method_17681() * 0.5, e.method_17682() * 0.4, e.method_17681() * 0.5, 0.05);
                stopped++;
            } else if (e instanceof class_1308 mob && e.getClass().getName().startsWith("daot.") && !TitanHooks.isTitanPart(e)
                    && !(e instanceof ShifterTitan) && mob.method_5805()) {
                MasteryManager.freeze(mob, now + ticks); // pure titans, from further away than ordinary mobs
                mob.method_6092(new class_1293(class_1294.field_5912, ticks, 0, false, false));
                stopped++;
            }
        }
        if (stopped > 0) {
            player.method_7353(class_2561.method_43470("Coordinate: " + stopped + " titan(s) stopped.").method_27692(class_124.field_1065), false);
        }
        class_238 box = titan.method_5829().method_1014(64);
        for (class_1308 mob : world.method_8390(class_1308.class, box,
                m -> m != titan && !(m instanceof ShifterTitan) && !TitanHooks.isTitanPart(m) && m.method_5805())) {
            MasteryManager.freeze(mob, now + 240);
            mob.method_6092(new class_1293(class_1294.field_5912, 240, 0, false, false));
        }
        for (class_3222 other : world.method_8390(class_3222.class, box, p -> p != player)) {
            other.method_6092(new class_1293(class_1294.field_5909, 80, 3));
            other.method_6092(new class_1293(class_1294.field_5916, 120, 0));
        }
        burst(world, titan, class_2398.field_11207, 250);
        sound(world, titan, class_3417.field_15203, 6f, 0.6f); // elder guardian curse
        sound(world, titan, class_3417.field_14703, 6f, 0.5f);
    }

    // ------------------------------------------------------------------ helpers

    private static void aura(class_3218 world, class_1297 titan, TitanType type) {
        class_2394 p = switch (type) {
            case ATTACK, WARHAMMER, FEMALE -> class_2398.field_11207;
            case COLOSSAL -> class_2398.field_11204;
            case ARMORED -> class_2398.field_23956;
            case BEAST -> class_2398.field_11203;
            case JAW -> class_2398.field_11205;
            case CART -> class_2398.field_11251;
            case FOUNDING -> class_2398.field_28479;
        };
        world.method_14199(p, titan.method_23317(), titan.method_23318() + titan.method_17682() * 0.5, titan.method_23321(),
                8, titan.method_17681() * 0.4, titan.method_17682() * 0.4, titan.method_17681() * 0.4, 0.01);
    }

    static List<class_1309> victims(class_3218 world, class_1297 titan, class_1657 rider, double radius) {
        return world.method_8390(class_1309.class, titan.method_5829().method_1014(radius),
                e -> TitanHooks.isValidVictim(e, titan, rider) && e.method_5858(titan) <= (radius + titan.method_17681()) * (radius + titan.method_17681()));
    }

    static void hurt(class_1297 titan, class_1309 victim, float amount) {
        victim.method_5643(titan.method_48923().method_48812((class_1309) titan), amount); // damage(mobAttack(titan))
    }

    static void push(class_1309 v, class_243 add) {
        v.method_18799(v.method_18798().method_1019(add)); // setVelocity(getVelocity().add)
        v.field_6037 = true; // velocityModified, so players feel it
    }

    static class_243 away(class_1297 from, class_1297 to) {
        class_243 d = to.method_19538().method_1020(from.method_19538());
        d = new class_243(d.field_1352, 0, d.field_1350);
        return d.method_1027() < 1.0E-4 ? new class_243(0, 0, 1) : d.method_1029();
    }

    static class_243 flatLook(class_1657 player) {
        float yaw = (float) Math.toRadians(player.method_36454());
        return new class_243(-Math.sin(yaw), 0, Math.cos(yaw));
    }

    static class_243 aim(class_1657 player, class_1297 titan, double spread) {
        class_243 look = player.method_5720(); // getRotationVector
        var r = player.method_59922();
        return look.method_1031((r.method_43058() - 0.5) * 2 * spread, (r.method_43058() - 0.5) * 2 * spread,
                (r.method_43058() - 0.5) * 2 * spread).method_1029();
    }

    /** A hit-scan shot from the titan's head along dir. Hits the first valid victim. */
    static void hitscan(class_1657 player, class_3218 world, class_1297 titan, class_243 dir, int range, float damage,
                        class_2394 trail, boolean small) {
        class_243 pos = new class_243(titan.method_23317(), titan.method_23318() + titan.method_17682() * 0.85, titan.method_23321())
                .method_1019(dir.method_1021(titan.method_17681() * 0.6));
        List<class_1309> candidates = victims(world, titan, player, range);
        for (int i = 0; i < range; i++) {
            pos = pos.method_1019(dir);
            if (i % 2 == 0) world.method_14199(trail, pos.field_1352, pos.field_1351, pos.field_1350, 1, 0, 0, 0, 0);
            for (class_1309 v : candidates) {
                if (v.method_5829().method_1014(small ? 0.4 : 1.0).method_1006(pos)) { // contains
                    hurt(titan, v, damage);
                    push(v, dir.method_1021(small ? 0.2 : 1.0));
                    world.method_14199(small ? class_2398.field_11205 : class_2398.field_11236,
                            pos.field_1352, pos.field_1351, pos.field_1350, small ? 6 : 1, 0.2, 0.2, 0.2, 0.1);
                    return;
                }
            }
            if (!world.method_8320(net.minecraft.class_2338.method_49638(pos)).method_26215()) { // hit a solid block
                world.method_14199(class_2398.field_11203, pos.field_1352, pos.field_1351, pos.field_1350, 4, 0.3, 0.3, 0.3, 0.02);
                return;
            }
        }
    }

    static void burst(class_3218 world, class_1297 titan, class_2394 particle, int count) {
        world.method_14199(particle, titan.method_23317(), titan.method_23318() + titan.method_17682() * 0.5, titan.method_23321(),
                count, titan.method_17681() * 0.7, titan.method_17682() * 0.5, titan.method_17681() * 0.7, 0.15);
    }

    static void sound(class_3218 world, class_1297 at, class_3414 sound, float volume, float pitch) {
        world.method_43128(null, at.method_23317(), at.method_23318(), at.method_23321(), sound, class_3419.field_15248, volume, pitch);
    }
}
