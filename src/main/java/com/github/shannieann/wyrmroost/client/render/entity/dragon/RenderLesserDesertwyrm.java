package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelLesserDesertwyrm;
import com.github.shannieann.wyrmroost.entity.dragon.EntityLesserDesertwyrm;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RenderLesserDesertwyrm extends WRDragonRender<EntityLesserDesertwyrm> {
    public RenderLesserDesertwyrm(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelLesserDesertwyrm());
        this.shadowRadius = 0.0F;
    }
}

