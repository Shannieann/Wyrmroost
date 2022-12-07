package com.github.shannieann.wyrmroost.client.model.entity;// Made with Blockbench 4.5.1
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.render.entity.dragon.DragonEyesLayer;
import com.github.shannieann.wyrmroost.entities.dragon.RoostStalkerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class GeckoRoostStalkerModel<T extends RoostStalkerEntity> extends AnimatedGeoModel<T> {

	private static final ResourceLocation modelResource = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/roost_stalker.geo.json");
	private static final ResourceLocation textureResource = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/body.png");
	private static final ResourceLocation specialTexture = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/body_spe.png");
	private static final ResourceLocation animationResource = new ResourceLocation(Wyrmroost.MOD_ID, "animations/roost_stalker.animation.json");

	@Override
	public ResourceLocation getModelLocation(T object) {
		return modelResource;
	}

	@Override
	public ResourceLocation getTextureLocation(RoostStalkerEntity object) {
		switch (object.getVariant()){
			case 1: return specialTexture;
			default: return textureResource;
		}
	}

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

	@Override
	public ResourceLocation getAnimationFileLocation(T animatable) {
		return animationResource;
	}

	public static class RoostStalkerEyesLayer<T extends RoostStalkerEntity> extends DragonEyesLayer<T> {

		private static final ResourceLocation eyesResource = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/body_glow.png");
		private static final ResourceLocation eyesSpecialTexture = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/body_spe_glow.png");

		public RoostStalkerEyesLayer(IGeoRenderer<T> entityRendererIn) {
			super(entityRendererIn);
		}

		@Override
		public RenderType getRenderType(ResourceLocation textureLocation) {
			return RenderType.eyes(textureLocation);
		}

		@Override
		protected ResourceLocation getEntityTexture(T entityIn) {
			if (entityIn.isSleeping()) return BLANK_EYES;
			switch (entityIn.getVariant()){
				case 1: return eyesSpecialTexture;
				default: return eyesResource;
			}
		}

		@Override
		public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			RenderType cameo =  getRenderType(getEntityTexture(entityLivingBaseIn));
			matrixStackIn.pushPose();
			//Move or scale the model as you see fit
			//matrixStackIn.scale(1.0f, 1.0f, 1.0f);
			//matrixStackIn.translate(0.0d, 0.0d, 0.0d);
			this.getRenderer().render(this.getEntityModel().getModel(modelResource), entityLivingBaseIn, partialTicks, cameo, matrixStackIn, bufferIn,
					bufferIn.getBuffer(cameo), packedLightIn, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
			matrixStackIn.popPose();
		}
	}
}