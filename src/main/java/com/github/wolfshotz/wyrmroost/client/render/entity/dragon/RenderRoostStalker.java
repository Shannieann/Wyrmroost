package com.github.wolfshotz.wyrmroost.client.render.entity.dragon;

import com.github.wolfshotz.wyrmroost.client.model.entity.dragon.ModelRoostStalker;
import com.github.wolfshotz.wyrmroost.entities.dragon.RoostStalkerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderRoostStalker extends GeoEntityRenderer<RoostStalkerEntity> {
        public RenderRoostStalker(EntityRendererProvider.Context renderManager) {
            super(renderManager, new ModelRoostStalker());
            this.addLayer(new ModelRoostStalker.RoostStalkerEyesLayer<>(this));
            //TODO: Shadow?
            this.shadowRadius = 0.0F;
        }
}


