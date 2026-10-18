package titanmastery;

import net.minecraft.class_124;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_266;
import net.minecraft.class_269;
import net.minecraft.class_274;
import net.minecraft.class_3222;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_5250;
import net.minecraft.class_9013;
import net.minecraft.server.MinecraftServer;

/**
 * Mastery XP lives in scoreboard objectives (tm_attack, tm_colossal, ...), so it saves with the world
 * and ops can inspect or edit it with /scoreboard as well as /mastery.
 * Yarn: class_269 Scoreboard, class_266 ScoreboardObjective, class_274 ScoreboardCriterion, class_2561 Text.
 */
public final class Mastery {
    private Mastery() {}

    public static final int[] THRESHOLDS = {0, 200, 600, 1500, 3000, 6000};
    public static final String[] STAGES = {"Feral", "Unstable", "Awakened", "Disciplined", "Mastered", "Perfected"};
    public static final class_124[] STAGE_COLORS = {
            class_124.field_1079, class_124.field_1061, class_124.field_1065,
            class_124.field_1054, class_124.field_1060, class_124.field_1075};

    public static class_266 objective(MinecraftServer server, TitanType type) {
        class_269 sb = server.method_3845(); // getScoreboard
        class_266 obj = sb.method_1170(type.objective()); // getNullableObjective
        if (obj == null) {
            obj = sb.method_1168(type.objective(), class_274.field_1468, // DUMMY
                    class_2561.method_43470(type.display + " Mastery"),
                    class_274.class_275.field_1472, true, null); // RenderType.INTEGER
        }
        return obj;
    }

    public static int xp(class_3222 player, TitanType type) {
        MinecraftServer server = player.method_5682();
        if (server == null) return 0;
        class_9013 s = server.method_3845().method_55430(player, objective(server, type)); // getScore
        return s == null ? 0 : s.method_55397();
    }

    public static int stageFor(int xp) {
        int stage = 0;
        for (int i = 0; i < THRESHOLDS.length; i++) {
            if (xp >= THRESHOLDS[i]) stage = i;
        }
        return stage;
    }

    public static int stage(class_3222 player, TitanType type) {
        return stageFor(xp(player, type));
    }

    public static void setXp(class_3222 player, TitanType type, int value) {
        MinecraftServer server = player.method_5682();
        if (server == null) return;
        int before = stage(player, type);
        server.method_3845().method_1180(player, objective(server, type)).method_55410(Math.max(0, value)); // getOrCreateScore.setScore
        int after = stage(player, type);
        if (after > before) announceStageUp(player, type, after);
    }

    public static void addXp(class_3222 player, TitanType type, int amount) {
        if (amount <= 0) return;
        setXp(player, type, xp(player, type) + amount);
    }

    public static class_5250 stageText(int stage) {
        return class_2561.method_43470(STAGES[stage]).method_27692(STAGE_COLORS[stage]);
    }

    public static class_5250 progressText(class_3222 player, TitanType type) {
        int xp = xp(player, type);
        int stage = stageFor(xp);
        class_5250 t = class_2561.method_43470(type.display + " ").method_54663(type.color)
                .method_10852(class_2561.method_43470("· ").method_27692(class_124.field_1063))
                .method_10852(stageText(stage));
        if (stage < THRESHOLDS.length - 1) {
            t.method_10852(class_2561.method_43470("  " + xp + "/" + THRESHOLDS[stage + 1] + " XP").method_27692(class_124.field_1080));
        } else {
            t.method_10852(class_2561.method_43470("  " + xp + " XP (max)").method_27692(class_124.field_1080));
        }
        return t;
    }

    public static String unlockText(int stage, TitanType type) {
        return switch (stage) {
            case 1 -> "You can steer the Titan now, but rage can still take over.";
            case 2 -> "Danny's Titan abilities unlocked. Rage rarely takes over.";
            case 3 -> "Full control. Mastery ability unlocked: " + type.abilityName + " (J).";
            case 4 -> "Mastery form unlocked: " + type.formName + " (U).";
            case 5 -> "Perfected. Your form lasts twice as long and your Titan is at its peak.";
            default -> "";
        };
    }

    private static void announceStageUp(class_3222 player, TitanType type, int stage) {
        player.method_7353(class_2561.method_43470("✦ ").method_27692(class_124.field_1065)
                .method_10852(class_2561.method_43470(type.display + " mastery: ").method_54663(type.color))
                .method_10852(stageText(stage).method_27692(class_124.field_1067)), false);
        player.method_7353(class_2561.method_43470(unlockText(stage, type)).method_27692(class_124.field_1080), false);
        player.method_37908().method_43128(null, player.method_23317(), player.method_23318(), player.method_23321(),
                class_3417.field_14709, class_3419.field_15248, 1.0f, 0.8f); // ENTITY_PLAYER_LEVELUP, PLAYERS
    }
}
