package com.github.wolfshotz.wyrmroost.client.render;

import com.github.wolfshotz.wyrmroost.client.model.TarragonTomeModel;
import com.github.wolfshotz.wyrmroost.items.book.TarragonTomeItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;


public class TarragonTomeRenderer extends GeoItemRenderer<TarragonTomeItem>
{
    public TarragonTomeRenderer() {
        super(new TarragonTomeModel());
    }
    //private final BookModel model;
    //private static final ResourceLocation MODEL_TEXTURE = Wyrmroost.id("textures/item/tarragon_tome_model.png");
    //public static final ModelResourceLocation SPRITE_MODEL_LOCATION = new ModelResourceLocation(Wyrmroost.id("models/tarragon_tome"));
/*
    public TarragonTomeRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet set) {
        super(dispatcher, set);
       // model = new BookModel(set.bakeLayer(ModelLayers.BOOK));
    }
*/
    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transforms, PoseStack ms, MultiBufferSource buffer, int light, int overlay)
    {
        switch (transforms) {
            case GROUND, FIXED, GUI -> {
                //System.out.println(SPRITE_MODEL_LOCATION);
                ms.pushPose();
                Minecraft.getInstance().getItemRenderer().render(stack, transforms, false, ms, buffer, light, overlay, );
                ms.popPose();
            }
            default -> super.renderByItem(stack, transforms, ms, buffer, light, overlay);
        }
    }

    public void renderSprite(ItemStack stack, PoseStack ms, MultiBufferSource buffer, ItemTransforms.TransformType transforms, int light, int overlay)
    {
        ms.pushPose();
        ms.translate(0.5, 0.5, 0.5);
        //BakedModel model = ClientEvents.getClient().getModelManager().getModel(SPRITE_MODEL_LOCATION);
        //ClientEvents.getClient().getItemRenderer().render(stack, transforms, false, ms, buffer, light, overlay, model);
        ms.popPose();
    }

    /*public PoseStack renderModel(ItemStack stack, PoseStack ms, ItemTransforms.TransformType transforms)
    {
        //BookModel model = new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));
        //VertexConsumer builder = ItemRenderer.getFoilBuffer(buffer, model.renderType(MODEL_TEXTURE), false, stack.hasFoil());
        //TarragonTomeItem.Animations animations = ModUtils.getCapability(IAnimatable.CapImpl.CAPABILITY, stack);
        float delta = ClientEvents.getClient().getFrameTime();
        float bob = ClientEvents.getPlayer().tickCount + delta;
        //float flipTime = animations.flipTime.get(delta);
        boolean usingBook = ClientEvents.getClient().screen instanceof BookScreen;
        //float page1 = Mth.clamp(Mth.frac(1.0f + (usingBook? 0.1f : 0.25f)) * 1.6f - 0.3f, 0, 1);
        //float page2 = Mth.clamp(Mth.frac(1.0f + (usingBook? 0.9f : 0.75f)) * 1.6f - 0.3f, 0, 1);

        ms.pushPose();
        if (transforms == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND || transforms == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
        {
            ms.translate(0, 0, 0);
            RenderHelper.mirrorX(ms);
        }

        ms.translate(-0.0625, 0, 0);
        if (transforms.firstPerson())
        {
            if (usingBook)
            {
                ms.translate(0, 0, 0);
                ms.mulPose(Vector3f.YN.rotationDegrees(90));
            }
            else
            {
                ms.translate(0, 0, 0);
                ms.mulPose(Vector3f.YN.rotationDegrees(115));
                ms.mulPose(Vector3f.ZP.rotationDegrees(25f));
            }
        }
        else
        {
            ms.translate(0, 0, Mth.sin(bob * 0.1f) * 0.2f);
            ms.mulPose(Vector3f.XP.rotationDegrees(45f));
            ms.mulPose(Vector3f.YN.rotationDegrees(115));
            ms.mulPose(new Quaternion(Mth.sin(bob * 0.15f) * -0.05f, 0, 0, 1));
        }

        //model.setupAnim(bob, page1, page2, usingBook? 1.15f : 1f);
        //model.render(ms, builder, light, overlay, 1f, 1f, 1f, 1f);
        return ms;
    }*/
}
