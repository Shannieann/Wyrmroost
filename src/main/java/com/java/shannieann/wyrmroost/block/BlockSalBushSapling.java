package com.java.shannieann.wyrmroost.block;

import com.java.shannieann.wyrmroost.Wyrmroost;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;

public class BlockSalBushSapling extends SaplingBlock {
    //TODO: Implementation
    private static final ResourceKey<Feature> SAL_BUSH_FEATURE =
            ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(Wyrmroost.MODID, "sal_bush"));

    //TODO: Confirm method correct
    public static final TreeGrower SAL_BUSH_GROWER = new TreeGrower(Wyrmroost.MODID + ":sal_bush",
            WeightedList.of(),
            WeightedList.of(),
            WeightedList.of(),
            SAL_BUSH_FEATURE);

    public BlockSalBushSapling(Properties properties) {
        super(SAL_BUSH_GROWER, properties);
    }

    //TODO: Implementation
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        return false;
    }
}
