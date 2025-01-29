
package com.github.shannieann.wyrmroost.entity.dragon;

import com.github.shannieann.wyrmroost.entity.dragon.interfaces.IBreedable;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRDragonBreedGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRRandomLiftOffGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRAvoidEntityGoal;
import com.github.shannieann.wyrmroost.entity.dragon.ai.goals.WRFollowOwnerGoal;
import com.github.shannieann.wyrmroost.network.packets.SGGlidePacket;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import com.github.shannieann.wyrmroost.util.LerpedFloat;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.manager.AnimationData;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

import static net.minecraft.world.entity.ai.attributes.Attributes.*;


public class EntitySilverGlider extends WRDragonEntity implements IBreedable
{
    //TODO: BOIDS
    /*
    private static final EntitySerializer<EntitySilverGlider> SERIALIZER = WRDragonEntity.SERIALIZER.concat(b -> b
            .track(EntitySerializer.STRING, "Gender", WRDragonEntity::getGender, WRDragonEntity::setGender));


     */
    public final LerpedFloat sitTimer = LerpedFloat.unit();
    public final LerpedFloat flightTimer = LerpedFloat.unit();

    public TemptGoal temptGoal;
    public boolean isGliding; // controlled by player-gliding.

    public EntitySilverGlider(EntityType<? extends WRDragonEntity> dragon, Level level)
    {
        super(dragon, level);
    }

    /*
    @Override
    public EntitySerializer<EntitySilverGlider> getSerializer()
    {
        return SERIALIZER;
    }

     */
    public int attackAnimationVariants(){
        return 0;
    }

    @Override
    public int idleAnimationVariants(){
        return 0;
    }
    @Override
    public boolean speciesCanFly() {
        return true;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        goalSelector.addGoal(3, temptGoal = new TemptGoal(this, 0.8d,  Ingredient.of(ItemTags.FISHES), true));
        goalSelector.addGoal(4, new WRAvoidEntityGoal<>(this, Player.class, 10f, 0.8));
        goalSelector.addGoal(5, new WRDragonBreedGoal(this));
        goalSelector.addGoal(6, new WRFollowOwnerGoal(this));
        goalSelector.addGoal(7, new SwoopGoal());
        //goalSelector.addGoal(8, new WRRandomLiftOffGoal(this, 1));
        goalSelector.addGoal(9, new LookAtPlayerGoal(this, LivingEntity.class, 7f));
        goalSelector.addGoal(10, new RandomLookAroundGoal(this));
    }

    /*
    @Override
    public <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        return null; // Todo Implement animations
    }
    */

    @Override
    public void registerControllers(AnimationData data) {

    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        if (isGliding && !isRiding()) isGliding = false;

        sitTimer.add((isInSittingPose() || getSleeping())? 0.2f : -0.2f);
        sleepTimer.add(getSleeping()? 0.05f : -0.1f);
        flightTimer.add(isUsingFlyingNavigator() || isDiving()? 0.1f : -0.1f);
    }

    @Override
    public boolean speciesCanBeRidden() {
        return false;
    }

    @Override
    public void rideTick()
    {
        super.rideTick();

        if (!(getVehicle() instanceof Player)) return;
        Player player = (Player) getVehicle();
        final boolean FLAG = shouldGlide(player);

        if (level.isClientSide && isGliding != FLAG)
        {
            SGGlidePacket.send(FLAG);
            isGliding = FLAG;
        }

        if (isGliding)
        {
            Vec3 vec3d = player.getLookAngle().scale(0.3);
            player.setDeltaMovement(player.getDeltaMovement().scale(0.6).add(vec3d.x, Math.min(vec3d.y * 2, 0), vec3d.z));
            player.fallDistance = 0;
        }
    }

    @Override
    public void travel(Vec3 vec3d)
    {
        Vec3 look = getLookAngle();
        if (isUsingFlyingNavigator() && look.y < 0) setDeltaMovement(getDeltaMovement().add(0, look.y * 0.25, 0));

        super.travel(vec3d);
    }

    @Override
    public boolean speciesCanWalk() {
        return true;
    }

    /*
    @Override
    public InteractionResult playerInteraction(Player player, InteractionHand hand, ItemStack stack)
    {
        InteractionResult result = super.playerInteraction(player, hand, stack);
        if (result.consumesAction()) return result;

        if (!isTame() && isFood(stack))
        {
            if (!level.isClientSide && (temptGoal.isRunning() || player.isCreative()))
            {
                tame(getRandom().nextDouble() < 0.333, player);
                eat(stack);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        if (isOwnedBy(player) && player.getPassengers().isEmpty() && !player.isShiftKeyDown() && !isFood(stack) && !isLeashed())
        {
            startRiding(player, true);
            setOrderedToSit(false);
            clearAI();
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

     */

    public boolean shouldGlide(Player player)
    {
        if (isBaby()) return false;
        if (!player.jumping) return false;
        if (player.getAbilities().flying) return false;
        if (player.isFallFlying()) return false;
        if (player.isInWater()) return false;
        if (player.getDeltaMovement().y > 0) return false;
        if (isDiving() && !player.isOnGround()) return true;
        return getAltitude() - 1.8 > 4;
    }

    @Override
    public void doSpecialEffects()
    {
        if (getVariant().equals("special") && tickCount % 5 == 0)
        {
            double x = getX() + getRandom().nextGaussian();
            double y = getY() + getRandom().nextDouble();
            double z = getZ() + getRandom().nextGaussian();
            level.addParticle(new DustParticleOptions(new Vector3f(1f, 0.8f, 0), 1f), x, y, z, 0, 0.2f, 0);
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose)
    {
        EntityDimensions size = getType().getDimensions().scale(getScale());
        if (isInSittingPose() || getSleeping()) size = size.scale(1, 0.87f);
        return size;
    }

    @Override
    public String getDefaultVariant() {
        return "yellow";
    }

    @Override
    public String determineVariant()
    {
        if (getRandom().nextDouble() < 0.002) {
            return "special";
        }
        return switch (getRandom().nextInt(3)){
            case 1 -> "green";
            case 2 -> "blue";
            default -> getDefaultVariant();
        };
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return WRSounds.ENTITY_SILVERGLIDER_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return WRSounds.ENTITY_SILVERGLIDER_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return WRSounds.ENTITY_SILVERGLIDER_DEATH.get();
    }

    @Override
    public Vec3 getRidingPosOffset(int passengerIndex)
    {
        return new Vec3(0, 1.81, 0.5d);
    }


    @Override
    public boolean shouldUseFlyingNavigator()
    {
        return isRiding()? isDiving() : super.shouldUseFlyingNavigator();
    }

    @Override
    public int getHeadRotSpeed()
    {
        return 30;
    }

    @Override
    public int getYawRotationSpeed()
    {
        return isUsingFlyingNavigator()? 5 : 75;
    }

    @Override
    public boolean speciesCanSwim() {
        return false;
    }

    public boolean isDiving()
    {
        return isGliding;
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return stack.is(ItemTags.FISHES);
    }

    @Override
    public Vec2 getTomeDepictionOffset() {
        return switch (getVariant()) {
            case "special" -> new Vec2(1, 1);
            default -> new Vec2(0, 1);
        };
    }

    public static boolean getSpawnPlacement(EntityType<EntitySilverGlider> fEntityType, ServerLevelAccessor level, MobSpawnType spawnReason, BlockPos blockPos, Random random)
    {
        if (spawnReason == MobSpawnType.SPAWNER) return true;
        Block block = level.getBlockState(blockPos.below()).getBlock();
        return block == Blocks.AIR || block == Blocks.SAND && level.getRawBrightness(blockPos, 0) > 8;
    }

    @Override
    public Attribute[] getScaledAttributes()
    {
        return new Attribute[] {MAX_HEALTH};
    }

    @Override
    public float ageProgressAmount() {
        return 0;
    }

    @Override
    public float initialBabyScale() {
        return 0;
    }

    public static AttributeSupplier.Builder getAttributeSupplier()
    {
        return Mob.createMobAttributes()
                .add(MAX_HEALTH, 20)
                .add(MOVEMENT_SPEED, 0.23)
                .add(FLYING_SPEED, 0.12);
    }

    @Override
    public InteractionResult breedLogic(Player tamer, ItemStack stack) {
        return null;
    }

    @Override
    public int hatchTime() {
        return 50;
    }

    @Override
    public int getBreedingLimit() {
        return 0;
    }


    /*@Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return null;
    }*/


    public class SwoopGoal extends Goal
    {
        private BlockPos pos;

        public SwoopGoal()
        {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse()
        {
            if (!isUsingFlyingNavigator()) return false;
            if (isRiding()) return false;
            if (getRandom().nextDouble() > 0.001) return false;
            if (level.getFluidState(this.pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockPosition()).below()).isEmpty())
                return false;
            return getY() - pos.getY() > 8;
        }

        @Override
        public boolean canContinueToUse()
        {
            return blockPosition().distSqr(pos) > 8;
        }

        @Override
        public void tick()
        {
            if (getNavigation().isDone()) getNavigation().moveTo(pos.getX(), pos.getY() + 2, pos.getZ(), 1);
            getLookControl().setLookAt(pos.getX(), pos.getY() + 2, pos.getZ());
        }
    }

    public InteractionResult tameLogic (Player tamer, ItemStack stack) {
        return InteractionResult.PASS;
    };

}
