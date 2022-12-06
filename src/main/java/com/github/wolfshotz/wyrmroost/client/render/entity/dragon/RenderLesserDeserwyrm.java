package com.github.wolfshotz.wyrmroost.client.render.entity.dragon;

import com.github.wolfshotz.wyrmroost.client.model.entity.dragon.ModelLesserDesertwyrm;
import com.github.wolfshotz.wyrmroost.entities.dragon.LesserDesertwyrmEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderLesserDeserwyrm extends GeoEntityRenderer<LesserDesertwyrmEntity> {
    public RenderLesserDeserwyrm(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelLesserDesertwyrm());
        this.shadowRadius = 0.0F;
    }

}

