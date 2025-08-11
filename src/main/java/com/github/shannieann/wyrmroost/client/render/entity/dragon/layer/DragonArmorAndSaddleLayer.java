package com.github.shannieann.wyrmroost.client.render.entity.dragon.layer;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.item.DragonArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

// Merged into one layer
public class DragonArmorAndSaddleLayer<T extends WRDragonEntity> extends GeoLayerRenderer<T> {
    private final boolean renderSaddle;
    public DragonArmorAndSaddleLayer(IGeoRenderer<T> entityRendererIn, boolean renderSaddle) {
        super(entityRendererIn);
        this.renderSaddle = renderSaddle;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemStack = entityLivingBaseIn.getArmor();
        if (itemStack.getItem() instanceof DragonArmorItem){
            renderModel(getEntityModel(), getArmorResourceLocation(entityLivingBaseIn, itemStack), matrixStackIn, bufferIn, packedLightIn, entityLivingBaseIn, partialTicks, 1.0f, 1.0f, 1.0f);
        }
        if (renderSaddle && entityLivingBaseIn.isSaddled()){
            renderModel(getEntityModel(), getSaddleResourceLocation(entityLivingBaseIn), matrixStackIn, bufferIn, packedLightIn, entityLivingBaseIn, partialTicks, 1.0f, 1.0f, 1.0f);
            // Saddle (if present)
        }
    }

    private ResourceLocation getArmorResourceLocation(T dragon, ItemStack stack){
        String dragonId = dragon.getType().getRegistryName().getPath();
        String armorName = stack.getItem().getRegistryName().getPath();
        return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/" + dragonId +"/accessories/"+armorName+".png");

    }
    // Maybe cache this??
    private ResourceLocation getSaddleResourceLocation(T dragon){
        String dragonId = dragon.getType().getRegistryName().getPath();
        return new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/" + dragonId +"/accessories/saddle.png");
    }
}
