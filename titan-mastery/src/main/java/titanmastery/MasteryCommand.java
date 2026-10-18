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
 * /mastery founding <players>               (ops) make them the Founding Titan, like /daot shifter set
 * /mastery founding release                 (ops) nobody holds it; the next royal-blooded newcomer can be born with it
 * /mastery founding chance <percent>        (ops) chance for a royal-blooded player to be born the Founder (default 25)
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
                .then(class_2170.method_9247("founding").requires(s -> s.method_9259(2))
                        .then(class_2170.method_9247("release").executes(MasteryCommand::foundingRelease))
                        .then(class_2170.method_9247("model")
                                .then(class_2170.method_9247("original").executes(c -> foundingModel(c, 1)))
                                .then(class_2170.method_9247("springdaot").executes(c -> foundingModel(c, 0))))
                        .then(class_2170.method_9247("chance")
                                .then(class_2170.method_9244("percent", IntegerArgumentType.integer(0, 100))
                                        .executes(MasteryCommand::foundingChance)))
                        .then(class_2170.method_9244("players", class_2186.method_9308())
                                .executes(MasteryCommand::founding)))
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

    private static int founding(CommandContext<class_2168> c) throws CommandSyntaxException {
        Collection<class_3222> players = class_2186.method_9312(c, "players");
        // The Founding Titan has one holder at a time, so only the last player listed keeps it.
        for (class_3222 p : players) TitanHooks.runFunction(p, "founding:power/inherit");
        c.getSource().method_9226(() -> class_2561.method_43470("Granted the Founding Titan. Press O to transform.")
                .method_27692(class_124.field_1065), true);
        return players.size();
    }

    private static int foundingRelease(CommandContext<class_2168> c) {
        var server = c.getSource().method_9211();
        // Bumping the generation strips the power from whoever holds it (even offline) the next time they're seen.
        server.method_3734().method_44252(c.getSource().method_9217().method_9206(2), "scoreboard players add #gen founding.g 1");
        server.method_3734().method_44252(c.getSource().method_9217().method_9206(2), "scoreboard players set #claimed founding.cfg 0");
        server.method_3734().method_44252(c.getSource().method_9217().method_9206(2), "tag @a remove " + titanmastery.founding.FounderManager.ROLLED_TAG);
        c.getSource().method_9226(() -> class_2561.method_43470("The Founding Titan is unclaimed. A royal-blooded player may be born with it."), true);
        return 1;
    }

    private static int foundingModel(CommandContext<class_2168> c, int original) {
        c.getSource().method_9211().method_3734().method_44252(c.getSource().method_9217().method_9206(2),
                "scoreboard players set #original_model founding.cfg " + original);
        boolean spring = net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("springdaot");
        c.getSource().method_9226(() -> class_2561.method_43470(original == 1
                ? "The Founder now transforms into the Titan Mastery Founding Titan."
                : spring ? "The Founder now transforms into SpringDAOT's Ymir Founding Titan."
                         : "SpringDAOT isn't installed, so the Titan Mastery model is used."), true);
        return 1;
    }

    private static int foundingChance(CommandContext<class_2168> c) {
        int pct = IntegerArgumentType.getInteger(c, "percent");
        c.getSource().method_9211().method_3734().method_44252(c.getSource().method_9217().method_9206(2),
                "scoreboard players set #chance founding.cfg " + pct);
        c.getSource().method_9226(() -> class_2561.method_43470("Royal-blooded players now have a " + pct + "% chance to be born the Founder."), true);
        return pct;
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
