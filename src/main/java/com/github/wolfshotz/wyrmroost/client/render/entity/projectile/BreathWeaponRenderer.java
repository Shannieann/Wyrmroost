package com.github.wolfshotz.wyrmroost.client.render.entity.projectile;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.entities.projectile.DragonProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;

public class BreathWeaponRenderer extends EntityRenderer<DragonProjectileEntity>
{
    public static final ResourceLocation BLUE_FIRE = Wyrmroost.id("entity/projectiles/rr_breath/blue_fire");

    public BreathWeaponRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager);
    }

    @Override
    public void render(DragonProjectileEntity entity, float yaw, float partialTicks, PoseStack ms, MultiBufferSource typeBuffer, int packedLine)
    {
        if (entity.isOnFire())
        {
            renderFire(ms, typeBuffer, entity);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(DragonProjectileEntity entity)
    {
        return null;
    }

    private void renderFire(PoseStack ms, MultiBufferSource typeBuffer, Entity entity)
    {
        Function<ResourceLocation, TextureAtlasSprite> func = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
        TextureAtlasSprite fireSprite1 = func.apply(BLUE_FIRE);
        ms.pushPose();
        float width = entity.getBbWidth() * 1.4F;
        ms.scale(width, width, width);
        float x = 0.5F;
        float height = entity.getBbHeight() / width;
        float y = 0.0F;
        ms.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        ms.translate(0, 0, (-0.3f + (float) ((int) height) * 0.02f));
        float z = 0;
        VertexConsumer vertex = typeBuffer.getBuffer(Sheets.cutoutBlockSheet());
        PoseStack.Pose msEntry = ms.last();

        for (int i = 0; height > 0; i++)
        {
            float minU = fireSprite1.getU0();
            float minV = fireSprite1.getV0();
            float maxU = fireSprite1.getU1();
            float maxV = fireSprite1.getV1();
            if (i / 2 % 2 == 0)
            {
                float prevMaxU = maxU;
                maxU = minU;
                minU = prevMaxU;
            }

            vertex(msEntry, vertex, x, -y, z, maxU, maxV);
            vertex(msEntry, vertex, -x, -y, z, minU, maxV);
            vertex(msEntry, vertex, -x, 1.4f - y, z, minU, minV);
            vertex(msEntry, vertex, x, 1.4f - y, z, maxU, minV);
            height -= 0.45f;
            y -= 0.45f;
            x *= 0.9f;
            z += 0.03f;
        }

        ms.popPose();
    }

    private static void vertex(PoseStack.Pose msEntry, VertexConsumer bufferIn, float x, float y, float z, float texU, float texV)
    {
        bufferIn.vertex(msEntry.pose(), x, y, z)
                .color(255, 255, 255, 255)
                .uv(texU, texV)
                .overlayCoords(0, 10)
                .uv2(240)
                .normal(msEntry.normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
