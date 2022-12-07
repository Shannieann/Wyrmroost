package com.github.shannieann.wyrmroost.world.features;

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
