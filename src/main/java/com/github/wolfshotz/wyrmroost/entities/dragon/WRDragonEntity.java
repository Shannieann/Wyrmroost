package com.github.wolfshotz.wyrmroost.entities.dragon;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public class WRDragonEntity extends PathfinderMob {

    private static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(LesserDesertwyrmEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> ANIMATION_TYPE = SynchedEntityData.defineId(LesserDesertwyrmEntity.class, EntityDataSerializers.INT);
    /**
     * ANIMATION TYPE:
     * Case 1: LOOP
     * Case 2: PLAY_ONCE
     * Case 3: HOLD_ON_LAST_FRAME
     */

    protected WRDragonEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData()
    {
        this.entityData.define(ANIMATION, "base");
        this.entityData.define(ANIMATION_TYPE, 1);
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
}
