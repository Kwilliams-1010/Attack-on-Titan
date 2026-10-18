package titanmastery.founding;

/**
 * Jump key state for the Founding Titan. Movement of a ridden Titan runs on the Founder's own client,
 * so the client's input mixin writes this and the entity reads it in the same game.
 */
public final class FoundingInput {
    private FoundingInput() {}

    public static volatile boolean jumping = false;
}
