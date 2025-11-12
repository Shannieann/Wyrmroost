package com.github.shannieann.wyrmroost.client.model.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.EntityCoinDragon;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelCoinDragon extends AnimatedGeoModel<EntityCoinDragon>
{
    private static final ResourceLocation MODEL_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/dragon/coin_dragon/coin_dragon.geo.json");
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/coin_dragon/coin_dragon.animation.json");

    @Override
    public ResourceLocation getModelLocation(EntityCoinDragon object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCoinDragon object) {
        return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/coin_dragon/coin_dragon_" + object.getVariant() + ".png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityCoinDragon animatable) {
        return ANIMATION_RESOURCE;
    }
}
