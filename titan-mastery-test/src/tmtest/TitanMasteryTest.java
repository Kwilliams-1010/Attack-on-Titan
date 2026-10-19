package tmtest;

import com.mojang.authlib.GameProfile;
import daot.BloodlineData;
import daot.BloodlineType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_2338;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;
import titanmastery.Mastery;
import titanmastery.MasteryAbilities;
import titanmastery.MasteryManager;
import titanmastery.Session;
import titanmastery.TitanHooks;
import titanmastery.TitanMastery;
import titanmastery.TitanType;
import titanmastery.founding.FounderManager;
import titanmastery.founding.FoundingTitanEntity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/** Scripted server-side test of Titan Mastery's Founding Titan on a real dedicated server with Danny's AoT. */
public class TitanMasteryTest implements ModInitializer {
    static int pass = 0, fail = 0;
    static final List<Step> steps = new ArrayList<>();
    static int index = 0, wait = 0;
    static MinecraftServer server;
    static class_3218 world;
    static class_3222 founder, rival, feral;
    static class_2338 spawn;
    static daot.AttackTitanEntity rivalTitan;
    static class_1308 pure;
    static class_1308 pigRef;
    static double pureX, pureZ;
    static int pureAge;
    static class_3222 ymirFounder, heirRef, marley;
    static boolean pig() { return pigRef != null && pigRef.method_5987(); }

    record Step(int delay, String name, Consumer<MinecraftServer> body) {}

    static void check(String name, boolean ok, String detail) {
        if (ok) pass++; else fail++;
        System.out.println("[TM-TEST] " + (ok ? "PASS " : "FAIL ") + name + (detail.isEmpty() ? "" : "  (" + detail + ")"));
    }

    static void step(int delay, String name, Consumer<MinecraftServer> body) { steps.add(new Step(delay, name, body)); }

    static void cmd(String c) {
        server.method_3734().method_44252(server.method_3739().method_9217().method_9206(4), c);
    }

    /** A simulated player that joins like a real one (in the player list, ticked, selectable with @a). */
    static class_3222 fake(String name) {
        GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(("tm:" + name).getBytes()), name);
        class_3222 p = new class_3222(server, world, profile, net.minecraft.class_8791.method_53821());
        net.minecraft.class_2535 conn = new net.minecraft.class_2535(net.minecraft.class_2598.field_11941);
        new io.netty.channel.embedded.EmbeddedChannel(conn);
        server.method_3760().method_14570(conn, p, net.minecraft.class_8792.method_53824(profile, false));
        p.method_5808(spawn.method_10263() + 0.5, spawn.method_10264() + 1, spawn.method_10260() + 0.5, 0f, 0f);
        return p;
    }

    static class_1309 summon(class_1299<? extends class_1309> type, double dx, double dz) {
        class_1309 e = type.method_5883(world);
        e.method_5808(spawn.method_10263() + dx, spawn.method_10264() + 1, spawn.method_10260() + dz, 0f, 0f);
        if (e instanceof class_1308 m) m.method_5977(true); // hold still
        world.method_8649(e);
        return e;
    }

    static boolean hasHandler(Class<?> c) {
        for (Method m : c.getDeclaredMethods()) if (m.getName().contains("titanmastery")) return true;
        return false;
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(s -> {
            server = s;
            world = s.method_30002();
            spawn = world.method_43126();
            buildSteps();
            System.out.println("[TM-TEST] starting " + steps.size() + " steps at spawn " + spawn);
        });
        ServerTickEvents.END_SERVER_TICK.register(s -> {
            if (server == null || index >= steps.size()) return;
            if (wait > 0) { wait--; return; }
            Step st = steps.get(index++);
            try {
                st.body().accept(s);
            } catch (Throwable t) {
                check(st.name(), false, "threw " + t);
                t.printStackTrace(System.out);
            }
            if (index < steps.size()) wait = steps.get(index).delay();
            else {
                System.out.println("[TM-TEST] DONE passed=" + pass + " failed=" + fail);
                s.method_3747(false);
            }
        });
    }

    static void buildSteps() {
        step(0, "mixins applied to Danny's titans", s -> {
            boolean ok = hasHandler(daot.AttackTitanEntity.class) && hasHandler(daot.ColossalTitanEntity.class)
                    && hasHandler(daot.ArmoredTitanEntity.class) && hasHandler(daot.FemaleTitanEntity.class)
                    && hasHandler(daot.BeastTitanEntity.class) && hasHandler(daot.WarhammerTitanEntity.class)
                    && hasHandler(daot.CartShifterTitanEntity.class) && hasHandler(net.minecraft.class_1309.class)
                    && hasHandler(net.minecraft.class_1657.class);
            check("mixins applied to Danny's titans and players", ok, "");
        });
        step(0, "datapack loaded", s -> check("Founding datapack loaded (chance=25, unclaimed)",
                FounderManager.cfg(s, "#chance") == 25 && FounderManager.cfg(s, "#claimed") == 0,
                "chance=" + FounderManager.cfg(s, "#chance") + " claimed=" + FounderManager.cfg(s, "#claimed")));

        step(0, "royal birth", s -> {
            cmd("mastery founding chance 100");
            cmd("mastery founding model original"); // core tests use the Titan Mastery body
            founder = fake("Tester");
            BloodlineData.get(s).setBloodline(founder.method_5667(), BloodlineType.ROYAL);
            FounderManager.rollBloodline(founder);
        });
        step(2, "royal birth result", s -> check("royal-blooded player is born the Founder",
                FounderManager.isFounder(founder) && FounderManager.cfg(s, "#claimed") == 1, "claimed=" + FounderManager.cfg(s, "#claimed")));

        step(0, "eldian no roll", s -> {
            class_3222 eldian = fake("Eldian");
            BloodlineData.get(s).setBloodline(eldian.method_5667(), BloodlineType.ELDIAN);
            FounderManager.rollBloodline(eldian);
            check("a non-royal player is never born the Founder", !FounderManager.isFounder(eldian), "");
            rival = fake("Rival");
            BloodlineData.get(s).setBloodline(rival.method_5667(), BloodlineType.ROYAL);
            FounderManager.rollBloodline(rival);
        });
        step(2, "only one founder", s -> check("a second royal can't be born the Founder while one exists",
                !FounderManager.isFounder(rival), ""));

        step(0, "shift", s -> {
            Mastery.setXp(founder, TitanType.FOUNDING, 6000); // Perfected: no rage, all powers
            FounderManager.toggleShift(founder);
            FoundingTitanEntity t = FounderManager.titanOf(founder);
            check("O key transforms the Founder into the Founding Titan", t != null && !t.method_31481()
                    && t.method_6032() == 500f && founder.method_5854() == t, t == null ? "no titan" : "hp=" + t.method_6032());
        });
        step(2, "session", s -> {
            Session ses = MasteryManager.session(founder);
            check("mastery tracks the Founding Titan (Perfected, no rage)",
                    ses != null && ses.type == TitanType.FOUNDING && ses.stage == 5 && !ses.inFury,
                    ses == null ? "no session" : ses.type + " stage " + ses.stage + " fury " + ses.inFury);
            check("Founder is invisible inside the Titan", founder.method_6059(net.minecraft.class_1294.field_5905), "");
        });

        step(70, "nape", s -> { // new players are invulnerable for their first 3 seconds
            FoundingTitanEntity t = FounderManager.titanOf(founder);
            class_1309 z = summon(class_1299.field_6051, 6, 0);
            float before = t.method_6032(), pBefore = founder.method_6032();
            founder.method_5643(world.method_48963().method_48812(z), 10f);
            check("hits on the Founder land on the Titan's nape x3, Founder unhurt",
                    founder.method_6032() == pBefore && before - t.method_6032() >= 15f,
                    "titan " + before + "->" + t.method_6032() + " founder " + pBefore + "->" + founder.method_6032());
            t.method_6033(500f);
            z.method_31472();
        });

        step(0, "strike", s -> {
            FoundingTitanEntity t = FounderManager.titanOf(founder);
            class_1309 z = summon(class_1299.field_6051, 7, 0);
            t.strike(z);
            check("Founding Titan punch hits hard", !z.method_5805() || z.method_6032() < 5f, "zombie hp=" + z.method_6032());
            z.method_31472();
        });

        step(15, "coordinate", s -> {
            class_1309 pig = summon(class_1299.field_6093, 20, 5);
            ((class_1308) pig).method_5977(false);
            pigRef = (class_1308) pig;
            // A rival shifter inside Danny's Attack Titan, 40 blocks away
            daot.AttackTitanEntity a = (daot.AttackTitanEntity) net.minecraft.class_7923.field_41177
                    .method_10223(net.minecraft.class_2960.method_60655("dannys-aot", "attack_titan")).method_5883(world);
            a.method_5808(spawn.method_10263() + 40, spawn.method_10264() + 1, spawn.method_10260(), 0f, 0f);
            world.method_8649(a);
            a.setShifterUUID(rival.method_5667());
            rival.method_5873(a, true);
            rivalTitan = a;
            // A pure titan 70 blocks away
            pure = (class_1308) net.minecraft.class_7923.field_41177
                    .method_10223(net.minecraft.class_2960.method_60655("dannys-aot", "titan")).method_5883(world);
            pure.method_5808(spawn.method_10263(), spawn.method_10264() + 1, spawn.method_10260() + 52, 0f, 0f);
            world.method_8649(pure);
        });
        step(2, "coordinate fire", s -> {
            check("rival shifter is inside Danny's Attack Titan", TitanHooks.shiftedTitan(rival) == rivalTitan, "");
            MasteryAbilities.handle(founder, MasteryAbilities.ACTION_ABILITY);
        });
        step(1, "coordinate result", s -> {
            check("J: Coordinate freezes nearby creatures", pig() , "");
            check("J: Coordinate stops a shifter inside Danny's Titan (Commanded)", rival.method_6059(TitanMastery.COMMANDED), "");
            check("J: Coordinate freezes pure titans 50+ blocks away", pure.method_5752().contains("tm_frozen"),
                    "class=" + pure.getClass().getName() + " removed=" + pure.method_31481() + " dist=" + Math.sqrt(pure.method_5858(FounderManager.titanOf(founder)))
                    + " tags=" + pure.method_5752() + " found=" + world.method_8335(FounderManager.titanOf(founder), FounderManager.titanOf(founder).method_5829().method_1014(96)).contains(pure));
            check("a Commanded shifter's punches are blocked", titanmastery.MasteryCombat.blockManualAttack(rivalTitan), "");
            rivalTitan.startDismounting(rival);
            check("a Commanded shifter can't revert", rival.method_5854() == rivalTitan, "");
            check("the Founder is not affected by their own command", !founder.method_6059(TitanMastery.COMMANDED), "");
            rival.method_5848();
            rivalTitan.method_31472();
        });
        step(0, "pure stays put", s -> { pureX = pure.method_23317(); pureZ = pure.method_23321(); pureAge = pure.field_6012; });
        step(60, "pure still", s -> check("a stopped pure titan doesn't move or act for 3 seconds",
                pure.field_6012 == pureAge && pure.method_23317() == pureX && pure.method_23321() == pureZ, "age " + pureAge + "->" + pure.field_6012));
        step(0, "paths", s -> {
            MasteryAbilities.handle(founder, MasteryAbilities.ACTION_FORM);
            Session ses = MasteryManager.session(founder);
            check("U: Paths of Ymir form activates", ses != null && ses.formActive, "");
        });
        step(25, "paths buffs", s -> check("Paths of Ymir buffs players nearby",
                founder.method_6059(net.minecraft.class_1294.field_5924), ""));
        step(0, "rumbling", s -> MasteryAbilities.handle(founder, MasteryAbilities.ACTION_RUMBLING));
        step(2, "rumbling result", s -> {
            int walls = world.method_8390(class_1309.class, founder.method_5829().method_1014(80),
                    e -> e.method_5752().contains("founding.wall")).size();
            check("P: The Rumbling raises the Wall Titans", walls == 7, "walls=" + walls);
            cmd("kill @e[type=minecraft:giant,tag=founding.wall]");
        });

        step(0, "revert", s -> {
            FoundingTitanEntity t = FounderManager.titanOf(founder);
            MasteryAbilities.handle(founder, MasteryAbilities.ACTION_FOUNDING_SHIFT);
            check("O key again reverts to human form", FounderManager.titanOf(founder) == null && founder.method_5854() == null
                    && !founder.method_6059(net.minecraft.class_1294.field_5905), "");
        });
        step(5, "titan gone", s -> check("the empty Titan body steams away",
                world.method_8390(FoundingTitanEntity.class, founder.method_5829().method_1014(40), e -> true).isEmpty(), ""));

        step(0, "not founder", s -> {
            FounderManager.toggleShift(rival);
            check("a non-Founder can't transform", FounderManager.titanOf(rival) == null, "");
        });

        step(260, "release", s -> {
            check("the pure titan wakes up again when the command wears off", !pure.method_5752().contains("tm_frozen") && !pure.method_5987(),
                    "tags=" + pure.method_5752() + " age " + pureAge + "->" + pure.field_6012);
            pure.method_31472();
        });

        // Rage: a new, Feral Founder
        step(0, "feral setup", s -> {
            feral = fake("Feral");
            TitanHooks.runFunction(feral, "founding:power/inherit");
            Mastery.setXp(feral, TitanType.FOUNDING, 0);
        });
        step(1, "feral shift", s -> {
            check("/mastery founding-style inheritance works", FounderManager.isFounder(feral), "");
            FounderManager.toggleShift(feral);
        });
        step(2, "feral rage", s -> {
            Session ses = MasteryManager.session(feral);
            check("a Feral Founder's Titan rages on shifting", ses != null && ses.inFury && feral.method_6059(TitanMastery.FURY), "");
            MasteryAbilities.handle(feral, MasteryAbilities.ACTION_FOUNDING_SHIFT);
            check("can't revert while raging", FounderManager.titanOf(feral) != null, "");
            ses.furyEnd = MasteryManager.now(s); // burn the rage out now
        });
        step(3, "feral burnout", s -> check("when the rage burns out the Titan collapses and ejects its Founder",
                FounderManager.titanOf(feral) == null && feral.method_6059(net.minecraft.class_1294.field_5911), ""));

        if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("springdaot")) {
            step(0, "ymir setup", s -> {
                cmd("mastery founding model springdaot");
                ymirFounder = fake("YmirFounder");
                BloodlineData.get(s).setBloodline(ymirFounder.method_5667(), BloodlineType.ROYAL); // Danny won't give titans to Marleyans
                TitanHooks.runFunction(ymirFounder, "founding:power/inherit");
                Mastery.setXp(ymirFounder, TitanType.FOUNDING, 6000);
            });
            step(70, "ymir grant", s -> check("the Founder is given SpringDAOT's Ymir power through Danny's system",
                    ymirFounder.method_5752().contains("ymirfounding"), String.valueOf(ymirFounder.method_5752())));
            step(0, "ymir shift", s -> FounderManager.toggleShift(ymirFounder));
            step(100, "ymir body", s -> {
                class_1297 body = FounderManager.founderBody(ymirFounder);
                check("O runs Danny's real shift into SpringDAOT's Ymir (lifted to the nape, invisible)",
                        body != null && body.getClass().getSimpleName().contains("Ymir") && ymirFounder.method_5767()
                                && ymirFounder.method_23318() > spawn.method_10264() + 15,
                        (body == null ? "none" : body.getClass().getSimpleName()) + " y=" + (int) ymirFounder.method_23318());
                Session ses = MasteryManager.session(ymirFounder);
                check("mastery treats SpringDAOT's Ymir as the Founding Titan", ses != null && ses.type == TitanType.FOUNDING,
                        ses == null ? "no session" : ses.type.toString());
                MasteryAbilities.handle(ymirFounder, MasteryAbilities.ACTION_ABILITY);
                check("J Coordinate works from the Ymir Titan",
                        ses != null && ses.abilityReadyAt.getOrDefault(TitanType.FOUNDING, 0L) > MasteryManager.now(s), "");
            });
            step(160, "ymir revert press", s -> {
                class_1297 body = FounderManager.founderBody(ymirFounder);
                MasteryAbilities.handle(ymirFounder, MasteryAbilities.ACTION_FOUNDING_SHIFT);
                boolean dismounting = body instanceof daot.ShifterTitan st && st.isDismounting();
                check("O starts Danny's dismount for the Ymir Titan (same as his B key)",
                        dismounting || FounderManager.founderBody(ymirFounder) == null,
                        body == null ? "no body" : "dismounting=" + dismounting
                                + " cooldown=" + ((daot.AttackTitanEntity) body).isDismountToggleOnCooldown()
                                + " knocked=" + ((daot.AttackTitanEntity) body).isKnocked()
                                + " incap=" + ((daot.AttackTitanEntity) body).isIncapacitated());
            });
            // Danny finishes the dismount together with the player's client; a headless server can only check it starts.
            step(0, "ymir handoff", s -> {
                class_3222 heir = fake("Heir");
                BloodlineData.get(s).setBloodline(heir.method_5667(), BloodlineType.ELDIAN);
                TitanHooks.runFunction(heir, "founding:power/inherit"); // power moves to someone else
                heirRef = heir;
            });
            step(70, "ymir handoff result", s -> check("when the Founding Titan passes on, the Ymir power moves with it",
                    !ymirFounder.method_5752().contains("ymirfounding") && heirRef.method_5752().contains("ymirfounding"),
                    "old=" + ymirFounder.method_5752() + " new=" + heirRef.method_5752()));
            step(0, "marleyan founder", s -> {
                marley = fake("Marley");
                BloodlineData.get(s).setBloodline(marley.method_5667(), BloodlineType.MARLEYAN);
                TitanHooks.runFunction(marley, "founding:power/inherit");
            });
            step(70, "marleyan shift", s -> FounderManager.toggleShift(marley));
            step(5, "marleyan body", s -> check("a Marleyan Founder (Danny forbids them Titan powers) still transforms, into the Titan Mastery body",
                    FounderManager.titanOf(marley) != null, String.valueOf(marley.method_5854())));
        }

        // Danny's AoT still behaves with our hooks in place
        step(0, "danny titan", s -> {
            daot.AttackTitanEntity a = (daot.AttackTitanEntity) net.minecraft.class_7923.field_41177
                    .method_10223(net.minecraft.class_2960.method_60655("dannys-aot", "attack_titan")).method_5883(world);
            a.method_5808(spawn.method_10263() + 30, spawn.method_10264() + 1, spawn.method_10260(), 0f, 0f);
            world.method_8649(a);
            float before = a.method_6032();
            a.method_5643(world.method_48963().method_48830(), 20f);
            TitanHooks.attack(a);
            check("Danny's Attack Titan takes damage normally through our hooks", a.method_6032() < before,
                    before + " -> " + a.method_6032());
            a.method_31472();
        });
    }
}
