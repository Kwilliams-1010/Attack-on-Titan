package titanmastery;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.class_124;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2186;
import net.minecraft.class_2561;
import net.minecraft.class_3222;

import java.util.Collection;

/**
 * /mastery                         your progress with all nine Titans
 * /mastery show <player>           someone else's progress
 * /mastery set|add <players> <titan> <xp>   (ops)
 * /mastery fury <players> <seconds>         (ops) force a rampage for testing
 * Yarn: class_2168 ServerCommandSource, class_2170 CommandManager, class_2186 EntityArgumentType.
 */
public final class MasteryCommand {
    private MasteryCommand() {}

    public static void register(CommandDispatcher<class_2168> d) {
        d.register(class_2170.method_9247("mastery")
                .executes(c -> show(c, c.getSource().method_9207()))
                .then(class_2170.method_9247("show")
                        .then(class_2170.method_9244("player", class_2186.method_9305())
                                .executes(c -> show(c, class_2186.method_9315(c, "player")))))
                .then(class_2170.method_9247("set").requires(s -> s.method_9259(2))
                        .then(class_2170.method_9244("players", class_2186.method_9308())
                                .then(class_2170.method_9244("titan", StringArgumentType.word()).suggests(MasteryCommand::suggestTitans)
                                        .then(class_2170.method_9244("xp", IntegerArgumentType.integer(0))
                                                .executes(c -> setXp(c, false))))))
                .then(class_2170.method_9247("add").requires(s -> s.method_9259(2))
                        .then(class_2170.method_9244("players", class_2186.method_9308())
                                .then(class_2170.method_9244("titan", StringArgumentType.word()).suggests(MasteryCommand::suggestTitans)
                                        .then(class_2170.method_9244("xp", IntegerArgumentType.integer(0))
                                                .executes(c -> setXp(c, true))))))
                .then(class_2170.method_9247("fury").requires(s -> s.method_9259(2))
                        .then(class_2170.method_9244("players", class_2186.method_9308())
                                .then(class_2170.method_9244("seconds", IntegerArgumentType.integer(1, 600))
                                        .executes(MasteryCommand::fury)))));
    }

    private static java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestTitans(
            CommandContext<class_2168> c, com.mojang.brigadier.suggestion.SuggestionsBuilder b) {
        for (TitanType t : TitanType.values()) b.suggest(t.key);
        return b.buildFuture();
    }

    private static int show(CommandContext<class_2168> c, class_3222 player) {
        class_2168 src = c.getSource();
        src.method_9226(() -> class_2561.method_43470("— Titan Mastery: ").method_27692(class_124.field_1065)
                .method_10852(player.method_5477()).method_10852(class_2561.method_43470(" —").method_27692(class_124.field_1065)), false);
        for (TitanType t : TitanType.values()) {
            src.method_9226(() -> Mastery.progressText(player, t), false);
        }
        src.method_9226(() -> class_2561.method_43470("Stages: Feral → Unstable (200) → Awakened (600) → Disciplined (1500) → Mastered (3000) → Perfected (6000)")
                .method_27692(class_124.field_1063), false);
        return 1;
    }

    private static int setXp(CommandContext<class_2168> c, boolean add) throws CommandSyntaxException {
        Collection<class_3222> players = class_2186.method_9312(c, "players");
        TitanType type = TitanType.byKey(StringArgumentType.getString(c, "titan"));
        int xp = IntegerArgumentType.getInteger(c, "xp");
        if (type == null) {
            c.getSource().method_9213(class_2561.method_43470("Unknown titan. Use: attack, colossal, armored, female, beast, warhammer, jaw, cart, founding"));
            return 0;
        }
        for (class_3222 p : players) {
            if (add) Mastery.addXp(p, type, xp);
            else Mastery.setXp(p, type, xp);
            Session s = MasteryManager.session(p);
            if (s != null && s.type == type) s.stage = Mastery.stage(p, type);
        }
        c.getSource().method_9226(() -> class_2561.method_43470((add ? "Added " : "Set ") + xp + " " + type.display
                + " mastery XP for " + players.size() + " player(s)."), true);
        return players.size();
    }

    private static int fury(CommandContext<class_2168> c) throws CommandSyntaxException {
        int seconds = IntegerArgumentType.getInteger(c, "seconds");
        int n = 0;
        for (class_3222 p : class_2186.method_9312(c, "players")) {
            Session s = MasteryManager.session(p);
            if (s == null) continue;
            MasteryManager.startFury(p, s, seconds * 20, false, MasteryManager.now(p.method_5682()));
            n++;
        }
        int count = n;
        c.getSource().method_9226(() -> class_2561.method_43470("Forced rage on " + count + " shifted player(s)."), true);
        return n;
    }
}
