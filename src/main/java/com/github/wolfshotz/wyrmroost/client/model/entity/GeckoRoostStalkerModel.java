package com.github.wolfshotz.wyrmroost.client.model.entity;// Made with Blockbench 4.5.1
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.RoostStalkerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GeckoRoostStalkerModel<T extends RoostStalkerEntity> extends AnimatedGeoModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	private static final ResourceLocation modelResource = new ResourceLocation(Wyrmroost.MOD_ID, "geo/roost_stalker.geo.json");
	private static final ResourceLocation textureResource = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/body.png");
	private static final ResourceLocation animationResource = new ResourceLocation(Wyrmroost.MOD_ID, "animations/roost_stalker.animation.json");

	@Override
	public ResourceLocation getModelLocation(T object) {
		return modelResource;
	}

	@Override
	public ResourceLocation getTextureLocation(T object) {
		return textureResource;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(T animatable) {
		return animationResource;
	}
}