package com.java.shannieann.wyrmroost.block.plant.ground;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;


public class BlockWyrmMushroom extends MushroomBlock {

    public BlockWyrmMushroom(Properties properties) {
        super(null, properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState ground = level.getBlockState(pos.below());
        return ground.is(BlockTags.SUPPORTS_VEGETATION) || ground.is(BlockTags.OVERRIDES_MUSHROOM_LIGHT_REQUIREMENT);
    }

    //ToDo: Configurable
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return false;
    }
}
