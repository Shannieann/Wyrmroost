package com.github.shannieann.wyrmroost.datagen;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.registry.WRBlocks;
import com.github.shannieann.wyrmroost.registry.WRItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class WRBlockTagsProvider extends BlockTagsProvider {
    public WRBlockTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, Wyrmroost.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(){
        //this.tags(WRTags.Tags.whatever).add(items/blocks)
        tag(BlockTags.NEEDS_DIAMOND_TOOL).add(WRBlocks.PURPLE_GEODE_ORE.get());
        tag(BlockTags.NEEDS_DIAMOND_TOOL).add(WRBlocks.PURPLE_GEODE_BLOCK.get());
        tag(BlockTags.NEEDS_DIAMOND_TOOL).add(WRBlocks.RED_GEODE_ORE.get());
        tag(BlockTags.NEEDS_DIAMOND_TOOL).add(WRBlocks.RED_GEODE_BLOCK.get());
        tag(BlockTags.NEEDS_IRON_TOOL).add(WRBlocks.BLUE_GEODE_ORE.get());
        tag(BlockTags.NEEDS_IRON_TOOL).add(WRBlocks.BLUE_GEODE_BLOCK.get());

        //Ore Blocks
        tag(Tags.Blocks.ORES).add(
                WRBlocks.PURPLE_GEODE_ORE.get(),
                WRBlocks.RED_GEODE_ORE.get(),
                WRBlocks.PURPLE_GEODE_ORE.get(),
                WRBlocks.PLATINUM_ORE.get());

        //Storage Blocks
        tag(Tags.Blocks.ORES).add(
                WRBlocks.PURPLE_GEODE_BLOCK.get(),
                WRBlocks.RED_GEODE_BLOCK.get(),
                WRBlocks.BLUE_GEODE_BLOCK.get(),
                WRBlocks.PLATINUM_BLOCK.get());

        //Planks, Logs, etc
        //Check both Forge and Minecraft superclasses
    }
}
