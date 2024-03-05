package com.github.shannieann.wyrmroost.client.model.entity.dragon;// Made with Blockbench 4.5.1
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.EntityOverworldDrake;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelOverworldDrake<T extends EntityOverworldDrake> extends AnimatedGeoModel<T> {

	private static final ResourceLocation modelResource = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/dragon/overworld_drake/overworld_drake.geo.json");
	private static final ResourceLocation animationResource = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/overworld_drake/overworld_drake.animation.json");

	@Override
	public ResourceLocation getModelLocation(T object) {
		return modelResource;
	}

	@Override
	public ResourceLocation getTextureLocation(EntityOverworldDrake object) {

		int variant = object.getVariant();
		int gender = (object.isBaby())? -1 : object.getGender();
		return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/overworld_drake/" + gender + "_" + variant + ".png");
	}


	@Override
	public ResourceLocation getAnimationFileLocation(T animatable) {
		return animationResource;
	}
}