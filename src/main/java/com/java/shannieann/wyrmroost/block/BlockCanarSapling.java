package com.java.shannieann.wyrmroost.block;

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

public class BlockCanarSapling extends SaplingBlock {
    //TODO: Implementation
    private static final ResourceKey<ConfiguredFeature<?, ?>> CANAR_FEATURE =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(Wyrmroost.MODID, "canar"));

    //TODO: Confirm method correct
    public static final TreeGrower CANAR_GROWER = new TreeGrower(Wyrmroost.MODID + ":canar", 0.0F,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    public BlockCanarSapling(Properties properties) {
        super(CANAR_GROWER, properties);
    }

    //TODO: Implementation
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return false;
    }
}
