package com.github.wolfshotz.wyrmroost.client.model.entity;// Made with Blockbench 4.5.1
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.RoyalRedEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class GeckoRoyalRedModel<T extends RoyalRedEntity> extends AnimatedGeoModel<T> {

	private static final ResourceLocation modelResource = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/roost_stalker.geo.json");
	private static final ResourceLocation textureResource = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/body.png");
	private static final ResourceLocation specialTexture = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/body_spe.png");
	private static final ResourceLocation animationResource = new ResourceLocation(Wyrmroost.MOD_ID, "animations/roost_stalker.animation.json");

	@Override
	public ResourceLocation getModelLocation(T object) {
		return modelResource;
	}

	@Override
	public ResourceLocation getTextureLocation(RoyalRedEntity object) {
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

}