package com.github.wolfshotz.wyrmroost.client.model.entity.dragon;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.LesserDesertwyrmEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelLesserDesertwyrm extends AnimatedGeoModel<LesserDesertwyrmEntity>
{
    @Override
    public ResourceLocation getModelLocation(LesserDesertwyrmEntity object) {
        return new ResourceLocation(Wyrmroost.MOD_ID,"geo/entity/dragon/lesser_desertwyrm/lesser_desertwyrm.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(LesserDesertwyrmEntity object) {
        return new ResourceLocation(Wyrmroost.MOD_ID,"textures/entity/dragon/lesser_desertwyrm/lesser_desertwyrm.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(LesserDesertwyrmEntity animatable) {
        return new ResourceLocation(Wyrmroost.MOD_ID,"animations/entity/dragon/lesser_desertwyrm/lesser_desertwyrm.json");
    }
}