package com.github.wolfshotz.wyrmroost.world.features;

import com.github.wolfshotz.wyrmroost.registry.WRBlocks;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Random;

/*public class MossVinesFeature extends Feature<NoneFeatureConfiguration>
{
    public MossVinesFeature()
    {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, Random random, BlockPos pos, NoneFeatureConfiguration feature)
    {
        BlockPos.MutableBlockPos mutable = pos.mutable();

        int maxHeight = level.getMaxBuildHeight();
        for (int i = 64; i < maxHeight; ++i)
        {
            mutable.setWithOffset(pos, Mafs.nextInt(random, 4), i, Mafs.nextInt(random, 4));
            if (level.isEmptyBlock(mutable))
            {
                for (Direction direction : ModUtils.DIRECTIONS)
                {
                    if (direction != Direction.DOWN && acceptableNeighbor(level, mutable.relative(direction), direction))
                    {
                        level.setBlock(mutable, WRBlocks.MOSS_VINE.get()
                                .setValue(VineBlock.getPropertyForFace(direction), true), 2);
                        break;
                    }
                }
            }
        }

        return true;
    }

    static boolean acceptableNeighbor(WorldGenLevel level, BlockPos pos, Direction dir)
    {
        BlockState state = level.getBlockState(pos);
        return state.getMaterial().isSolidBlocking() && Block.isFaceFull(state.getCollisionShape(level, pos), dir.getOpposite());
    }
}*/
