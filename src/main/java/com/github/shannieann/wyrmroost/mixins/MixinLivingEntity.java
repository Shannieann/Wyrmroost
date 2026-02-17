package com.github.shannieann.wyrmroost.mixins;

import com.github.shannieann.wyrmroost.api.IDragonVehicle;
import com.github.shannieann.wyrmroost.entity.dragon.EntitySilverGlider;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.util.EntityAccessHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Adds dragon passenger state and Silver Glider fall-flying to LivingEntity.
 * Only Players use dragon passengers and fall-flying in practice.
 */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements IDragonVehicle {

    @Unique
    private ImmutableList<UUID> wyrmroost$dragonPassengers = ImmutableList.of();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void wyrmroost$onConstructor(CallbackInfo ci) {
        this.wyrmroost$dragonPassengers = ImmutableList.of();
    }

    // --------------- IDragonVehicle ---------------

    @Override
    public void addDragonPassenger(WRDragonEntity dragon) {
        UUID id = dragon.getUUID();
        if (this.wyrmroost$dragonPassengers.size() < 3 && !this.wyrmroost$dragonPassengers.contains(id)) {
            if (this.wyrmroost$dragonPassengers.isEmpty()) {
                this.wyrmroost$dragonPassengers = ImmutableList.of(id);
            } else {
                List<UUID> list = Lists.newArrayList(this.wyrmroost$dragonPassengers);
                list.add(id);
                this.wyrmroost$dragonPassengers = ImmutableList.copyOf(list);
            }
        }
    }

    @Override
    public void removeDragonPassenger(WRDragonEntity dragon) {
        UUID dragonId = dragon.getUUID();
        if (this.wyrmroost$dragonPassengers.size() == 1 && this.wyrmroost$dragonPassengers.get(0).equals(dragonId)) {
            this.wyrmroost$dragonPassengers = ImmutableList.of();
        } else {
            this.wyrmroost$dragonPassengers = ImmutableList.copyOf(
                this.wyrmroost$dragonPassengers.stream().filter(id -> !id.equals(dragonId)).toList());
        }
    }

    @Override
    public boolean hasDragonPassengers() {
        return !wyrmroost$dragonPassengers.isEmpty();
    }

    @Override
    public boolean hasDragonPassenger(WRDragonEntity dragon) {
        return this.wyrmroost$dragonPassengers.contains(dragon.getUUID());
    }

    @Override
    public List<WRDragonEntity> getDragonPassengers() {
        List<WRDragonEntity> result = new ArrayList<>();
        for (int i = 0; i < wyrmroost$dragonPassengers.size(); i++) {
            WRDragonEntity e = getDragonPassenger(i);
            if (e != null) result.add(e);
        }
        return result;
    }

    @Override
    public WRDragonEntity getDragonPassenger(int position) {
        if (position < 0 || position >= 3 || position >= wyrmroost$dragonPassengers.size()) {
            return null;
        }
        UUID uuid = wyrmroost$dragonPassengers.get(position);
        Entity self = (Entity) (Object) this;
        // Dragons are never in player.getPassengers(); they use WRDragonEntity posOnPlayer (custom riding). Resolve by UUID from level.
        Level level = EntityAccessHelper.getLevel(self);
        AABB box = self.getBoundingBox().inflate(32);
        for (WRDragonEntity e : level.getEntitiesOfClass(WRDragonEntity.class, box)) {
            if (e.getUUID().equals(uuid)) {
                return e;
            }
        }
        return null;
    }

    // --------------- Save/load ---------------

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void wyrmroost$addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        ListTag listTag = new ListTag();
        for (UUID uuid : this.wyrmroost$dragonPassengers) {
            CompoundTag uuidTag = new CompoundTag();
            uuidTag.putUUID("id", uuid);
            listTag.add(uuidTag);
        }
        tag.put("wyrmroost_dragon_passengers", listTag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void wyrmroost$readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("wyrmroost_dragon_passengers", Tag.TAG_LIST)) {
            ListTag uuidList = tag.getList("wyrmroost_dragon_passengers", Tag.TAG_COMPOUND);
            List<UUID> temp = new ArrayList<>();
            for (int i = 0; i < uuidList.size(); i++) {
                CompoundTag uuidTag = uuidList.getCompound(i);
                if (uuidTag.hasUUID("id")) {
                    temp.add(uuidTag.getUUID("id"));
                }
            }
            this.wyrmroost$dragonPassengers = ImmutableList.copyOf(temp);
        }
    }

    // --------------- Fall-flying (Silver Glider elytra) ---------------

    @Inject(method = "updateFallFlying", at = @At("HEAD"), cancellable = true)
    private void wyrmroost$onUpdateFallFlying(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Player) || !hasDragonPassengers()) {
            return;
        }

        Entity selfEntity = (Entity) (Object) this;
        boolean flag = EntityAccessHelper.getSharedFlag(selfEntity, 7);
        boolean shouldStopGliding = self.isOnGround() || self.isSwimming() || self.getDeltaMovement().length() <= 0.1;

        // Don't use Silver Glider elytra when player has creative flight active
        if (self instanceof Player player && player.getAbilities().flying) {
            flag = false;
        } else if (flag && !shouldStopGliding && !selfEntity.isPassenger() && !self.hasEffect(MobEffects.LEVITATION)) {
            List<WRDragonEntity> passengers = getDragonPassengers();
            boolean silverGliderFlying = false;
            for (WRDragonEntity dragon : passengers) {
                if (dragon instanceof EntitySilverGlider glider && glider.isAdult()) {
                    silverGliderFlying = glider.silverGliderFlightTick(self);
                    break;
                }
            }
            if (silverGliderFlying) {
                flag = true;
            } else {
                ItemStack itemstack = self.getItemBySlot(EquipmentSlot.CHEST);
                flag = !itemstack.isEmpty() && itemstack.canElytraFly(self);
            }
        } else {
            flag = false;
        }

        EntityAccessHelper.setSharedFlag(selfEntity, 7, flag);

        ci.cancel();
    }
}
