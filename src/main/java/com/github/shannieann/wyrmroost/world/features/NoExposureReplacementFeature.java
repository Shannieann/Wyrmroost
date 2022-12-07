package com.github.shannieann.wyrmroost.world.features;

import com.github.shannieann.wyrmroost.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;


public class NoExposureReplacementFeature extends Feature<ReplaceBlockConfiguration>
{
    public NoExposureReplacementFeature()
    {
        super(ReplaceBlockConfiguration.CODEC);
    }


    private static boolean checkExposure(WorldGenLevel level, BlockPos initialPos)
    {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (Direction direction : ModUtils.DIRECTIONS)
        {
            BlockState state = level.getBlockState(pos.setWithOffset(initialPos, direction));
            if (state.isAir()) return false;
        }
        return true;
    }

    @Override
    public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> cxt) {
        WorldGenLevel level = cxt.level();
        BlockPos pos = cxt.origin();
        ReplaceBlockConfiguration config = cxt.config();
        OreConfiguration.TargetBlockState state = config.targetStates.get(0);

        if (level.getBlockState(pos).is(state.state.getBlock()) && checkExposure(level, pos))
            level.setBlock(pos, state.state, 2);

        return true;
    }
}
