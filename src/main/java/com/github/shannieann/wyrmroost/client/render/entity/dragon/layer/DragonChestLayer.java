package com.github.shannieann.wyrmroost.client.render.entity.dragon.layer;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.WRRideableDragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class DragonChestLayer<T extends WRRideableDragonEntity> extends GeoLayerRenderer<T> {

    public DragonChestLayer(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        if (entityLivingBaseIn.isChested()){
            renderModel(getEntityModel(), getTexture(entityLivingBaseIn), matrixStackIn, bufferIn, packedLightIn, entityLivingBaseIn, partialTicks, 1.0f, 1.0f, 1.0f);
        }
    }
    private ResourceLocation getTexture(WRRideableDragonEntity dragon){
        return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/" + dragon.getType().getRegistryName().getPath() +"/accessories/chest.png");
    }

}
