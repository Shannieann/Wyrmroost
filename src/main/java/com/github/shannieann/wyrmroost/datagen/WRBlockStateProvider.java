package com.github.shannieann.wyrmroost.datagen;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.registry.WRBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class WRBlockStateProvider extends BlockStateProvider {
    public WRBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Wyrmroost.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockItem(WRBlocks.BLUE_GEODE_BLOCK.get(),cubeAll(WRBlocks.BLUE_GEODE_BLOCK.get()));
        simpleBlockItem(WRBlocks.BLUE_GEODE_ORE.get(),cubeAll(WRBlocks.BLUE_GEODE_ORE.get()));
        simpleBlockItem(WRBlocks.RED_GEODE_BLOCK.get(),cubeAll(WRBlocks.RED_GEODE_BLOCK.get()));
        simpleBlockItem(WRBlocks.RED_GEODE_ORE.get(),cubeAll(WRBlocks.RED_GEODE_ORE.get()));
        simpleBlockItem(WRBlocks.PURPLE_GEODE_BLOCK.get(),cubeAll(WRBlocks.PURPLE_GEODE_BLOCK.get()));
        simpleBlockItem(WRBlocks.PURPLE_GEODE_ORE.get(),cubeAll(WRBlocks.PURPLE_GEODE_ORE.get()));
        simpleBlockItem(WRBlocks.PLATINUM_BLOCK.get(),cubeAll(WRBlocks.PLATINUM_BLOCK.get()));
        simpleBlockItem(WRBlocks.PLATINUM_ORE.get(),cubeAll(WRBlocks.PLATINUM_ORE.get()));
    }
}
