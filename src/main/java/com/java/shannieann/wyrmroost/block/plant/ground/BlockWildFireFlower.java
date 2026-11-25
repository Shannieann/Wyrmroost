package com.java.shannieann.wyrmroost.block.plant.ground;

import com.java.shannieann.wyrmroost.block.plant.BlockWyrmPlant;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;


public class BlockWildFireFlower extends BlockWyrmPlant {
    public static final MapCodec<BlockWildFireFlower> CODEC = simpleCodec(BlockWildFireFlower::new);
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    
    //BURNING
    private static final int BURN_TICKS = 40;
    private static final int IGNITE_SECONDS = 3;

    //DROPS
    private static final int MIN_FLOWERS = 1;
    private static final int MAX_FLOWERS = 2;

    //PARTICLES
    private static final int FLAME_CHANCE = 2;

    public BlockWildFireFlower(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
    }

    @Override
    public MapCodec<BlockWildFireFlower> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.is(Items.FLINT_AND_STEEL) || state.getValue(LIT)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(LIT, true), 3);
            level.scheduleTick(pos, this, BURN_TICKS);
            stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }
        level.setBlock(pos, state.setValue(LIT, false), 3);
        int flowers = MIN_FLOWERS + random.nextInt(MAX_FLOWERS - MIN_FLOWERS + 1);
        popResource(level, pos, new ItemStack(this, flowers));
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (state.getValue(LIT) && !entity.fireImmune()) {
            entity.igniteForSeconds(IGNITE_SECONDS);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT) && random.nextInt(FLAME_CHANCE) == 0) {
            level.addParticle(ParticleTypes.FLAME, pos.getX() + 0.3D + random.nextDouble() * 0.4D, pos.getY() + 0.4D + random.nextDouble() * 0.4D,
                    pos.getZ() + 0.3D + random.nextDouble() * 0.4D, 0.0D, 0.02D, 0.0D);
        }
    }
}
