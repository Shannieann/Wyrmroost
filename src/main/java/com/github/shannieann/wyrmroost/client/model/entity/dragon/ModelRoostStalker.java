package com.github.shannieann.wyrmroost.client.model.entity.dragon;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.layer.DragonEyesLayer;
import com.github.shannieann.wyrmroost.entities.dragon.EntityRoostStalker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class ModelRoostStalker extends AnimatedGeoModel<EntityRoostStalker> {
    private static final ResourceLocation MODEL_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/dragon/roost_stalker/roost_stalker.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/roost_stalker.png");
    private static final ResourceLocation TEXTURE_RESOURCE_SPECIAL = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/roost_stalker_sp.png");
    private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/roost_stalker/roost_stalker.json");

    @Override
    public ResourceLocation getModelLocation(EntityRoostStalker object) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityRoostStalker object) {
        switch (object.getVariant()){
            case -1 -> { return TEXTURE_RESOURCE_SPECIAL;}
            default -> {return  TEXTURE_RESOURCE;}
        }
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EntityRoostStalker animatable) {
        return ANIMATION_RESOURCE;
    }

    public static class RoostStalkerEyesLayer<T extends EntityRoostStalker> extends DragonEyesLayer<T> {

        private static final ResourceLocation EYES_TEXTURE = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/roost_stalker_eyes.png");
        private static final ResourceLocation EYES_TEXTURE_SPECIAL = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/roost_stalker_eyes_sp.png");

        public RoostStalkerEyesLayer(IGeoRenderer<T> entityRendererIn) {
            super(entityRendererIn, ModelRoostStalker.MODEL_RESOURCE);
        }


        @Override
        protected ResourceLocation getEntityTexture(T entityIn) {
            if (entityIn.isSleeping()) return BLANK_EYES;
            switch (entityIn.getVariant()){
                case -1 -> {return EYES_TEXTURE_SPECIAL;}
                default -> {return EYES_TEXTURE;}
            }
        }

    }
    public static class RoostStalkerMouthItemLayer< T extends EntityRoostStalker> extends GeoLayerRenderer<T>{

        public RoostStalkerMouthItemLayer(IGeoRenderer<T> entityRendererIn) {
            super(entityRendererIn);
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T roostStalker, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            // First we get the item and itemrenderer
            ItemStack item = roostStalker.getItem();

            if (item.isEmpty()) return; // We don't want to even begin rendering if there's no item
            ItemInHandRenderer renderer = Minecraft.getInstance().getItemInHandRenderer();


            // Get the model of the rooststalker
            GeoModelProvider<T> provider = getEntityModel();
            GeoModel model = provider.getModel(provider.getModelLocation(roostStalker));

            // Then get the location of where the item should be rendered on the roost stalker (if the bone isnt in the model, throw an error instead)
            if (model.getBone("mouthItem").isEmpty()){
                throw new ReportedException(CrashReport.forThrowable(new Throwable(), "Dragon should have a bone named 'mouthItem' to show its held item in its mouth!"));
            }
            GeoBone bone = model.getBone("mouthItem").get();


            // Translate the item based on whether its a tool/block
            if (item.getItem() instanceof TieredItem)
            {
                matrixStackIn.translate(-0.25f, 0, 0);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-45));
            }
            else if (item.getItem() instanceof BlockItem)
            {
                matrixStackIn.translate(0, 0, -0.3f);
                matrixStackIn.scale(0.8f, 0.8f, 0.8f);
                //matrixStackIn.mulPose(Vector3f.XP.rotation(jaw.xRot + 1.57f));
            }

            // Translate the item's render location to the bone's location
            // (Don't ask me why its multiplied by 1/16, I was wondering why it rendered so far away and looked at Alex's ice and fire code so thanks Alex)
            matrixStackIn.translate(bone.getModelPosition().x * 00.0625F, bone.getModelPosition().y * 00.0625F, bone.getModelPosition().z * 00.0625F);
            // And rotate correctly
            matrixStackIn.mulPoseMatrix(bone.getModelRotationMat());
            // And scale it
            matrixStackIn.scale(1.5f, 1.5f, 1.5f);

            // Finally, render the item

            renderer.renderItem(roostStalker, item, ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);

        }
    }

}


    //TODO: EYES
    /*
    @Override
    public void setCustomAnimations(T animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        IBone head = this.getAnimationProcessor().getBone("neck");

        EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * Mth.DEG_TO_RAD);
            head.setRotationY(extraData.netHeadYaw * Mth.DEG_TO_RAD);
        }
    }
    */
