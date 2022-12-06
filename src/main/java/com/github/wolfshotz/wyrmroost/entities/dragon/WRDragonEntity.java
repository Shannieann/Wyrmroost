package com.github.wolfshotz.wyrmroost.entities.dragon;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class WRDragonEntity extends PathfinderMob implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(LesserDesertwyrmEntity.class, EntityDataSerializers.STRING);
    /**
     * ANIMATION_TYPE:
     * Case 1: LOOP
     * Case 2: PLAY_ONCE
     * Case 3: HOLD_ON_LAST_FRAME
     */
    private static final EntityDataAccessor<Integer> ANIMATION_TYPE = SynchedEntityData.defineId(LesserDesertwyrmEntity.class, EntityDataSerializers.INT);
    /**
     * ATTACKING_STATE:
     * Case 0: Not attacking
     * Case n: Attacking with attack type n
     */
    private static final EntityDataAccessor<Integer> ATTACKING_STATE = SynchedEntityData.defineId(LesserDesertwyrmEntity.class, EntityDataSerializers.INT);
    /**
     * MOVING_STATE:
     * Case 0: Ground
     * Case 1: Flying
     * Case 2: Swimming
     */
    private static final EntityDataAccessor<Integer> MOVING_STATE = SynchedEntityData.defineId(LesserDesertwyrmEntity.class, EntityDataSerializers.INT);

    protected WRDragonEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "generalController", 0, this::generalPredicate));
        data.addAnimationController(new AnimationController(this, "moveController", 0, this::movingPredicate));

    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public <E extends IAnimatable> PlayState generalPredicate(AnimationEvent<E> event)
    {
        String animation = this.getAnimation();
        //If we do have an Ability animation play that
        if (!animation.equals("base")) {
            int animationType = this.getAnimationType();
            ILoopType loopType;
            switch (animationType) {
                case 1: loopType = ILoopType.EDefaultLoopTypes.LOOP;
                    break;
                case 2: loopType = ILoopType.EDefaultLoopTypes.PLAY_ONCE;
                    break;
                case 3: loopType = ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME;
                    break;
                default:
                    return PlayState.STOP;
            }
            event.getController().setAnimation(new AnimationBuilder().addAnimation(animation, loopType));
            return PlayState.CONTINUE;
        }
        //Else, do basic locomotion
        //Death
        if ((this.dead || this.getHealth() < 0.01 || this.isDeadOrDying())) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("death", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }
        //Attacking:
        int attackingState = this.getAttackingState();
        if (attackingState != 0) {
            for (int state = 1; state <= 4; state++){
                if (attackingState == state) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("attack_"+state, ILoopType.EDefaultLoopTypes.LOOP));
                    return PlayState.CONTINUE;
                }
            }
        }
        //Moving
        //This moving only plays if it's *just* moving and not doing anything else, as its only reached under those conditions...
        int movingState = this.getMovingState();
        if (event.isMoving()) {
            switch (movingState) {
                case 1 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP));
                case 2 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("fly", ILoopType.EDefaultLoopTypes.LOOP));
                case 3 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("swim", ILoopType.EDefaultLoopTypes.LOOP));
            }
            return PlayState.CONTINUE;
        }
        //Idle:
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    public <E extends IAnimatable> PlayState movingPredicate(AnimationEvent<E> event)
    {
        int movingState = this.getMovingState();
        if (event.isMoving()) {
            switch (movingState) {
                case 1 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("base_walk", ILoopType.EDefaultLoopTypes.LOOP));
                case 2 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("base_fly", ILoopType.EDefaultLoopTypes.LOOP));
                case 3 -> event.getController().setAnimation(new AnimationBuilder().addAnimation("base_swim", ILoopType.EDefaultLoopTypes.LOOP));
            }
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    protected void defineSynchedData()
    {
        this.entityData.define(ANIMATION, "base");
        this.entityData.define(ANIMATION_TYPE, 1);
        this.entityData.define(ATTACKING_STATE, 0);
        this.entityData.define(MOVING_STATE, 0);
        super.defineSynchedData();
    }

    public String getAnimation()
    {
        return entityData.get(ANIMATION);
    }

    public void setAnimation(String animation)
    {
        entityData.set(ANIMATION, animation);
    }

    public int getAnimationType()
    {
        return entityData.get(ANIMATION_TYPE);
    }

    public void setAnimationType(int animation)
    {
        entityData.set(ANIMATION_TYPE, animation);
    }

    public int getAttackingState()
    {
        return entityData.get(ATTACKING_STATE);
    }

    public void setAttackingState(int attackingState)
    {
        entityData.set(ATTACKING_STATE, attackingState);
    }

    public int getMovingState()
    {
        return entityData.get(ANIMATION_TYPE);
    }

    public void setMovingState(int movingState)
    {
        entityData.set(MOVING_STATE, movingState);
    }
}
