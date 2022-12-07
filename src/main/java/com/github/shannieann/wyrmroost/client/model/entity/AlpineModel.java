package com.github.shannieann.wyrmroost.client.model.entity;

/*import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.model.ModelAnimator;
import com.github.wolfshotz.wyrmroost.entities.dragon.AlpineEntity;
import com.github.wolfshotz.wyrmroost.util.Mafs;
import com.mojang.blaze3d.matrix.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Mth;

/**
 * WRAlpineDragonNew - Ukan
 * Created using Tabula 8.0.0
public class AlpineModel extends DragonEntityModel<AlpineEntity>
{
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[6];

    public WRModelPart body1;
    public WRModelPart body2;
    public WRModelPart neck1;
    public WRModelPart wing1L;
    public WRModelPart wing1R;
    public WRModelPart leg1R;
    public WRModelPart leg1L;
    public WRModelPart fin7;
    public WRModelPart backleg1L;
    public WRModelPart backleg1R;
    public WRModelPart tail1;
    public WRModelPart backleg2L;
    public WRModelPart backleg3L;
    public WRModelPart backfoot1L;
    public WRModelPart backfoot2L;
    public WRModelPart backleg2R;
    public WRModelPart backleg3R;
    public WRModelPart backfoot1R;
    public WRModelPart backfoot2R;
    public WRModelPart tail2;
    public WRModelPart fin8;
    public WRModelPart tailfin1L;
    public WRModelPart tailfin1R;
    public WRModelPart tail3;
    public WRModelPart fin9;
    public WRModelPart tailfin2L;
    public WRModelPart tailfin2R;
    public WRModelPart tail4;
    public WRModelPart fin10;
    public WRModelPart tailfin3L;
    public WRModelPart tailfin3R;
    public WRModelPart tail5;
    public WRModelPart fin11;
    public WRModelPart tailfin4L;
    public WRModelPart tailfin4R;
    public WRModelPart tail6;
    public WRModelPart fin12;
    public WRModelPart tailfin5L;
    public WRModelPart tailfin5R;
    public WRModelPart tail7;
    public WRModelPart fin13;
    public WRModelPart tailfin6L;
    public WRModelPart tailfin6R;
    public WRModelPart tail8;
    public WRModelPart fin14;
    public WRModelPart tailfin7L;
    public WRModelPart tailfin7R;
    public WRModelPart fin15;
    public WRModelPart tailfin8L;
    public WRModelPart neck2;
    public WRModelPart fin6;
    public WRModelPart neck3;
    public WRModelPart fin5;
    public WRModelPart neck4;
    public WRModelPart fin4;
    public WRModelPart neck5;
    public WRModelPart fin3;
    public WRModelPart neck6;
    public WRModelPart fin2;
    public WRModelPart head;
    public WRModelPart fin1;
    public WRModelPart snout;
    public WRModelPart jaw;
    public WRModelPart earL;
    public WRModelPart earR;
    public WRModelPart eyeL;
    public WRModelPart eyeR;
    public WRModelPart teeth1;
    public WRModelPart teeth2;
    public WRModelPart wing2L;
    public WRModelPart feathers1L;
    public WRModelPart wing3L;
    public WRModelPart feathers2L;
    public WRModelPart feathers3L;
    public WRModelPart finger1L;
    public WRModelPart finger2L;
    public WRModelPart finger3L;
    public WRModelPart finger4L;
    public WRModelPart finger5L;
    public WRModelPart finger6L;
    public WRModelPart finger7L;
    public WRModelPart finger8L;
    public WRModelPart finger8L_1;
    public WRModelPart wing2R;
    public WRModelPart feathers1R;
    public WRModelPart wing3R;
    public WRModelPart feathersR;
    public WRModelPart feathers3R;
    public WRModelPart finger1R;
    public WRModelPart finger2R;
    public WRModelPart finger3R;
    public WRModelPart finger4R;
    public WRModelPart finger5R;
    public WRModelPart finger6R;
    public WRModelPart finger7R;
    public WRModelPart finger8R;
    public WRModelPart finger8R_1;
    public WRModelPart leg2R;
    public WRModelPart foot1R;
    public WRModelPart foot2R;
    public WRModelPart leg2L;
    public WRModelPart foot1L;
    public WRModelPart foot2L;

    public final WRModelPart[] headArray;
    public final WRModelPart[] tailArray;
    private final WRModelPart[][] fingerArrays;

    public AlpineModel()
    {
        this.texWidth = 150;
        this.texHeight = 100;
        this.fin7 = new WRModelPart(this, 31, 68);
        this.fin7.setPos(0.0F, -3.5F, 4.9F);
        this.fin7.addBox(0.0F, -4.0F, -4.0F, 0.0F, 4.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.wing3R = new WRModelPart(this, 99, 42);
        this.wing3R.mirror = true;
        this.wing3R.setPos(-19.0F, 0.0F, 0.0F);
        this.wing3R.addBox(-15.0F, -1.5F, -5.0F, 15.0F, 3.0F, 10.0F, 0.0F, -0.1F, 0.0F);
        this.setRotateAngle(wing3R, 0.0F, 2.7207937602813157F, 0.0F);
        this.backleg3R = new WRModelPart(this, 37, 59);
        this.backleg3R.setPos(0.0F, 2.8F, 0.1F);
        this.backleg3R.addBox(-1.5F, -0.8F, -0.6F, 3.0F, 5.0F, 2.0F, -0.2F, 0.0F, 0.0F);
        this.setRotateAngle(backleg3R, -0.9384635467868342F, 0.0F, 0.0F);
        this.neck3 = new WRModelPart(this, 0, 27);
        this.neck3.setPos(0.0F, 0.0F, -2.0F);
        this.neck3.addBox(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 3.0F, 0.2F, 0.2F, 0.0F);
        this.setRotateAngle(neck3, -0.4113740993687413F, 0.0F, 0.0F);
        this.leg2R = new WRModelPart(this, 37, 59);
        this.leg2R.setPos(-1.0F, 4.7F, 0.0F);
        this.leg2R.addBox(-1.5F, -0.8F, -1.0F, 3.0F, 5.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(leg2R, -0.7305948355374707F, 0.0F, 0.0F);
        this.tailfin7L = new WRModelPart(this, 45, 64);
        this.tailfin7L.setPos(-0.5F, -0.4F, 3.0F);
        this.tailfin7L.addBox(0.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailfin7L, 0.0F, -0.17453292519943295F, 0.0F);
        this.fin2 = new WRModelPart(this, 15, 81);
        this.fin2.setPos(0.01F, -2.0F, -1.5F);
        this.fin2.addBox(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.feathers1L = new WRModelPart(this, 27, 16);
        this.feathers1L.setPos(1.5F, -0.5F, 0.5F);
        this.feathers1L.addBox(0.0F, -0.5F, -1.5F, 19.0F, 1.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.finger4L = new WRModelPart(this, 55, 90);
        this.finger4L.setPos(15.0F, -0.77F, -0.5F);
        this.finger4L.addBox(0.0F, -0.5F, -1.0F, 28.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger4L, 0.0F, -0.27314402127920984F, 0.0F);
        this.tailfin1L = new WRModelPart(this, 45, 64);
        this.tailfin1L.setPos(-1.5F, -1.0F, 3.0F);
        this.tailfin1L.addBox(0.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailfin1L, 0.0F, -0.17453292519943295F, 0.0F);
        this.tail2 = new WRModelPart(this, 77, 11);
        this.tail2.mirror = true;
        this.tail2.setPos(0.0F, 0.0F, 5.0F);
        this.tail2.addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 6.0F, -0.2F, -0.2F, 0.0F);
        this.eyeR = new WRModelPart(this, 0, 0);
        this.eyeR.setPos(-1.7F, -1.0F, -3.5F);
        this.eyeR.addBox(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(eyeR, 0.0F, -0.3186971254089062F, 0.0F);
        this.fin8 = new WRModelPart(this, 13, 75);
        this.fin8.setPos(0.01F, -2.0F, 3.0F);
        this.fin8.addBox(0.0F, -4.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.neck2 = new WRModelPart(this, 0, 27);
        this.neck2.mirror = true;
        this.neck2.setPos(0.0F, -0.2F, -2.0F);
        this.neck2.addBox(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 3.0F, 0.3F, 0.3F, 0.0F);
        this.setRotateAngle(neck2, -0.4891369717499691F, 0.0F, 0.0F);
        this.tailfin3L = new WRModelPart(this, 45, 64);
        this.tailfin3L.setPos(-0.5F, -0.8F, 3.0F);
        this.tailfin3L.addBox(0.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailfin3L, 0.0F, -0.17453292519943295F, 0.0F);
        this.wing1R = new WRModelPart(this, 89, 14);
        this.wing1R.mirror = true;
        this.wing1R.setPos(-3.0F, -1.9F, -2.0F);
        this.wing1R.addBox(-20.0F, -1.5F, -1.5F, 20.0F, 3.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(wing1R, 0.0F, 0.9861110273767961F, 0.5914920728482339F);
        this.foot1L = new WRModelPart(this, 18, 61);
        this.foot1L.mirror = true;
        this.foot1L.setPos(0.0F, 3.6F, 0.2F);
        this.foot1L.addBox(-1.5F, -0.1F, -1.0F, 3.0F, 3.0F, 2.0F, -0.1F, 0.0F, 0.0F);
        this.setRotateAngle(foot1L, -0.7723081940074907F, 0.0F, 0.0F);
        this.backfoot1R = new WRModelPart(this, 18, 61);
        this.backfoot1R.setPos(0.0F, 3.6F, 0.7F);
        this.backfoot1R.addBox(-1.5F, -0.1F, -1.1F, 3.0F, 3.0F, 2.0F, -0.1F, 0.0F, 0.0F);
        this.setRotateAngle(backfoot1R, -0.7913322855173901F, 0.0F, 0.0F);
        this.wing1L = new WRModelPart(this, 89, 14);
        this.wing1L.setPos(3.0F, -1.9F, -2.0F);
        this.wing1L.addBox(0.0F, -1.5F, -1.5F, 20.0F, 3.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(wing1L, 0.0F, -0.9859365237463688F, -0.5916666430576886F);
        this.jaw = new WRModelPart(this, 0, 52);
        this.jaw.setPos(0.0F, 1.5F, -3.8F);
        this.jaw.addBox(-1.5F, -0.5F, -5.0F, 3.0F, 1.0F, 5.0F, -0.1F, 0.0F, 0.0F);
        this.finger8L = new WRModelPart(this, 116, 90);
        this.finger8L.setPos(15.0F, -0.73F, 3.5F);
        this.finger8L.addBox(0.0F, -0.5F, -1.0F, 14.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger8L, 0.0F, -0.956091342937205F, 0.0F);
        this.finger1L = new WRModelPart(this, 0, 90);
        this.finger1L.setPos(15.0F, -0.8F, -3.5F);
        this.finger1L.addBox(0.0F, -0.5F, -1.0F, 25.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.tailfin6L = new WRModelPart(this, 45, 64);
        this.tailfin6L.setPos(-0.5F, -0.59F, 3.0F);
        this.tailfin6L.addBox(0.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailfin6L, 0.0F, -0.17453292519943295F, 0.0F);
        this.fin15 = new WRModelPart(this, 28, 75);
        this.fin15.setPos(0.0F, -0.5F, 3.0F);
        this.fin15.addBox(0.0F, -4.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.tail5 = new WRModelPart(this, 116, 0);
        this.tail5.setPos(0.0F, 0.0F, 5.0F);
        this.tail5.addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.finger7L = new WRModelPart(this, 110, 94);
        this.finger7L.setPos(15.0F, -0.74F, 2.5F);
        this.finger7L.addBox(0.0F, -0.5F, -1.0F, 17.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger7L, 0.0F, -0.6829473549475088F, 0.0F);
        this.tailfin4L = new WRModelPart(this, 45, 64);
        this.tailfin4L.setPos(-0.5F, -0.79F, 3.0F);
        this.tailfin4L.addBox(0.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailfin4L, 0.0F, -0.17453292519943295F, 0.0F);
        this.tail6 = new WRModelPart(this, 116, 0);
        this.tail6.setPos(0.0F, 0.0F, 5.0F);
        this.tail6.addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 6.0F, -0.2F, -0.2F, 0.0F);
        this.tailfin2L = new WRModelPart(this, 45, 64);
        this.tailfin2L.setPos(-1.0F, -0.99F, 3.0F);
        this.tailfin2L.addBox(0.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailfin2L, 0.0F, -0.17453292519943295F, 0.0F);
        this.fin3 = new WRModelPart(this, 13, 72);
        this.fin3.setPos(0.0F, -2.0F, -1.5F);
        this.fin3.addBox(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.tailfin2R = new WRModelPart(this, 45, 64);
        this.tailfin2R.mirror = true;
        this.tailfin2R.setPos(1.0F, -0.99F, 3.0F);
        this.tailfin2R.addBox(-8.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailfin2R, 0.0F, 0.17453292519943295F, 0.0F);
        this.tailfin6R = new WRModelPart(this, 45, 64);
        this.tailfin6R.mirror = true;
        this.tailfin6R.setPos(0.5F, -0.59F, 3.0F);
        this.tailfin6R.addBox(-8.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailfin6R, 0.0F, 0.17453292519943295F, 0.0F);
        this.wing2R = new WRModelPart(this, 89, 28);
        this.wing2R.setPos(-19.0F, 0.0F, 3.51F);
        this.wing2R.addBox(-20.0F, -1.5F, -5.0F, 20.0F, 3.0F, 10.0F, 0.0F, -0.15F, 0.0F);
        this.setRotateAngle(wing2R, 0.0F, -2.6029840357716987F, 0.0F);
        this.finger6L = new WRModelPart(this, 65, 94);
        this.finger6L.setPos(15.0F, -0.75F, 1.5F);
        this.finger6L.addBox(0.0F, -0.5F, -1.0F, 21.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger6L, 0.0F, -0.500909508638178F, 0.0F);
        this.wing2L = new WRModelPart(this, 89, 28);
        this.wing2L.setPos(19.0F, 0.0F, 3.5F);
        this.wing2L.addBox(0.0F, -1.5F, -5.0F, 20.0F, 3.0F, 10.0F, 0.0F, -0.15F, 0.0F);
        this.setRotateAngle(wing2L, 0.0F, 2.6028095987202984F, 0.0F);
        this.neck1 = new WRModelPart(this, 0, 18);
        this.neck1.setPos(0.0F, -0.5F, -4.0F);
        this.neck1.addBox(-2.5F, -2.5F, -3.0F, 5.0F, 5.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(neck1, -0.39025563139857644F, 0.0F, 0.0F);
        this.feathers3L = new WRModelPart(this, 27, 38);
        this.feathers3L.mirror = true;
        this.feathers3L.setPos(7.5F, -0.5F, -3.0F);
        this.feathers3L.addBox(-7.5F, -0.5F, -1.5F, 15.0F, 1.0F, 10.0F, -0.1F, -0.2F, 0.0F);
        this.body2 = new WRModelPart(this, 37, 0);
        this.body2.setPos(0.0F, 0.6F, 3.5F);
        this.body2.addBox(-4.0F, -3.5F, 0.0F, 8.0F, 6.0F, 9.0F, 0.1F, 0.45F, 0.0F);
        this.earL = new WRModelPart(this, 0, 68);
        this.earL.setPos(2.0F, -1.0F, -2.0F);
        this.earL.addBox(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(earL, -1.0471975511965976F, 0.45535640450848164F, 0.0F);
        this.finger2R = new WRModelPart(this, 0, 94);
        this.finger2R.mirror = true;
        this.finger2R.setPos(-15.0F, -0.79F, -2.5F);
        this.finger2R.addBox(-30.0F, -0.5F, -1.0F, 30.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger2R, 0.0F, 0.0911061832922575F, 0.0F);
        this.feathersR = new WRModelPart(this, 27, 27);
        this.feathersR.mirror = true;
        this.feathersR.setPos(-10.0F, -0.5F, -4.5F);
        this.feathersR.addBox(-10.0F, -0.5F, 0.0F, 21.0F, 1.0F, 10.0F, -0.1F, -0.1F, 0.0F);
        this.body1 = new WRModelPart(this, 0, 0);
        this.body1.setPos(0.0F, 12.4F, 0.0F);
        this.body1.addBox(-4.0F, -3.5F, -5.0F, 8.0F, 7.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.tailfin7R = new WRModelPart(this, 45, 64);
        this.tailfin7R.mirror = true;
        this.tailfin7R.setPos(0.5F, -0.4F, 3.0F);
        this.tailfin7R.addBox(-8.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailfin7R, 0.0F, 0.17453292519943295F, 0.0F);
        this.teeth2 = new WRModelPart(this, 0, 59);
        this.teeth2.setPos(0.0F, -1.0F, -0.7F);
        this.teeth2.addBox(-1.0F, -0.5F, -4.0F, 2.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.snout = new WRModelPart(this, 0, 44);
        this.snout.setPos(0.0F, 0.0F, -3.8F);
        this.snout.addBox(-1.5F, -1.0F, -5.0F, 3.0F, 2.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.eyeL = new WRModelPart(this, 0, 0);
        this.eyeL.mirror = true;
        this.eyeL.setPos(1.7F, -1.0F, -3.5F);
        this.eyeL.addBox(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(eyeL, 0.0F, 0.3186971254089062F, 0.0F);
        this.fin4 = new WRModelPart(this, 15, 81);
        this.fin4.setPos(0.01F, -2.0F, -1.5F);
        this.fin4.addBox(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.tailfin5R = new WRModelPart(this, 45, 64);
        this.tailfin5R.mirror = true;
        this.tailfin5R.setPos(0.5F, -0.6F, 3.0F);
        this.tailfin5R.addBox(-8.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailfin5R, 0.0F, 0.17453292519943295F, 0.0F);
        this.feathers2L = new WRModelPart(this, 27, 27);
        this.feathers2L.setPos(10.0F, -0.5F, -4.5F);
        this.feathers2L.addBox(-11.0F, -0.5F, 0.0F, 21.0F, 1.0F, 10.0F, -0.1F, -0.1F, 0.0F);
        this.backleg2L = new WRModelPart(this, 37, 49);
        this.backleg2L.mirror = true;
        this.backleg2L.setPos(1.5F, 2.6F, -0.6F);
        this.backleg2L.addBox(-1.5F, -0.5F, -1.5F, 3.0F, 4.0F, 3.0F, -0.1F, 0.0F, 0.0F);
        this.setRotateAngle(backleg2L, 0.9292133230565531F, 0.0F, 0.0F);
        this.wing3L = new WRModelPart(this, 99, 42);
        this.wing3L.setPos(19.0F, 0.0F, 0.0F);
        this.wing3L.addBox(0.0F, -1.5F, -5.0F, 15.0F, 3.0F, 10.0F, 0.0F, -0.1F, -0.1F);
        this.setRotateAngle(wing3L, 0.0F, -2.7214917748030256F, 0.0F);
        this.tail4 = new WRModelPart(this, 95, 0);
        this.tail4.setPos(0.0F, 0.0F, 5.0F);
        this.tail4.addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 6.0F, -0.2F, -0.2F, 0.0F);
        this.neck6 = new WRModelPart(this, 0, 27);
        this.neck6.mirror = true;
        this.neck6.setPos(0.0F, 0.0F, -2.0F);
        this.neck6.addBox(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 3.0F, -0.1F, -0.1F, 0.0F);
        this.setRotateAngle(neck6, 0.4993387096482221F, 0.0F, 0.0F);
        this.backfoot2R = new WRModelPart(this, 18, 68);
        this.backfoot2R.mirror = true;
        this.backfoot2R.setPos(0.0F, 2.6F, -0.1F);
        this.backfoot2R.addBox(-1.5F, -0.4F, -1.0F, 3.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(backfoot2R, -0.570024531038187F, 0.0F, 0.0F);
        this.backleg2R = new WRModelPart(this, 37, 49);
        this.backleg2R.setPos(-1.5F, 2.6F, -0.6F);
        this.backleg2R.addBox(-1.5F, -0.5F, -1.5F, 3.0F, 4.0F, 3.0F, -0.1F, 0.0F, 0.0F);
        this.setRotateAngle(backleg2R, 0.9292133230565531F, 0.0F, 0.0F);
        this.tailfin3R = new WRModelPart(this, 45, 64);
        this.tailfin3R.mirror = true;
        this.tailfin3R.setPos(0.5F, -0.8F, 3.0F);
        this.tailfin3R.addBox(-8.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailfin3R, 0.0F, 0.17453292519943295F, 0.0F);
        this.finger4R = new WRModelPart(this, 55, 90);
        this.finger4R.mirror = true;
        this.finger4R.setPos(-15.0F, -0.77F, -0.5F);
        this.finger4R.addBox(-28.0F, -0.5F, -1.0F, 28.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger4R, 0.0F, 0.27314402127920984F, 0.0F);
        this.fin11 = new WRModelPart(this, 13, 75);
        this.fin11.setPos(0.0F, -1.5F, 3.0F);
        this.fin11.addBox(0.0F, -4.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.fin5 = new WRModelPart(this, 22, 72);
        this.fin5.setPos(0.0F, -2.0F, -1.5F);
        this.fin5.addBox(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.finger2L = new WRModelPart(this, 0, 94);
        this.finger2L.setPos(15.0F, -0.79F, -2.5F);
        this.finger2L.addBox(0.0F, -0.5F, -1.0F, 30.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger2L, 0.0F, -0.0911061832922575F, 0.0F);
        this.finger6R = new WRModelPart(this, 65, 94);
        this.finger6R.mirror = true;
        this.finger6R.setPos(-15.0F, -0.75F, 1.5F);
        this.finger6R.addBox(-21.0F, -0.5F, -1.0F, 21.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger6R, 0.0F, 0.500909508638178F, 0.0F);
        this.leg1L = new WRModelPart(this, 19, 27);
        this.leg1L.setPos(3.0F, 1.5F, -2.2F);
        this.leg1L.addBox(0.0F, -1.1F, -1.5F, 2.0F, 6.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(leg1L, 0.5801474726576871F, 0.0F, 0.0F);
        this.neck4 = new WRModelPart(this, 0, 27);
        this.neck4.mirror = true;
        this.neck4.setPos(0.0F, 0.0F, -2.0F);
        this.neck4.addBox(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 3.0F, 0.1F, 0.1F, 0.0F);
        this.setRotateAngle(neck4, 0.13962634015954636F, 0.0F, 0.0F);
        this.finger8R = new WRModelPart(this, 116, 90);
        this.finger8R.mirror = true;
        this.finger8R.setPos(-15.0F, -0.73F, 3.5F);
        this.finger8R.addBox(-14.0F, -0.5F, -1.0F, 14.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger8R, 0.0F, 0.956091342937205F, 0.0F);
        this.foot2R = new WRModelPart(this, 18, 68);
        this.foot2R.mirror = true;
        this.foot2R.setPos(0.0F, 2.6F, 0.1F);
        this.foot2R.addBox(-1.5F, -0.4F, -1.0F, 3.0F, 3.0F, 2.0F, -0.15F, 0.0F, 0.0F);
        this.setRotateAngle(foot2R, -0.6410594262680339F, 0.0F, 0.0F);
        this.foot2L = new WRModelPart(this, 18, 68);
        this.foot2L.setPos(0.0F, 2.6F, 0.1F);
        this.foot2L.addBox(-1.5F, -0.4F, -1.0F, 3.0F, 3.0F, 2.0F, -0.15F, 0.0F, 0.0F);
        this.setRotateAngle(foot2L, -0.6410594262680339F, 0.0F, 0.0F);
        this.tail3 = new WRModelPart(this, 95, 0);
        this.tail3.setPos(0.0F, 0.0F, 5.0F);
        this.tail3.addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.head = new WRModelPart(this, 0, 35);
        this.head.setPos(0.01F, 0.01F, -2.0F);
        this.head.addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(head, 0.27279496408408455F, 0.0F, 0.0F);
        this.finger5L = new WRModelPart(this, 0, 90);
        this.finger5L.setPos(15.0F, -0.76F, 0.5F);
        this.finger5L.addBox(0.0F, -0.5F, -1.0F, 25.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger5L, 0.0F, -0.3642502295386026F, 0.0F);
        this.fin12 = new WRModelPart(this, 13, 75);
        this.fin12.setPos(0.01F, -1.0F, 3.0F);
        this.fin12.addBox(0.0F, -4.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.fin9 = new WRModelPart(this, 13, 75);
        this.fin9.setPos(0.0F, -2.0F, 3.0F);
        this.fin9.addBox(0.0F, -4.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.tail1 = new WRModelPart(this, 72, 0);
        this.tail1.setPos(0.0F, -0.7F, 8.0F);
        this.tail1.addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.tail8 = new WRModelPart(this, 135, 0);
        this.tail8.setPos(0.0F, 0.0F, 4.5F);
        this.tail8.addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 5.0F, -0.2F, -0.2F, 0.0F);
        this.tail7 = new WRModelPart(this, 135, 0);
        this.tail7.setPos(0.0F, 0.0F, 4.5F);
        this.tail7.addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.teeth1 = new WRModelPart(this, 0, 65);
        this.teeth1.setPos(0.01F, 1.4F, -0.81F);
        this.teeth1.addBox(-1.0F, -0.5F, -4.0F, 2.0F, 1.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.backfoot2L = new WRModelPart(this, 18, 68);
        this.backfoot2L.setPos(0.0F, 2.6F, -0.1F);
        this.backfoot2L.addBox(-1.5F, -0.4F, -1.0F, 3.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(backfoot2L, -0.570024531038187F, 0.0F, 0.0F);
        this.finger7R = new WRModelPart(this, 110, 94);
        this.finger7R.mirror = true;
        this.finger7R.setPos(-15.0F, -0.74F, 2.5F);
        this.finger7R.addBox(-17.0F, -0.5F, -1.0F, 17.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger7R, 0.0F, 0.6829473549475088F, 0.0F);
        this.foot1R = new WRModelPart(this, 18, 61);
        this.foot1R.setPos(0.0F, 3.6F, 0.2F);
        this.foot1R.addBox(-1.5F, -0.1F, -1.0F, 3.0F, 3.0F, 2.0F, -0.1F, 0.0F, 0.0F);
        this.setRotateAngle(foot1R, -0.7723081940074907F, 0.0F, 0.0F);
        this.finger5R = new WRModelPart(this, 0, 90);
        this.finger5R.mirror = true;
        this.finger5R.setPos(-15.0F, -0.76F, 0.5F);
        this.finger5R.addBox(-25.0F, -0.5F, -1.0F, 25.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger5R, 0.0F, 0.3642502295386026F, 0.0F);
        this.finger3R = new WRModelPart(this, 0, 94);
        this.finger3R.mirror = true;
        this.finger3R.setPos(-15.0F, -0.78F, -1.5F);
        this.finger3R.addBox(-30.0F, -0.5F, -1.0F, 30.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger3R, 0.0F, 0.18203784630933073F, 0.0F);
        this.finger3L = new WRModelPart(this, 0, 94);
        this.finger3L.setPos(15.0F, -0.78F, -1.5F);
        this.finger3L.addBox(0.0F, -0.5F, -1.0F, 30.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger3L, 0.0F, -0.18203784630933073F, 0.0F);
        this.feathers3R = new WRModelPart(this, 27, 38);
        this.feathers3R.setPos(-7.5F, -0.5F, -3.0F);
        this.feathers3R.addBox(-7.5F, -0.5F, -1.5F, 15.0F, 1.0F, 10.0F, -0.1F, -0.2F, 0.0F);
        this.fin13 = new WRModelPart(this, 13, 75);
        this.fin13.setPos(0.0F, -1.0F, 3.0F);
        this.fin13.addBox(0.0F, -4.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.finger8L_1 = new WRModelPart(this, 122, 86);
        this.finger8L_1.setPos(15.0F, -0.72F, 4.5F);
        this.finger8L_1.addBox(0.0F, -0.5F, -1.0F, 11.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger8L_1, 0.0F, -1.2747885016356248F, 0.0F);
        this.tailfin1R = new WRModelPart(this, 45, 64);
        this.tailfin1R.mirror = true;
        this.tailfin1R.setPos(1.5F, -1.0F, 3.0F);
        this.tailfin1R.addBox(-8.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailfin1R, 0.0F, 0.17453292519943295F, 0.0F);
        this.tailfin4R = new WRModelPart(this, 45, 64);
        this.tailfin4R.mirror = true;
        this.tailfin4R.setPos(0.5F, -0.79F, 3.0F);
        this.tailfin4R.addBox(-8.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailfin4R, 0.0F, 0.17453292519943295F, 0.0F);
        this.fin6 = new WRModelPart(this, 15, 81);
        this.fin6.setPos(0.01F, -2.0F, -1.5F);
        this.fin6.addBox(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.leg1R = new WRModelPart(this, 19, 27);
        this.leg1R.mirror = true;
        this.leg1R.setPos(-3.0F, 1.5F, -2.2F);
        this.leg1R.addBox(-2.0F, -1.1F, -1.5F, 2.0F, 6.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(leg1R, 0.5801474726576871F, 0.0F, 0.0F);
        this.backleg3L = new WRModelPart(this, 37, 59);
        this.backleg3L.mirror = true;
        this.backleg3L.setPos(0.0F, 2.8F, 0.1F);
        this.backleg3L.addBox(-1.5F, -0.8F, -0.6F, 3.0F, 5.0F, 2.0F, -0.2F, 0.0F, 0.0F);
        this.setRotateAngle(backleg3L, -0.9384635467868342F, 0.0F, 0.0F);
        this.fin10 = new WRModelPart(this, 13, 75);
        this.fin10.setPos(0.01F, -1.5F, 3.0F);
        this.fin10.addBox(0.0F, -4.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.backleg1L = new WRModelPart(this, 18, 49);
        this.backleg1L.setPos(2.5F, 0.6F, 6.0F);
        this.backleg1L.addBox(0.0F, -1.6F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(backleg1L, -0.17749998625940386F, 0.0F, 0.0F);
        this.tailfin8L = new WRModelPart(this, 54, 64);
        this.tailfin8L.setPos(0.0F, -0.39F, 3.0F);
        this.tailfin8L.addBox(-8.0F, 0.0F, -3.0F, 16.0F, 0.0F, 16.0F, 0.0F, 0.0F, 0.0F);
        this.earR = new WRModelPart(this, 0, 68);
        this.earR.setPos(-2.0F, -1.0F, -2.0F);
        this.earR.addBox(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(earR, -1.0471975511965976F, -0.45535640450848164F, 0.0F);
        this.neck5 = new WRModelPart(this, 0, 27);
        this.neck5.setPos(0.0F, 0.0F, -2.0F);
        this.neck5.addBox(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(neck5, 0.4363323129985824F, 0.0F, 0.0F);
        this.backfoot1L = new WRModelPart(this, 18, 61);
        this.backfoot1L.mirror = true;
        this.backfoot1L.setPos(0.0F, 3.6F, 0.7F);
        this.backfoot1L.addBox(-1.5F, -0.1F, -1.1F, 3.0F, 3.0F, 2.0F, -0.1F, 0.0F, 0.0F);
        this.setRotateAngle(backfoot1L, -0.7913322855173901F, 0.0F, 0.0F);
        this.finger8R_1 = new WRModelPart(this, 122, 86);
        this.finger8R_1.mirror = true;
        this.finger8R_1.setPos(-15.0F, -0.72F, 4.5F);
        this.finger8R_1.addBox(-11.0F, -0.5F, -1.0F, 11.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(finger8R_1, 0.0F, 1.2747885016356248F, 0.0F);
        this.feathers1R = new WRModelPart(this, 27, 16);
        this.feathers1R.mirror = true;
        this.feathers1R.setPos(-1.5F, -0.5F, 0.5F);
        this.feathers1R.addBox(-19.0F, -0.5F, -1.5F, 19.0F, 1.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.finger1R = new WRModelPart(this, 0, 90);
        this.finger1R.mirror = true;
        this.finger1R.setPos(-15.0F, -0.8F, -3.5F);
        this.finger1R.addBox(-25.0F, -0.5F, -1.0F, 25.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.leg2L = new WRModelPart(this, 37, 59);
        this.leg2L.mirror = true;
        this.leg2L.setPos(1.0F, 4.7F, 0.0F);
        this.leg2L.addBox(-1.5F, -0.8F, -1.0F, 3.0F, 5.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(leg2L, -0.7305948355374707F, 0.0F, 0.0F);
        this.backleg1R = new WRModelPart(this, 18, 49);
        this.backleg1R.mirror = true;
        this.backleg1R.setPos(-2.5F, 0.6F, 6.0F);
        this.backleg1R.addBox(-3.0F, -1.6F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(backleg1R, -0.17749998625940386F, 0.0F, 0.0F);
        this.fin1 = new WRModelPart(this, 13, 72);
        this.fin1.setPos(0.0F, -2.0F, -1.5F);
        this.fin1.addBox(0.0F, -4.0F, -2.0F, 0.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.tailfin5L = new WRModelPart(this, 45, 64);
        this.tailfin5L.setPos(-0.5F, -0.6F, 3.0F);
        this.tailfin5L.addBox(0.0F, 0.0F, -3.0F, 8.0F, 0.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailfin5L, 0.0F, -0.17453292519943295F, 0.0F);
        this.fin14 = new WRModelPart(this, 13, 75);
        this.fin14.setPos(0.01F, -0.5F, 3.0F);
        this.fin14.addBox(0.0F, -4.0F, -3.0F, 0.0F, 4.0F, 6.0F, 0.0F, 0.0F, 0.0F);
        this.body2.addChild(this.fin7);
        this.wing2R.addChild(this.wing3R);
        this.backleg2R.addChild(this.backleg3R);
        this.neck2.addChild(this.neck3);
        this.leg1R.addChild(this.leg2R);
        this.tail7.addChild(this.tailfin7L);
        this.neck5.addChild(this.fin2);
        this.wing1L.addChild(this.feathers1L);
        this.wing3L.addChild(this.finger4L);
        this.tail1.addChild(this.tailfin1L);
        this.tail1.addChild(this.tail2);
        this.head.addChild(this.eyeR);
        this.tail1.addChild(this.fin8);
        this.neck1.addChild(this.neck2);
        this.tail3.addChild(this.tailfin3L);
        this.body1.addChild(this.wing1R);
        this.leg2L.addChild(this.foot1L);
        this.backleg3R.addChild(this.backfoot1R);
        this.body1.addChild(this.wing1L);
        this.head.addChild(this.jaw);
        this.wing3L.addChild(this.finger8L);
        this.wing3L.addChild(this.finger1L);
        this.tail6.addChild(this.tailfin6L);
        this.tail8.addChild(this.fin15);
        this.tail4.addChild(this.tail5);
        this.wing3L.addChild(this.finger7L);
        this.tail4.addChild(this.tailfin4L);
        this.tail5.addChild(this.tail6);
        this.tail2.addChild(this.tailfin2L);
        this.neck4.addChild(this.fin3);
        this.tail2.addChild(this.tailfin2R);
        this.tail6.addChild(this.tailfin6R);
        this.wing1R.addChild(this.wing2R);
        this.wing3L.addChild(this.finger6L);
        this.wing1L.addChild(this.wing2L);
        this.body1.addChild(this.neck1);
        this.wing3L.addChild(this.feathers3L);
        this.body1.addChild(this.body2);
        this.head.addChild(this.earL);
        this.wing3R.addChild(this.finger2R);
        this.wing2R.addChild(this.feathersR);
        this.tail7.addChild(this.tailfin7R);
        this.jaw.addChild(this.teeth2);
        this.head.addChild(this.snout);
        this.head.addChild(this.eyeL);
        this.neck3.addChild(this.fin4);
        this.tail5.addChild(this.tailfin5R);
        this.wing2L.addChild(this.feathers2L);
        this.backleg1L.addChild(this.backleg2L);
        this.wing2L.addChild(this.wing3L);
        this.tail3.addChild(this.tail4);
        this.neck5.addChild(this.neck6);
        this.backfoot1R.addChild(this.backfoot2R);
        this.backleg1R.addChild(this.backleg2R);
        this.tail3.addChild(this.tailfin3R);
        this.wing3R.addChild(this.finger4R);
        this.tail4.addChild(this.fin11);
        this.neck2.addChild(this.fin5);
        this.wing3L.addChild(this.finger2L);
        this.wing3R.addChild(this.finger6R);
        this.body1.addChild(this.leg1L);
        this.neck3.addChild(this.neck4);
        this.wing3R.addChild(this.finger8R);
        this.foot1R.addChild(this.foot2R);
        this.foot1L.addChild(this.foot2L);
        this.tail2.addChild(this.tail3);
        this.neck6.addChild(this.head);
        this.wing3L.addChild(this.finger5L);
        this.tail5.addChild(this.fin12);
        this.tail2.addChild(this.fin9);
        this.body2.addChild(this.tail1);
        this.tail7.addChild(this.tail8);
        this.tail6.addChild(this.tail7);
        this.snout.addChild(this.teeth1);
        this.backfoot1L.addChild(this.backfoot2L);
        this.wing3R.addChild(this.finger7R);
        this.leg2R.addChild(this.foot1R);
        this.wing3R.addChild(this.finger5R);
        this.wing3R.addChild(this.finger3R);
        this.wing3L.addChild(this.finger3L);
        this.wing3R.addChild(this.feathers3R);
        this.tail6.addChild(this.fin13);
        this.wing3L.addChild(this.finger8L_1);
        this.tail1.addChild(this.tailfin1R);
        this.tail4.addChild(this.tailfin4R);
        this.neck1.addChild(this.fin6);
        this.body1.addChild(this.leg1R);
        this.backleg2L.addChild(this.backleg3L);
        this.tail3.addChild(this.fin10);
        this.body2.addChild(this.backleg1L);
        this.tail8.addChild(this.tailfin8L);
        this.head.addChild(this.earR);
        this.neck4.addChild(this.neck5);
        this.backleg3L.addChild(this.backfoot1L);
        this.wing3R.addChild(this.finger8R_1);
        this.wing1R.addChild(this.feathers1R);
        this.wing3R.addChild(this.finger1R);
        this.leg1L.addChild(this.leg2L);
        this.body2.addChild(this.backleg1R);
        this.neck6.addChild(this.fin1);
        this.tail5.addChild(this.tailfin5L);
        this.tail7.addChild(this.fin14);

        this.headArray = new WRModelPart[]{neck1, neck2, neck3, neck4, neck5, neck6, head};
        this.tailArray = new WRModelPart[]{tail1, tail2, tail3, tail4, tail5, tail6, tail7, tail8};

        this.fingerArrays = new WRModelPart[][]{
                {finger1R, finger2R, finger3R, finger4R, finger5R, finger6R, finger7R, finger8R, finger8R_1},
                {finger1L, finger2L, finger3L, finger4L, finger5L, finger6L, finger7L, finger8L, finger8L_1}
        };

        setDefaultPose();
    }

    @Override
    public ResourceLocation getTexture(AlpineEntity entity)
    {
        int variant = entity.getVariant();
        if (TEXTURES[variant] == null)
        {
            String path = FOLDER + "alpine/body_" + variant;
            return TEXTURES[variant] = Wyrmroost.id(path + ".png");
        }
        return TEXTURES[variant];
    }

    @Override
    public void scale(AlpineEntity entity, PoseStack ms, float partialTicks)
    {
        super.scale(entity, ms, partialTicks);
        ms.scale(2f, 2f, 2f);
        ms.translate(0, 0, -0.2f);
    }

    @Override
    public float getShadowRadius(AlpineEntity entity)
    {
        return 2f;
    }

    @Override
    public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        body1.render(ms, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(AlpineEntity entity, float limbSwing, float limbSwingAmount, float bob, float netHeadYaw, float headPitch)
    {
        reset();
        animator().tick(entity, this, partialTicks);

        this.bob = bob;

        if (!entity.isSleeping() && !entity.isInSittingPose() && entity.getAnimation() != AlpineEntity.WIND_GUST_ANIMATION)
        {
            if (entity.isFlying()) // flight
            {
                chainWave(headArray, globalSpeed - 0.25f, 0.05f, 3, limbSwing, limbSwingAmount);
                chainWave(tailArray, globalSpeed - 0.25f, -0.05f, -3, limbSwing, limbSwingAmount);

                flap(wing1R, globalSpeed - 0.25f, 0.9f, false, 0, -0.6f, limbSwing, limbSwingAmount);
                walk(wing1R, globalSpeed - 0.25f, 0.4f, false, 0.6f, 0, limbSwing, limbSwingAmount);
                flap(wing2R, globalSpeed - 0.25f, 0.65f, false, -1f, 0.1f, limbSwing, limbSwingAmount);
                walk(wing2R, globalSpeed - 0.25f, 0.15f, false, 0.6f, 0, limbSwing, limbSwingAmount);
                flap(wing3R, globalSpeed - 0.25f, 0.3f, false, -1.5f, -0.2f, limbSwing, limbSwingAmount);
                walk(wing3R, globalSpeed - 0.25f, 0.05f, false, 0.6f, 0, limbSwing, limbSwingAmount);
                flap(wing1L, globalSpeed - 0.25f, 0.9f, true, 0, -0.6f, limbSwing, limbSwingAmount);
                walk(wing1L, globalSpeed - 0.25f, 0.3f, false, 0.6f, 0, limbSwing, limbSwingAmount);
                flap(wing2L, globalSpeed - 0.25f, 0.65f, true, -1f, 0.1f, limbSwing, limbSwingAmount);
                walk(wing2L, globalSpeed - 0.25f, 0.05f, false, 0.6f, 0, limbSwing, limbSwingAmount);
                flap(wing3L, globalSpeed - 0.25f, 0.4f, true, -1.5f, -0.2f, limbSwing, limbSwingAmount);
                walk(wing3L, globalSpeed - 0.25f, 0.15f, false, 0.6f, 0, limbSwing, limbSwingAmount);

                walk(leg1L, globalSpeed - 0.25f, 0.075f, true, 0, 0, limbSwing, limbSwingAmount);
                walk(leg1R, globalSpeed - 0.25f, 0.075f, true, 0, 0, limbSwing, limbSwingAmount);
                walk(backleg1L, globalSpeed - 0.25f, 0.075f, true, 0, 0, limbSwing, limbSwingAmount);
                walk(backleg1R, globalSpeed - 0.25f, 0.075f, true, 0, 0, limbSwing, limbSwingAmount);

                if (entity.getDeltaMovement().y < 0 && entity.getDeltaMovement().length() > 0.1)
                {
                    flap(wing1R, globalSpeed - 0.3f, 0.15f, false, 0, 0, bob, 0.5f);
                    walk(wing1R, globalSpeed + 0.65f, 0.1f, false, 0, 0, bob, 0.5f);
                    flap(wing1L, globalSpeed - 0.3f, 0.15f, true, 0, 0, bob, 0.5f);
                    walk(wing1L, globalSpeed + 0.65f, 0.1f, false, 0, 0, bob, 0.5f);
                }

                boolean wingsDown = wing1R.zRot < 0.8;
                if (!entity.wingsDown && wingsDown) entity.flapWings();
                entity.wingsDown = wingsDown;
            }
            else // walk cycle
            {
                body1.y += bob(0.8f, 0.2f, false, limbSwing, limbSwingAmount);

                walk(leg1L, globalSpeed, 1.1f, false, 0, 0.2f, limbSwing, limbSwingAmount);
                walk(leg2L, globalSpeed, -1f, false, -1.6f, -1f, limbSwing, limbSwingAmount);
                walk(foot1L, globalSpeed, 0.3f, false, -1.6f, 0.5f, limbSwing, limbSwingAmount);
                walk(foot2L, globalSpeed, 1.5f, false, -2f, 1.3f, limbSwing, limbSwingAmount);

                walk(leg1R, globalSpeed, 1.1f, true, 0, -0.2f, limbSwing, limbSwingAmount);
                walk(leg2R, globalSpeed, -1f, true, -1.6f, 1f, limbSwing, limbSwingAmount);
                walk(foot1R, globalSpeed, 0.3f, true, -1.6f, -0.5f, limbSwing, limbSwingAmount);
                walk(foot2R, globalSpeed, 1.5f, true, -2f, -1.3f, limbSwing, limbSwingAmount);

                walk(backleg1L, globalSpeed, -1.25f, false, 0, 0.5f, limbSwing, limbSwingAmount);
                walk(backleg2L, globalSpeed, 2f, false, 0.4f, 0.2f, limbSwing, limbSwingAmount);
                walk(backleg3L, globalSpeed, -2.2f, false, 0.4f, 0f, limbSwing, limbSwingAmount);
                walk(backfoot1L, globalSpeed, 1f, false, 0.4f, 0, limbSwing, limbSwingAmount);
                walk(backfoot2L, globalSpeed, 1.25f, false, 0.6f, 0.6f, limbSwing, limbSwingAmount);

                walk(backleg1R, globalSpeed, -1.25f, true, 0, -0.5f, limbSwing, limbSwingAmount);
                walk(backleg2R, globalSpeed, 2f, true, 0.4f, -0.2f, limbSwing, limbSwingAmount);
                walk(backleg3R, globalSpeed, -2.2f, true, 0.4f, 0f, limbSwing, limbSwingAmount);
                walk(backfoot1R, globalSpeed, 1f, true, 0.4f, 0, limbSwing, limbSwingAmount);
                walk(backfoot2R, globalSpeed, 1.25f, true, 0.6f, -0.6f, limbSwing, limbSwingAmount);
            }
        }

        sit(entity.sitTimer.get(partialTicks));
        sleep(entity.sleepTimer.get(partialTicks));
        flight(entity.flightTimer.get(partialTicks));
        idle(bob);

        netHeadYaw = Mth.wrapDegrees(netHeadYaw);
        if (!entity.isSleeping()) faceTarget(netHeadYaw, headPitch, 1, headArray);
        if (entity.flightTimer.get() == 1) body1.zRot = -(netHeadYaw * Mafs.PI / 180f) * 0.4f;
    }

    public void idle(float frame)
    {
        chainWave(headArray, globalSpeed - 0.45f, 0.04f, -1, frame, 0.5f);
        if (!entity.isInSittingPose() && !entity.isSleeping())
            chainWave(tailArray, globalSpeed - 0.45f, 0.08f, -3, frame, 0.5f);
        chainSwing(tailArray, globalSpeed - 0.42f, 0.08f, -3, frame, 0.5f);
        flap(wing1L, globalSpeed - 0.4f, 0.05f, false, 0, 0, frame, 0.5f);
        swing(wing1L, globalSpeed - 0.42f, 0.04f, false, 0, 0, frame, 0.5f);
        swing(wing2L, globalSpeed - 0.44f, 0.05f, false, 0, 0, frame, 0.5f);
        flap(wing1R, globalSpeed - 0.4f, 0.05f, true, 0, 0, frame, 0.5f);
        swing(wing1R, globalSpeed - 0.42f, 0.04f, true, 0, 0, frame, 0.5f);
        swing(wing2R, globalSpeed - 0.44f, 0.05f, true, 0, 0, frame, 0.5f);
    }

    public void sit(float frame)
    {
        setTime(frame);

        move(body1, 0, 4.8f, 0);

        rotate(leg1L, -1f, 0, 0);
        rotate(leg2L, -0.45f, 0, 0);
        rotate(foot1L, 0.7f, 0, 0);
        rotate(foot2L, 0.8f, 0, 0);

        rotate(backleg1L, -0.6f, -0.4f, 0);
        rotate(backleg2L, 0.4f, 0, 0);
        rotate(backleg3L, -1.15f, 0, 0);
        rotate(backfoot1L, 0.6f, 0, 0);
        rotate(backfoot2L, 0.8f, 0, 0);

        rotate(leg1R, -1f, 0, 0);
        rotate(leg2R, -0.45f, 0, 0);
        rotate(foot1R, 0.7f, 0, 0);
        rotate(foot2R, 0.8f, 0, 0);

        rotate(backleg1R, -0.6f, 0.4f, 0);
        rotate(backleg2R, 0.4f, 0, 0);
        rotate(backleg3R, -1.15f, 0, 0);
        rotate(backfoot1R, 0.6f, 0, 0);
        rotate(backfoot2R, 0.8f, 0, 0);

        for (int i = 0; i < tailArray.length; i++)
            rotate(tailArray[i], i > (tailArray.length - 2) / 2? 0.06f : -0.06f, 0, 0);
    }

    public void sleep(float frame)
    {
        setTime(frame);
        if (frame == 1)
        {
            eyeL.yRot += 3.1f;
            eyeR.yRot -= 3.1f;
        }

        rotate(neck1, 0.3f, 0, 0);
        rotate(neck2, 0.5f, 0, 0);
        rotate(neck3, 0.4f, 0, 0);
        rotate(neck4, -0.1f, 0, 0);
        rotate(neck5, -0.4f, 0, 0);
        rotate(neck6, -0.5f, 0, 0);
        rotate(head, -0.3f, 0, 0);

        for (WRModelPart part : headArray) rotate(part, 0.08f, 0.3f, 0);
        for (WRModelPart part : tailArray) rotate(part, 0, -0.35f, -0.035f);
    }


    private void flight(float v)
    {
        setTime(v);

        rotate(neck1, 0.4f, 0, 0);
        rotate(neck2, 0.48f, 0, 0);
        rotate(neck3, 0.41f, 0, 0);
        rotate(neck4, -0.14f, 0, 0);
        rotate(neck5, -0.45f, 0, 0);
        rotate(neck6, -0.5f, 0, 0);
        rotate(head, -0.28f, 0, 0);

        rotate(wing1L, 0, 1f, 0.15f);
        rotate(wing2L, 0, -2.5f, 0.2f);
        rotate(wing3L, 0, 2.8f, -0.3f);
        move(feathers1L, 0, 0, 7.5f);
        move(feathers2L, 0, 0, 7.5f);
        move(feathers3L, 0, 0, 7.5f);

        rotate(wing1R, 0, -1f, -0.15f);
        rotate(wing2R, 0, 2.5f, -0.2f);
        rotate(wing3R, 0, -2.8f, 0.3f);
        move(feathers1R, 0, 0, 7.5f);
        move(feathersR, 0, 0, 7.5f);
        move(feathers3R, 0, 0, 7.5f);

        rotate(leg1L, 0.3f, 0, 0);
        rotate(leg2L, 0.1f, 0, 0);
        rotate(foot1L, 1.25f, 0, 0);
        rotate(foot2L, 0.8f, 0, 0);

        rotate(backleg1L, 1f, 0, 0);
        rotate(backleg2L, -0.5f, 0, 0);
        rotate(backleg3L, 0.2f, 0, 0);
        rotate(backfoot1L, 1f, 0, 0);
        rotate(backfoot2L, 0.9f, 0, 0);

        rotate(leg1R, 0.3f, 0, 0);
        rotate(leg2R, 0.1f, 0, 0);
        rotate(foot1R, 1.25f, 0, 0);
        rotate(foot2R, 0.8f, 0, 0);

        rotate(backleg1R, 1f, 0, 0);
        rotate(backleg2R, -0.5f, 0, 0);
        rotate(backleg3R, 0.2f, 0, 0);
        rotate(backfoot1R, 1f, 0, 0);
        rotate(backfoot2R, 0.9f, 0, 0);
    }

    public void roarAnimation()
    {
        ModelAnimator animator = animator(); // reduce method calls

        animator.startKeyframe(12);

        if (!entity.isFlying())
        {
            animator.rotate(neck1, -0.1f, 0, 0)
                    .rotate(neck2, 0.35f, 0, 0)
                    .rotate(neck3, 0.35f, 0, 0)
                    .rotate(neck4, -0.15f, 0, 0)
                    .rotate(neck5, -0.5f, 0, 0)
                    .rotate(neck6, -0.4f, 0, 0)
                    .rotate(head, -0.3f, 0, 0)

                    .rotate(wing1L, -1, 1f, 0)
                    .rotate(wing2L, 0, -2f, 0)
                    .rotate(wing3L, 0, 3f, 0)

                    .move(feathers1L, 0, 0, 7.5f)
                    .move(feathers2L, 0, 0, 7.5f)
                    .move(feathers3L, 0, 0, 7.5f)

                    .rotate(wing1R, -1, -1f, 0)
                    .rotate(wing2R, 0, 2f, 0)
                    .rotate(wing3R, 0, -3f, 0)

                    .move(feathersR, 0, 0, 7.5f)
                    .move(feathers1R, 0, 0, 7.5f)
                    .move(feathers3R, 0, 0, 7.5f);

            for (int i = 0; i < fingerArrays.length; i++)
            {
                for (WRModelPart finger : fingerArrays[i])
                {
                    animator.rotate(finger, 0, i == 1? 0.4f : -0.4f, 0);
                }
            }
        }

        animator.rotate(jaw, 0.8f, 0, 0);
        int tick = entity.getAnimationTick();
        if (tick < 71)
        {
            chainFlap(headArray, globalSpeed, 0.1f, 2.5, bob, 0.5f);
            chainSwing(headArray, globalSpeed, 0.035f, 1, bob, 0.5f);
        }
        animator.endKeyframe();
        animator.setStaticKeyframe(60)
                .resetKeyframe(12);
    }


    public void windGustAnimation()
    {
        ModelAnimator animator = animator(); // reduce method calls

        animator.startKeyframe(5)
                .rotate(body1, -1, 0, 0)
                .rotate(wing1L, 0, 0, -0.5f)
                .rotate(wing2L, 0, 0, -0.6f)
                .rotate(wing1R, 0, 0, 0.5f)
                .rotate(wing2R, 0, 0, 0.6f);
        for (WRModelPart head : headArray) animator.rotate(head, 0.2f, 0, 0);
        animator.endKeyframe();

        animator.startKeyframe(5)
                .rotate(body1, -1, 0, 0)
                .rotate(wing1L, 0, 0, 1.25f)
                .rotate(wing2L, 0, 0, 0.8f)
                .rotate(wing3L, 0, 0, 0.8f)
                .rotate(wing1R, 0, 0, -1.25f)
                .rotate(wing2R, 0, 0, -0.8f)
                .rotate(wing3R, 0, 0, -0.8f);
        for (WRModelPart head : headArray) animator.rotate(head, 0.2f, 0, 0);
        animator.endKeyframe();

        animator.resetKeyframe(15);
    }

    public void biteAnimation()
    {
        animator().startKeyframe(3)
                .rotate(neck1, -0.3f, 0, 0)
                .rotate(neck2, 0.1f, 0, 0)
                .rotate(neck3, 0.4f, 0, 0)
                .rotate(head, 0.55f, 0, 0)
                .rotate(jaw, 0.8f, 0, 0)
                .endKeyframe();

        animator().startKeyframe(3)
                .rotate(neck1, 0.3f, 0, 0)
                .rotate(neck2, 0.25f, 0, 0)
                .rotate(neck3, 0.25f, 0, 0)
                .rotate(head, 0.35f, 0, 0)
                .endKeyframe();

        animator().resetKeyframe(4);
    }
}
*/