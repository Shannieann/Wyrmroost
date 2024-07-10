package com.github.shannieann.wyrmroost.entity.dragonegg;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.item.WRDragonEggItem;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class WRDragonEggEntity extends Entity {


    public WRDragonEggEntity(Level level, EntityType<? extends WRDragonEntity> containedDragon, int hatchTime) {
        super(WREntityTypes.DRAGON_EGG.get(), level);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return null;
    }
}
