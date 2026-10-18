package titanmastery;

import net.minecraft.class_1297;

import java.util.EnumMap;
import java.util.Map;

/** Runtime state for one shifted player. Not saved: it rebuilds the moment they shift again. */
public final class Session {
    public final TitanType type;
    public final class_1297 titan;
    public int stage;
    public int shiftedTicks;

    // Rage
    public boolean inFury;
    public boolean feralFury;      // stage 0: burns out and forces the shifter out
    public long furyEnd;
    public boolean lowHealthFuryUsed;

    // Mastery ability and form
    public long buffEnd;
    public float buffMultiplier = 1f;
    public boolean launchHits;
    public long immuneEnd;
    public boolean formActive;
    public long formEnd;
    public int burstShotsLeft;

    // Throttle for "you can't do that yet" messages
    public long lastDeniedMessage;

    public final Map<TitanType, Long> abilityReadyAt;
    public final Map<TitanType, Long> formReadyAt;

    public Session(TitanType type, class_1297 titan, Map<TitanType, Long> abilityReadyAt, Map<TitanType, Long> formReadyAt) {
        this.type = type;
        this.titan = titan;
        this.abilityReadyAt = abilityReadyAt;
        this.formReadyAt = formReadyAt;
    }

    public static Map<TitanType, Long> newCooldowns() {
        return new EnumMap<>(TitanType.class);
    }
}
