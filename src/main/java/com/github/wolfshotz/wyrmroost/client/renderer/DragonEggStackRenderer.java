package com.github.wolfshotz.wyrmroost.client.renderer;

import com.github.wolfshotz.wyrmroost.client.renderer.entity.DragonEggRenderer;
import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggEntity;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

public class DragonEggStackRenderer extends BlockEntityWithoutLevelRenderer
{
    public DragonEggStackRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }
    //public DragonEggStackRenderer() {
        //super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    //}

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transform, PoseStack ms, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        VertexConsumer builder = ItemRenderer.getFoilBuffer(buffer, DragonEggRenderer.MODEL.renderType(getEggTexture(stack)), false, stack.hasFoil());
        DragonEggRenderer.MODEL.renderToBuffer(ms, builder, combinedLight, combinedOverlay, 1, 1, 1, 1);
    }

    private static ResourceLocation getEggTexture(ItemStack stack)
    {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(DragonEggEntity.DATA_DRAGON_TYPE))
        {
            EntityType<?> type = ModUtils.getEntityTypeByKey(tag.getString(DragonEggEntity.DATA_DRAGON_TYPE));
            if (type != null) return DragonEggRenderer.getDragonEggTexture(type);
        }

        return DragonEggRenderer.DEFAULT_TEXTURE;
    }
}
