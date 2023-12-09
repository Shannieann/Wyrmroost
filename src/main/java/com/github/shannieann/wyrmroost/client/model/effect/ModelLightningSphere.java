package com.github.shannieann.wyrmroost.client.model.effect;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entities.effect.EffectLightningSphere;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelLightningSphere extends AnimatedGeoModel<EffectLightningSphere>
{
    private static final ResourceLocation MODEL_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/effect/lightning_sphere/lightning_sphere.geo.json");
    private static final ResourceLocation[] TEXTURE_RESOURCE = new ResourceLocation[] {
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_1.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_2.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_3.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_4.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_5.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_6.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_7.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_8.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_9.png"),
            new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/effect/lightning_nova/lightning_nova_10.png")
    };
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/effect/lightning_sphere/lightning_sphere.json");

    @Override
    public ResourceLocation getModelLocation(EffectLightningSphere object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureLocation(EffectLightningSphere object) {
        int index = object.tickCount % TEXTURE_RESOURCE.length;
        return TEXTURE_RESOURCE[index];
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EffectLightningSphere animatable) {
        return ANIMATION_RESOURCE;
    }
}