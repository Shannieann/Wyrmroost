package com.github.shannieann.wyrmroost.client.model.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.EntityLesserDesertwyrm;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelLesserDesertwyrm extends AnimatedGeoModel<EntityLesserDesertwyrm>
{
    private static final ResourceLocation MODEL_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/dragon/lesser_desertwyrm/lesser_desertwyrm.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/lesser_desertwyrm/lesser_desertwyrm.png");
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/lesser_desertwyrm/lesser_desertwyrm.json");

    @Override
    public ResourceLocation getModelLocation(EntityLesserDesertwyrm object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityLesserDesertwyrm object) {
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityLesserDesertwyrm animatable) {
        return ANIMATION_RESOURCE;
    }
}