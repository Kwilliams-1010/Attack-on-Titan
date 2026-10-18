package titanmastery;

import daot.ArmoredTitanEntity;
import daot.AttackTitanEntity;
import daot.BeastTitanEntity;
import daot.ColossalTitanEntity;
import daot.FemaleTitanEntity;
import daot.ShifterTitan;
import daot.WarhammerTitanEntity;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_1268;
import net.minecraft.class_3222;

import java.util.UUID;

/**
 * Thin bridge to Danny's AOT titan entities.
 * Yarn names: class_1297 Entity, class_1309 LivingEntity, class_1657 PlayerEntity, class_1531 ArmorStandEntity.
 */
public final class TitanHooks {
    private TitanHooks() {}

    /** Set while this mod drives a titan, so the "manual input" blocks let our own calls through. */
    public static boolean internal = false;

    /** The titan this player is currently shifted into, or null. */
    public static class_1297 shiftedTitan(class_1657 player) {
        class_1297 v = player.method_5854(); // getVehicle
        if (v instanceof ShifterTitan st) {
            UUID id = st.getShifterUUID();
            if (id != null && id.equals(player.method_5667())) return v; // getUuid
        }
        return null;
    }

    public static void attack(class_1297 titan) {
        internal = true;
        try {
            if (titan instanceof AttackTitanEntity a) a.triggerAttack();
            else if (titan instanceof ColossalTitanEntity a) a.triggerAttack();
            else if (titan instanceof ArmoredTitanEntity a) a.triggerAttack();
            else if (titan instanceof FemaleTitanEntity a) a.triggerAttack();
            else if (titan instanceof BeastTitanEntity a) a.triggerAttack();
            else if (titan instanceof WarhammerTitanEntity a) a.triggerAttack();
        } catch (Throwable t) {
            TitanMastery.LOG.debug("Titan attack call failed", t);
        } finally {
            internal = false;
        }
    }

    public static void roar(class_1297 titan) {
        internal = true;
        try {
            if (titan instanceof AttackTitanEntity a) a.triggerRoar();
            else if (titan instanceof BeastTitanEntity a) a.triggerRoar();
        } catch (Throwable t) {
            TitanMastery.LOG.debug("Titan roar call failed", t);
        } finally {
            internal = false;
        }
    }

    /** Our Founding Titan: the player themself, grown giant by the bundled datapack ("Titan Form"). */
    public static boolean inFoundingForm(class_1657 player) {
        return player.method_5752().contains("founding.titan"); // getCommandTags (server side only)
    }

    /** Runs a datapack function as this player, silently. */
    public static void runFunction(class_3222 player, String function) {
        var server = player.method_5682();
        if (server == null) return;
        server.method_3734().method_44252(player.method_5671().method_9217().method_9206(2), "function " + function);
    }

    /** A raging giant Founder throws a punch at whatever is in front of it. */
    public static void playerTitanStrike(class_3222 player, class_1297 target) {
        player.method_6104(class_1268.field_5808); // swingHand(MAIN_HAND)
        player.method_7324(target);                // attack
    }

    /** Forces the shifter out of the titan (used when a feral rampage burns out). */
    public static void forceRevert(class_1297 titan, class_1657 player) {
        if (titan == player && player instanceof class_3222 sp) {
            runFunction(sp, "founding:form/revert");
            return;
        }
        internal = true;
        try {
            if (titan instanceof AttackTitanEntity a) a.startDismounting(player);
            else if (titan instanceof ColossalTitanEntity a) a.startDismounting(player);
            else if (titan instanceof ArmoredTitanEntity a) a.startDismounting(player);
            else if (titan instanceof FemaleTitanEntity a) a.startDismounting(player);
            else if (titan instanceof BeastTitanEntity a) a.startDismounting(player);
            else if (titan instanceof WarhammerTitanEntity a) a.startDismounting(player);
        } catch (Throwable t) {
            TitanMastery.LOG.debug("Titan revert call failed", t);
        } finally {
            internal = false;
        }
    }

    /** Danny's titans are built from several helper entities (nape, eyes, grab box, legs). Never target those. */
    public static boolean isTitanPart(class_1297 e) {
        String n = e.getClass().getName();
        if (!n.startsWith("daot.")) return false;
        return n.contains("Nape") || n.contains("Eye") || n.contains("Grab") || n.contains("Leg")
                || n.contains("Hitbox") || n.contains("Shell") || n.contains("Rig");
    }

    /** Something a raging or attacking titan may hit. */
    public static boolean isValidVictim(class_1297 e, class_1297 titan, class_1657 rider) {
        if (!(e instanceof class_1309 living)) return false;
        if (e == titan || e == rider) return false;
        if (!living.method_5805()) return false; // isAlive
        if (e instanceof class_1531) return false;
        if (isTitanPart(e)) return false;
        if (e.method_5854() == titan) return false; // riding this titan
        if (e instanceof class_1657 p && (p.method_7325() || p.method_7337())) return false; // spectator / creative
        return true;
    }

    /** Nearest thing to rampage toward. Runs identically on client and server so both agree. */
    public static class_1297 nearestTarget(class_1297 titan, class_1657 rider, double range) {
        class_238 box = titan.method_5829().method_1014(range); // getBoundingBox().expand
        class_1297 best = null;
        double bestD = Double.MAX_VALUE;
        for (class_1297 e : titan.method_37908().method_8333(titan, box, x -> isValidVictim(x, titan, rider))) {
            double d = e.method_5858(titan); // squaredDistanceTo
            if (d < bestD) {
                bestD = d;
                best = e;
            }
        }
        return best;
    }

    public static float yawToward(class_1297 from, class_1297 to) {
        class_243 a = from.method_19538(); // getPos
        class_243 b = to.method_19538();
        double dx = b.field_1352 - a.field_1352;
        double dz = b.field_1350 - a.field_1350;
        return (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
    }

    public static float wrap(float deg) {
        deg %= 360f;
        if (deg >= 180f) deg -= 360f;
        if (deg < -180f) deg += 360f;
        return deg;
    }
}
