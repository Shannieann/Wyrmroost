package com.github.shannieann.wyrmroost.entity.dragon.ai.goals;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WRGetDroppedFoodGoal extends Goal {
    // data vars
    protected final WRDragonEntity dragon;
    protected int searchRadius;
    protected BlockPos itemAt;
    protected ItemEntity targetItemStack;
    protected ItemEntity oneItem;
    protected final Predicate<ItemEntity> itemFilter;
    // control vars
    protected boolean eatOnPickup = true;
    protected boolean readyForOtherAction = false;
    // cooldown vars
    protected int cooldown = 50;
    private static final int cooldownMax = 50; // only search for food every 2.5 seconds, avoid lag

    public WRGetDroppedFoodGoal(WRDragonEntity dragon, int searchRadius, boolean eatOnPickup) {
        this.dragon = dragon;
        if (this.dragon.isTame() && this.dragon.hasRestriction()) {
            this.searchRadius = Math.min(searchRadius, (int) this.dragon.getRestrictRadius());
        } else {
            this.searchRadius = searchRadius;
        }
        this.eatOnPickup = eatOnPickup;
        this.itemFilter = this::isAcceptedItem;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private boolean isAcceptedItem(ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        if (stack.isEmpty()) return false;
        return this.dragon.isFood(stack);
    }

    private List<ItemEntity> findDroppedItems() {
        Level level = this.dragon.level;
        AABB searchBox = this.dragon.getBoundingBox().inflate(this.searchRadius, this.searchRadius, this.searchRadius);
        return level.getEntitiesOfClass(ItemEntity.class, searchBox, this.itemFilter);
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        if ((this.eatOnPickup && this.dragon.getEatingCooldown() > 0)
            || this.dragon.getTarget() != null
            || this.dragon.isImmobile()
            || this.dragon.isRiding()
            || this.dragon.getPassengers().size() > 0
            || (this.dragon.isTame() && this.dragon.getSitting()))
        {
            return false;
        }

        List<ItemEntity> droppedItems = findDroppedItems();
        if (droppedItems.isEmpty()) {
            return false;
        }

        this.targetItemStack = droppedItems.get(0);
        if (this.dragon.hasRestriction() && this.dragon.getRestrictRadius() < this.dragon.distanceToSqr(this.targetItemStack)) {
            return false;
        }
        this.itemAt = this.targetItemStack.blockPosition();
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.dragon.getEatingCooldown() > 0
            || this.dragon.getTarget() != null
            || this.dragon.isImmobile()
            || this.dragon.isRiding()
            || this.dragon.getPassengers().size() > 0)
        {
            return false;
        }
        if (this.itemAt != null) {
            AABB checkBox = new AABB(this.itemAt).inflate(1, 1, 1);
            List<ItemEntity> itemsAtPos = this.dragon.level.getEntitiesOfClass(ItemEntity.class, checkBox, this.itemFilter);
            return !itemsAtPos.isEmpty();
        }
        return false;
    }

    @Override
    public void start() {
        if (this.targetItemStack != null) {
            this.itemAt = this.targetItemStack.blockPosition();
            this.dragon.getNavigation().moveTo(this.targetItemStack, 1.1D);
        }
    }

    @Override
    public void tick() {
        if (this.targetItemStack != null && this.targetItemStack.isAlive()) {
            this.dragon.getNavigation().moveTo(this.targetItemStack, 1.1D);
            double d2 = this.dragon.distanceToSqr(this.targetItemStack);
            // standard pickup radius
            if (d2 < 1.0D) {
                if (!this.targetItemStack.getItem().isEmpty() && this.isAcceptedItem(this.targetItemStack)) {
                    if (this.eatOnPickup) {
                        if (this.dragon.level.isClientSide) {
                            return;
                        }
                        if (this.dragon.getEatingCooldown() < 0) {
                            ItemStack itemStackOneItem = splitTargetItemStack();
                            this.dragon.eat(this.dragon.level, itemStackOneItem);
                            this.oneItem.discard();
                        }
                        stop();
                        return;
                    } else {
                        this.readyForOtherAction = true;
                    }
                }
            } else if ((! this.dragon.getNavigation().isInProgress())) {
                // Pathfinding sucks, shove closer
                this.dragon.getNavigation().moveTo(this.targetItemStack, 1.1D);
                Vec3 direction = this.targetItemStack.position().subtract(this.dragon.position()).normalize();
                direction = new Vec3(direction.x, 0, direction.z); // no vertical push so it doesn't get stuck climbing
                this.dragon.setDeltaMovement(direction.scale(0.1));
            }
        } else {
            // Item no longer exists, find a new one
            List<ItemEntity> droppedItems = findDroppedItems();
            if (!droppedItems.isEmpty()) {
                this.targetItemStack = droppedItems.get(0);
                this.itemAt = this.targetItemStack.blockPosition();
            } else {
                stop();
            }
        }
    }

    protected ItemStack splitTargetItemStack() {
        ItemStack itemStack = this.targetItemStack.getItem();
        ItemStack itemStackOneItem = itemStack.split(1);
        this.targetItemStack.setItem(itemStack);

        if (itemStack.getCount() > 0) {
            // Make new entity for new stack if old one has items left
            this.oneItem = new ItemEntity(this.dragon.level, this.targetItemStack.getX(), this.targetItemStack.getY(), this.targetItemStack.getZ(), itemStackOneItem);
            this.dragon.level.addFreshEntity(this.oneItem);
        }
        else {
            // otherwise, discard old entity
            this.targetItemStack.discard();
        }
        return itemStackOneItem;
    }

    @Override
    public void stop() {
        this.dragon.getNavigation().stop();
        this.targetItemStack = null;
        this.itemAt = null;
        this.readyForOtherAction = false;
        this.cooldown = cooldownMax;
        this.oneItem = null;

    }
}