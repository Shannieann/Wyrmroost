package com.github.shannieann.wyrmroost.client.render.entity;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragonegg.DragonEggEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class DragonEggRenderer extends EntityRenderer<DragonEggEntity>
{
    public static final ResourceLocation DEFAULT_TEXTURE = Wyrmroost.id("textures/entity/dragon/dragon_egg_wr.png");
    public static final Model MODEL = new Model();

    private static final Map<EntityType<?>, ResourceLocation> TEXTURE_MAP = new HashMap<>();

    public DragonEggRenderer(EntityRendererProvider.Context manager)
    {
        super(manager);
    }

    @Override
    public void render(DragonEggEntity entity, float entityYaw, float partialTicks, PoseStack ms, MultiBufferSource buffer, int packedLightIn)
    {
        ms.pushPose();
        scale(entity, ms);
        ms.translate(0, -1.5, 0);
        //MODEL.animate(entity, partialTicks);
        VertexConsumer builder = buffer.getBuffer(MODEL.renderType(getTextureLocation(entity)));
        MODEL.renderToBuffer(ms, builder, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        ms.popPose();

        super.render(entity, entityYaw, partialTicks, ms, buffer, packedLightIn);
    }

    @Override
    protected boolean shouldShowName(DragonEggEntity entity)
    {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(DragonEggEntity entity)
    {
        return getDragonEggTexture(entity.containedDragon);
    }

    public static ResourceLocation getDragonEggTexture(EntityType<?> type)
    {
        return TEXTURE_MAP.computeIfAbsent(type, t ->
        {
            ResourceLocation textureLoc = Wyrmroost.id(String.format("textures/entity/dragon/%s/egg.png", type.getRegistryName().getPath()));
            if (Minecraft.getInstance().getResourceManager().hasResource(textureLoc)) return textureLoc;
            return DEFAULT_TEXTURE;
        });
    }

    /**
     * Render Custom egg sizes / shapes. <P>
     * If none is defined, then calculate the model size according to egg size
    */
    private void scale(DragonEggEntity entity, PoseStack ms)
    {
        EntityDimensions size = entity.getDimensions();
        if (size != null) ms.scale(size.width * 3, -(size.height * 2), -(size.width * 3));
    }

    /**
     * WREggTemplate - Ukan
     * Created using Tabula 7.0.1
    */
    // TODO HAS TO BE REPLACED
    public static class Model extends EntityModel<DragonEggEntity>
    {
        @Override
        public void setupAnim(DragonEggEntity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {

        }

        @Override
        public void renderToBuffer(PoseStack p_103111_, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_) {

        }

        public ModelPart base;/*
        public ModelPart two;
        public ModelPart three;
        public ModelPart four;
        
        public Model()
        {
            super(RenderType::entitySolid);
            texWidth = 64;
            texHeight = 32;
            four = new ModelPart(this, 0, 19);
            four.setPos(0.0F, -1.3F, 0.0F);
            four.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3, 0.0F);
            two = new ModelPart(this, 17, 0);
            two.setPos(0.0F, -1.5F, 0.0F);
            two.addBox(-2.5F, -3.0F, -2.5F, 5, 6, 5, 0.0F);
            three = new ModelPart(this, 0, 9);
            three.setPos(0.0F, -2.0F, 0.0F);
            three.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
            base = new WRModelPart(this, 0, 0);
            base.setPos(0.0F, 22.0F, 0.0F);
            base.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
            three.addChild(four);
            base.addChild(two);
            two.addChild(three);

            base.setDefaultPose();
        }

        @Override
        public void setupAnim(DragonEggEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
        {
        }
        public void animate(DragonEggEntity entity, float partialTicks)
        {
            float time = entity.wiggleTime.get(partialTicks);
            base.xRot = time * entity.wiggleDirection.getStepX();
            base.zRot = time * entity.wiggleDirection.getStepZ();
        }
        @Override
        public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
        {
            base.render(ms, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            base.reset();
        }
    }*/
}
}
