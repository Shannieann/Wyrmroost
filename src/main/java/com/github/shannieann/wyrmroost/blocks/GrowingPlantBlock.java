package com.github.shannieann.wyrmroost.blocks;

/*import net.minecraft.block.*;
import net.minecraft.item.BlockUseOnContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LevelReader;
import net.minecraft.world.Level;
import net.minecraft.world.server.ServerLevel;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

public class GrowingPlantBlock extends AbstractTopPlantBlock
{
    private final Supplier<Block> body;
    private final int maxGrowthHeight;

    public GrowingPlantBlock(Properties properties, Direction dir, int maxGrowthHeight, double growthChance, Supplier<Block> body)
    {
        this(properties, dir, maxGrowthHeight, growthChance, false, body);
    }

    public GrowingPlantBlock(Properties properties, Direction dir, int maxGrowthHeight, double growthChance, boolean waterPlant, Supplier<Block> body)
    {
        super(properties, dir, WeepingVinesBlock.SHAPE, waterPlant, growthChance);
        this.body = body;
        this.maxGrowthHeight = maxGrowthHeight;
    }

    @Override
    public boolean isValidBonemealTarget(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return (!hasMaxHeight() || getHeight((LevelReader) worldIn, pos) < maxGrowthHeight) && super.isValidBonemealTarget(worldIn, pos, state, isClient);
    }

    @Override
    protected boolean canGrowInto(BlockState state)
    {
        return state.isAir();
    }

    @Override
    public void performBonemeal(ServerLevel level, Random rand, BlockPos pos, BlockState state)
    {
        BlockPos.MutableBlockPos mutable = pos.mutable().move(growthDirection);
        int i = 0;
        int amount = getBlocksToGrowWhenBonemealed(rand);
        if (hasMaxHeight()) amount = Math.min(amount, maxGrowthHeight - getHeight(level, pos));

        for (int k = 0; k < amount && canGrowInto(level.getBlockState(mutable)); k++)
        {
            level.setBlockAndUpdate(mutable, state.setValue(AGE, k == maxGrowthHeight - 1? 25 : (i = Math.min(i + 1, 25))));
            mutable.move(growthDirection);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        BlockPos below = pos.relative(growthDirection.getOpposite());
        BlockState belowState = worldIn.getBlockState(below);
        Block belowBlock = belowState.getBlock();

        if (canAttachToBlock(belowBlock))
        {
            if (belowBlock == getHeadBlock() || belowBlock == getBodyBlock() || Block.isFaceFull(belowState.getCollisionShape(worldIn, pos), growthDirection))
                return (!hasMaxHeight() || getHeight(worldIn, pos.relative(growthDirection.getOpposite()), true) + 1 <= maxGrowthHeight);
        }
        return false;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockUseOnContext context)
    {
        if (hasMaxHeight())
        {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos().relative(growthDirection.getOpposite());
            if (getHeight(level, pos, true) + 1 >= maxGrowthHeight) return defaultBlockState().setValue(AGE, 25);
        }
        return super.getStateForPlacement(context);
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(Random rand)
    {
        return PlantBlockHelper.getBlocksToGrowWhenBonemealed(rand);
    }

    @Override
    public Block getBodyBlock()
    {
        return body.get();
    }

    public boolean hasMaxHeight()
    {
        return maxGrowthHeight != 0;
    }

    public int getHeight(LevelReader level, BlockPos pos)
    {
        return getHeight(level, pos, true) + getHeight(level, pos, false) - 1;
    }

    public int getHeight(LevelReader level, BlockPos pos, boolean below)
    {
        Direction dir = below? growthDirection.getOpposite() : growthDirection;
        BlockPos.MutableBlockPos mutable = pos.mutable();
        BlockState state = level.getBlockState(mutable);
        int i = 0;
        for (; i < maxGrowthHeight + 1 && (state.is(getBodyBlock()) || state.is(this)); ++i)
            state = level.getBlockState(mutable.move(dir));
        return i;
    }

    public int getMaxGrowthHeight()
    {
        return maxGrowthHeight;
    }
}*/
