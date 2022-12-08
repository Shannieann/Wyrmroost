package com.github.shannieann.wyrmroost.client.model.entity;

/*import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.client.render.RenderHelper;
import com.github.shannieann.wyrmroost.entities.dragon.ButterflyLeviathanEntity;
import com.github.shannieann.wyrmroost.util.Mafs;
import com.mojang.blaze3d.matrix.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelPart;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Mth;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import org.apache.commons.lang3.ArrayUtils;

/**
 * butterfly leviathan - Kingdomall
 * Created using Tabula 8.0.0
public class ButterflyLeviathanModel extends DragonEntityModel<ButterflyLeviathanEntity>
{
    public static final ResourceLocation BLUE = texture("body_blue.png");
    public static final ResourceLocation PURPLE = texture("body_purple.png");
    // Special
    public static final ResourceLocation ALBINO = texture("body_albino.png");
    // Glow
    public static final ResourceLocation GLOW = texture("activated.png");

    private static final RenderMaterial CONDUIT_CAGE = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, new ResourceLocation("entity/conduit/cage"));
    private static final RenderMaterial CONDUIT_WIND = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, new ResourceLocation("entity/conduit/wind"));
    private static final RenderMaterial CONDUIT_VERTICAL_WIND = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, new ResourceLocation("entity/conduit/wind_vertical"));
    private static final RenderMaterial CONDUIT_OPEN_EYE = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, new ResourceLocation("entity/conduit/open_eye"));

    public WRModelPart body1;
    public WRModelPart body2;
    public WRModelPart neck1;
    public WRModelPart topWingFinPhalangeR1;
    public WRModelPart bottomWingFinPhalangeR1;
    public WRModelPart topWingFinPhalangeL1;
    public WRModelPart bottomWingFinPhalangeL1;
    public WRModelPart tail1;
    public WRModelPart legThighR1;
    public WRModelPart legThighL1;
    public WRModelPart tail2;
    public WRModelPart tail3;
    public WRModelPart tail4;
    public WRModelPart tail5;
    public WRModelPart tail6;
    public WRModelPart tailFinTop;
    public WRModelPart tailFinBottom;
    public WRModelPart legSegmentR1;
    public WRModelPart legSegmentR2;
    public WRModelPart footR;
    public WRModelPart legSegmentL1;
    public WRModelPart legSegmentL2;
    public WRModelPart footL;
    public WRModelPart neck2;
    public WRModelPart neck3;
    public WRModelPart head;
    public WRModelPart mouthTop;
    public WRModelPart mouthBottom;
    public WRModelPart headFrillR;
    public WRModelPart headFrillL;
    public WRModelPart eyeR;
    public WRModelPart eyeL;
    public WRModelPart teethTop;
    public WRModelPart teethBottom;
    public WRModelPart topWingFinPhalangeR2;
    public WRModelPart topWingFinMembraneR1;
    public WRModelPart topWingFinMembraneR2;
    public WRModelPart topWingFinMembraneR3;
    public WRModelPart bottomWingFinPhalangeR2;
    public WRModelPart bottomWingFinMembraneR1;
    public WRModelPart bottomWingFinMembraneR2;
    public WRModelPart bottomWingFinMembraneR3;
    public WRModelPart topWingFinPhalangeL2;
    public WRModelPart topWingFinMembraneL1;
    public WRModelPart topWingFinPhalangeL2_1;
    public WRModelPart topWingFinMembrane3L;
    public WRModelPart bottomWingFinPhalangeL2;
    public WRModelPart bottomWingFinMembraneL1;
    public WRModelPart bottomWingFinMembraneL2;
    public WRModelPart bottomWingFinMembraneL3;

    public WRModelPart[] tailArray;
    public final WRModelPart[] headArray;

    public ModelPart conduitEye;
    public ModelPart conduitWind;
    public ModelPart conduitCage;

    public ButterflyLeviathanModel()
    {
        this.texWidth = 150;
        this.texHeight = 250;
        this.topWingFinPhalangeL2_1 = new WRModelPart(this, 21, 173);
        this.topWingFinPhalangeL2_1.setPos(5.8F, 0.0F, 0.4F);
        this.topWingFinPhalangeL2_1.addBox(-5.8F, 0.0F, -10.7F, 13.0F, 0.0F, 11.0F, 0.0F, 0.0F, 0.0F);
        this.bottomWingFinPhalangeL2 = new WRModelPart(this, 32, 187);
        this.bottomWingFinPhalangeL2.setPos(-11.0F, -0.1F, -0.4F);
        this.bottomWingFinPhalangeL2.addBox(-13.8F, -1.1F, -0.9F, 14.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bottomWingFinPhalangeL2, 0.0F, 0.0F, 0.15358897750445236F);
        this.bottomWingFinMembraneR3 = new WRModelPart(this, 67, 200);
        this.bottomWingFinMembraneR3.setPos(11.0F, 0.0F, 0.3F);
        this.bottomWingFinMembraneR3.addBox(0.0F, 0.0F, -0.2F, 11.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bottomWingFinMembraneR3, 0.0F, 0.0F, -0.1354375539495016F);
        this.neck3 = new WRModelPart(this, 0, 0);
        this.neck3.setPos(0.0F, 0.0F, -4.7F);
        this.neck3.addBox(-3.0F, -3.9F, -7.1F, 6.0F, 8.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.topWingFinMembraneR2 = new WRModelPart(this, 84, 162);
        this.topWingFinMembraneR2.setPos(6.0F, 0.0F, 0.4F);
        this.topWingFinMembraneR2.addBox(-6.0F, 0.0F, -0.8F, 13.0F, 0.0F, 11.0F, 0.0F, 0.0F, 0.0F);
        this.topWingFinPhalangeR1 = new WRModelPart(this, 113, 155);
        this.topWingFinPhalangeR1.setPos(5.0F, -3.7F, -1.9F);
        this.topWingFinPhalangeR1.addBox(0.0F, -1.1F, -1.5F, 14.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(topWingFinPhalangeR1, 0.0F, 0.0F, -0.4300491170387584F);
        this.headFrillR = new WRModelPart(this, 31, 12);
        this.headFrillR.setPos(3.2F, -4.0F, -3.1F);
        this.headFrillR.addBox(0.1F, -4.3F, -4.5F, 0.0F, 5.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(headFrillR, 0.0F, 0.0F, 0.875631653767622F);
        this.bottomWingFinMembraneR1 = new WRModelPart(this, 115, 200);
        this.bottomWingFinMembraneR1.setPos(4.7F, -0.1F, -0.4F);
        this.bottomWingFinMembraneR1.addBox(-5.7F, 0.0F, -0.8F, 12.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.bottomWingFinPhalangeL1 = new WRModelPart(this, 0, 187);
        this.bottomWingFinPhalangeL1.setPos(-5.1F, -0.8F, -1.9F);
        this.bottomWingFinPhalangeL1.addBox(-11.9F, -1.1F, -1.5F, 12.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bottomWingFinPhalangeL1, 0.0F, 0.0F, -0.3909537457888271F);
        this.eyeL = new WRModelPart(this, 1, 19);
        this.eyeL.setPos(-2.8F, -1.5F, -5.5F);
        this.eyeL.addBox(-1.1F, -0.5F, -0.6F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.tailFinBottom = new WRModelPart(this, 0, 108);
        this.tailFinBottom.setPos(-0.2F, 1.6F, 5.4F);
        this.tailFinBottom.addBox(0.0F, -0.5F, -6.2F, 0.0F, 5.0F, 16.0F, 0.0F, 0.0F, 0.0F);
        this.footR = new WRModelPart(this, 74, 56);
        this.footR.setPos(0.0F, 3.7F, 0.0F);
        this.footR.addBox(-1.5F, -0.3F, -1.1F, 3.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(footR, 0.5337216839282855F, 0.0F, 0.0F);
        this.legSegmentR2 = new WRModelPart(this, 76, 46);
        this.legSegmentR2.setPos(-0.1F, 3.7F, 0.1F);
        this.legSegmentR2.addBox(-1.5F, -0.8F, -1.0F, 3.0F, 5.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legSegmentR2, -0.5330235695380348F, 0.0F, 0.0F);
        this.neck2 = new WRModelPart(this, 30, 0);
        this.neck2.setPos(0.0F, 0.0F, -4.7F);
        this.neck2.addBox(-3.5F, -4.0F, -7.1F, 7.0F, 9.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.topWingFinPhalangeR2 = new WRModelPart(this, 80, 155);
        this.topWingFinPhalangeR2.setPos(13.0F, -0.1F, -0.4F);
        this.topWingFinPhalangeR2.addBox(0.0F, -1.1F, -0.9F, 14.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(topWingFinPhalangeR2, 0.0F, 0.0F, 0.23177972000431504F);
        this.head = new WRModelPart(this, 0, 21);
        this.head.setPos(0.0F, 0.0F, -6.2F);
        this.head.addBox(-3.5F, -4.0F, -7.1F, 7.0F, 8.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.footL = new WRModelPart(this, 74, 56);
        this.footL.setPos(0.0F, 3.7F, 0.0F);
        this.footL.addBox(-1.5F, -0.3F, -1.1F, 3.0F, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(footL, 0.5337216839282855F, 0.0F, 0.0F);
        this.bottomWingFinPhalangeR2 = new WRModelPart(this, 32, 187);
        this.bottomWingFinPhalangeR2.setPos(11.0F, -0.1F, -0.4F);
        this.bottomWingFinPhalangeR2.addBox(0.0F, -1.1F, -0.9F, 14.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bottomWingFinPhalangeR2, 0.0F, 0.0F, -0.1926843487543837F);
        this.headFrillL = new WRModelPart(this, 31, 12);
        this.headFrillL.mirror = true;
        this.headFrillL.setPos(-3.4F, -4.0F, -3.1F);
        this.headFrillL.addBox(0.0F, -4.3F, -4.5F, 0.0F, 5.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(headFrillL, 0.0F, 0.0F, -0.875631653767622F);
        this.legThighR1 = new WRModelPart(this, 73, 21);
        this.legThighR1.setPos(3.1F, 2.1F, 7.7F);
        this.legThighR1.addBox(0.0F, -1.4F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legThighR1, 0.4836307197486622F, 0.0F, 0.0F);
        this.tail4 = new WRModelPart(this, 82, 129);
        this.tail4.setPos(0.0F, -0.1F, 10.2F);
        this.tail4.addBox(-3.0F, -3.7F, -2.2F, 6.0F, 8.0F, 13.0F, 0.0F, 0.0F, 0.0F);
        this.legSegmentR1 = new WRModelPart(this, 75, 36);
        this.legSegmentR1.setPos(1.4F, 4.2F, -0.3F);
        this.legSegmentR1.addBox(-1.5F, 0.1F, -1.6F, 3.0F, 4.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legSegmentR1, 1.011259657024578F, 0.0F, 0.0F);
        this.tail6 = new WRModelPart(this, 6, 130);
        this.tail6.setPos(0.1F, 0.1F, 10.0F);
        this.tail6.addBox(-2.2F, -3.9F, -2.2F, 4.0F, 6.0F, 14.0F, 0.0F, 0.0F, 0.0F);
        this.bottomWingFinMembraneL3 = new WRModelPart(this, 49, 210);
        this.bottomWingFinMembraneL3.setPos(-11.0F, 0.0F, 0.1F);
        this.bottomWingFinMembraneL3.addBox(-11.0F, 0.0F, 0.0F, 11.0F, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bottomWingFinMembraneL3, 0.0F, 0.0F, 0.25272366769929566F);
        this.bottomWingFinMembraneR2 = new WRModelPart(this, 92, 200);
        this.bottomWingFinMembraneR2.setPos(5.9F, 0.0F, 0.4F);
        this.bottomWingFinMembraneR2.addBox(-5.9F, 0.0F, -0.8F, 11.0F, 0.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.bottomWingFinPhalangeR1 = new WRModelPart(this, 0, 187);
        this.bottomWingFinPhalangeR1.setPos(5.1F, -0.8F, -1.9F);
        this.bottomWingFinPhalangeR1.addBox(0.0F, -1.1F, -1.5F, 12.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(bottomWingFinPhalangeR1, 0.0F, 0.0F, 0.3490658503988659F);
        this.body1 = new WRModelPart(this, 96, 0);
        this.body1.setPos(0.0F, 17.3F, 0.0F);
        this.body1.addBox(-5.5F, -6.3F, -4.6F, 11.0F, 13.0F, 15.0F, 0.0F, 0.0F, 0.0F);
        this.tailFinTop = new WRModelPart(this, 0, 97);
        this.tailFinTop.setPos(-0.2F, -3.4F, 5.4F);
        this.tailFinTop.addBox(0.0F, -6.8F, -6.2F, 0.0F, 7.0F, 16.0F, 0.0F, 0.0F, 0.0F);
        this.tail5 = new WRModelPart(this, 44, 130);
        this.tail5.setPos(0.1F, 0.3F, 10.2F);
        this.tail5.addBox(-2.6F, -3.9F, -2.2F, 5.0F, 7.0F, 13.0F, 0.0F, 0.0F, 0.0F);
        this.topWingFinPhalangeL1 = new WRModelPart(this, 113, 155);
        this.topWingFinPhalangeL1.setPos(-5.0F, -3.7F, -1.9F);
        this.topWingFinPhalangeL1.addBox(0.0F, -1.1F, -1.5F, 14.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(topWingFinPhalangeL1, 0.0F, 3.141592653589793F, 0.41887902047863906F);
        this.topWingFinMembraneR3 = new WRModelPart(this, 58, 162);
        this.topWingFinMembraneR3.setPos(13.0F, 0.0F, 0.1F);
        this.topWingFinMembraneR3.addBox(0.0F, 0.0F, -0.5F, 13.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(topWingFinMembraneR3, 0.0F, 0.0F, -0.2617993877991494F);
        this.tail2 = new WRModelPart(this, 106, 82);
        this.tail2.setPos(0.0F, 0.0F, 10.2F);
        this.tail2.addBox(-4.0F, -3.9F, -2.2F, 8.0F, 10.0F, 13.0F, 0.0F, 0.0F, 0.0F);
        this.tail1 = new WRModelPart(this, 106, 57);
        this.tail1.setPos(0.0F, -2.0F, 10.2F);
        this.tail1.addBox(-4.5F, -4.1F, -2.2F, 9.0F, 11.0F, 13.0F, 0.0F, 0.0F, 0.0F);
        this.legSegmentL2 = new WRModelPart(this, 76, 46);
        this.legSegmentL2.setPos(0.1F, 3.7F, 0.1F);
        this.legSegmentL2.addBox(-1.5F, -0.8F, -1.0F, 3.0F, 5.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legSegmentL2, -0.5330235695380348F, 0.0F, 0.0F);
        this.topWingFinMembrane3L = new WRModelPart(this, 50, 173);
        this.topWingFinMembrane3L.setPos(13.0F, 0.0F, 0.1F);
        this.topWingFinMembrane3L.addBox(0.0F, 0.0F, -9.4F, 13.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(topWingFinMembrane3L, 0.0F, 0.0F, -0.2617993877991494F);
        this.tail3 = new WRModelPart(this, 108, 107);
        this.tail3.setPos(0.0F, 0.1F, 10.2F);
        this.tail3.addBox(-3.5F, -3.9F, -2.2F, 7.0F, 9.0F, 13.0F, 0.0F, 0.0F, 0.0F);
        this.body2 = new WRModelPart(this, 102, 30);
        this.body2.setPos(0.0F, 0.1F, 10.2F);
        this.body2.addBox(-5.0F, -6.3F, -2.2F, 10.0F, 12.0F, 13.0F, 0.0F, 0.0F, 0.0F);
        this.eyeR = new WRModelPart(this, 1, 19);
        this.eyeR.setPos(2.8F, -1.5F, -5.5F);
        this.eyeR.addBox(0.1F, -0.5F, -0.6F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F);
        this.mouthTop = new WRModelPart(this, 0, 40);
        this.mouthTop.setPos(0.0F, 0.1F, -6.2F);
        this.mouthTop.addBox(-3.0F, -4.0F, -7.1F, 6.0F, 4.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.topWingFinMembraneR1 = new WRModelPart(this, 110, 162);
        this.topWingFinMembraneR1.setPos(4.6F, -0.1F, -0.4F);
        this.topWingFinMembraneR1.addBox(-5.6F, 0.0F, -0.8F, 14.0F, 0.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.bottomWingFinMembraneL2 = new WRModelPart(this, 22, 210);
        this.bottomWingFinMembraneL2.setPos(-5.3F, 0.0F, 0.4F);
        this.bottomWingFinMembraneL2.addBox(-5.7F, 0.0F, -0.8F, 11.0F, 0.0F, 9.0F, 0.0F, 0.0F, 0.0F);
        this.mouthBottom = new WRModelPart(this, 0, 55);
        this.mouthBottom.setPos(0.0F, 1.4F, -6.2F);
        this.mouthBottom.addBox(-3.0F, -1.3F, -7.1F, 6.0F, 4.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.bottomWingFinMembraneL1 = new WRModelPart(this, -7, 210);
        this.bottomWingFinMembraneL1.setPos(-5.9F, -0.1F, -0.4F);
        this.bottomWingFinMembraneL1.addBox(-5.1F, 0.0F, -0.8F, 12.0F, 0.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.legThighL1 = new WRModelPart(this, 73, 21);
        this.legThighL1.setPos(-3.9F, 2.1F, 7.7F);
        this.legThighL1.addBox(-2.2F, -1.4F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legThighL1, 0.568802805888113F, 0.0F, 0.03490658503988659F);
        this.legSegmentL1 = new WRModelPart(this, 75, 36);
        this.legSegmentL1.setPos(-0.6F, 4.2F, -0.3F);
        this.legSegmentL1.addBox(-1.5F, 0.1F, -1.6F, 3.0F, 4.0F, 3.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(legSegmentL1, 0.9261035498516674F, 0.0F, 0.0F);
        this.teethTop = new WRModelPart(this, 29, 40);
        this.teethTop.setPos(0.0F, -0.3F, -5.9F);
        this.teethTop.addBox(-2.5F, 0.3F, -0.7F, 5.0F, 1.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.topWingFinPhalangeL2 = new WRModelPart(this, 80, 155);
        this.topWingFinPhalangeL2.setPos(13.1F, -0.1F, 0.2F);
        this.topWingFinPhalangeL2.addBox(-0.1F, -1.1F, -0.9F, 14.0F, 2.0F, 2.0F, 0.0F, 0.0F, 0.0F);
        this.setRotateAngle(topWingFinPhalangeL2, 0.0F, 0.0F, 0.22689280275926282F);
        this.neck1 = new WRModelPart(this, 62, 0);
        this.neck1.setPos(0.0F, -1.5F, -2.9F);
        this.neck1.addBox(-4.0F, -4.1F, -7.1F, 8.0F, 10.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.topWingFinMembraneL1 = new WRModelPart(this, -10, 173);
        this.topWingFinMembraneL1.setPos(4.8F, -0.1F, -0.4F);
        this.topWingFinMembraneL1.addBox(-5.7F, 0.0F, -10.3F, 14.0F, 0.0F, 12.0F, 0.0F, 0.0F, 0.0F);
        this.teethBottom = new WRModelPart(this, 29, 55);
        this.teethBottom.setPos(0.0F, -1.6F, -5.9F);
        this.teethBottom.addBox(-2.5F, -0.7F, -0.7F, 5.0F, 1.0F, 8.0F, 0.0F, 0.0F, 0.0F);
        this.topWingFinPhalangeL2.addChild(this.topWingFinPhalangeL2_1);
        this.bottomWingFinPhalangeL1.addChild(this.bottomWingFinPhalangeL2);
        this.bottomWingFinPhalangeR2.addChild(this.bottomWingFinMembraneR3);
        this.neck2.addChild(this.neck3);
        this.topWingFinPhalangeR2.addChild(this.topWingFinMembraneR2);
        this.body1.addChild(this.topWingFinPhalangeR1);
        this.head.addChild(this.headFrillR);
        this.bottomWingFinPhalangeR1.addChild(this.bottomWingFinMembraneR1);
        this.body1.addChild(this.bottomWingFinPhalangeL1);
        this.head.addChild(this.eyeL);
        this.tail6.addChild(this.tailFinBottom);
        this.legSegmentR2.addChild(this.footR);
        this.legSegmentR1.addChild(this.legSegmentR2);
        this.neck1.addChild(this.neck2);
        this.topWingFinPhalangeR1.addChild(this.topWingFinPhalangeR2);
        this.neck3.addChild(this.head);
        this.legSegmentL2.addChild(this.footL);
        this.bottomWingFinPhalangeR1.addChild(this.bottomWingFinPhalangeR2);
        this.head.addChild(this.headFrillL);
        this.body2.addChild(this.legThighR1);
        this.tail3.addChild(this.tail4);
        this.legThighR1.addChild(this.legSegmentR1);
        this.tail5.addChild(this.tail6);
        this.bottomWingFinPhalangeL2.addChild(this.bottomWingFinMembraneL3);
        this.bottomWingFinPhalangeR2.addChild(this.bottomWingFinMembraneR2);
        this.body1.addChild(this.bottomWingFinPhalangeR1);
        this.tail6.addChild(this.tailFinTop);
        this.tail4.addChild(this.tail5);
        this.body1.addChild(this.topWingFinPhalangeL1);
        this.topWingFinPhalangeR2.addChild(this.topWingFinMembraneR3);
        this.tail1.addChild(this.tail2);
        this.body2.addChild(this.tail1);
        this.legSegmentL1.addChild(this.legSegmentL2);
        this.topWingFinPhalangeL2.addChild(this.topWingFinMembrane3L);
        this.tail2.addChild(this.tail3);
        this.body1.addChild(this.body2);
        this.head.addChild(this.eyeR);
        this.head.addChild(this.mouthTop);
        this.topWingFinPhalangeR1.addChild(this.topWingFinMembraneR1);
        this.bottomWingFinPhalangeL2.addChild(this.bottomWingFinMembraneL2);
        this.head.addChild(this.mouthBottom);
        this.bottomWingFinPhalangeL1.addChild(this.bottomWingFinMembraneL1);
        this.body2.addChild(this.legThighL1);
        this.legThighL1.addChild(this.legSegmentL1);
        this.mouthTop.addChild(this.teethTop);
        this.topWingFinPhalangeL1.addChild(this.topWingFinPhalangeL2);
        this.body1.addChild(this.neck1);
        this.topWingFinPhalangeL1.addChild(this.topWingFinMembraneL1);
        this.mouthBottom.addChild(this.teethBottom);

        tailArray = new WRModelPart[]{tail1, tail2, tail3, tail4, tail5, tail6};
        headArray = new WRModelPart[]{neck1, neck2, neck3, head};

        this.conduitEye = new ModelPart(16, 16, 0, 0);
        this.conduitWind = new ModelPart(64, 32, 0, 0);
        this.conduitCage = new ModelPart(32, 16, 0, 0);
        this.conduitEye.addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, 0.01F);
        this.conduitWind.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F);
        this.conduitCage.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);

        setDefaultPose();
    }

    @Override
    public ResourceLocation getTexture(ButterflyLeviathanEntity entity)
    {
        switch (entity.getVariant())
        {
            default:
            case 0:
                return BLUE;
            case 1:
                return PURPLE;
            case -1:
                return ALBINO;
        }
    }

    @Override
    public float getShadowRadius(ButterflyLeviathanEntity entity)
    {
        return 2f;
    }

    @Override
    public void scale(ButterflyLeviathanEntity entity, PoseStack ms, float partialTicks)
    {
        super.scale(entity, ms, partialTicks);
        ms.scale(3f, 3f, 3f);
    }

    @Override
    public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
    {
        body1.render(ms, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void postProcess(ButterflyLeviathanEntity entity, PoseStack ms, MultiBufferSource buffer, int light, float limbSwing, float limbSwingAmount, float age, float yaw, float pitch, float partialTicks)
    {
        float powerAlpha = Mth.clamp(entity.lightningCooldown, 1, 255);
        if (powerAlpha > 0)
        {
            VertexConsumer builder = buffer.getBuffer(RenderHelper.getTranslucentGlow(GLOW));
            renderToBuffer(ms, builder, 15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, powerAlpha);
        }

        renderConduit(ms, buffer, light, age, yaw, pitch);
    }

    private void renderConduit(PoseStack ms, MultiBufferSource buffer, int light, float age, float yaw, float pitch)
    {
        if ((entity.getAnimation() == ButterflyLeviathanEntity.CONDUIT_ANIMATION && entity.getAnimationTick() < 15) || !entity.hasConduit())
            return;

        float rotation = (age * -0.0375f) * (180f / Mafs.PI);
        float translation = Mth.sin(age * 0.1F) / 2.0F + 0.5F;
        translation = translation * translation + translation;

        ms.pushPose();
        relocateTo(ms, body1, neck1, neck2, neck3, head);
        ms.scale(0.33f, 0.33f, 0.33f);
        ms.translate(0, -2.25f, -0.7f);

        // Cage
        ms.pushPose();
        ms.translate(0, (0.3F + translation * 0.2F), 0);
        Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F);
        vector3f.normalize();
        ms.mulPose(new Quaternion(vector3f, rotation, true));
        conduitCage.render(ms, CONDUIT_CAGE.buffer(buffer, RenderType::entityCutoutNoCull), light, OverlayTexture.NO_OVERLAY);
        ms.popPose();

        // Wind
        int gen = entity.tickCount / 66 % 3;
        ms.pushPose();
        ms.translate(0, 0.5d, 0);
        if (gen == 1) ms.mulPose(Vector3f.XP.rotationDegrees(90));
        else if (gen == 2) ms.mulPose(Vector3f.ZP.rotationDegrees(90));
        VertexConsumer builder = (gen == 1? CONDUIT_VERTICAL_WIND : CONDUIT_WIND).buffer(buffer, RenderType::entityCutoutNoCull);
        conduitWind.render(ms, builder, light, OverlayTexture.NO_OVERLAY);
        ms.popPose();

        // Wind but its the second time
        ms.pushPose();
        ms.scale(0.875f, 0.875f, 0.875f);
        ms.mulPose(Vector3f.XP.rotationDegrees(180f));
        ms.mulPose(Vector3f.ZP.rotationDegrees(180f));
        conduitWind.render(ms, builder, light, OverlayTexture.NO_OVERLAY);
        ms.popPose();

        // Eye
        ActiveRenderInfo camera = ClientEvents.getClient().getEntityRenderDispatcher().camera;
        ms.pushPose();
        ms.translate(0, (0.3F + translation * 0.2F), 0);
        ms.mulPose(Vector3f.YP.rotationDegrees(camera.getYRot()));
        ms.mulPose(Vector3f.XP.rotationDegrees(camera.getXRot()));
        ms.scale(0.8f, 0.8f, 0.8f);
        conduitEye.render(ms, CONDUIT_OPEN_EYE.buffer(buffer, RenderType::entityCutoutNoCull), light, OverlayTexture.NO_OVERLAY);
        ms.popPose();

        ms.popPose();
    }

    @Override
    public void setupAnim(ButterflyLeviathanEntity entity, float limbSwing, float limbSwingAmount, float bob, float yaw, float pitch)
    {
        reset();
        animator().tick(entity, this, partialTicks);

        if (entity.beached)
        {
            bottomWingFinPhalangeL1.zRot = -0.65f;
            bottomWingFinPhalangeL2.zRot = 0.65f;
            bottomWingFinPhalangeR1.zRot = 0.65f;
            bottomWingFinPhalangeR2.zRot = -0.65f;
        }

        if (entity.isInWater())
        {
            chainSwing(headArray, globalSpeed - 0.1f, 0.15f, 3f, limbSwing, limbSwingAmount);
            chainSwing(ArrayUtils.addAll(tailArray, body2), globalSpeed - 0.1f, 0.2f, -3, limbSwing, limbSwingAmount);

            if (entity.isUnderWater())
            {
                flap(topWingFinPhalangeR1, globalSpeed - 0.1f, 0.75f, false, 0, -0.25f, limbSwing, limbSwingAmount);
                walk(topWingFinPhalangeR1, globalSpeed - 0.1f, 0.35f, false, 0.75f, 0, limbSwing, limbSwingAmount);
                flap(topWingFinPhalangeR2, globalSpeed - 0.1f, 0.75f, false, -2, -0.3f, limbSwing, limbSwingAmount);
                flap(topWingFinMembraneR3, globalSpeed - 0.1f, 0.5f, false, -2, 0.26f, limbSwing, limbSwingAmount);

                flap(topWingFinPhalangeL1, globalSpeed - 0.1f, 0.75f, true, 0, -0.25f, limbSwing, limbSwingAmount);
                walk(topWingFinPhalangeL1, globalSpeed - 0.1f, 0.35f, true, 0.75f, 0, limbSwing, limbSwingAmount);
                flap(topWingFinPhalangeL2, globalSpeed - 0.1f, 0.75f, false, -2, -0.3f, limbSwing, limbSwingAmount);
                flap(topWingFinMembrane3L, globalSpeed - 0.1f, 0.5f, false, -2, 0.26f, limbSwing, limbSwingAmount);

                flap(bottomWingFinPhalangeR1, globalSpeed - 0.1f, 0.75f, false, -0.5f, -0.4f, limbSwing, limbSwingAmount);
                walk(bottomWingFinPhalangeR1, globalSpeed - 0.1f, 0.35f, false, 1.25f, 0, limbSwing, limbSwingAmount);
                flap(bottomWingFinPhalangeR2, globalSpeed - 0.1f, 0.5f, false, -2f, 0.3f, limbSwing, limbSwingAmount);
                flap(bottomWingFinMembraneR3, globalSpeed - 0.1f, 0.5f, false, -2, 0.17f, limbSwing, limbSwingAmount);

                flap(bottomWingFinPhalangeL1, globalSpeed - 0.1f, 0.75f, true, -0.5f, -0.4f, limbSwing, limbSwingAmount);
                walk(bottomWingFinPhalangeL1, globalSpeed - 0.1f, 0.35f, false, 1.25f, 0, limbSwing, limbSwingAmount);
                flap(bottomWingFinPhalangeL2, globalSpeed - 0.1f, 0.5f, true, -2f, 0.3f, limbSwing, limbSwingAmount);
                flap(bottomWingFinMembraneL3, globalSpeed - 0.1f, 0.5f, true, -2, 0.17f, limbSwing, limbSwingAmount);
            }
        }
        else if (!entity.isJumpingOutOfWater())
        {
            if (limbSwingAmount > 0.152f) limbSwingAmount = 0.152f;
            swing(bottomWingFinPhalangeL1, globalSpeed, 2f, false, 0, 0, limbSwing, limbSwingAmount);
            flap(bottomWingFinPhalangeL1, globalSpeed, 1f, false, -1f, 0.25f, limbSwing, limbSwingAmount);
            walk(bottomWingFinPhalangeL1, globalSpeed, -1f, false, -1f, 0, limbSwing, limbSwingAmount);

            swing(bottomWingFinPhalangeL2, globalSpeed, -3f, false, 0, 2, limbSwing, limbSwingAmount);
            flap(bottomWingFinPhalangeL2, globalSpeed, -1f, false, -1f, 0, limbSwing, limbSwingAmount);
            walk(bottomWingFinPhalangeL2, globalSpeed, -0.5f, false, -1f, 0, limbSwing, limbSwingAmount);

            swing(bottomWingFinPhalangeR1, globalSpeed, 2f, false, 0, 0, limbSwing, limbSwingAmount);
            flap(bottomWingFinPhalangeR1, globalSpeed, 1f, true, 1f, 0.25f, limbSwing, limbSwingAmount);
            walk(bottomWingFinPhalangeR1, globalSpeed, -1f, true, -1f, 0, limbSwing, limbSwingAmount);

            swing(bottomWingFinPhalangeR2, globalSpeed, -3f, false, 0, -2, limbSwing, limbSwingAmount);
            flap(bottomWingFinPhalangeR2, globalSpeed, -1f, true, 1f, 0, limbSwing, limbSwingAmount);
            walk(bottomWingFinPhalangeR2, globalSpeed, -0.5f, true, -1f, 0, limbSwing, limbSwingAmount);
        }

        swim(entity.swimTimer.get(partialTicks));
        beach(entity.beachedTimer.get(partialTicks));
        sit(entity.sitTimer.get(partialTicks));

        idle(bob);

        if (!entity.beached)
        {
            body1.xRot = pitch * Mafs.PI / 180f;
            pitch = 0;
        }

        faceTarget(yaw, pitch, 1, headArray);
    }


    public void idle(float bob)
    {
        if (entity.isInWater())
        {
            chainWave(headArray, globalSpeed - 0.45f, 0.05f, 2, bob, 0.5f);
            chainSwing(tailArray, globalSpeed - 0.44f, 0.1f, -2, bob, 0.5f);
            flap(topWingFinPhalangeL1, globalSpeed - 0.45f, 0.15f, false, 0, 0, bob, 0.5f);
            flap(topWingFinPhalangeL2, globalSpeed - 0.45f, 0.15f, false, 0.75f, 0, bob, 0.5f);
            flap(topWingFinPhalangeR1, globalSpeed - 0.45f, 0.15f, true, 0, 0, bob, 0.5f);
            flap(topWingFinPhalangeR2, globalSpeed - 0.45f, 0.15f, false, 0.75f, 0, bob, 0.5f);
            flap(bottomWingFinPhalangeL1, globalSpeed - 0.45f, 0.15f, false, 0, 0, bob, 0.5f);
            flap(bottomWingFinPhalangeL2, globalSpeed - 0.45f, 0.15f, true, 0.75f, 0, bob, 0.5f);
            flap(bottomWingFinPhalangeR1, globalSpeed - 0.45f, 0.15f, true, 0, 0, bob, 0.5f);
            flap(bottomWingFinPhalangeR2, globalSpeed - 0.45f, 0.15f, false, 0.75f, 0, bob, 0.5f);
        }
        else if (!entity.isJumpingOutOfWater())
        {
            chainWave(headArray, globalSpeed - 0.45f, 0.07f, -2, bob, 0.5f);
            chainSwing(tailArray, globalSpeed - 0.46f, 0.4f, -3, bob, 0.5f);
            flap(topWingFinPhalangeL1, globalSpeed - 0.43f, 0.15f, false, 0, 0, bob, 0.5f);
            swing(topWingFinPhalangeL1, globalSpeed - 0.45f, 0.1f, false, 0.5f, 0, bob, 0.5f);
            swing(topWingFinPhalangeL2, globalSpeed - 0.45f, 0.1f, false, 0.5f, 0, bob, 0.5f);
            flap(topWingFinPhalangeL2, globalSpeed - 0.44f, 0.1f, false, 0.5f, 0, bob, 0.5f);
            flap(topWingFinPhalangeR1, globalSpeed - 0.43f, 0.15f, true, 0, 0, bob, 0.5f);
            swing(topWingFinPhalangeR1, globalSpeed - 0.45f, 0.1f, true, 0.5f, 0, bob, 0.5f);
            swing(topWingFinPhalangeR2, globalSpeed - 0.45f, 0.1f, true, 0.5f, 0, bob, 0.5f);
            flap(topWingFinPhalangeR2, globalSpeed - 0.44f, 0.1f, true, 0.5f, 0, bob, 0.5f);

            if (entity.isInSittingPose())
            {
                flap(bottomWingFinPhalangeL1, globalSpeed - 0.43f, -0.1f, false, 0.25f, 0, bob, 0.5f);
                swing(bottomWingFinPhalangeL1, globalSpeed - 0.45f, 0.075f, false, 0.75f, 0, bob, 0.5f);
                flap(bottomWingFinPhalangeR1, globalSpeed - 0.43f, -0.1f, true, 0.25f, 0, bob, 0.5f);
                swing(bottomWingFinPhalangeR1, globalSpeed - 0.45f, 0.075f, true, 0.75f, 0, bob, 0.5f);
            }
        }
    }

    public void roarAnimation()
    {
        animator().startKeyframe(10)
                .rotate(neck1, -0.3f, 0, 0)
                .rotate(neck2, 0.1f, 0, 0)
                .rotate(neck3, 0.2f, 0, 0)
                .rotate(head, 0.1f, 0, 0)
                .endKeyframe();
        animator().startKeyframe(5)
                .rotate(neck1, -0.3f, 0, 0)
                .rotate(mouthTop, -0.3f, 0, 0)
                .rotate(mouthBottom, 0.3f, 0, 0)
                .endKeyframe();
        animator().setStaticKeyframe(43);
        if (entity.getAnimationTick() > 11)
            chainSwing(headArray, globalSpeed - 0.4f, 0.2f, -2, entity.getAnimationTick() - 12 + partialTicks, 0.5f);
        animator().resetKeyframe(6);
    }

    public void conduitAnimation()
    {
        animator().startKeyframe(8)
                .rotate(neck1, -0.3f, 0, 0)
                .rotate(mouthTop, -0.3f, 0, 0)
                .rotate(mouthBottom, 0.3f, 0, 0)
                .endKeyframe();
        animator().setStaticKeyframe(43);
        chainSwing(headArray, globalSpeed - 0.4f, 0.2f, -2, entity.getAnimationTick() - 1 + partialTicks, 0.5f);
        animator().resetKeyframe(8);
    }

    public void biteAnimation()
    {
        animator().startKeyframe(6);
        animator().rotate(neck1, -0.6f, 0, 0);
        animator().rotate(neck2, 0.3f, 0, 0);
        animator().rotate(neck3, 0.4f, 0, 0);
        animator().rotate(head, 0.4f, 0, 0);
        animator().rotate(mouthTop, -0.5f, 0, 0);
        animator().rotate(mouthBottom, 0.5f, 0, 0);
        animator().endKeyframe();
        animator().startKeyframe(3);
        animator().rotate(neck1, 0.45f, 0, 0);
        animator().endKeyframe();
        animator().resetKeyframe(8);
    }

    private void beach(float v)
    {
        setTime(v);

        rotate(neck1, -0.6f, 0, 0);
        rotate(neck2, 0.15f, 0, 0);
        rotate(neck3, 0.2f, 0, 0);
        rotate(head, 0.3f, 0, 0);
    }

    private void swim(float v)
    {
        setTime(v);

        rotate(topWingFinPhalangeL1, 0, 1.05f, 0);
        rotate(topWingFinPhalangeL2, 0, 0.5f, 0);
        rotate(topWingFinPhalangeR1, 0, -1.05f, 0);
        rotate(topWingFinPhalangeR2, 0, -0.5f, 0);
    }

    private void sit(float v)
    {
        setTime(v);

        rotate(bottomWingFinPhalangeL1, -0.35f, 1.05f, -0.35f);
        rotate(bottomWingFinPhalangeL2, 0, 0.5f, -0.1f);
        rotate(bottomWingFinPhalangeR1, -0.35f, -1.05f, 0.35f);
        rotate(bottomWingFinPhalangeR2, 0, -0.5f, 0.1f);
    }

    private static ResourceLocation texture(String png)
    {
        return Wyrmroost.id(FOLDER + "butterfly_leviathan/" + png);
    }
}*/
