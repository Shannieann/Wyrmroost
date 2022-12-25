package com.github.shannieann.wyrmroost.client.model.effect;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entities.effect.EffectLightningSphere;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelLightningSphere extends AnimatedGeoModel<EffectLightningSphere>
{
    private static final ResourceLocation MODEL_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/effect/lightning_sphere/lightning_sphere.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/effect/lightning_sphere/lightning_sphere.png");
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/effect/lightning_sphere/lightning_sphere.json");

    @Override
    public ResourceLocation getModelLocation(EffectLightningSphere object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureLocation(EffectLightningSphere object) {
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EffectLightningSphere animatable) {
        return ANIMATION_RESOURCE;
    }
}