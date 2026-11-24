package com.java.shannieann.wyrmroost.block.tree;

import java.util.Optional;

import com.java.shannieann.wyrmroost.Wyrmroost;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class BlockOseriSapling extends SaplingBlock {
    //TODO: Implementation
    private static final ResourceKey<ConfiguredFeature<?, ?>> OSERI_FEATURE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(Wyrmroost.MODID, "oseri"));

    //TODO: Confirm method correct
    public static final TreeGrower OSERI_GROWER = new TreeGrower(Wyrmroost.MODID + ":oseri", 0.0F,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    public BlockOseriSapling(Properties properties) {
        super(OSERI_GROWER, properties);
    }

    //TODO: Implementation
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return false;
    }
}
