package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelRoostStalker;
import com.github.shannieann.wyrmroost.entities.dragon.EntityRoostStalker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public class RenderRoostStalker extends WRDragonRender<EntityRoostStalker> {
        public RenderRoostStalker(EntityRendererProvider.Context renderManager) {
            super(renderManager, new ModelRoostStalker());
            // TODO readd glowing eyes. Will need to create textures separating the eyes for this.
            //this.addLayer(new ModelRoostStalker.RoostStalkerEyesLayer<>(this));
            this.addLayer(new ModelRoostStalker.RoostStalkerMouthItemLayer<>(this));
            //TODO: Shadow?
            this.shadowRadius = 0.0F;
        }

    /*@Override
    public void renderLate(EntityRoostStalker animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
                           VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
                           float alpha) {
        poseStack.scale(0.625f, 0.625f, 0.625f);
        super.renderLate(animatable, poseStack, partialTick, bufferSource,
                buffer, packedLight, packedOverlay, red, green, blue,
        alpha);
    }*/
}


