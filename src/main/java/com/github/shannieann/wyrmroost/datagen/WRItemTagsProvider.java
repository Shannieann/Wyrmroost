package com.github.shannieann.wyrmroost.datagen;

import com.github.shannieann.wyrmroost.registry.WRItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

public class WRItemTagsProvider extends ItemTagsProvider {
    public WRItemTagsProvider(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider) {
        super(pGenerator, pBlockTagsProvider);
    }

    @Override
    protected void addTags() {
        //Eggs
        tag(Tags.Items.EGGS).add(WRItems.DRAGON_EGG.get());

        //Piglin
        tag(ItemTags.PIGLIN_LOVED).add(WRItems.GOLD_DRAGON_ARMOR.get());

        //Gems
        tag(Tags.Items.GEMS).add(
                WRItems.BLUE_GEODE.get(),
                WRItems.RED_GEODE.get(),
                WRItems.PURPLE_GEODE.get());

        //Arrows
        tag(ItemTags.ARROWS).add(
                WRItems.BLUE_GEODE_ARROW.get(),
                WRItems.RED_GEODE_ARROW.get(),
                WRItems.PURPLE_GEODE_ARROW.get());

        //
    }
}