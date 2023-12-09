package com.github.shannieann.wyrmroost.client.model.effect;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entities.effect.EffectLightningNova;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelLightningNova extends AnimatedGeoModel<EffectLightningNova>
{
    private static final ResourceLocation MODEL_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/effect/LIGHTNING_NOVA/LIGHTNING_NOVA.geo.json");
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
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/effect/LIGHTNING_NOVA/LIGHTNING_NOVA.json");

    @Override
    public ResourceLocation getModelLocation(EffectLightningNova object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureLocation(EffectLightningNova object) {
        int index = object.tickCount % TEXTURE_RESOURCE.length;
        return TEXTURE_RESOURCE[index];
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EffectLightningNova animatable) {
        return ANIMATION_RESOURCE;
    }
}