package com.github.shannieann.wyrmroost.client.model.entity.dragon;
import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.EntityOverworldDrake;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelOverworldDrake<T extends EntityOverworldDrake> extends AnimatedGeoModel<T> {

	private static final ResourceLocation MODEL_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "geo/entity/dragon/overworld_drake/overworld_drake.geo.json");
	private static final ResourceLocation ANIMATION_RESOURCE = new ResourceLocation(Wyrmroost.MOD_ID, "animations/entity/dragon/overworld_drake/overworld_drake.animation.json");

	@Override
	public ResourceLocation getModelLocation(T object) {
		return MODEL_RESOURCE;
	}

	@Override
	public ResourceLocation getTextureLocation(EntityOverworldDrake object) {

		String variant = object.getVariant();
		String gender = (object.isHatchling())? "baby" : object.getGenderString();
		return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/overworld_drake/" + gender + "_" + variant + ".png");
	}


	@Override
	public ResourceLocation getAnimationFileLocation(T animatable) {
		return ANIMATION_RESOURCE;
	}
}