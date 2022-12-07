package com.github.shannieann.wyrmroost.client.model.entity;

/*import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.dragon.RoyalRedEntity;
import com.mojang.blaze3d.matrix.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ResourceLocation;

/**
 * WRRoyalRed - Ukan
 * Created using Tabula 8.0.0

public class RoyalRedModel extends DragonEntityModel<RoyalRedEntity>
{
    public static final ResourceLocation[] TEXTURES = new ResourceLocation[20];

    public WRModelPart body1;
    public WRModelPart body2;
    public WRModelPart neck1;
    public WRModelPart arm1L;
    public WRModelPart arm1R;
    public WRModelPart spike4;
    public WRModelPart wingR1;
    public WRModelPart wingL1;
    public WRModelPart tail1;
    public WRModelPart leg1R;
    public WRModelPart leg1L;
    public WRModelPart spike5;
    public WRModelPart tail2;
    public WRModelPart spike6;
    public WRModelPart tail3;
    public WRModelPart spike7;
    public WRModelPart tail4;
    public WRModelPart spike8;
    public WRModelPart tail5;
    public WRModelPart spike9;
    public WRModelPart tail6;
    public WRModelPart spike10;
    public WRModelPart tail7;
    public WRModelPart spike11;
    public WRModelPart tail8;
    public WRModelPart spike12;
    public WRModelPart tailspikeL1;
    public WRModelPart tailspikeL2;
    public WRModelPart tailspikeR1;
    public WRModelPart tailspikeR2;
    public WRModelPart spike13;
    public WRModelPart leg2R;
    public WRModelPart leg3R;
    public WRModelPart footR;
    public WRModelPart toe1R;
    public WRModelPart toe2R;
    public WRModelPart toe3R;
    public WRModelPart leg2L;
    public WRModelPart leg3L;
    public WRModelPart footL;
    public WRModelPart toe1L;
    public WRModelPart toe2L;
    public WRModelPart toe3L;
    public WRModelPart neck2;
    public WRModelPart spike3;
    public WRModelPart neck3;
    public WRModelPart spike2;
    public WRModelPart head;
    public WRModelPart spike1;
    public WRModelPart snout;
    public WRModelPart jaw;
    public WRModelPart frill1;
    public WRModelPart frillL;
    public WRModelPart frillR;
    public WRModelPart eyeL;
    public WRModelPart eyeR;
    public WRModelPart teeth1;
    public WRModelPart teeth2;
    public WRModelPart crownHorn1;
    public WRModelPart crownHornL1;
    public WRModelPart crownHornL2;
    public WRModelPart crownHornR2;
    public WRModelPart crownHornR1;
    public WRModelPart arm2L;
    public WRModelPart palmL;
    public WRModelPart claw3L;
    public WRModelPart claw2L;
    public WRModelPart claw1L;
    public WRModelPart arm2R;
    public WRModelPart palmR;
    public WRModelPart claw3R;
    public WRModelPart claw2R;
    public WRModelPart claw1R;
    public WRModelPart wingR2;
    public WRModelPart membraneR1;
    public WRModelPart palmR_1;
    public WRModelPart membraneR3;
    public WRModelPart fingerR1part1;
    public WRModelPart fingerR2part1;
    public WRModelPart fingerR4part1;
    public WRModelPart fingerR3part1;
    public WRModelPart fingerR1part2;
    public WRModelPart membraneR7;
    public WRModelPart fingerR2part2;
    public WRModelPart membraneR6;
    public WRModelPart fingerR4part2;
    public WRModelPart membraneR4;
    public WRModelPart fingerR3part2;
    public WRModelPart membraneR5;
    public WRModelPart membraneR2;
    public WRModelPart wingL2;
    public WRModelPart membraneL1;
    public WRModelPart palmL_1;
    public WRModelPart membraneL3;
    public WRModelPart fingerL1part1;
    public WRModelPart fingerL2part1;
    public WRModelPart fingerL4part1;
    public WRModelPart fingerL3part1;
    public WRModelPart fingerL1part2;
    public WRModelPart membraneL7;
    public WRModelPart fingerL2part2;
    public WRModelPart membraneL6;
    public WRModelPart fingerL4part2;
    public WRModelPart membraneL4;
    public WRModelPart fingerL3part2;
    public WRModelPart membraneL5;
    public WRModelPart membraneL2;

    public WRModelPart[][] toes;
    public WRModelPart[][] wings;
    public WRModelPart[] tails;
    public WRModelPart[] neck;

    public RoyalRedModel()
    {
        this.texWidth = 200;
        this.texHeight = 200;
        this.neck3 = new WRModelPart(this, 77, 0);
        this.neck3.setPos(0.0F, 0.0F, -3.0F);
        this.neck3.addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 4.0F, 0.02F, 0.02F, 0.0F);
        this.setRotateAngle(neck3, 0.4488986716287166F, 0.0F, 0.0F);
        this.wingL1 = new WRModelPart(this, 0, 46);
        this.wingL1.setPos(2.0F, -1.8F, -1.3F);
        this.wingL1.addBox(0.0F, -1.0F, -1.5F, 10.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(wingL1, 0.0F, -0.19547687289441354F, 0.0F);
        this.teeth2 = new WRModelPart(this, 77, 36);
        this.teeth2.setPos(0.0F, -1.0F, -0.7F);
        this.teeth2.addBox(-1.5F, -1.0F, -4.0F, 3.0F, 1.0F, 4.0F, 0.2F, 0.0F, 0.0F);
        this.membraneR2 = new WRModelPart(this, 71, 70);
        this.membraneR2.setPos(-3.0F, -0.01F, 5.5F);
        this.membraneR2.addBox(-10.0F, 0.0F, -5.5F, 10.0F, 0.1F, 11.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(membraneR2, -0.1563815016444822F, 0.0F, -0.4300491170387584F);
        this.leg1R = new WRModelPart(this, 0, 25);
        this.leg1R.setPos(-1.4F, 2.0F, 4.0F);
        this.leg1R.addBox(-3.0F, -1.0F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(leg1R, -0.03909537541112055F, 0.0F, 0.0F);
        this.fingerL2part2 = new WRModelPart(this, 0, 58);
        this.fingerL2part2.setPos(20.0F, 0.0F, 0.0F);
        this.fingerL2part2.addBox(0.0F, -0.5F, -0.5F, 20.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerL2part2, 0.0F, -0.2275909337942703F, 0.0F);
        this.palmR_1 = new WRModelPart(this, 36, 53);
        this.palmR_1.mirror = true;
        this.palmR_1.setPos(-14.5F, 0.01F, 0.01F);
        this.palmR_1.addBox(-2.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(palmR_1, 0.0F, 1.3683381601951652F, 0.0F);
        this.snout = new WRModelPart(this, 77, 20);
        this.snout.setPos(0.0F, -0.4F, -3.0F);
        this.snout.addBox(-2.0F, -1.0F, -5.0F, 4.0F, 2.0F, 5.0F, -0.1F, 0.0F, 0.0F);
        this.setRotateAngle(snout, 0.03909537541112055F, 0.0F, 0.0F);
        this.tail7 = new WRModelPart(this, 54, 30);
        this.tail7.setPos(0.0F, 0.0F, 4.0F);
        this.tail7.addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 5.0F, 0.2F, 0.2F, 0.0F);
        this.toe3L = new WRModelPart(this, 41, 36);
        this.toe3L.mirror = true;
        this.toe3L.setPos(-1.0F, 0.8F, -1.5F);
        this.toe3L.addBox(-0.5F, -1.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(toe3L, 0.0F, 0.4098033003787853F, 0.0F);
        this.membraneR7 = new WRModelPart(this, 89, 3);
        this.membraneR7.setPos(0.0F, 0.1F, 0.0F);
        this.membraneR7.addBox(-40.0F, 0.0F, 0.0F, 40.0F, 0.1F, 15.0F, 0.0F, 0.0F, 0.0F);
        this.tail2 = new WRModelPart(this, 54, 0);
        this.tail2.setPos(0.0F, 0.0F, 4.0F);
        this.tail2.addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 5.0F, -0.2F, -0.2F, 0.0F);
        this.crownHorn1 = new WRModelPart(this, 22, 21);
        this.crownHorn1.setPos(0.0F, -2.5F, 0.1F);
        this.crownHorn1.addBox(-0.5F, -3.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(crownHorn1, 0.0F, -0.7853981633974483F, 0.0F);
        this.jaw = new WRModelPart(this, 77, 29);
        this.jaw.setPos(0.0F, 1.75F, -3.0F);
        this.jaw.addBox(-2.0F, -1.0F, -5.0F, 4.0F, 1.0F, 5.0F, -0.2F, 0.0F, 0.0F);
        this.spike7 = new WRModelPart(this, 28, 43);
        this.spike7.setPos(0.0F, -1.8F, 1.5F);
        this.spike7.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(spike7, 0.3281218860591285F, -0.3117158150588316F, 0.7463027588580033F);
        this.fingerR3part2 = new WRModelPart(this, 43, 58);
        this.fingerR3part2.mirror = true;
        this.fingerR3part2.setPos(-15.0F, 0.0F, 0.0F);
        this.fingerR3part2.addBox(-15.0F, -0.5F, -0.5F, 15.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerR3part2, 0.0F, 0.2275909337942703F, 0.0F);
        this.footR = new WRModelPart(this, 41, 29);
        this.footR.setPos(0.0F, 4.0F, -1.3F);
        this.footR.addBox(-1.5F, 0.0F, -2.0F, 3.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(footR, 0.23474678106428595F, 0.0F, 0.0F);
        this.fingerR2part2 = new WRModelPart(this, 0, 58);
        this.fingerR2part2.mirror = true;
        this.fingerR2part2.setPos(-20.0F, 0.0F, 0.0F);
        this.fingerR2part2.addBox(-20.0F, -0.5F, -0.5F, 20.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerR2part2, 0.0F, 0.2275909337942703F, 0.0F);
        this.spike2 = new WRModelPart(this, 28, 43);
        this.spike2.setPos(0.0F, -1.5F, -3.5F);
        this.spike2.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(spike2, 0.3281218860591285F, -0.3117158150588316F, 0.7463027588580033F);
        this.membraneL2 = new WRModelPart(this, 71, 70);
        this.membraneL2.setPos(3.0F, -0.01F, 5.5F);
        this.membraneL2.addBox(0.0F, 0.0F, -5.5F, 10.0F, 0.1F, 11.0F, 0.0F, 0.0F, 0.0F);
        this.membraneR5 = new WRModelPart(this, 95, 43);
        this.membraneR5.setPos(0.0F, 0.0F, 0.0F);
        this.membraneR5.addBox(-30.0F, 0.0F, -10.0F, 30.0F, 0.1F, 20.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(membraneR5, 0.0F, 0.11728612207217244F, 0.0F);
        this.spike4 = new WRModelPart(this, 28, 43);
        this.spike4.setPos(0.0F, -2.5F, 0.3F);
        this.spike4.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(spike4, 0.3281218860591285F, -0.3117158150588316F, 0.7463027588580033F);
        this.fingerR1part2 = new WRModelPart(this, 0, 58);
        this.fingerR1part2.mirror = true;
        this.fingerR1part2.setPos(-20.0F, 0.0F, 0.0F);
        this.fingerR1part2.addBox(-20.0F, -0.5F, -0.5F, 20.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerR1part2, 0.0F, 0.2275909337942703F, 0.0F);
        this.fingerL3part2 = new WRModelPart(this, 43, 58);
        this.fingerL3part2.setPos(15.0F, 0.0F, 0.0F);
        this.fingerL3part2.addBox(0.0F, -0.5F, -0.5F, 15.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerL3part2, 0.0F, -0.2275909337942703F, 0.0F);
        this.claw1R = new WRModelPart(this, 31, 36);
        this.claw1R.mirror = true;
        this.claw1R.setPos(-0.4F, 1.2F, 0.5F);
        this.claw1R.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, -0.1F);
        this.setRotateAngle(claw1R, 0.5462880425584197F, 0.0F, -0.5462880425584197F);
        this.fingerL3part1 = new WRModelPart(this, 43, 58);
        this.fingerL3part1.setPos(1.0F, -0.1F, 0.0F);
        this.fingerL3part1.addBox(0.0F, -0.5F, -0.5F, 15.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerL3part1, 0.0F, -1.0269517345366246F, 0.0F);
        this.arm1L = new WRModelPart(this, 0, 36);
        this.arm1L.mirror = true;
        this.arm1L.setPos(2.0F, 0.0F, -0.5F);
        this.arm1L.addBox(0.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(arm1L, 0.6112143120378974F, 0.0F, 0.0F);
        this.tail3 = new WRModelPart(this, 54, 11);
        this.tail3.setPos(0.0F, 0.0F, 4.0F);
        this.tail3.addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 5.0F, 0.1F, 0.1F, 0.0F);
        this.claw1L = new WRModelPart(this, 31, 36);
        this.claw1L.setPos(0.4F, 1.2F, 0.5F);
        this.claw1L.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, -0.1F);
        this.setRotateAngle(claw1L, 0.5462880425584197F, 0.0F, 0.5462880425584197F);
        this.fingerL1part2 = new WRModelPart(this, 0, 58);
        this.fingerL1part2.setPos(20.0F, 0.0F, 0.0F);
        this.fingerL1part2.addBox(0.0F, -0.5F, -0.5F, 20.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerL1part2, 0.0F, -0.2275909337942703F, 0.0F);
        this.fingerL2part1 = new WRModelPart(this, 0, 58);
        this.fingerL2part1.setPos(1.4F, -0.2F, 0.5F);
        this.fingerL2part1.addBox(0.0F, -0.5F, -0.5F, 20.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerL2part1, 0.0F, -0.41486477118853543F, 0.0F);
        this.fingerR4part1 = new WRModelPart(this, 45, 55);
        this.fingerR4part1.mirror = true;
        this.fingerR4part1.setPos(-0.5F, 0.0F, 0.5F);
        this.fingerR4part1.addBox(-10.0F, -0.5F, -0.5F, 10.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerR4part1, 0.0F, 1.560847955384851F, 0.0F);
        this.fingerR4part2 = new WRModelPart(this, 45, 55);
        this.fingerR4part2.mirror = true;
        this.fingerR4part2.setPos(-10.0F, 0.0F, 0.0F);
        this.fingerR4part2.addBox(-10.0F, -0.5F, -0.5F, 10.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerR4part2, 0.0F, 0.2275909337942703F, 0.0F);
        this.spike10 = new WRModelPart(this, 28, 43);
        this.spike10.setPos(0.0F, -1.0F, 1.5F);
        this.spike10.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(spike10, 0.3281218860591285F, -0.3117158150588316F, 0.7463027588580033F);
        this.membraneL7 = new WRModelPart(this, 89, 3);
        this.membraneL7.mirror = true;
        this.membraneL7.setPos(0.0F, 0.1F, 0.0F);
        this.membraneL7.addBox(0.0F, 0.0F, 0.0F, 40.0F, 0.1F, 15.0F, 0.0F, 0.0F, 0.0F);
        this.tailspikeL2 = new WRModelPart(this, 21, 15);
        this.tailspikeL2.mirror = true;
        this.tailspikeL2.setPos(0.0F, 0.0F, 3.5F);
        this.tailspikeL2.addBox(0.0F, -0.5F, -1.0F, 6.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailspikeL2, 0.0F, -1.1383037594559906F, -0.27314402127920984F);
        this.spike13 = new WRModelPart(this, 28, 43);
        this.spike13.setPos(0.0F, -0.3F, 1.5F);
        this.spike13.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(spike13, 0.3281218860591285F, -0.3117158150588316F, 0.7463027588580033F);
        this.neck2 = new WRModelPart(this, 77, 0);
        this.neck2.setPos(0.0F, 0.0F, -3.0F);
        this.neck2.addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 4.0F, 0.01F, 0.01F, 0.0F);
        this.setRotateAngle(neck2, -0.3380702907586876F, 0.0F, 0.0F);
        this.arm1R = new WRModelPart(this, 0, 36);
        this.arm1R.setPos(-2.0F, 0.0F, -0.5F);
        this.arm1R.addBox(-2.0F, 0.0F, -1.5F, 2.0F, 5.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(arm1R, 0.6112143120378974F, 0.0F, 0.0F);
        this.frill1 = new WRModelPart(this, 39, 16);
        this.frill1.setPos(0.0F, -1.5F, -3.0F);
        this.frill1.addBox(-2.0F, -3.0F, -0.5F, 4.0F, 3.0F, 1.0F, 0.1F, 0.0F, 0.0F);
        this.setRotateAngle(frill1, -1.0471975511965976F, 0.0F, 0.0F);
        this.teeth1 = new WRModelPart(this, 77, 43);
        this.teeth1.setPos(-0.01F, 1.8F, 0.1F);
        this.teeth1.addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 4.0F, 0.2F, 0.0F, 0.0F);
        this.leg3R = new WRModelPart(this, 29, 27);
        this.leg3R.setPos(0.0F, 5.0F, 3.0F);
        this.leg3R.addBox(-1.5F, 0.0F, -2.1F, 3.0F, 5.0F, 2.0F, -0.15F, 0.0F, 0.0F);
        this.setRotateAngle(leg3R, -0.6632251157578453F, 0.0F, 0.0F);
        this.claw3L = new WRModelPart(this, 31, 36);
        this.claw3L.setPos(0.4F, 1.4F, -0.5F);
        this.claw3L.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, -0.1F);
        this.setRotateAngle(claw3L, -0.7285004590772052F, 0.4098033003787853F, 0.591841146688116F);
        this.membraneL3 = new WRModelPart(this, 1, 66);
        this.membraneL3.mirror = true;
        this.membraneL3.setPos(7.5F, 0.07F, 0.0F);
        this.membraneL3.addBox(-7.5F, 0.0F, 0.0F, 15.0F, 0.1F, 20.0F, 0.0F, 0.0F, 0.0F);
        this.fingerR1part1 = new WRModelPart(this, 0, 58);
        this.fingerR1part1.mirror = true;
        this.fingerR1part1.setPos(-1.4F, -0.3F, -0.5F);
        this.fingerR1part1.addBox(-20.0F, -0.5F, -0.5F, 20.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerR1part1, 0.0F, 0.9773843811168246F, 0.0F);
        this.claw3R = new WRModelPart(this, 31, 36);
        this.claw3R.mirror = true;
        this.claw3R.setPos(-0.4F, 1.4F, -0.5F);
        this.claw3R.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, -0.1F);
        this.setRotateAngle(claw3R, -0.7285004590772052F, -0.4098033003787853F, -0.591841146688116F);
        this.crownHornL2 = new WRModelPart(this, 13, 21);
        this.crownHornL2.setPos(2.0F, -1.7F, 0.0F);
        this.crownHornL2.addBox(-0.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(crownHornL2, 0.27314402127920984F, -0.18203784630933073F, -0.2275909337942703F);
        this.tail8 = new WRModelPart(this, 54, 30);
        this.tail8.setPos(0.0F, 0.0F, 4.0F);
        this.tail8.addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.neck1 = new WRModelPart(this, 77, 0);
        this.neck1.setPos(0.0F, -0.7F, -2.5F);
        this.neck1.addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(neck1, -0.2602285888091934F, 0.0F, 0.0F);
        this.leg2R = new WRModelPart(this, 15, 27);
        this.leg2R.setPos(-1.5F, 4.0F, -2.0F);
        this.leg2R.addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 3.0F, -0.1F, 0.0F, 0.0F);
        this.setRotateAngle(leg2R, 0.500909508638178F, 0.0F, 0.0F);
        this.membraneR4 = new WRModelPart(this, 111, 66);
        this.membraneR4.setPos(0.0F, 0.05F, 0.0F);
        this.membraneR4.addBox(-20.0F, 0.0F, -10.0F, 20.0F, 0.1F, 20.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(membraneR4, 0.0F, -0.0781907508222411F, 0.0F);
        this.toe1R = new WRModelPart(this, 41, 36);
        this.toe1R.setPos(1.0F, 0.8F, -1.5F);
        this.toe1R.addBox(-0.5F, -1.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(toe1R, 0.0F, -0.4098033003787853F, 0.0F);
        this.fingerR3part1 = new WRModelPart(this, 43, 58);
        this.fingerR3part1.mirror = true;
        this.fingerR3part1.setPos(-1.0F, -0.1F, 0.0F);
        this.fingerR3part1.addBox(-15.0F, -0.5F, -0.5F, 15.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerR3part1, 0.0F, 1.496096256114828F, 0.0F);
        this.eyeR = new WRModelPart(this, 46, 24);
        this.eyeR.mirror = true;
        this.eyeR.setPos(-1.6F, -1.3F, -3.8F);
        this.eyeR.addBox(-1.5F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(eyeR, 0.0F, 1.2946852444557158F, 0.0F);
        this.membraneR6 = new WRModelPart(this, 79, 22);
        this.membraneR6.setPos(0.0F, -0.05F, 0.0F);
        this.membraneR6.addBox(-40.0F, 0.0F, -10.0F, 40.0F, 0.1F, 20.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(membraneR6, 0.0F, 0.11728612207217244F, 0.0F);
        this.arm2L = new WRModelPart(this, 12, 36);
        this.arm2L.mirror = true;
        this.arm2L.setPos(1.0F, 4.0F, 0.0F);
        this.arm2L.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, -0.1F, 0.0F, 0.0F);
        this.setRotateAngle(arm2L, -1.255589906495298F, 0.0F, 0.0F);
        this.tail4 = new WRModelPart(this, 54, 11);
        this.tail4.setPos(0.0F, 0.0F, 4.0F);
        this.tail4.addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 5.0F, -0.1F, -0.1F, 0.0F);
        this.spike8 = new WRModelPart(this, 28, 43);
        this.spike8.setPos(0.0F, -1.5F, 1.5F);
        this.spike8.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(spike8, 0.3281218860591285F, -0.3117158150588316F, 0.7463027588580033F);
        this.membraneL1 = new WRModelPart(this, 70, 86);
        this.membraneL1.setPos(5.0F, 0.2F, 0.0F);
        this.membraneL1.addBox(-5.0F, 0.0F, 0.0F, 10.0F, 0.1F, 11.0F, 0.0F, 0.0F, 0.0F);
        this.membraneL5 = new WRModelPart(this, 95, 43);
        this.membraneL5.mirror = true;
        this.membraneL5.setPos(0.0F, 0.0F, 0.0F);
        this.membraneL5.addBox(0.0F, 0.0F, -10.0F, 30.0F, 0.1F, 20.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(membraneL5, 0.0F, -0.11728612207217244F, 0.0F);
        this.spike12 = new WRModelPart(this, 28, 43);
        this.spike12.setPos(0.0F, -0.5F, 1.5F);
        this.spike12.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(spike12, 0.3281218860591285F, -0.3117158150588316F, 0.7463027588580033F);
        this.membraneL4 = new WRModelPart(this, 111, 66);
        this.membraneL4.mirror = true;
        this.membraneL4.setPos(0.0F, 0.05F, 0.0F);
        this.membraneL4.addBox(0.0F, 0.0F, -10.0F, 20.0F, 0.1F, 20.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(membraneL4, 0.0F, -0.11728612207217244F, 0.0F);
        this.fingerR2part1 = new WRModelPart(this, 0, 58);
        this.fingerR2part1.mirror = true;
        this.fingerR2part1.setPos(-1.4F, -0.2F, 0.5F);
        this.fingerR2part1.addBox(-20.0F, -0.5F, -0.5F, 20.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerR2part1, 0.0F, 1.2358676007266074F, 0.0F);
        this.spike1 = new WRModelPart(this, 28, 43);
        this.spike1.setPos(0.0F, -1.5F, -3.5F);
        this.spike1.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(spike1, 0.3281218860591285F, -0.3117158150588316F, 0.7463027588580033F);
        this.membraneR1 = new WRModelPart(this, 70, 86);
        this.membraneR1.setPos(-5.0F, 0.2F, 0.0F);
        this.membraneR1.addBox(-5.0F, 0.0F, 0.0F, 10.0F, 0.1F, 11.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(membraneR1, 0.0F, -0.6255260065779288F, 0.0F);
        this.toe2R = new WRModelPart(this, 41, 36);
        this.toe2R.mirror = true;
        this.toe2R.setPos(0.0F, 0.8F, -1.5F);
        this.toe2R.addBox(-0.5F, -1.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.frillR = new WRModelPart(this, 34, 22);
        this.frillR.mirror = true;
        this.frillR.setPos(-1.5F, 0.0F, -3.0F);
        this.frillR.addBox(-3.0F, -2.0F, -0.5F, 3.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(frillR, 0.0F, 0.7740534966278743F, 0.591841146688116F);
        this.membraneL6 = new WRModelPart(this, 79, 22);
        this.membraneL6.mirror = true;
        this.membraneL6.setPos(0.0F, -0.05F, 0.0F);
        this.membraneL6.addBox(0.0F, 0.0F, -10.0F, 40.0F, 0.1F, 20.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(membraneL6, 0.0F, -0.11728612207217244F, 0.0F);
        this.toe2L = new WRModelPart(this, 41, 36);
        this.toe2L.setPos(0.0F, 0.8F, -1.5F);
        this.toe2L.addBox(-0.5F, -1.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.eyeL = new WRModelPart(this, 46, 24);
        this.eyeL.setPos(1.6F, -1.3F, -3.8F);
        this.eyeL.addBox(-0.5F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(eyeL, 0.0F, -1.2946852444557158F, 0.0F);
        this.palmL_1 = new WRModelPart(this, 36, 53);
        this.palmL_1.setPos(14.5F, 0.01F, 0.01F);
        this.palmL_1.addBox(0.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.spike9 = new WRModelPart(this, 28, 43);
        this.spike9.setPos(0.0F, -1.3F, 1.5F);
        this.spike9.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(spike9, 0.3281218860591285F, -0.3117158150588316F, 0.7463027588580033F);
        this.fingerL4part2 = new WRModelPart(this, 45, 55);
        this.fingerL4part2.setPos(10.0F, 0.0F, 0.0F);
        this.fingerL4part2.addBox(0.0F, -0.5F, -0.5F, 10.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerL4part2, 0.0F, -0.2275909337942703F, 0.0F);
        this.membraneR3 = new WRModelPart(this, 1, 66);
        this.membraneR3.setPos(-7.5F, 0.07F, 0.0F);
        this.membraneR3.addBox(-7.5F, 0.0F, 0.0F, 15.0F, 0.1F, 20.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(membraneR3, 0.0F, 0.3127630032889644F, 0.0F);
        this.palmR = new WRModelPart(this, 22, 36);
        this.palmR.setPos(-0.3F, 4.5F, -0.01F);
        this.palmR.addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(palmR, 0.0F, 0.0F, -0.4098033003787853F);
        this.tail1 = new WRModelPart(this, 54, 0);
        this.tail1.setPos(0.0F, 1.8F, 6.0F);
        this.tail1.addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.tailspikeR1 = new WRModelPart(this, 0, 15);
        this.tailspikeR1.setPos(0.0F, 0.0F, 1.0F);
        this.tailspikeR1.addBox(-8.0F, -0.5F, -1.0F, 8.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailspikeR1, 0.0F, 0.8196066007575706F, 0.5462880425584197F);
        this.wingR2 = new WRModelPart(this, 0, 53);
        this.wingR2.mirror = true;
        this.wingR2.setPos(-9.5F, 0.0F, 0.0F);
        this.wingR2.addBox(-15.0F, -1.0F, -1.0F, 15.0F, 2.0F, 2.0F, 0.0F, -0.1F, 0.0F);
        this.setRotateAngle(wingR2, -0.46914448828868976F, -1.9547687622336491F, 0.0F);
        this.tailspikeR2 = new WRModelPart(this, 21, 15);
        this.tailspikeR2.setPos(0.0F, 0.0F, 3.5F);
        this.tailspikeR2.addBox(-6.0F, -0.5F, -1.0F, 6.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailspikeR2, 0.0F, 1.1383037594559906F, 0.27314402127920984F);
        this.tail6 = new WRModelPart(this, 54, 21);
        this.tail6.setPos(0.0F, 0.0F, 4.0F);
        this.tail6.addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 5.0F, 0.0F, 0.0F, 0.0F);
        this.toe3R = new WRModelPart(this, 41, 36);
        this.toe3R.setPos(-1.0F, 0.8F, -1.5F);
        this.toe3R.addBox(-0.5F, -1.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(toe3R, 0.0F, 0.4098033003787853F, 0.0F);
        this.claw2L = new WRModelPart(this, 31, 36);
        this.claw2L.setPos(0.4F, 1.7F, 0.0F);
        this.claw2L.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, -0.1F);
        this.setRotateAngle(claw2L, 0.0F, 0.0F, 0.5462880425584197F);
        this.leg3L = new WRModelPart(this, 29, 27);
        this.leg3L.setPos(0.0F, 5.0F, 3.0F);
        this.leg3L.addBox(-1.5F, 0.0F, -2.1F, 3.0F, 5.0F, 2.0F, -0.15F, 0.0F, 0.0F);
        this.setRotateAngle(leg3L, -0.6632251157578453F, 0.0F, 0.0F);
        this.crownHornR1 = new WRModelPart(this, 13, 21);
        this.crownHornR1.mirror = true;
        this.crownHornR1.setPos(-2.0F, -0.5F, 0.0F);
        this.crownHornR1.addBox(-3.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(crownHornR1, 0.0F, 0.18203784630933073F, -0.18203784630933073F);
        this.body1 = new WRModelPart(this, 0, 0);
        this.body1.setPos(0, 1.7f, -2.5f);
        this.body1.addBox(-3.0F, -3.0F, -3.5F, 6.0F, 6.0F, 7.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(body1, -0.0781907508222411F, 0.0F, 0.0F);
        this.wingR1 = new WRModelPart(this, 0, 46);
        this.wingR1.mirror = true;
        this.wingR1.setPos(-2.0F, -1.8F, -1.3F);
        this.wingR1.addBox(-10.0F, -1.0F, -1.5F, 10.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(wingR1, 0.0F, 0.8210028961170991F, 1.0555751236166873F);
        this.head = new WRModelPart(this, 77, 10);
        this.head.setPos(0.01F, -0.1F, -3.0F);
        this.head.addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 4.0F, 0.05F, 0.05F, 0.0F);
        this.setRotateAngle(head, 0.500909508638178F, 0.0F, 0.0F);
        this.arm2R = new WRModelPart(this, 12, 36);
        this.arm2R.setPos(-1.0F, 4.0F, 0.0F);
        this.arm2R.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, -0.1F, 0.0F, 0.0F);
        this.setRotateAngle(arm2R, -1.255589906495298F, 0.0F, 0.0F);
        this.spike6 = new WRModelPart(this, 28, 43);
        this.spike6.setPos(0.0F, -2.0F, 1.5F);
        this.spike6.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(spike6, 0.3281218860591285F, -0.3117158150588316F, 0.7463027588580033F);
        this.spike5 = new WRModelPart(this, 28, 43);
        this.spike5.setPos(0.0F, -0.5F, 3.0F);
        this.spike5.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(spike5, 0.3281218860591285F, -0.3117158150588316F, 0.7463027588580033F);
        this.crownHornL1 = new WRModelPart(this, 13, 21);
        this.crownHornL1.setPos(2.0F, -0.5F, 0.0F);
        this.crownHornL1.addBox(-0.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(crownHornL1, 0.0F, -0.18203784630933073F, 0.18203784630933073F);
        this.fingerL4part1 = new WRModelPart(this, 45, 55);
        this.fingerL4part1.setPos(0.5F, 0.0F, 0.5F);
        this.fingerL4part1.addBox(0.0F, -0.5F, -0.5F, 10.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerL4part1, 0.0F, -1.560847955384851F, 0.0F);
        this.footL = new WRModelPart(this, 41, 29);
        this.footL.setPos(0.01F, 4.0F, -1.3F);
        this.footL.addBox(-1.5F, 0.0F, -2.0F, 3.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(footL, 0.23474678106428595F, 0.0F, 0.0F);
        this.spike11 = new WRModelPart(this, 28, 43);
        this.spike11.setPos(0.0F, -0.8F, 1.5F);
        this.spike11.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(spike11, 0.3281218860591285F, -0.3117158150588316F, 0.7463027588580033F);
        this.spike3 = new WRModelPart(this, 28, 43);
        this.spike3.setPos(0.0F, -1.5F, -3.5F);
        this.spike3.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(spike3, 0.3281218860591285F, -0.3117158150588316F, 0.7463027588580033F);
        this.crownHornR2 = new WRModelPart(this, 13, 21);
        this.crownHornR2.mirror = true;
        this.crownHornR2.setPos(-2.0F, -1.7F, 0.0F);
        this.crownHornR2.addBox(-3.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(crownHornR2, 0.27314402127920984F, 0.18203784630933073F, 0.2275909337942703F);
        this.toe1L = new WRModelPart(this, 41, 36);
        this.toe1L.mirror = true;
        this.toe1L.setPos(1.0F, 0.8F, -1.5F);
        this.toe1L.addBox(-0.5F, -1.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(toe1L, 0.0F, -0.4098033003787853F, 0.0F);
        this.tail5 = new WRModelPart(this, 54, 21);
        this.tail5.setPos(0.0F, 0.0F, 4.0F);
        this.tail5.addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 5.0F, 0.2F, 0.2F, 0.0F);
        this.leg1L = new WRModelPart(this, 0, 25);
        this.leg1L.mirror = true;
        this.leg1L.setPos(1.4F, 2.0F, 4.0F);
        this.leg1L.addBox(0.0F, -1.0F, -2.0F, 3.0F, 5.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(leg1L, -0.03909537541112055F, 0.0F, 0.0F);
        this.frillL = new WRModelPart(this, 34, 22);
        this.frillL.setPos(1.5F, 0.0F, -3.0F);
        this.frillL.addBox(0.0F, -2.0F, -0.5F, 3.0F, 3.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(frillL, 0.0F, -0.7740534966278743F, -0.591841146688116F);
        this.wingL2 = new WRModelPart(this, 0, 53);
        this.wingL2.setPos(9.5F, 0.0F, 0.0F);
        this.wingL2.addBox(0.0F, -1.0F, -1.0F, 15.0F, 2.0F, 2.0F, 0.0F, -0.1F, 0.0F);
        this.setRotateAngle(wingL2, 0.0F, 0.3909537457888271F, 0.0F);
        this.palmL = new WRModelPart(this, 22, 36);
        this.palmL.mirror = true;
        this.palmL.setPos(0.3F, 4.5F, -0.01F);
        this.palmL.addBox(-0.5F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(palmL, 0.0F, 0.0F, 0.4098033003787853F);
        this.claw2R = new WRModelPart(this, 31, 36);
        this.claw2R.mirror = true;
        this.claw2R.setPos(-0.4F, 1.7F, 0.0F);
        this.claw2R.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, 0.0F, -0.1F);
        this.setRotateAngle(claw2R, 0.0F, 0.0F, -0.5462880425584197F);
        this.fingerL1part1 = new WRModelPart(this, 0, 58);
        this.fingerL1part1.setPos(1.4F, -0.3F, -0.5F);
        this.fingerL1part1.addBox(0.0F, -0.5F, -0.5F, 20.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(fingerL1part1, 0.0F, 0.11728612207217244F, 0.0F);
        this.leg2L = new WRModelPart(this, 15, 27);
        this.leg2L.mirror = true;
        this.leg2L.setPos(1.5F, 4.0F, -2.0F);
        this.leg2L.addBox(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 3.0F, -0.1F, 0.0F, 0.0F);
        this.setRotateAngle(leg2L, 0.500734971718237F, 0.0F, 0.0F);
        this.body2 = new WRModelPart(this, 27, 0);
        this.body2.setPos(0, 9.3f, -1.5f);
        this.body2.addBox(-3.0F, -1.0F, 0.0F, 6.0F, 6.0F, 7.0F, 0.1F, 0.1F, 0.0F);
        this.setRotateAngle(body2, 0.0781907508222411F, 0.0F, 0.0F);
        this.tailspikeL1 = new WRModelPart(this, 0, 15);
        this.tailspikeL1.mirror = true;
        this.tailspikeL1.setPos(0.0F, 0.0F, 1.0F);
        this.tailspikeL1.addBox(0.0F, -0.5F, -1.0F, 8.0F, 1.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(tailspikeL1, 0.0F, -0.8196066007575706F, -0.5462880425584197F);
        this.neck2.addChild(this.neck3);
        this.body1.addChild(this.wingL1);
        this.jaw.addChild(this.teeth2);
        this.membraneR1.addChild(this.membraneR2);
        this.body2.addChild(this.leg1R);
        this.fingerL2part1.addChild(this.fingerL2part2);
        this.wingR2.addChild(this.palmR_1);
        this.head.addChild(this.snout);
        this.tail6.addChild(this.tail7);
        this.footL.addChild(this.toe3L);
        this.fingerR1part1.addChild(this.membraneR7);
        this.tail1.addChild(this.tail2);
        this.frill1.addChild(this.crownHorn1);
        this.head.addChild(this.jaw);
        this.tail2.addChild(this.spike7);
        this.fingerR3part1.addChild(this.fingerR3part2);
        this.leg3R.addChild(this.footR);
        this.fingerR2part1.addChild(this.fingerR2part2);
        this.neck2.addChild(this.spike2);
        this.membraneL1.addChild(this.membraneL2);
        this.fingerR3part1.addChild(this.membraneR5);
        this.body1.addChild(this.spike4);
        this.fingerR1part1.addChild(this.fingerR1part2);
        this.fingerL3part1.addChild(this.fingerL3part2);
        this.palmR.addChild(this.claw1R);
        this.palmL_1.addChild(this.fingerL3part1);
        this.body1.addChild(this.arm1L);
        this.tail2.addChild(this.tail3);
        this.palmL.addChild(this.claw1L);
        this.fingerL1part1.addChild(this.fingerL1part2);
        this.palmL_1.addChild(this.fingerL2part1);
        this.palmR_1.addChild(this.fingerR4part1);
        this.fingerR4part1.addChild(this.fingerR4part2);
        this.tail5.addChild(this.spike10);
        this.fingerL1part1.addChild(this.membraneL7);
        this.tail8.addChild(this.tailspikeL2);
        this.tail8.addChild(this.spike13);
        this.neck1.addChild(this.neck2);
        this.body1.addChild(this.arm1R);
        this.head.addChild(this.frill1);
        this.snout.addChild(this.teeth1);
        this.leg2R.addChild(this.leg3R);
        this.palmL.addChild(this.claw3L);
        this.wingL2.addChild(this.membraneL3);
        this.palmR_1.addChild(this.fingerR1part1);
        this.palmR.addChild(this.claw3R);
        this.frillL.addChild(this.crownHornL2);
        this.tail7.addChild(this.tail8);
        this.body1.addChild(this.neck1);
        this.leg1R.addChild(this.leg2R);
        this.fingerR4part1.addChild(this.membraneR4);
        this.footR.addChild(this.toe1R);
        this.palmR_1.addChild(this.fingerR3part1);
        this.head.addChild(this.eyeR);
        this.fingerR2part1.addChild(this.membraneR6);
        this.arm1L.addChild(this.arm2L);
        this.tail3.addChild(this.tail4);
        this.tail3.addChild(this.spike8);
        this.wingL1.addChild(this.membraneL1);
        this.fingerL3part1.addChild(this.membraneL5);
        this.tail7.addChild(this.spike12);
        this.fingerL4part1.addChild(this.membraneL4);
        this.palmR_1.addChild(this.fingerR2part1);
        this.neck3.addChild(this.spike1);
        this.wingR1.addChild(this.membraneR1);
        this.footR.addChild(this.toe2R);
        this.head.addChild(this.frillR);
        this.fingerL2part1.addChild(this.membraneL6);
        this.footL.addChild(this.toe2L);
        this.head.addChild(this.eyeL);
        this.wingL2.addChild(this.palmL_1);
        this.tail4.addChild(this.spike9);
        this.fingerL4part1.addChild(this.fingerL4part2);
        this.wingR2.addChild(this.membraneR3);
        this.arm2R.addChild(this.palmR);
        this.body2.addChild(this.tail1);
        this.tail8.addChild(this.tailspikeR1);
        this.wingR1.addChild(this.wingR2);
        this.tail8.addChild(this.tailspikeR2);
        this.tail5.addChild(this.tail6);
        this.footR.addChild(this.toe3R);
        this.palmL.addChild(this.claw2L);
        this.leg2L.addChild(this.leg3L);
        this.frillR.addChild(this.crownHornR1);
        this.body1.addChild(this.wingR1);
        this.neck3.addChild(this.head);
        this.arm1R.addChild(this.arm2R);
        this.tail1.addChild(this.spike6);
        this.body2.addChild(this.spike5);
        this.frillL.addChild(this.crownHornL1);
        this.palmL_1.addChild(this.fingerL4part1);
        this.leg3L.addChild(this.footL);
        this.tail6.addChild(this.spike11);
        this.neck1.addChild(this.spike3);
        this.frillR.addChild(this.crownHornR2);
        this.footL.addChild(this.toe1L);
        this.tail4.addChild(this.tail5);
        this.body2.addChild(this.leg1L);
        this.head.addChild(this.frillL);
        this.wingL1.addChild(this.wingL2);
        this.arm2L.addChild(this.palmL);
        this.palmR.addChild(this.claw2R);
        this.palmL_1.addChild(this.fingerL1part1);
        this.leg1L.addChild(this.leg2L);
        this.body2.addChild(this.body1);
        this.tail8.addChild(this.tailspikeL1);

        this.tails = new WRModelPart[] {tail1, tail2, tail3, tail4, tail5, tail6, tail7, tail8};
        this.neck = new WRModelPart[] {neck1, neck2, neck3, head};
        this.wings = new WRModelPart[][] {
                {wingR1, wingR2, palmR_1, fingerR1part1, fingerR1part2, fingerR2part1, fingerR2part2, fingerR3part1, fingerR3part2, fingerR4part1, fingerR4part2, membraneR1, membraneR2, membraneR3, membraneR4, membraneR5, membraneR6, membraneR7},
                {wingL1, wingL2, palmL_1, fingerL1part1, fingerL1part2, fingerL2part1, fingerL2part2, fingerL3part1, fingerL3part2, fingerL4part1, fingerL4part2, membraneL1, membraneL2, membraneL3, membraneL4, membraneL5, membraneL6, membraneL7}
        };
        this.toes = new WRModelPart[][] {
                {toe1R, toe2R, toe3R},
                {toe1L, toe2L, toe3L}
        };

        setDefaultPose();
    }

    @Override
    public ResourceLocation getTexture(RoyalRedEntity entity)
    {
        int index = entity.isHatchling()? 2 : entity.isMale()? 0 : 1;
        if (entity.getVariant() == -1) index |= 4;
        if (TEXTURES[index] == null)
        {
            String path = FOLDER + "royal_red/";
            path += ((index & 2) != 0)? "child" : ((index & 1) != 0)? "female" : "male";
            if ((index & 4) != 0) path += "_spe";
            return TEXTURES[index] = Wyrmroost.id(path + ".png");
        }
        return TEXTURES[index];
    }

    public ResourceLocation getEyesTexture(RoyalRedEntity entity)
    {
        int index = 8;
        if (entity.isHatchling()) index |= 2;
        if (entity.getVariant() == -1) index |= 4;
        if (TEXTURES[index] == null)
        {
            String path = FOLDER + "royal_red/";
            if ((index & 2) != 0) path += "child_";
            if ((index & 4) != 0) path += "spe_";
            return TEXTURES[index] = Wyrmroost.id(path + "eyes.png");
        }
        return TEXTURES[index];
    }

    @Override
    public float getShadowRadius(RoyalRedEntity entity)
    {
        return 2.5f;
    }

    @Override
    public void scale(RoyalRedEntity entity, PoseStack ms, float partialTicks)
    {
        super.scale(entity, ms, partialTicks);
        ms.scale(3.6f, 3.6f, 3.6f);
    }

    @Override
    public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha)
    {
        body2.render(ms, buffer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void postProcess(RoyalRedEntity entity, PoseStack ms, MultiBufferSource buffer, int light, float limbSwing, float limbSwingAmount, float age, float yaw, float pitch, float partialTicks)
    {
        if (!entity.isSleeping() && !entity.isKnockedOut() && entity.tickCount % 200 > 3) renderEyes(getEyesTexture(entity), ms, buffer);
        renderArmorOverlay(ms, buffer, light);
    }

    @Override
    public void setupAnim(RoyalRedEntity entity, float limbSwing, float limbSwingAmount, float bob, float yaw, float pitch)
    {
        float flightTime = entity.flightTimer.get(partialTicks);
        float sitTime = entity.sitTimer.get(partialTicks);
        float sleepTime = entity.sleepTimer.get(partialTicks);
        float knockoutTime = entity.knockOutTimer.get(partialTicks);

        limbSwingAmount *= 1 - sitTime;
        limbSwingAmount *= 1 - knockoutTime;
        float walkDelta = (1 - flightTime) * limbSwingAmount;
        float flightDelta = flightTime * limbSwingAmount;

        reset();
        animator().tick(entity, this, partialTicks);

        if (flightDelta > 0)
        {
            flap(wingR1, 0.1f, 1f, false, 0, 0, limbSwing, flightDelta);
        }

        if (walkDelta > 0)
        {
            body2.y += bob(0.8f, 1f, false, limbSwing, walkDelta);
            body2.yRot += -limbSwing(0.4f, -0.1f, 0, 0, limbSwing, walkDelta);

            leg1R.xRot += limbSwing(0.4f, 1.5f, 0, 0, limbSwing, walkDelta);
            leg2R.xRot += limbSwing(0.4f, -1.25f, 0.65f, 0.3f, limbSwing, walkDelta);
            leg3R.xRot += limbSwing(0.4f, 0.5f, 0.5f, -0.85f, limbSwing, walkDelta);
            footR.xRot += limbSwing(0.4f, -0.9f, 0.25f, 0.9f, limbSwing, walkDelta);
            leg1L.xRot += -limbSwing(0.4f, 1.5f, 0, 0, limbSwing, walkDelta);
            leg2L.xRot += -limbSwing(0.4f, -1.25f, 0.65f, -0.3f, limbSwing, walkDelta);
            leg3L.xRot += -limbSwing(0.4f, 0.5f, 0.5f, 0.85f, limbSwing, walkDelta);
            footL.xRot += -limbSwing(0.4f, -0.9f, 0.25f, -0.9f, limbSwing, walkDelta);

            float rightToes = limbSwing(0.4f, -0.6f, 0.25f, 0.6f, limbSwing, walkDelta);
            float leftToes = -limbSwing(0.4f, -0.6f, 0.25f, -0.6f, limbSwing, walkDelta);
            for (int i = 0; i < toes[0].length; i++)
            {
                toes[0][i].xRot = rightToes;
                toes[1][i].xRot = leftToes;
            }

            wingR1.yRot += limbSwing(0.8f, -0.05f, 0, 0, limbSwing, walkDelta);
            wingR2.yRot += limbSwing(0.8f, 0.1f, 0, 0, limbSwing, walkDelta);

            arm1L.xRot = arm1R.xRot += limbSwing(0.8f, -0.1f, 0, 0.1f, limbSwing, walkDelta);
            arm2L.xRot = arm2R.xRot += limbSwing(0.8f, -0.1f, 0, 0.1f, limbSwing, walkDelta);
        }

        float breath = entity.breathTimer.get(partialTicks);
        if (breath > 0)
        {
            jaw.xRot += 0.45 * breath;
            snout.xRot -= 0.55 * breath;
        }

        if (sitTime != 0) sitPositions(sitTime - sleepTime);
        if (sleepTime != 0) sleepPositions(sleepTime);
        if (knockoutTime != 0) knockoutPositions(knockoutTime);
        flightPositions(flightTime);

        faceTarget(yaw, pitch, 1f, neck);
    }

    private void flightPositions(float time)
    {
        setTime(time);
        for (int i = 0; i < wings[0].length; i++)
        {
            // from back-front perspective
            WRModelPart right = wings[0][i]; // grounded positioned wings
            WRModelPart left = wings[1][i]; // flight positioned wings

            // when flying, copy left set of wings default rotations for flight positions
            rotateFrom0(right, left.defaultRotationX, -left.defaultRotationY, -left.defaultRotationZ);
            right.mirrorRotationsTo(left);
        }

        if (time == 0) return;

        for (WRModelPart part : neck)
        {
            part.xRot *= 1 - time;
        }

        rotate(arm1R, 0, 0, 0.25f);
        rotate(arm2R, 0.5f, 0, 0);
        rotate(arm1L, 0, 0, -0.25f);
        rotate(arm2L, 0.5f, 0, 0);

        rotate(leg1L, 0.65f, 0, 0);
        rotate(leg2L, 0.35f, 0, 0);
        rotate(footL, 1f, 0, 0);
        rotate(leg1R, 0.65f, 0, 0);
        rotate(leg2R, 0.35f, 0, 0);
        rotate(footR, 1f, 0, 0);
    }

    private void sitPositions(float time)
    {
        setTime(time);

        if (entity.isJuvenile())
        {
            rotate(body2, -0.6f, 0.0f, 0.0f);
            move(body2, 0.0f, 6.9f, 0.0f);
            rotate(leg1L, -0.9f, -0.4f, -0.2f);
            rotate(leg2L, 0.7f, 0.0f, 0.0f);
            rotate(leg3L, -0.55f, 0.0f, 0.0f);
            rotate(footL, 1.25f, 0.0f, 0.0f);
            move(footL, 0.0f, 0.0f, -0.8f);
            rotate(leg1R, -0.9f, 0.4f, 0.2f);
            rotate(leg2R, 0.7f, 0.0f, 0.0f);
            rotate(leg3R, -0.55f, 0.0f, 0.0f);
            rotate(footR, 1.25f, 0.0f, 0.0f);
            move(footR, 0.0f, 0.0f, -0.8f);
            rotate(neck3, 0.15f, 0.0f, 0.0f);
            rotate(head, 0.15f, 0.0f, 0.0f);
            rotate(wingR1, 0.0f, -0.3f, 0.0f);
            rotate(membraneR1, 0.0f, 0.3f, 0.0f);
            rotate(membraneR2, 0.15f, -0.45f, 0.1f);
            rotate(tail1, 0.35f, 0.0f, 0.0f);
            rotate(tail2, 0.075f, 0.0f, 0.0f);
            rotate(tail3, 0.05f, 0.0f, 0.0f);
        }
        else
        {
            rotate(body2, -1.05f, 0, 0);
            move(body2, 0, 6f, 0);

            rotate(neck3, 0.15f, 0, 0);
            rotate(head, 0.32f, 0, 0);

            rotate(arm1R, 0.1f, -0.3f, 0.25f);
            rotate(arm2R, 1f, -0.75f, -0.5f);
            rotate(arm1L, 0.1f, 0.3f, -0.25f);
            rotate(arm2L, 1f, 0.75f, 0.5f);

            rotate(wingR1, 0, -0.6f, 0);
            rotate(membraneR1, 0, 0.6f, 0);
            rotate(membraneR2, 0.2f, 0, 0.2f);

            rotate(tail1, 0.85f, 0.2f, 0.5f);
            rotate(tail2, 0.175f, 0.1f, 0.2f);
            rotate(tail4, -0.1f, 0.2f, 0.025f);
            rotate(tail7, -0.1f, 0, 0);

            for (WRModelPart tail : tails) rotate(tail, 0, 0.35f, 0);

            rotate(leg1L, -0.5f, -0.15f, -0.3f);
            rotate(leg2L, 0.75f, 0, 0);
            rotate(leg3L, -0.55f, 0, 0);

            rotate(leg1R, -0.5f, 0.15f, 0.3f);
            rotate(leg2R, 0.75f, 0, 0);
            rotate(leg3R, -0.55f, 0, 0);

            for (int i = 0; i < 3; i++)
            {
                rotate(toes[0][i], 0.5f, 0, 0);
                rotate(toes[1][i], 0.5f, 0, 0);
            }

            rotate(neck1, 0.1f, 0, 0);
            rotate(neck2, 0.3f, 0, 0);
        }
    }

    public void sleepPositions(float time)
    {
        setTime(time);

        move(body2, 0.0f, 10.0f, 0.0f);
        move(leg1L, 0.0f, 0.5f, 0.0f);
        rotate(leg1L, -1.25f, -0.5f, 0.0f);
        rotate(leg2L, -0.8f, 0.0f, 0.0f);
        move(leg2L, 0.0f, -6.0f, 2.0f);
        rotate(leg3L, 0.6f, 0.0f, 0.0f);
        rotate(footL, 1.4f, 0.0f, 0.0f);
        move(footL, 0.0f, 0.0f, -0.8f);
        move(leg1R, 0.0f, 0.5f, 0.0f);
        rotate(leg1R, -1.25f, 0.5f, 0.0f);
        rotate(leg2R, -0.8f, 0.0f, 0.0f);
        move(leg2R, 0.0f, -6.0f, 2.0f);
        rotate(leg3R, 0.6f, 0.0f, 0.0f);
        rotate(footR, 1.4f, 0.0f, 0.0f);
        move(footR, 0.0f, 0.0f, -0.8f);
        rotate(arm1R, 0.5f, 0.0f, 0.0f);
        rotate(arm2R, -1.5f, 0.15f, -0.15f);
        rotate(arm1L, 0.5f, 0.0f, 0.0f);
        rotate(arm2L, -1.5f, -0.15f, 0.15f);
        rotate(tail1, -0.1f, 0.0f, 0.0f);
        rotate(tail2, -0.1f, 0.0f, 0.0f);
        rotate(tail3, -0.05f, 0.0f, 0.0f);
        rotate(tail4, 0.1f, 0.0f, 0.0f);
        rotate(tail5, 0.05f, 0.0f, 0.0f);
        rotate(neck1, 0.2f, 0.0f, 0.0f);
        rotate(neck2, 0.7f, 0.0f, 0.0f);
        rotate(neck3, -0.6f, 0.0f, 0.0f);
        rotate(head, -0.6f, 0.0f, 0.0f);
        rotate(wingR1, 0.0f, 0.0f, -1.25f);
        rotate(wingR2, 0.4f, 0.0f, 0.25f);
        rotate(palmR_1, 0.0f, 0.0f, 0.175f);
        rotate(membraneR3, -0.2f, 0.0f, 0.1f);
        rotate(membraneR1, 0.01f, 0.4f, 0.0f);
        rotate(membraneR2, 0.1f, -0.4f, 0.2f);
    }


    private void knockoutPositions(float time)
    {
        setTime(time);

        move(body2, 0.0f, 10.0f, 0.0f);
        rotate(tail1, -0.1f, 0.0f, 0.0f);
        rotate(tail2, -0.1f, 0.0f, 0.0f);
        rotate(tail3, -0.05f, 0.0f, 0.0f);
        rotate(tail4, 0.1f, 0.0f, 0.0f);
        rotate(tail5, 0.05f, 0.0f, 0.0f);
        rotate(neck1, 0.2f, 0.0f, 0.0f);
        rotate(neck2, 0.7f, 0.0f, 0.0f);
        rotate(neck3, -0.6f, 0.0f, 0.0f);
        rotate(head, -0.6f, 0.0f, 0.0f);

        rotate(leg1R, 1.2f, -0.6f, 0.0f);
        rotate(leg2R, -0.2f, 0.1f, -0.2f);
        rotate(leg3R, 0.55f, 0.1f, -0.1f);
        rotate(footR, 1.45f, -0.1f, 0.0f);
        move(footR, 0.0f, 0.0f, -0.96f);
        rotate(arm1R, -1.0f, 0.0f, 0.85f);
        rotate(arm2R, 0.3f, -0.4f, 0.75f);
        rotate(palmR, 0.0f, 0.2f, 0.5f);
        rotate(wingR1, 0.1f, 0.0f, -1.0f);
        rotate(wingR2, -0.1f, 0.0f, 0.6f);
        rotate(palmR_1, 0.0f, -0.3f, 0.3f);

        rotate(leg1L, 1.2f, 0.6f, 0.0f);
        rotate(leg2L, -0.2f, -0.1f, 0.2f);
        rotate(leg3L, 0.55f, -0.1f, 0.1f);
        rotate(footL, 1.45f, 0.1f, 0.0f);
        move(footL, 0.0f, 0.0f, -0.96f);
        rotate(arm1L, -1.0f, 0.0f, -0.85f);
        rotate(arm2L, 0.3f, 0.4f, -0.75f);
        rotate(palmL, 0.0f, -0.2f, -0.5f);
        rotate(wingL1, 0.1f, 0.0f, 1.0f);
        rotate(wingL2, -0.1f, 0.0f, -0.6f);
        rotate(palmL_1, 0.0f, 0.3f, -0.3f);

        rotate(membraneR3, -0.35f, 0.0f, 0.0f);
        rotate(membraneR1, 0.01f, 0.4f, 0.0f);
        rotate(membraneR2, 0.25f, -0.4f, 0.0f);
    }

    public void roarAnimation()
    {

    }

    public void slapAttackAnimation()
    {

    }

    public void biteAttackAnimation()
    {

    }
}*/
