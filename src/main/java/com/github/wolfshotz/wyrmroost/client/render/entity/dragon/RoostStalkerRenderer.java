package com.github.wolfshotz.wyrmroost.client.render.entity.dragon;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.client.model.entity.RoostStalkerModel;
import com.github.wolfshotz.wyrmroost.entities.dragon.RoostStalkerEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;

public class RoostStalkerRenderer<T extends RoostStalkerEntity> extends MobRenderer<T, RoostStalkerModel<T>> {
    private static final ResourceLocation ROOSTSTALKER_LOCATION = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/body.png");
    private static final ResourceLocation SPECIAL_LOCATION = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/body_spe.png");
    public RoostStalkerRenderer(EntityRendererProvider.Context context) {
        super(context, new RoostStalkerModel<>(context.bakeLayer(RoostStalkerModel.LAYER_LOCATION)), 0.5f);
        this.addLayer(new RoostStalkerEyeLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(RoostStalkerEntity dragon) {
        final int variant = dragon.getVariant();
        switch (variant){
            case 1: return SPECIAL_LOCATION;
            default: return ROOSTSTALKER_LOCATION;
        }
    }

    public class RoostStalkerEyeLayer<T extends RoostStalkerEntity> extends DragonEyesLayer<T, RoostStalkerModel<T>> {
        private static final ResourceLocation EYES_TEX = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/body_glow.png");
        private static final ResourceLocation SPE_EYES_TEX = new ResourceLocation(Wyrmroost.MOD_ID, "textures/entity/dragon/roost_stalker/body_spe_glow.png");
        public RoostStalkerEyeLayer(RenderLayerParent<T, RoostStalkerModel<T>> p_116981_) {
            super(p_116981_);
        }

        @Override
        public RenderType renderType(T dragon) {
            ResourceLocation eyes;
            switch(dragon.getVariant()){
                case 1: eyes = SPE_EYES_TEX; break;
                default: eyes = EYES_TEX;
            }
            return RenderType.eyes(eyes);
        }
    }
}