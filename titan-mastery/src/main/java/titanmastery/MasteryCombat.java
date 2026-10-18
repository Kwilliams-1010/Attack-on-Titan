package titanmastery;

import daot.ShifterTitan;
import net.minecraft.class_124;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_3222;

/** Damage scaling from mastery, and the gates that hold back a titan you can't control yet. */
public final class MasteryCombat {
    private MasteryCombat() {}

    /** Applied once per hit (see the two damage mixins). Server side only. */
    public static float modify(class_1309 victim, class_1282 source, float amount) {
        if (victim.method_37908().field_9236 || amount <= 0) return amount;

        // Attacker bonus
        class_1297 attacker = source.method_5529();
        if (attacker == null) attacker = source.method_5526();
        Session atk = sessionOfAttacker(attacker);
        if (atk != null) {
            float mult = 1f + 0.08f * atk.stage;
            mult *= atk.buffMultiplier;
            if (atk.formActive) {
                mult *= switch (atk.type) {
                    case ATTACK -> 1.3f;
                    case ARMORED -> 1.1f;
                    case BEAST -> 1.2f;
                    case JAW -> 1.5f;
                    default -> 1f;
                };
            }
            amount *= mult;
            if (atk.launchHits && !(victim instanceof ShifterTitan)) {
                victim.method_18799(victim.method_18798().method_1019(new class_243(0, 1.1, 0)));
                victim.field_6037 = true;
            }
        }

        // Defender reduction
        class_3222 rider = MasteryManager.riderOf(victim);
        if (rider != null) {
            Session def = MasteryManager.session(rider);
            if (def != null) {
                long now = MasteryManager.now(rider.method_5682());
                if (now < def.immuneEnd) return 0f;
                float reduction = 0.05f * def.stage;
                if (def.formActive) {
                    reduction += switch (def.type) {
                        case ATTACK -> 0.5f;
                        case COLOSSAL -> 0.4f;
                        case ARMORED -> 0.7f;
                        case FEMALE -> 1.0f;
                        case WARHAMMER -> 0.6f;
                        case CART -> 0.35f;
                        case JAW -> -0.15f;
                        default -> 0f;
                    };
                }
                amount *= Math.max(0f, 1f - Math.min(1f, reduction));
            }
        }
        return amount;
    }

    private static Session sessionOfAttacker(class_1297 attacker) {
        if (attacker == null) return null;
        if (attacker instanceof ShifterTitan || attacker instanceof titanmastery.founding.FoundingTitanEntity)
            return MasteryManager.session(MasteryManager.riderOf(attacker));
        if (attacker instanceof class_3222 p) return MasteryManager.session(p);
        return null;
    }

    // ------------------------------------------------------------------ gates for Danny's own controls

    /** True when the shifter tries to leave the Titan while it is raging. */
    public static boolean isCommanded(class_1657 player) {
        return player != null && player.method_6059(TitanMastery.COMMANDED);
    }

    public static boolean blockRevert(class_1297 titan, class_1657 player) {
        if (TitanHooks.internal || player == null || player.method_37908().field_9236) return false;
        // Never trap anyone: a beaten, incapacitated or dying Titan always lets its shifter out.
        if (titan instanceof ShifterTitan st && st.isIncapacitated()) return false;
        if (titan instanceof class_1309 l && l.method_6032() <= l.method_6063() * 0.05f) return false;
        if (isCommanded(player)) {
            player.method_7353(class_2561.method_43470("The Founder's command holds you in place.").method_27692(class_124.field_1065), true);
            return true;
        }
        Session s = MasteryManager.session(player);
        if (s != null && s.inFury) {
            player.method_7353(class_2561.method_43470("The Titan won't let you go!").method_27692(class_124.field_1061), true);
            return true;
        }
        return false;
    }

    /** True when a manual punch should be ignored (raging, or sealed in the crystal cocoon). */
    public static boolean blockManualAttack(class_1297 titan) {
        if (TitanHooks.internal || titan.method_37908().field_9236) return false;
        class_3222 rider = MasteryManager.riderOf(titan);
        if (isCommanded(rider)) return true;
        Session s = MasteryManager.session(rider);
        if (s == null) return false;
        return s.inFury || (s.formActive && s.type == TitanType.FEMALE);
    }

    /** True when one of Danny's titan abilities should be ignored because control is too weak. */
    public static boolean blockManualAbility(class_1297 titan) {
        if (TitanHooks.internal || titan.method_37908().field_9236) return false;
        class_3222 rider = MasteryManager.riderOf(titan);
        if (isCommanded(rider)) return true;
        Session s = MasteryManager.session(rider);
        if (s == null) return false;
        if (s.inFury) return true;
        if (s.stage < 2) {
            long now = MasteryManager.now(rider.method_5682());
            if (now - s.lastDeniedMessage > 20) {
                s.lastDeniedMessage = now;
                rider.method_7353(class_2561.method_43470("Your control is too weak for that. Abilities unlock at Awakened ("
                        + Mastery.THRESHOLDS[2] + " XP).").method_27692(class_124.field_1080), true);
            }
            return true;
        }
        return false;
    }
}
