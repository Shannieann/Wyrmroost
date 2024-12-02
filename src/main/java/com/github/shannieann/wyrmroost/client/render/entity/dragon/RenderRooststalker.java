package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelRooststalker;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.layer.DragonEyesLayer;
import com.github.shannieann.wyrmroost.entity.dragon.EntityButterflyLeviathan;
import com.github.shannieann.wyrmroost.entity.dragon.EntityRooststalker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RenderRooststalker extends WRDragonRender<EntityRooststalker> {
    private static final ResourceLocation EYES_TEXTURE = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/rooststalker/rooststalker_eyes.png");
    private static final ResourceLocation EYES_TEXTURE_SPECIAL = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/rooststalker/rooststalker_eyes_sp.png");


    public RenderRooststalker(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelRooststalker());
        this.addLayer(new DragonEyesLayer<>(this,
                (entity) -> entity.isAlbino()? EYES_TEXTURE_SPECIAL : EYES_TEXTURE,
                getGeoModelProvider()::getModelLocation,
                (entity) -> true));
        this.addLayer(new ModelRooststalker.RooststalkerMouthItemLayer<>(this));


    }


    @Override
    public void renderLate(EntityRooststalker animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
                           VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
                           float alpha) {
        //float ageProgressScale = animatable.getScale();

        //poseStack.scale(0.625f*ageProgressScale, 0.625f*ageProgressScale, 0.625f*ageProgressScale);
        super.renderLate(animatable, poseStack, partialTick, bufferSource,
                buffer, packedLight, packedOverlay, red, green, blue,
        alpha);
    }


}


