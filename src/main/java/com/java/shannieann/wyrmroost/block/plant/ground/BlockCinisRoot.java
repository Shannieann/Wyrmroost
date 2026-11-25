package com.java.shannieann.wyrmroost.block.plant.ground;

import com.java.shannieann.wyrmroost.registry.WyrmroostPlantItemRegistry;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;


public class BlockCinisRoot extends CropBlock {
    public static final MapCodec<BlockCinisRoot> CODEC = simpleCodec(BlockCinisRoot::new);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    public static final int AGE_STAGES = 4;
    private static final int MAX_AGE = AGE_STAGES - 1;

    public BlockCinisRoot(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public MapCodec<BlockCinisRoot> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return WyrmroostPlantItemRegistry.CINIS_ROOT_ITEM.get();
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.FARMLAND) || state.is(BlockTags.SUPPORTS_VEGETATION);
    }

    //TODO: Swap the water check for ash water
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return super.canSurvive(state, level, pos) && hasWaterNearby(level, pos.below());
    }

    public static boolean hasWaterNearby(LevelReader level, BlockPos ground) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (level.getFluidState(ground.relative(direction)).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }
}
