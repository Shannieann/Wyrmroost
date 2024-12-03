package com.github.shannieann.wyrmroost.entity.projectile;


import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class DragonProjectileEntity extends Projectile implements IEntityAdditionalSpawnData
{
    @Nullable // Potentially if the dragon is unloaded, or is not synced yet.
    public WRDragonEntity shooter;
    public Vec3 acceleration;
    public float growthRate = 1f;
    public int life;
    public boolean hasCollided;

    protected DragonProjectileEntity(EntityType<? extends Projectile> type, Level level)
    {
        super(type, level);
    }

    public DragonProjectileEntity(EntityType<? extends DragonProjectileEntity> type, WRDragonEntity shooter, Vec3 position, Vec3 direction) {
        super(type, shooter.level);

        direction = direction.add(random.nextGaussian() * getAccelerationOffset(), random.nextGaussian() * getAccelerationOffset(), random.nextGaussian() * getAccelerationOffset());
        double length = direction.length();
        this.acceleration = new Vec3(direction.x / length * getMotionFactor(), direction.y / length * getMotionFactor(), direction.z / length * getMotionFactor());

        this.shooter = shooter;
        this.life = 50;

        setDeltaMovement(acceleration);
        position = position.add(getDeltaMovement()).subtract(0, getBbHeight() / 2, 0);

        moveTo(position.x, position.y, position.z, getYRot(), getXRot());
    }

    @Override
    public void tick() {
        if ((!level.isClientSide && (!shooter.isAlive() || tickCount > life || tickCount > getMaxLife())) || !level.hasChunkAt(blockPosition()))
        {
            remove(RemovalReason.DISCARDED);
            return;
        }

        super.tick();
        if (growthRate != 1) refreshDimensions();

        switch (getEffectType())
        {
            case RAYTRACE:
            {
                HitResult rayTrace = ProjectileUtil.getHitResult(this, this::canImpactEntity);
                if (rayTrace.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, rayTrace))
                    hit(rayTrace);
                break;
            }
            case COLLIDING:
            {
                AABB box = getBoundingBox().inflate(0.05);
                for (Entity entity : level.getEntities(this, box, this::canImpactEntity))
                    onEntityImpact(entity);

                Vec3 position = position();
                Vec3 end = position.add(getDeltaMovement());
                BlockHitResult rtr = level.clip(new ClipContext(position, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                if (rtr.getType() != HitResult.Type.MISS) onBlockImpact(rtr.getBlockPos(), rtr.getDirection());
            }
            default:
                break;
        }

        Vec3 motion = getDeltaMovement();
        if (!isNoGravity()) setDeltaMovement(motion = motion.add(0, -0.05, 0));
        double x = getX() + motion.x;
        double y = getY() + motion.y;
        double z = getZ() + motion.z;

        if (isInWater())
        {
            setDeltaMovement(motion.scale(0.95f));
            for (int i = 0; i < 4; ++i)
                level.addParticle(ParticleTypes.BUBBLE, getX() * 0.25d, getY() * 0.25d, getZ() * 0.25D, motion.x, motion.y, motion.z);
        }
        absMoveTo(x, y, z);
    }

    public boolean canImpactEntity(Entity entity) {
        if (entity == shooter) return false;
        if (!entity.isAlive()) return false;
        if (!(entity instanceof LivingEntity)) return false;
        if (entity.getRootVehicle() == shooter) return false;
        if (entity.isSpectator() || !entity.isPickable() || entity.noPhysics) return false;
        return shooter != null && !entity.isAlliedTo(shooter);
    }

    public void hit(HitResult result) {
        HitResult.Type type = result.getType();
        if (type == HitResult.Type.BLOCK)
        {
            final BlockHitResult brtr = (BlockHitResult) result;
            onBlockImpact(brtr.getBlockPos(), brtr.getDirection());
        }
        else if (type == HitResult.Type.ENTITY) onEntityImpact(((EntityHitResult) result).getEntity());
    }

    public void onEntityImpact(Entity entity){
    }

    public void onBlockImpact(BlockPos pos, Direction direction) {
    }

    @Override
    public void setDeltaMovement(Vec3 motionIn) {
        super.setDeltaMovement(motionIn);
        ProjectileUtil.rotateTowardsMovement(this, 1);
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn) {
        if (growthRate == 1) return getType().getDimensions();
        float size = Math.min(getBbWidth() * growthRate, 2.25f);
        return EntityDimensions.scalable(size, size);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = getBoundingBox().getSize() * 4;
        if (Double.isNaN(d0)) d0 = 4;
        d0 *= 64;
        return distance < d0 * d0;
    }

    public DamageSource getDamageSource(String name) {
        return new IndirectEntityDamageSource(name, this, shooter).setProjectile().setScalesWithDifficulty();
    }

    protected EffectType getEffectType() {
        return EffectType.NONE;
    }

    protected float getMotionFactor() {
        return 0.95f;
    }

    protected double getAccelerationOffset() {
        return 0.1;
    }

    protected int getMaxLife() {
        return 150;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public float getBrightness() {
        return 1f;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public float getPickRadius() {
        return getBbWidth();
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override // Does not Serialize
    protected void readAdditionalSaveData(CompoundTag compound)
    {
    }

    @Override // Does not Serialize
    protected void addAdditionalSaveData(CompoundTag compound)
    {
    }

    @Override
    public Packet<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buf)
    {
        buf.writeInt(shooter.getId());
        buf.writeFloat(growthRate);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buf)
    {
        this.shooter = (WRDragonEntity) level.getEntity(buf.readInt());
        this.growthRate = buf.readFloat();
    }

    public enum EffectType
    {
        NONE,
        RAYTRACE,
        COLLIDING
    }
}
