package com.github.wolfshotz.wyrmroost.client.model.entity;// Made with Blockbench 4.5.1
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.RoostStalkerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class RoostStalkerModel<T extends RoostStalkerEntity> extends HierarchicalModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Wyrmroost.MOD_ID, "roost_stalker"), "main");
	private final ModelPart torso;
	private final ModelPart root;
	public RoostStalkerModel(ModelPart root) {
		this.torso = root.getChild("torso");
		this.root = root;
	}
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition torso = partdefinition.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.5F, -7.5F, 7.0F, 7.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.8F, 0.0F));

		PartDefinition neck = torso.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(45, 0).addBox(-2.0F, -2.5F, -4.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, -6.5F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(35, 40).addBox(-2.5F, -3.0F, -10.0F, 5.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, -3.0F, 0.025F, 0.0F, 0.0F));

		head.addOrReplaceChild("hornl", CubeListBuilder.create().texOffs(40, 25).mirror().addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.5F, -2.0F, -1.5F, 0.6829F, -0.2731F, -0.182F));

		head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(35, 60).addBox(-2.49F, 0.0F, -10.0F, 5.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.0F, 0.0F));

		head.addOrReplaceChild("hornl_1", CubeListBuilder.create().texOffs(40, 25).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -2.0F, -1.5F, 0.6829F, 0.2731F, 0.182F));

		PartDefinition leg1Left = torso.addOrReplaceChild("leg1Left", CubeListBuilder.create().texOffs(20, 72).addBox(0.0F, -1.5F, -1.5F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 0.0F, -5.0F, 0.0911F, 0.3187F, 0.3395F));

		leg1Left.addOrReplaceChild("foot1Left", CubeListBuilder.create().texOffs(20, 80).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3187F));

		PartDefinition leg2Left = torso.addOrReplaceChild("leg2Left", CubeListBuilder.create().texOffs(20, 72).addBox(0.0F, -1.5F, -1.5F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3187F));

		leg2Left.addOrReplaceChild("foot2Left", CubeListBuilder.create().texOffs(20, 80).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3187F));

		PartDefinition leg3Left = torso.addOrReplaceChild("leg3Left", CubeListBuilder.create().texOffs(20, 72).addBox(0.0F, -1.5F, -1.5F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 0.0F, 5.0F, -0.0911F, -0.3187F, 0.3187F));

		leg3Left.addOrReplaceChild("foot3Left", CubeListBuilder.create().texOffs(20, 80).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3187F));

		PartDefinition leg1Right = torso.addOrReplaceChild("leg1Right", CubeListBuilder.create().texOffs(20, 72).mirror().addBox(-5.0F, -1.5F, -1.5F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.5F, 0.0F, -5.0F, 0.0911F, -0.3187F, -0.3187F));

		leg1Right.addOrReplaceChild("foot1Right", CubeListBuilder.create().texOffs(20, 80).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3187F));

		PartDefinition leg2Right = torso.addOrReplaceChild("leg2Right", CubeListBuilder.create().texOffs(20, 72).mirror().addBox(-5.0F, -1.5F, -1.5F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3187F));

		leg2Right.addOrReplaceChild("foot2Right", CubeListBuilder.create().texOffs(20, 80).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3187F));

		PartDefinition leg3Right = torso.addOrReplaceChild("leg3Right", CubeListBuilder.create().texOffs(20, 72).mirror().addBox(-5.0F, -1.5F, -1.5F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.5F, 0.0F, 5.0F, -0.0911F, 0.3187F, -0.3187F));

		leg3Right.addOrReplaceChild("foot3Right", CubeListBuilder.create().texOffs(20, 80).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3187F));

		PartDefinition tail1 = torso.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 25).addBox(-2.5F, -2.4F, 0.0F, 5.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.5F, 7.0F, -0.0511F, -0.04F, 0.0F));

		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(0, 40).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 6.5F, -0.0711F, -0.04F, 0.0F));

		tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(0, 55).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 6.5F, -0.1111F, -0.04F, 0.0F));

		return LayerDefinition.create(meshdefinition, 80, 90);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}
	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		torso.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}