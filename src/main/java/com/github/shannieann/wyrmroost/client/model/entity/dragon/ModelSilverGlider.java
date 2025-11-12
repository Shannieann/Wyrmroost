package com.github.shannieann.wyrmroost.client.model.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.EntitySilverGlider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static com.github.shannieann.wyrmroost.util.WRModUtils.TEXTURE_FOLDER;

public class ModelSilverGlider<T extends EntitySilverGlider> extends AnimatedGeoModel<T> {

    private static final ResourceLocation MODEL_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/dragon/silver_glider/silver_glider.geo.json");
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/silver_glider/silver_glider.animation.json");

    @Override
    public ResourceLocation getModelLocation(T object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureLocation(EntitySilverGlider dragon) {
        if (dragon.isAdult()) {
            return new ResourceLocation(Wyrmroost.MOD_ID, TEXTURE_FOLDER + "silver_glider/" + dragon.getVariant() + ".png");
        }
        if (dragon.isGolden()) {
            return new ResourceLocation(Wyrmroost.MOD_ID, TEXTURE_FOLDER + "silver_glider/spe_baby.png");
        }
        return new ResourceLocation(Wyrmroost.MOD_ID, TEXTURE_FOLDER + "silver_glider/baby.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(T animatable) {
        return ANIMATION_RESOURCE;
    }

}