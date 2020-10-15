package WolfShotz.Wyrmroost.client.render;

import WolfShotz.Wyrmroost.client.ClientEvents;
import WolfShotz.Wyrmroost.client.model.ModelAnimator;
import WolfShotz.Wyrmroost.client.model.WRModelRenderer;
import WolfShotz.Wyrmroost.client.render.entity.fog_wraith.FogWraithModel;
import WolfShotz.Wyrmroost.client.render.entity.fog_wraith.FogWraithRenderer;
import WolfShotz.Wyrmroost.entities.dragon.FogWraithEntity;
import WolfShotz.Wyrmroost.entities.util.animation.Animation;
import WolfShotz.Wyrmroost.entities.util.animation.CapabilityAnimationHandler;
import WolfShotz.Wyrmroost.entities.util.animation.IAnimatable;
import WolfShotz.Wyrmroost.items.FogWraithTailsItem;
import WolfShotz.Wyrmroost.registry.WREntities;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

public class FogWraithTailsStackRenderer extends ItemStackTileEntityRenderer
{
    public static final Animation TAIL_SWIPE_ANIMATION = new Animation(15);
    public static final Animation GRAPPLE_ANIMATION = new Animation(13);
    public static FogWraithModel model = null;

    @Override
    @SuppressWarnings("unchecked")
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transforms, MatrixStack ms, IRenderTypeBuffer typeBuffer, int light, int overlay)
    {
        if (model == null)
            model = ((LivingRenderer<FogWraithEntity, FogWraithModel>) ClientEvents
                    .getClient()
                    .getRenderManager()
                    .renderers
                    .get(WREntities.FOG_WRAITH.get()))
                    .getEntityModel();

        final IVertexBuilder buffer = ItemRenderer.getBuffer(typeBuffer, RenderType.getEntityCutout(FogWraithRenderer.TEXTURE), false, stack.hasEffect());
        final float partialTicks = ClientEvents.getPartialTicks();
        final float tick = ClientEvents.getPlayer().ticksExisted + partialTicks;
        float timer = 0;
        int tailUseNum = 1;

        model.resetToDefaultPose();

        switch (transforms)
        {
            case THIRD_PERSON_LEFT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                tailUseNum = 0;
            case THIRD_PERSON_RIGHT_HAND:
            case FIRST_PERSON_RIGHT_HAND:
            {
                timer = FogWraithTailsItem.getCapability(stack).transition.get(partialTicks);
                final IAnimatable animCap = stack.getCapability(CapabilityAnimationHandler.ANIMATABLE_CAPABILITY).orElseThrow(NullPointerException::new);
                final ModelAnimator animator = model.animator;
                final WRModelRenderer[] parts = model.tails[tailUseNum];

                model.animator.update(animCap, partialTicks);

                if (timer > 0)
                {
                    model.setTime(timer);
                    model.chainWave(parts, 0.1f, 0.05f, -2, tick, timer * 0.5f);
                    model.rotate(parts[0], 0.8f, 0.15f, 0);
                    for (WRModelRenderer part : parts) model.rotate(part, -0.2f, 0, 0);
                }

                if (animator.setAnimation(GRAPPLE_ANIMATION))
                {
                    animator.startKeyframe(5);
                    animator.rotate(parts[0], -1.6f, 0, 0);
                    for (WRModelRenderer part : parts)
                    {
                        animator.rotate(part, 0.2f, 0, 0);
                    }
                    animator.endKeyframe();
                    animator.setStaticKeyframe(5);
                    animator.resetKeyframe(3);
                }
                else if (animator.setAnimation(TAIL_SWIPE_ANIMATION))
                {
                    animator.startKeyframe(2);
                    animator.rotate(parts[0], -1.3f, 0, 0);
                    for (WRModelRenderer part : parts) animator.rotate(part, 0.2f, 0, 0);
                    animator.endKeyframe();
                    animator.setStaticKeyframe(9);
                    animator.resetKeyframe(2);
                    model.chainSwing(parts, 0.6f, 0.75f, -2, animCap.getAnimationTick() + partialTicks, 0.5f);
                }
            }
            default:
                for (int i = 0; i < model.tails.length; i++)
                {
                    model.animateTail(i, tick, i == tailUseNum? -timer + 1: 1);
                    model.tails[i][0].render(ms, buffer, light, overlay);
                }
        }
    }
}