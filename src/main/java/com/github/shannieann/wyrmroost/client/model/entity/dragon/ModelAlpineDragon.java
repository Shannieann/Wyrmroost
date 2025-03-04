package com.github.shannieann.wyrmroost.client.model.entity.dragon;


import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.EntityAlpineDragon;
import com.github.shannieann.wyrmroost.entity.dragon.EntityOverworldDrake;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelAlpineDragon<T extends EntityAlpineDragon> extends AnimatedGeoModel<T> {

    private static final ResourceLocation modelResource = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/dragon/alpine_dragon/alpine_dragon.geo.json");
    private static final ResourceLocation animationResource = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/alpine_dragon/alpine_dragon.json");

    @Override
    public ResourceLocation getModelLocation(T object) {
        if (object.isBaby()){
            return new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/dragon/alpine_dragon/alpine_dragon_baby.geo.json");
        }
        return modelResource;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityAlpineDragon object) {
        if (object.isBaby()){
            return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/alpine_dragon/alpine_dragon_baby.png");
        }
        String variant = object.getVariant();
        String gender = object.getGenderString();
        return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/alpine_dragon/alpine_dragon_" + gender + "_" + variant + ".png");
    }


    @Override
    public ResourceLocation getAnimationFileLocation(T animatable) {
        return animationResource;
    }
}