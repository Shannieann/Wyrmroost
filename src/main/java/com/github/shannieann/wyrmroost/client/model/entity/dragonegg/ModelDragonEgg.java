package com.github.shannieann.wyrmroost.client.model.entity.dragonegg;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon_egg.WRDragonEggEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelDragonEgg extends AnimatedGeoModel<WRDragonEggEntity> {
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/royal_red/royal_red.json");

    @Override
    public ResourceLocation getModelLocation(WRDragonEggEntity egg) {
        String containedDragon = egg.getContainedDragon();
        String dragonName = containedDragon.contains(":") ? containedDragon.substring(containedDragon.indexOf(':') + 1) : "";
        String modelLocation = "geo/entity/dragon_egg/" + dragonName + "_egg.geo.json";
        return new ResourceLocation(Wyrmroost.MOD_ID, modelLocation);
    }
    @Override
    public ResourceLocation getTextureLocation(WRDragonEggEntity egg) {
        String containedDragon = egg.getContainedDragon();
        String dragonName = containedDragon.contains(":") ? containedDragon.substring(containedDragon.indexOf(':') + 1) : "";
        String textureLocation = "textures/entity/dragon_egg/" + dragonName + "_egg.png";
        return new ResourceLocation(Wyrmroost.MOD_ID, textureLocation);
    }

    @Override
    public ResourceLocation getAnimationFileLocation(WRDragonEggEntity egg) {
        return ANIMATION_RESOURCE;
    }
}