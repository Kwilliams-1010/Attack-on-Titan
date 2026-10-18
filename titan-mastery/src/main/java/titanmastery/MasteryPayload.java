package titanmastery;

import net.minecraft.class_2960;
import net.minecraft.class_8710;
import net.minecraft.class_9129;
import net.minecraft.class_9135;
import net.minecraft.class_9139;

/** Client to server: the player pressed the mastery ability (0) or form (1) key. */
public record MasteryPayload(int action) implements class_8710 {
    public static final class_8710.class_9154<MasteryPayload> ID =
            new class_8710.class_9154<>(class_2960.method_60655(TitanMastery.MOD_ID, "action"));

    public static final class_9139<class_9129, MasteryPayload> CODEC =
            class_9139.method_56434(class_9135.field_49675, MasteryPayload::action, MasteryPayload::new);

    @Override
    public class_8710.class_9154<? extends class_8710> method_56479() {
        return ID;
    }
}
