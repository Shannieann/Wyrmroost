package com.github.shannieann.wyrmroost.client.model.entity;

/*import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entities.dragon.LesserDesertwyrmEntity;
import com.mojang.blaze3d.matrix.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.ResourceLocation;

/**
 * WR Lesser Desertwyrm - Ukan
 * Created using Tabula 7.0.1
public class LesserDesertwyrmModel extends WREntityModel<LesserDesertwyrmEntity>
{
    private final ResourceLocation TEXTURE = Wyrmroost.id(DragonEntityModel.FOLDER + "lesser_desertwyrm/body.png");

    public WRModelPart body1;
    public WRModelPart body2;
    public WRModelPart neck;
    public WRModelPart leg1;
    public WRModelPart leg1_1;
    public WRModelPart wingL;
    public WRModelPart wingR;
    public WRModelPart body3;
    public WRModelPart body4;
    public WRModelPart body5;
    public WRModelPart tail1;
    public WRModelPart tail2;
    public WRModelPart tail3;
    public WRModelPart jaw;
    public WRModelPart head;

    private final WRModelPart[] body;

    public LesserDesertwyrmModel()
    {
        this.texWidth = 30;
        this.texHeight = 30;
        this.wingL = new WRModelPart(this, 0, 22);
        this.wingL.setPos(0.5F, -0.7F, 2.0F);
        this.wingL.addBox(0.0F, -2.0F, 0.0F, 0, 2, 3, 0.0F);
        this.setRotateAngle(wingL, 0.6829473363053812F, 0.0F, 0.5462880558742251F);
        this.body5 = new WRModelPart(this, 0, 0);
        this.body5.setPos(-0.02F, -0.02F, 3.0F);
        this.body5.addBox(-1.5F, -1.0F, 0.0F, 3, 2, 4, 0.0F);
        this.tail3 = new WRModelPart(this, 0, 17);
        this.tail3.setPos(0.0F, 0.0F, 3.0F);
        this.tail3.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4, 0.0F);
        this.leg1_1 = new WRModelPart(this, 18, 22);
        this.leg1_1.setPos(-0.7F, 0.0F, 0.5F);
        this.leg1_1.addBox(-2.0F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        this.setRotateAngle(leg1_1, 0.0F, 0.0F, -0.40980330836826856F);
        this.body1 = new WRModelPart(this, 0, 9);
        this.body1.setPos(0.02F, 23.0F, -5.5F);
        this.body1.addBox(-1.0F, -1.0F, -2.0F, 2, 2, 4, 0.0F);
        this.body4 = new WRModelPart(this, 0, 0);
        this.body4.setPos(0.02F, 0.02F, 3.0F);
        this.body4.addBox(-1.5F, -1.0F, 0.0F, 3, 2, 4, 0.0F);
        this.neck = new WRModelPart(this, 16, 0);
        this.neck.setPos(-0.02F, 0.02F, -1.0F);
        this.neck.addBox(-1.0F, -1.0F, -3.0F, 2, 2, 3, 0.0F);
        this.setRotateAngle(neck, 0.0F, 0.0F, 0.0F);
        this.body3 = new WRModelPart(this, 0, 0);
        this.body3.setPos(-0.02F, -0.02F, 3.0F);
        this.body3.addBox(-1.5F, -1.0F, 0.0F, 3, 2, 4, 0.0F);
        this.wingR = new WRModelPart(this, 0, 22);
        this.wingR.setPos(-0.5F, -0.7F, 2.0F);
        this.wingR.addBox(0.0F, -2.0F, 0.0F, 0, 2, 3, 0.0F);
        this.setRotateAngle(wingR, 0.6829473363053812F, 0.0F, -0.5462880558742251F);
        this.head = new WRModelPart(this, 18, 14);
        this.head.setPos(0.02F, -0.6F, -2.5F);
        this.head.addBox(-1.0F, -0.5F, -3.0F, 2, 1, 3, 0.0F);
        this.setRotateAngle(head, 0.0F, 0.0F, 0.0F);
        this.tail1 = new WRModelPart(this, 0, 9);
        this.tail1.setPos(0.0F, 0.02F, 3.0F);
        this.tail1.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 4, 0.0F);
        this.leg1 = new WRModelPart(this, 18, 22);
        this.leg1.setPos(0.7F, 0.0F, 0.5F);
        this.leg1.addBox(0.0F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        this.setRotateAngle(leg1, 0.0F, 0.0F, 0.40980330836826856F);
        this.body2 = new WRModelPart(this, 0, 0);
        this.body2.setPos(0.02F, 0.02F, 1.0F);
        this.body2.addBox(-1.5F, -1.0F, 0.0F, 3, 2, 4, 0.0F);
        this.tail2 = new WRModelPart(this, 0, 9);
        this.tail2.setPos(0.02F, 0.02F, 3.0F);
        this.tail2.addBox(-1.0F, -1.0F, 0.0F, 2, 2, 4, 0.0F);
        this.jaw = new WRModelPart(this, 18, 7);
        this.jaw.setPos(0.02F, 0.6F, -2.2F);
        this.jaw.addBox(-1.0F, -0.5F, -3.0F, 2, 1, 3, 0.0F);
        this.setRotateAngle(jaw, 0.0F, 0.0F, 0.0F);
        this.body1.addChild(this.wingL);
        this.body4.addChild(this.body5);
        this.tail2.addChild(this.tail3);
        this.body1.addChild(this.leg1_1);
        this.body3.addChild(this.body4);
        this.body1.addChild(this.neck);
        this.body2.addChild(this.body3);
        this.body1.addChild(this.wingR);
        this.neck.addChild(this.head);
        this.body5.addChild(this.tail1);
        this.body1.addChild(this.leg1);
        this.body1.addChild(this.body2);
        this.tail1.addChild(this.tail2);
        this.neck.addChild(this.jaw);

        setDefaultPose();

        body = new WRModelPart[] {body1, body2, body3, body4, body5, tail1, tail2, tail3};
    }

    @Override
    public ResourceLocation getTexture(LesserDesertwyrmEntity entity)
    {
        return TEXTURE;
    }

    @Override
    public float getShadowRadius(LesserDesertwyrmEntity entity)
    {
        return 0;
    }

    @Override
    public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        body1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(LesserDesertwyrmEntity entity, float limbSwing, float limbSwingAmount, float bob, float netHeadYaw, float headPitch)
    {
        animator().tick(entity, this, partialTicks);
        reset();

        if (entity.isBurrowed())
        {
            body1.xRot = -0.8f;
            body1.y = 26.5f;
            body2.xRot = 0.8f;
            neck.xRot = -0.8f;
            jaw.xRot = 1f;
            head.xRot = -1f;

            neck.y = bob(0.45f - globalSpeed, 0.15f, false, bob, 0.5f);
        }

        if (entity.getAnimation() != LesserDesertwyrmEntity.BITE_ANIMATION)
        {
            walk(jaw, 0.45f - globalSpeed, 0.1f, false, 0, 0, bob, 0.5f);
            walk(head, 0.45f - globalSpeed, 0.1f, true, 0, (entity.isBurrowed()? 0f : 0.5f), bob, 0.5f);
        }
        flap(wingL, 0.45f - globalSpeed, 0.15f, false, 0, 0, bob, 0.5f);
        flap(wingR, 0.45f - globalSpeed, 0.15f, true, 0, 0, bob, 0.5f);
        flap(leg1, 0.45f - globalSpeed, 0.15f, true, 0, 0, bob, 0.5f);
        flap(leg1_1, 0.45f - globalSpeed, 0.15f, false, 0, 0, bob, 0.5f);

        chainSwing(body, globalSpeed, 0.3f, 5, -limbSwing, limbSwingAmount);
        faceTarget(netHeadYaw, headPitch, 1, head);
    }

    public void biteAnimation()
    {
        animator().startKeyframe(4);
        animator().rotate(head, 1f, 0, 0);
        animator().rotate(jaw, -1f, 0, 0);
        animator().move(body1, 0, -1f, 0);
        animator().endKeyframe();
        
        animator().resetKeyframe(7);
    }
}
*/