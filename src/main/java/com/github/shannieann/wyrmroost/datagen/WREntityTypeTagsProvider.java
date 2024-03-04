package com.github.shannieann.wyrmroost.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;

import static com.github.shannieann.wyrmroost.registry.WREntityTypes.GEODE_TIPPED_ARROW;

public class WREntityTypeTagsProvider extends EntityTypeTagsProvider {
    public WREntityTypeTagsProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void addTags()
    {
        tag(EntityTypeTags.ARROWS).add(GEODE_TIPPED_ARROW.get());
    }
}
