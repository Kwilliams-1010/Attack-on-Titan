package titanmastery;

import daot.ArmoredTitanEntity;
import daot.AttackTitanEntity;
import daot.BeastTitanEntity;
import daot.CartShifterTitanEntity;
import daot.ColossalTitanEntity;
import daot.FemaleTitanEntity;
import daot.FoundingTitanEntity;
import daot.OgreShifterTitanEntity;
import daot.TestShifterTitanEntity;
import daot.WarhammerTitanEntity;
import net.minecraft.class_1297;

/** The nine Titans, with the mastery ability and form each one unlocks. */
public enum TitanType {
    ATTACK("attack", "Attack Titan", 0xC0392B, "War Cry", "Hardened Attack Titan", 40),
    COLOSSAL("colossal", "Colossal Titan", 0xE67E22, "Steam Burst", "Scalding Steam", 45),
    ARMORED("armored", "Armored Titan", 0xBDC3C7, "Battering Ram", "Full Plating", 35),
    FEMALE("female", "Female Titan", 0xF1C40F, "Crystal Strike", "Crystal Cocoon", 30),
    BEAST("beast", "Beast Titan", 0xA1887F, "Rock Barrage", "Pitcher's Stance", 40),
    WARHAMMER("warhammer", "War Hammer Titan", 0xECEFF1, "Spike Eruption", "Crystal Core", 40),
    JAW("jaw", "Jaw Titan", 0x90A4AE, "Lunge Bite", "Frenzy", 25),
    CART("cart", "Cart Titan", 0x8D6E63, "Panzer Fire", "Endurance March", 35),
    FOUNDING("founding", "Founding Titan", 0xD4AF37, "Coordinate", "Paths of Ymir", 60);

    public final String key;
    public final String display;
    public final int color;
    public final String abilityName;
    public final String formName;
    public final int abilityCooldownSeconds;

    TitanType(String key, String display, int color, String abilityName, String formName, int abilityCooldownSeconds) {
        this.key = key;
        this.display = display;
        this.color = color;
        this.abilityName = abilityName;
        this.formName = formName;
        this.abilityCooldownSeconds = abilityCooldownSeconds;
    }

    public String objective() {
        return "tm_" + key;
    }

    public static TitanType byKey(String key) {
        for (TitanType t : values()) {
            if (t.key.equalsIgnoreCase(key)) return t;
        }
        return null;
    }

    /** Which of the nine a shifter titan entity is (subclasses are checked before AttackTitanEntity). */
    public static TitanType of(class_1297 e) {
        if (e instanceof titanmastery.founding.FoundingTitanEntity) return FOUNDING;
        if (e instanceof FoundingTitanEntity) return FOUNDING;
        if (e instanceof CartShifterTitanEntity) return CART;
        if (e instanceof OgreShifterTitanEntity) return null;
        if (e instanceof TestShifterTitanEntity) return JAW;
        if (e instanceof AttackTitanEntity) return ATTACK;
        if (e instanceof ColossalTitanEntity) return COLOSSAL;
        if (e instanceof ArmoredTitanEntity) return ARMORED;
        if (e instanceof FemaleTitanEntity) return FEMALE;
        if (e instanceof BeastTitanEntity) return BEAST;
        if (e instanceof WarhammerTitanEntity) return WARHAMMER;
        return null;
    }
}
