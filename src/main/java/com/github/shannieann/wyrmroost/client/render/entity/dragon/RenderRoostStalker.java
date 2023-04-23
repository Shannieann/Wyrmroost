package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelRoostStalker;
import com.github.shannieann.wyrmroost.entities.dragon.EntityRoostStalker;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RenderRoostStalker extends WRDragonRender<EntityRoostStalker> {
        public RenderRoostStalker(EntityRendererProvider.Context renderManager) {
            super(renderManager, new ModelRoostStalker(), "");
            this.addLayer(new ModelRoostStalker.RoostStalkerEyesLayer<>(this));
            this.addLayer(new ModelRoostStalker.RoostStalkerMouthItemLayer<>(this));
            //TODO: Shadow?
            this.shadowRadius = 0.0F;
        }
}


