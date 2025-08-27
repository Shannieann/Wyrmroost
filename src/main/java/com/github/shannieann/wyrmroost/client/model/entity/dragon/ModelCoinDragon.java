package com.github.shannieann.wyrmroost.client.model.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.EntityCoinDragon;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelCoinDragon extends AnimatedGeoModel<EntityCoinDragon>
{
    private static final ResourceLocation MODEL_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/dragon/coin_dragon/coin_dragon.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/coin_dragon/body_0.png");
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/coin_dragon/coin_dragon.json");

    @Override
    public ResourceLocation getModelLocation(EntityCoinDragon object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityCoinDragon object) {
        // Use variant-based texture if available
        String variant = object.getVariant();
        if (variant != null && !variant.isEmpty()) {
            try {
                int variantIndex = Integer.parseInt(variant);
                return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/coin_dragon/body_" + variantIndex + ".png");
            } catch (NumberFormatException e) {
                // use default body_0
            }
        }
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityCoinDragon animatable) {
        return ANIMATION_RESOURCE;
    }
}
