package com.github.shannieann.wyrmroost.client.model.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.EntityCanariWyvern;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static com.github.shannieann.wyrmroost.util.WRModUtils.TEXTURE_FOLDER;

public class ModelCanariWyvern<T extends EntityCanariWyvern> extends AnimatedGeoModel<T> {

    private static final ResourceLocation MODEL_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/dragon/canari_wyvern/canari_wyvern.geo.json");
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/canari_wyvern/canari_wyvern.animation.json");

    @Override
    public ResourceLocation getModelLocation(T object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCanariWyvern dragon) {
        if (dragon.isHatchling()) {
            return new ResourceLocation(Wyrmroost.MOD_ID, TEXTURE_FOLDER + "canari_wyvern/baby.png");
        }
        else if (dragon.hasCustomName() && dragon.getCustomName().getContents().equalsIgnoreCase("lady")){
            return new ResourceLocation(Wyrmroost.MOD_ID, TEXTURE_FOLDER + "canari_wyvern/lady.png");
        }
        else if (dragon.hasCustomName() && dragon.getCustomName().getContents().equalsIgnoreCase("rudy")){
            return new ResourceLocation(Wyrmroost.MOD_ID, TEXTURE_FOLDER + "canari_wyvern/rudy.png");
        }
        return new ResourceLocation(Wyrmroost.MOD_ID, TEXTURE_FOLDER + "canari_wyvern/body_" + (dragon.getGender() == 0 ? "f" : "m") + dragon.getVariant() + ".png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(T animatable) {
        return ANIMATION_RESOURCE;
    }

}