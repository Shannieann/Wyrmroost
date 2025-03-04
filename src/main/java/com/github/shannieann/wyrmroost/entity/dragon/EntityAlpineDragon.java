package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.config.WRServerConfig;
import com.github.shannieann.wyrmroost.events.ClientEvents;
import com.github.shannieann.wyrmroost.util.LerpedFloat;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeMod;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;

public class EntityAlpineDragon extends WRDragonEntity {

    // ====================================
    //      A.1) General Attributes
    // ====================================

    public final LerpedFloat sitTimer = LerpedFloat.unit();

    public EntityAlpineDragon(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNavigator(NavigationType.GROUND);
    }

    // ====================================
    //      A.2) Entity Attributes
    // ====================================

    public static AttributeSupplier.Builder getAttributeSupplier() {
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonAttributesConfig.maxHealth.get())
                .add(MOVEMENT_SPEED, 0.22)
                .add(FLYING_SPEED, 0.185f)
                .add(ForgeMod.SWIM_SPEED.get(), 0.15F)
                .add(KNOCKBACK_RESISTANCE, 1)
                .add(ATTACK_DAMAGE, WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonAttributesConfig.attackDamage.get())
                .add(FOLLOW_RANGE, 50);
    }

    // ====================================
    //      A.3) Growth & Scaling
    // ====================================

    @Override
    public float ageProgressAmount() {
        return WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonBreedingConfig.ageProgress.get() / 100F;
    }

    @Override
    public float initialBabyScale() {
        return 0.1F;
    }

    // ====================================
    //      A.4) Species Capabilities
    // ====================================

    @Override
    public boolean speciesCanWalk() {
        return true;
    }

    @Override
    public boolean speciesCanFly() {
        return true;
    }

    @Override
    public boolean speciesCanSwim() {
        return false;
    }

    @Override
    public boolean speciesCanBeRidden() {
        return true;
    }

    // ====================================
    //      A.5) Behavior & Mechanics
    // ====================================

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.isEdible() && stack.getFoodProperties(this).isMeat();
    }

    @Override
    public boolean defendsHome() {
        return true;
    }

    @Override
    public float getRestrictRadius() {
        return WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonAttributesConfig.homeRadius.get() *
                WRServerConfig.SERVER.ENTITIES.ALPINE_DRAGON.dragonAttributesConfig.homeRadius.get();
    }

    // ====================================
    //      A.6) Variants
    // ====================================

    @Override
    public String getDefaultVariant() {
        return "black";
    }

    @Override
    public String determineVariant() {
        return switch (getRandom().nextInt(7)) {
            case 1 -> "blue";
            case 2 -> "green";
            case 3 -> "mint";
            case 4 -> "red";
            case 5 -> "white";
            case 6 -> "yellow";
            default -> getDefaultVariant();
        };
    }

    // ====================================
    //      A.7) Entity Data: Miscellaneous
    // ====================================

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return getType().getDimensions().scale(getScale());
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions size) {
        return size.height * 0.6f;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.noCollision(this);
    }

    @Override
    public Vec2 getTomeDepictionOffset() {
        return null;
    }

    @Override
    public int idleAnimationVariants() {
        return 0;
    }

    // ====================================
    //      A.8) AI & Updates
    // ====================================

    @Override
    public void aiStep() {
        super.aiStep();

        // =====================
        //       Update Timers
        // =====================
        sitTimer.add(isInSittingPose() ? 0.1f : -0.1f);
    }

    // ====================================
    //      A.9) Camera Controls
    // ====================================

    @Override
    public void setupThirdPersonCamera(boolean backView, EntityViewRenderEvent.CameraSetup event, Player player) {
        if (backView) {
            event.getCamera().move(ClientEvents.performCollisionCalculations(-10, this, player), 0, 0);
        } else {
            event.getCamera().move(ClientEvents.performCollisionCalculations(-0.5, this, player), 0.3, 0);
        }
    }

    // ====================================
    //      A.10) Mounting & Passengers
    // ====================================

    @Override
    public float getStepHeight() {
        return 2;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return super.canAddPassenger(passenger) && isJuvenile();
    }

    @Override
    public int getMaxPassengers() {
        return 2;
    }

    @Override
    public Vec3 getPassengerPosOffset(Entity entity, int index) {
        return new Vec3(0, this.getType().getDimensions().height * 0.95D, index == 1 ? -2 : 0);
    }
}