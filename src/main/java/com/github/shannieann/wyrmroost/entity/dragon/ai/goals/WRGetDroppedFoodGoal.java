package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import java.util.EnumSet;
import java.util.List;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WRGetDroppedFoodGoal extends Goal {
    // data vars
    protected final WRDragonEntity dragon;
    protected int searchRadius;
    protected BlockPos itemAt;
    protected ItemEntity targetItemEntity;
    protected ItemEntity oneItemEntity;
    // control vars
    protected boolean eatOnPickup = true;
    protected boolean readyForOtherAction = false;
    protected Item acceptedFood;
    // cooldown vars
    protected int cooldown = 50;
    private static final int cooldownMax = 20; // only search for food every 2 seconds (goal tick = 0.1), avoid lag

    public WRGetDroppedFoodGoal(WRDragonEntity dragon, int searchRadius, boolean eatOnPickup) {
        this.dragon = dragon;
        if (this.dragon.isTame() && this.dragon.hasRestriction()) {
            this.searchRadius = Math.min(searchRadius, (int) this.dragon.getRestrictRadius());
        } else {
            this.searchRadius = searchRadius;
        }
        this.eatOnPickup = eatOnPickup;
        this.acceptedFood = null;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public WRGetDroppedFoodGoal(WRDragonEntity dragon, int searchRadius, boolean eatOnPickup, Item acceptedFood) {
        this.dragon = dragon;
        if (this.dragon.isTame() && this.dragon.hasRestriction()) {
            this.searchRadius = Math.min(searchRadius, (int) this.dragon.getRestrictRadius());
        } else {
            this.searchRadius = searchRadius;
        }
        this.eatOnPickup = eatOnPickup;
        this.acceptedFood = acceptedFood;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private boolean isAcceptedItem(ItemEntity itemEntity) {
        if (itemEntity == null || itemEntity.getItem() == null || itemEntity.getItem().isEmpty()) {
            return false;
        }
        return this.acceptedFood != null
            ? this.acceptedFood.equals(itemEntity.getItem().getItem())
            : this.dragon.isFood(itemEntity.getItem());
    }    

    private List<ItemEntity> findDroppedItems() {
        Level level = this.dragon.level;
        AABB searchBox = this.dragon.getBoundingBox().inflate(this.searchRadius, this.searchRadius, this.searchRadius);
        return level.getEntitiesOfClass(ItemEntity.class, searchBox, itemEntity -> isAcceptedItem(itemEntity));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        if ((this.eatOnPickup && this.dragon.getEatingCooldown() > 0)
            || this.dragon.getTarget() != null
            || this.dragon.isPassenger()
            || this.dragon.getPassengers().size() > 0
            || this.dragon.getSitting())
        {
            return false;
        }

        List<ItemEntity> droppedItems = findDroppedItems();
        if (droppedItems.isEmpty()) {
            return false;
        }

        this.targetItemEntity = droppedItems.get(0);
        if (this.dragon.hasRestriction() && this.dragon.getRestrictRadius() < this.dragon.distanceToSqr(this.targetItemEntity)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.dragon.getEatingCooldown() > 0
            || this.dragon.getTarget() != null
            || this.dragon.isPassenger()
            || this.dragon.getPassengers().size() > 0)
        {
            return false;
        }
        if (this.itemAt != null) {
            AABB checkBox = new AABB(this.itemAt).inflate(1, 1, 1);
            List<ItemEntity> itemsAtPos = this.dragon.level.getEntitiesOfClass(ItemEntity.class, checkBox, itemEntity -> isAcceptedItem(itemEntity));
            return !itemsAtPos.isEmpty();
        }
        return false;
    }

    @Override
    public void start() {
        if (this.targetItemEntity != null) {
            this.itemAt = this.targetItemEntity.blockPosition();
            this.dragon.getNavigation().createPath(this.targetItemEntity, 1);
            if (this.dragon.getNavigation().getPath() == null) {
                this.dragon.getNavigation().moveTo(this.targetItemEntity, 1.1D);
            }
        }
    }

    @Override
    public void tick() {

        if (this.targetItemEntity != null && this.targetItemEntity.isAlive()) { // item exists

            if (this.dragon.getNavigation().getPath() == null) {
                this.dragon.getNavigation().createPath(this.targetItemEntity, 1);
                if (this.dragon.getNavigation().getPath() == null) {
                    this.dragon.getNavigation().moveTo(this.targetItemEntity, 1.1D);
                }
            }
            double d2 = this.dragon.distanceToSqr(this.targetItemEntity);

            // standard pickup radius
            if (d2 < 1.0D) {
                if (!this.targetItemEntity.getItem().isEmpty() && this.isAcceptedItem(this.targetItemEntity)) {
                    if (this.eatOnPickup) { // Regular action
                        if (this.dragon.level.isClientSide) {
                            return;
                        }
                        if (this.dragon.getEatingCooldown() <= 0) {
                            ItemStack oldItemStack = this.targetItemEntity.getItem();
                            ItemStack itemStackOneItem = oldItemStack.split(1);
                            if (itemStackOneItem != ItemStack.EMPTY) {
                                this.dragon.eat(this.dragon.level, itemStackOneItem);
                                this.targetItemEntity.setItem(oldItemStack);
                            }
                        }
                        stop();
                        return;
                    } else { // Override - do something else (currently only rooststalker)
                        this.readyForOtherAction = true;
                    }
                }
            } else if ((! this.dragon.getNavigation().isInProgress())) {
                // Pathfinding sucks, shove closer
                this.dragon.getNavigation().moveTo(this.targetItemEntity, 1.1D);
                Vec3 direction = this.targetItemEntity.position().subtract(this.dragon.position()).normalize();
                direction = new Vec3(direction.x, 0, direction.z); // no vertical push so it doesn't get stuck climbing
                this.dragon.setDeltaMovement(direction.scale(0.1));
            }
        } else {
            // Item no longer exists, find a new one
            List<ItemEntity> droppedItems = findDroppedItems();
            if (!droppedItems.isEmpty()) {
                this.targetItemEntity = droppedItems.get(0);
                this.itemAt = this.targetItemEntity.blockPosition();
            } else {
                stop();
            }
        }
    }

    protected ItemStack splitTargetItemEntity() {
        ItemStack oldItemStack = this.targetItemEntity.getItem();
        ItemStack itemStackOneItem = oldItemStack.split(1);

        if (oldItemStack.getCount() > 0) {
            // Make new entity for new stack if old one has multiple items left, give new stack 1 item
            this.oneItemEntity = new ItemEntity(this.dragon.level, this.targetItemEntity.getX(), this.targetItemEntity.getY(), this.targetItemEntity.getZ(), itemStackOneItem);
            this.dragon.level.addFreshEntity(this.oneItemEntity);
            this.targetItemEntity.setItem(oldItemStack); // old item entity gets stack-1 items
        }
        else {
            // only 1 item left, just use old entity as new one and give it its item back
            this.targetItemEntity.setItem(itemStackOneItem);
            this.oneItemEntity = this.targetItemEntity;
        }
        return itemStackOneItem;
    }

    @Override
    public void stop() {
        this.dragon.getNavigation().stop();
        this.searchRadius = 0;
        this.targetItemEntity = null;
        this.itemAt = null;
        this.readyForOtherAction = false;
        this.cooldown = cooldownMax;
        this.oneItemEntity = null;
    }
}