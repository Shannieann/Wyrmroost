package com.java.shannieann.wyrmroost.block.plant.ground;

import com.java.shannieann.wyrmroost.block.plant.BlockWyrmPlant;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BlockSidewPitcher extends BlockWyrmPlant {
    public static final MapCodec<BlockSidewPitcher> CODEC = simpleCodec(BlockSidewPitcher::new);
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");
    private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    //BUBBLES
    private static final int FULL_BUBBLES = 12;
    private static final int EMPTY_BUBBLES = 4;
    private static final int PATCH_BUBBLES = 6;
    private static final int PATCH_RADIUS = 2;
    private static final double BUBBLE_SPREAD = 0.25D;
    private static final double BUBBLE_SPEED = 0.05D;

    //DRIPS
    private static final int SPLASH_CHANCE = 6;

    //TODO: Growth stages
    public BlockSidewPitcher(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FILLED, false));
    }

    @Override
    public MapCodec<BlockSidewPitcher> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FILLED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    //ToDo Make a BlockEntity? Make it so tubes in tech mods can pull from it?

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        boolean filled = state.getValue(FILLED);
        if (stack.is(Items.WATER_BUCKET) && !filled) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(FILLED, true), 3);
                if (!player.isCreative()) {
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.is(Items.BUCKET) && filled) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(FILLED, false), 3);
                if (!player.isCreative()) {
                    stack.shrink(1);
                    ItemStack water = new ItemStack(Items.WATER_BUCKET);
                    if (stack.isEmpty()) {
                        player.setItemInHand(hand, water);
                    } else if (!player.getInventory().add(water)) {
                        player.drop(water, false);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel) level;
        bubble(serverLevel, pos, state.getValue(FILLED) ? FULL_BUBBLES : EMPTY_BUBBLES);
        for (BlockPos nearby : BlockPos.betweenClosed(pos.offset(-PATCH_RADIUS, -1, -PATCH_RADIUS), pos.offset(PATCH_RADIUS, 1, PATCH_RADIUS))) {
            if (nearby.equals(pos)) {
                continue;
            }
            BlockState other = level.getBlockState(nearby);
            if (other.is(this) && other.getValue(FILLED)) {
                bubble(serverLevel, nearby, PATCH_BUBBLES);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(FILLED) && random.nextInt(SPLASH_CHANCE) == 0) {
            level.addParticle(ParticleTypes.SPLASH, pos.getX() + 0.25D + random.nextDouble() * 0.5D, pos.getY() + 0.95D,
                    pos.getZ() + 0.25D + random.nextDouble() * 0.5D, 0.0D, 0.0D, 0.0D);
        }
    }

    private static void bubble(ServerLevel level, BlockPos pos, int count) {
        level.sendParticles(ParticleTypes.BUBBLE_POP, pos.getX() + 0.5D, pos.getY() + 0.95D, pos.getZ() + 0.5D,
                count, BUBBLE_SPREAD, 0.1D, BUBBLE_SPREAD, BUBBLE_SPEED);
    }
}
