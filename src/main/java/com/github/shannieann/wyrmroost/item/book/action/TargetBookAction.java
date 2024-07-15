package com.github.shannieann.wyrmroost.item.book.action;

import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.client.render.RenderHelper;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.util.WRMathsUtility;
import com.github.shannieann.wyrmroost.util.WRModUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;

public class TargetBookAction implements BookAction
{
    @Override
    public void onSelected(WRDragonEntity dragon, Player player, ItemStack stack)
    {
        dragon.clearAI();
        dragon.clearHome();
        dragon.setOrderedToSit(false);
    }

    @Override
    public InteractionResult rightClick(WRDragonEntity dragon, Player player, ItemStack stack)
    {
        EntityHitResult ertr = clip(player, dragon);
        if (ertr != null)
        {
            dragon.setTarget((LivingEntity) ertr.getEntity());
            if (player.level.isClientSide)
                WRModUtils.playLocalSound(player.level, player.blockPosition(), SoundEvents.BLAZE_SHOOT, 1, 0.5f);
            return InteractionResult.sidedSuccess(player.level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void render(WRDragonEntity dragon, PoseStack ms, float partialTicks)
    {
        EntityHitResult rtr = clip(ClientEvents.getPlayer(), dragon);
        if (rtr != null && rtr.getEntity() != dragon.getTarget())
            RenderHelper.renderEntityOutline(rtr.getEntity(), 255, 0, 0, (int) (Mth.cos((dragon.tickCount + partialTicks) * 0.2f) * 35 + 45));
    }

    @Nullable
    private EntityHitResult clip(Player player, WRDragonEntity dragon)
    {
        if (dragon == null) return null;
        return WRMathsUtility.clipEntities(player,
                40,
                0.35,
                e -> e instanceof LivingEntity && dragon.wantsToAttack((LivingEntity) e, dragon.getOwner()));
    }

    @Override
    public String getTranslateKey(@Nullable WRDragonEntity dragon)
    {
        return TRANSLATE_PATH + "target";
    }
}
