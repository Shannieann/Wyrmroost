package com.github.shannieann.wyrmroost.client.render.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.model.entity.dragon.ModelRoyalRed;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.layer.DragonEyesLayer;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.layer.DragonRiderLayer;
import com.github.shannieann.wyrmroost.entity.dragon.EntityRoyalRed;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class RenderRoyalRed extends WRDragonRender<EntityRoyalRed> {
    private static final ResourceLocation SPEYES_TEXTURE = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/royal_red/spe_eyes.png");
    private static final ResourceLocation EYES_TEXTURE = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/royal_red/eyes.png");


    public RenderRoyalRed(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ModelRoyalRed(), "body2");
        this.addLayer(new DragonRiderLayer<>(this));
        this.addLayer(new DragonEyesLayer<>(this,
                (entity) -> entity.getVariant().equals("special")? SPEYES_TEXTURE : EYES_TEXTURE,
                getGeoModelProvider()::getModelLocation,
                (entity) -> !entity.isHatchling()));
        //TODO: Shadow?
    }


    // TODO may be a better idea to instead just texture the inside of the nail.
    // NoCull might lag the game more?
    @Override
    public RenderType getRenderType(EntityRoyalRed animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }
}