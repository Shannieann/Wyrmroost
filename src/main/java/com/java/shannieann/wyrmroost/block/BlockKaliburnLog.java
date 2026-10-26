package com.java.shannieann.wyrmroost.block;

import com.java.shannieann.wyrmroost.registry.WyrmroostBlockRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BlockKaliburnLog extends RotatedPillarBlock {
    public static final BooleanProperty NATURAL = BooleanProperty.create("natural");

    //FLAMES
    private static final float IGNITE_CHANCE = 0.35F;
    private static final int MIN_FLAMES = 2;
    private static final int MAX_FLAMES = 5;
    private static final int FLAME_SPREAD = 2;
    private static final int MIN_FLAME_TICKS = 40;
    private static final int MAX_FLAME_TICKS = 120;

    public BlockKaliburnLog(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(NATURAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NATURAL);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(NATURAL);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() >= IGNITE_CHANCE) {
            return;
        }
        BlockSulphuricFire fire = WyrmroostBlockRegistry.SULPHURIC_FIRE.get();
        int flames = MIN_FLAMES + random.nextInt(MAX_FLAMES - MIN_FLAMES + 1);

        for (int attempt = 0; attempt < MAX_FLAMES * 4 && flames > 0; attempt++) {
            BlockPos log = pos.offset(random.nextInt(FLAME_SPREAD * 2 + 1) - FLAME_SPREAD,
                    random.nextInt(FLAME_SPREAD * 2 + 1) - FLAME_SPREAD,
                    random.nextInt(FLAME_SPREAD * 2 + 1) - FLAME_SPREAD);
            BlockState logState = level.getBlockState(log);
            if (!logState.is(this) || !logState.getValue(NATURAL)) {
                continue;
            }

            BlockPos firePos = log.relative(Direction.values()[random.nextInt(6)]);
            if (!level.getBlockState(firePos).isAir()) {
                continue;
            }
            BlockState fireState = fire.getStateFor(level, firePos);
            if (!fireState.canSurvive(level, firePos)) {
                continue;
            }

            level.setBlock(firePos, fireState, 3);
            level.scheduleTick(firePos, fire, MIN_FLAME_TICKS + random.nextInt(MAX_FLAME_TICKS - MIN_FLAME_TICKS + 1));
            flames--;
        }
    }
}
