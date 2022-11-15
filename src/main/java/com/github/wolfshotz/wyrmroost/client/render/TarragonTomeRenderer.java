package com.github.wolfshotz.wyrmroost.client.render;

/*import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.client.screen.BookScreen;
import com.github.wolfshotz.wyrmroost.items.book.TarragonTomeItem;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.github.wolfshotz.wyrmroost.util.animation.IAnimatable;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class TarragonTomeRenderer extends BlockEntityWithoutLevelRenderer
{
    private static final BookModel MODEL = new BookModel(new ModelPart[1]);
    private static final ResourceLocation MODEL_TEXTURE = Wyrmroost.id("textures/item/tarragon_tome_model.png");
    public static final ModelResourceLocation SPRITE_MODEL_LOCATION = new ModelResourceLocation(Wyrmroost.id("book_sprite"), "inventory");

    public TarragonTomeRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        super(p_172550_, p_172551_);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transforms, PoseStack ms, MultiBufferSource buffer, int light, int overlay)
    {
        switch(transforms)
        {
            case GROUND:
            case FIXED:
            case GUI:
                renderSprite(stack, ms, buffer, transforms, light, overlay);
                break;
            default:
                renderModel(stack, ms, buffer, transforms, light, overlay);
                break;
        }
    }

    public void renderSprite(ItemStack stack, PoseStack ms, MultiBufferSource buffer, ItemTransforms.TransformType transforms, int light, int overlay)
    {
        ms.pushPose();
        ms.translate(0.5, 0.5, 0.5);
        BakedModel model = ClientEvents.getClient().getModelManager().getModel(SPRITE_MODEL_LOCATION);
        ClientEvents.getClient().getItemRenderer().render(stack, transforms, false, ms, buffer, light, overlay, model);
        ms.popPose();
    }

    public void renderModel(ItemStack stack, PoseStack ms, MultiBufferSource buffer, ItemTransforms.TransformType transforms, int light, int overlay)
    {
        VertexConsumer builder = ItemRenderer.getFoilBuffer(buffer, MODEL.renderType(MODEL_TEXTURE), false, stack.hasFoil());
        TarragonTomeItem.Animations animations = ModUtils.getCapability(IAnimatable.CapImpl.CAPABILITY, stack);
        float delta = ClientEvents.getClient().getFrameTime();
        float bob = ClientEvents.getPlayer().tickCount + delta;
        float flipTime = animations.flipTime.get(delta);
        boolean usingBook = ClientEvents.getClient().screen instanceof BookScreen;
        float page1 = Mth.clamp(Mth.frac(flipTime + (usingBook? 0.1f : 0.25f)) * 1.6f - 0.3f, 0, 1);
        float page2 = Mth.clamp(Mth.frac(flipTime + (usingBook? 0.9f : 0.75f)) * 1.6f - 0.3f, 0, 1);

        ms.pushPose();
        if (transforms == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND || transforms == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND)
        {
            ms.translate(1, 0, 0);
            RenderHelper.mirrorX(ms);
        }

        ms.translate(-0.0625, 0, 0);
        if (transforms.firstPerson())
        {
            if (usingBook)
            {
                ms.translate(0, 1.02, 0.6);
                ms.mulPose(Vector3f.YN.rotationDegrees(90));
            }
            else
            {
                ms.translate(0.825, 0.5, 0);
                ms.mulPose(Vector3f.YN.rotationDegrees(115));
                ms.mulPose(Vector3f.ZP.rotationDegrees(25f));
            }
        }
        else
        {
            ms.translate(0.75f, 1.25f, 1.1f + Mth.sin(bob * 0.1f) * 0.2f);
            ms.mulPose(Vector3f.XP.rotationDegrees(45f));
            ms.mulPose(Vector3f.YN.rotationDegrees(115));
            ms.mulPose(new Quaternion(Mth.sin(bob * 0.15f) * -0.05f, 0, 0, 1));
        }

        MODEL.setupAnim(bob, page1, page2, usingBook? 1.15f : 1f);
        MODEL.render(ms, builder, light, overlay, 1f, 1f, 1f, 1f);
        ms.popPose();
    }
}
*/