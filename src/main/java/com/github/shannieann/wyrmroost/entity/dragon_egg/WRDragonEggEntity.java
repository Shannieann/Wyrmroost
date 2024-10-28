package com.github.shannieann.wyrmroost.entity.dragon_egg;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import com.github.shannieann.wyrmroost.util.WRModUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;

public class WRDragonEggEntity extends LivingEntity implements IAnimatable {

    public WRDragonEggEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    //Constructor called by DragonEggItem
    public WRDragonEggEntity(Level level, WRDragonEntity containedDragon, int hatchTime) {
        super(WREntityTypes.DRAGON_EGG.get(), level);
        setHatchTime(hatchTime);
        setContainedDragon(EntityType.getKey(containedDragon.getType()).toString());
    }
    
    public static final EntityDataAccessor<String> CONTAINED_DRAGON = SynchedEntityData.defineId(WRDragonEggEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> HATCH_TIME = SynchedEntityData.defineId(WRDragonEggEntity.class,EntityDataSerializers.INT);

    // ================================
    //           Entity NBT
    // ================================
    @Override
    protected void defineSynchedData() {
        entityData.define(CONTAINED_DRAGON, "none");
        entityData.define(HATCH_TIME, 0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        setHatchTime(nbt.getInt("hatch_time"));
        setContainedDragon(nbt.getString("contained_dragon"));

    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return null;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot pSlot) {
        return null;
    }

    @Override
    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {

    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("hatch_time",getHatchTime());
        nbt.putString("contained_dragon",getContainedDragon());

    }

    public String getContainedDragon(){
        return entityData.get(CONTAINED_DRAGON);
    }

    public void setContainedDragon(String containedDragon){
        entityData.set(CONTAINED_DRAGON,containedDragon);
    }

    public int getHatchTime(){
        return entityData.get(HATCH_TIME);
    }

    public void setHatchTime(int hatchTime){
        entityData.set(HATCH_TIME,hatchTime);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return null;
    }

    // ====================================
    //      A) TICK
    // ====================================
    @Override
    public void tick() {
        if (!level.isClientSide && getContainedDragon() == null) {
            return;
        }

        super.tick();

        if (!level.isClientSide) {
            setHatchTime(getHatchTime()-1);
                if (getHatchTime() <= 0) {
                    hatch();
                }
            }
    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    public void hatch() {
        //On server side, hatch the dragon
        if (!level.isClientSide) {
            WRDragonEntity newDragon = ((WRDragonEntity) WRModUtils.getEntityTypeByKey(getContainedDragon()).create(this.level));
            if (newDragon == null) {
                Wyrmroost.LOG.error("Wyrmroost: Error creating new Dragon @ {}", blockPosition());
                return;
            }
            newDragon.moveTo(getX(), getY(), getZ(), 0, 0);
            newDragon.setAge(0);
            newDragon.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(blockPosition()), MobSpawnType.BREEDING, null, null);
            level.addFreshEntity(newDragon);
        }
        remove(RemovalReason.DISCARDED);
    }

    @Override
    public void registerControllers(AnimationData data) {

    }

    @Override
    public AnimationFactory getFactory() {
        return null;
    }

    public static AttributeSupplier.Builder getAttributeSupplier() {
        // base male attributes
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, 20);
    }
}