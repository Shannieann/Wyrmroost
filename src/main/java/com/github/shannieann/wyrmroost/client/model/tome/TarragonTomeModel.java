package com.github.shannieann.wyrmroost.client.model.tome;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.items.book.TarragonTomeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class TarragonTomeModel extends AnimatedGeoModel<TarragonTomeItem> {
    private static final ResourceLocation modelResource = Wyrmroost.id("geo/tarragon_tome.geo.json");
    private static final ResourceLocation textureResource = Wyrmroost.id("textures/item/tarragon_tome_model.png");
    //private static final ResourceLocation animationResource = Wyrmroost.id("animations/jackinthebox.animation.json");

    @Override
    public ResourceLocation getModelLocation(TarragonTomeItem object) {
        return modelResource;
    }

    @Override
    public ResourceLocation getTextureLocation(TarragonTomeItem object) {
        return textureResource;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(TarragonTomeItem object) {
        return null;
    }
}

