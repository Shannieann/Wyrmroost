package com.java.shannieann.wyrmroost.block.plant.attached;

import com.java.shannieann.wyrmroost.registry.WyrmroostItemRegistry;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;


public class BlockMesemDrop extends BlockAttachedPlant {
    public static final MapCodec<BlockMesemDrop> CODEC = simpleCodec(BlockMesemDrop::new);
    public static final BooleanProperty RIPE = BooleanProperty.create("ripe");
    private static final VoxelShape[] SHAPES = makeShapes(3.0D, 12.0D);

    //TODO: Double check harvest
    private static final int RIPEN_CHANCE = 8;
    private static final int MIN_DROPS = 1;
    private static final int MAX_DROPS = 2;

    public BlockMesemDrop(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.DOWN).setValue(RIPE, true));
    }

    @Override
    public MapCodec<BlockMesemDrop> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(RIPE);
    }

    @Override
    protected boolean canFace(Direction facing) {
        return facing != Direction.UP;
    }

    @Override
    protected VoxelShape getShapeFor(Direction facing) {
        return SHAPES[facing.get3DDataValue()];
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!state.getValue(RIPE)) {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }
        if (!level.isClientSide()) {
            int drops = MIN_DROPS + level.getRandom().nextInt(MAX_DROPS - MIN_DROPS + 1);
            popResource(level, pos, new ItemStack(WyrmroostItemRegistry.MESEM_GEM_ITEM.get(), drops));
            level.setBlock(pos, state.setValue(RIPE, false), 3);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(RIPE);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(RIPEN_CHANCE) == 0) {
            level.setBlock(pos, state.setValue(RIPE, true), 3);
        }
    }
}