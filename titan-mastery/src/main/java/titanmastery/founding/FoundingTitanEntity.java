package titanmastery.founding;

import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1314;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_3218;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_4048;
import net.minecraft.class_5132;
import net.minecraft.class_5134;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

/**
 * The Founding Titan body. Its Founder rides inside the head and steers it like any shifter Titan.
 * It has no AI of its own; with no Founder inside it dissolves into steam.
 * Yarn: class_1314 PathAwareEntity, class_1657 PlayerEntity, class_243 Vec3d, class_5134 EntityAttributes.
 */
public class FoundingTitanEntity extends class_1314 implements GeoEntity {
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.founding_titan.idle");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.founding_titan.walk");
    public static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.founding_titan.attack");
    public static final RawAnimation ROAR = RawAnimation.begin().thenPlay("animation.founding_titan.roar");

    public static final float WIDTH = 5.0f;
    public static final float HEIGHT = 20.0f;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private UUID owner;
    private int riderlessTicks;
    private int strikeCooldown;

    public FoundingTitanEntity(class_1299<? extends class_1314> type, class_1937 world) {
        super(type, world);
    }

    public static class_5132.class_5133 createAttributes() {
        return class_1308.method_26828()
                .method_26868(class_5134.field_23716, 500.0)   // max health
                .method_26868(class_5134.field_23719, 0.30)    // movement speed
                .method_26868(class_5134.field_23721, 30.0)    // attack damage
                .method_26868(class_5134.field_23718, 1.0)     // knockback resistance
                .method_26868(class_5134.field_47761, 4.0)     // step height
                .method_26868(class_5134.field_23724, 10.0)    // armor
                .method_26868(class_5134.field_49079, 40.0)    // safe fall distance
                .method_26868(class_5134.field_23717, 16.0);   // follow range
    }

    public UUID getOwner() { return owner; }
    public void setOwner(UUID owner) { this.owner = owner; }

    /** The Founder steering this body, if they are inside it. */
    public class_1657 getFounder() {
        class_1297 p = this.method_31483(); // getFirstPassenger
        if (p instanceof class_1657 player && owner != null && owner.equals(player.method_5667())) return player;
        return null;
    }

    // ------------------------------------------------------------------ riding

    @Override
    protected void method_5959() {
        // initGoals: no AI. The Founder is the mind.
    }

    @Override
    public class_1309 method_5642() { // getControllingPassenger
        return getFounder();
    }

    @Override
    protected void method_49481(class_1657 rider, class_243 input) { // tickControlled
        super.method_49481(rider, input);
        float yaw = rider.method_36454();
        this.method_36456(yaw);          // setYaw
        this.field_5982 = yaw;           // prevYaw
        this.field_6283 = yaw;           // bodyYaw
        this.field_6241 = yaw;           // headYaw
        this.method_36457(0f);           // setPitch
        if (FoundingInput.jumping && this.method_24828()) { // onGround
            class_243 v = this.method_18798();
            this.method_18800(v.field_1352, 0.85, v.field_1350);
        }
    }

    @Override
    protected class_243 method_49482(class_1657 rider, class_243 input) { // getControlledMovementInput
        float side = rider.field_6212 * 0.5f;    // sidewaysSpeed
        float forward = rider.field_6250;        // forwardSpeed
        if (forward < 0) forward *= 0.4f;
        return new class_243(side, 0.0, forward);
    }

    @Override
    protected float method_49485(class_1657 rider) { // getSaddledSpeed
        return (float) this.method_45325(class_5134.field_23719);
    }

    @Override
    protected class_243 method_52533(class_1297 passenger, class_4048 dimensions, float scale) { // getPassengerAttachmentPos
        // Sit the Founder inside the head, looking out through the eyes.
        return new class_243(0.0, HEIGHT - 3.3, 0.9);
    }

    @Override
    protected boolean method_5818(class_1297 passenger) { // canAddPassenger
        return this.method_5685().isEmpty();
    }

    @Override
    public boolean method_5810() { return false; } // isPushable

    @Override
    public boolean method_5747(float distance, float mult, class_1282 source) { return false; } // handleFallDamage

    @Override
    public void method_5982() { } // checkDespawn: never

    @Override
    public boolean method_5974(double distanceSquared) { return false; } // canImmediatelyDespawn

    @Override
    public boolean method_5643(class_1282 source, float amount) { // damage
        class_1657 founder = getFounder();
        if (founder != null && (source.method_5529() == founder || source.method_5526() == founder)) return false;
        return super.method_5643(source, amount);
    }

    @Override
    public void method_6078(class_1282 source) { // onDeath
        class_1657 founder = getFounder();
        if (founder != null && !this.method_37908().field_9236) FounderManager.onTitanFallen(founder, this);
        super.method_6078(source);
    }

    @Override
    public void method_5773() { // tick
        super.method_5773();
        if (strikeCooldown > 0) strikeCooldown--;
        if (this.method_37908().field_9236) return;

        if (getFounder() == null) {
            if (++riderlessTicks > 3) {
                FounderManager.steamAway(this);
                this.method_31472(); // discard
            }
        } else {
            riderlessTicks = 0;
            if (this.field_6012 % 10 == 0) { // age: steady steam off the body
                ((class_3218) this.method_37908()).method_14199(class_2398.field_11204,
                        this.method_23317(), this.method_23318() + HEIGHT * 0.6, this.method_23321(), 2, 1.5, 3.0, 1.5, 0.01);
            }
        }
    }

    // ------------------------------------------------------------------ combat

    public boolean canStrike() { return strikeCooldown <= 0; }

    /** A punch from the Founding Titan: the target takes the blow, anything close beside it takes half. */
    public void strike(class_1297 target) {
        if (strikeCooldown > 0 || !(target instanceof class_1309 victim)) return;
        strikeCooldown = 12;
        triggerAnim("action", "attack");
        float damage = (float) this.method_45325(class_5134.field_23721);
        victim.method_5643(this.method_48923().method_48812(this), damage);
        class_243 push = victim.method_19538().method_1020(this.method_19538()).method_1029().method_1021(1.6).method_1031(0, 0.4, 0);
        victim.method_18799(victim.method_18798().method_1019(push));
        victim.field_6037 = true;

        class_1657 founder = getFounder();
        for (class_1309 near : this.method_37908().method_8390(class_1309.class, victim.method_5829().method_1014(3.0),
                e -> e != victim && e != this && e != founder && e.method_5805())) {
            near.method_5643(this.method_48923().method_48812(this), damage * 0.5f);
        }
        this.method_37908().method_43128(null, victim.method_23317(), victim.method_23318(), victim.method_23321(),
                class_3417.field_14649, class_3419.field_15251, 3.0f, 0.5f); // iron golem attack
    }

    public void roar() {
        triggerAnim("action", "roar");
        this.method_37908().method_43128(null, this.method_23317(), this.method_23318(), this.method_23321(),
                class_3417.field_14733, class_3419.field_15251, 6.0f, 0.45f); // ravager roar
    }

    // ------------------------------------------------------------------ save

    @Override
    public void method_5652(class_2487 nbt) { // writeCustomDataToNbt
        super.method_5652(nbt);
        if (owner != null) nbt.method_25927("Founder", owner);
    }

    @Override
    public void method_5749(class_2487 nbt) { // readCustomDataFromNbt
        super.method_5749(nbt);
        if (nbt.method_25928("Founder")) owner = nbt.method_25926("Founder");
    }

    // ------------------------------------------------------------------ GeckoLib

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "move", 6, state -> {
            double dx = this.method_23317() - this.field_6014;
            double dz = this.method_23321() - this.field_5969;
            return state.setAndContinue(dx * dx + dz * dz > 1.0E-4 ? WALK : IDLE);
        }));
        controllers.add(new AnimationController<>(this, "action", 3, state -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK)
                .triggerableAnim("roar", ROAR));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
