package com.github.shannieann.wyrmroost.items.book.action;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.items.book.TarragonTomeItem;
import com.github.shannieann.wyrmroost.util.ModUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

//import com.github.shannieann.wyrmroost.client.render.RenderHelper;

public class HomeBookAction implements BookAction
{
    @Override
    public void onSelected(WRDragonEntity dragon, Player player, ItemStack stack)
    {
        if (dragon.hasRestriction())
        {
            dragon.clearHome();
            TarragonTomeItem.setAction(BookActions.DEFAULT, player, stack);
        }
        else if (player.level.isClientSide)
            player.displayClientMessage(new TranslatableComponent(TRANSLATE_PATH + "home.set.info"), true);
    }

    @Override
    public InteractionResult clickBlock(WRDragonEntity dragon, UseOnContext context)
    {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        TarragonTomeItem.setAction(BookActions.DEFAULT, context.getPlayer(), stack);
        if (level.getBlockState(pos).getMaterial().isSolid())
        {
            dragon.setHomePos(pos);
            ModUtils.playLocalSound(level, pos, SoundEvents.BEACON_POWER_SELECT, 0.75f, 2f);
            ModUtils.playLocalSound(level, pos, SoundEvents.BOOK_PAGE_TURN, 0.75f, 1f);
        }
        else
        {
            ModUtils.playLocalSound(level, pos, SoundEvents.REDSTONE_TORCH_BURNOUT, 0.75f, 1);
            for (int i = 0; i < 10; i++)
                level.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5d, pos.getY() + 1, pos.getZ() + 0.5d, 0, i * 0.025, 0);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void render(WRDragonEntity dragon, PoseStack ms, float partialTicks)
    {
        /*HitResult rtr = ClientEvents.getClient().hitResult;
        int ticks = (dragon == null)? 0 : dragon.tickCount;
        if (rtr instanceof BlockHitResult)
            RenderHelper.drawBlockPos(ms,
                    ((BlockHitResult) rtr).getBlockPos(),
                    Math.cos((ticks + partialTicks) * 0.2) * 4.5 + 4.5,
                    0x4d0000ff,
                    true);*/
    }

    @Override
    public String getTranslateKey(@Nullable WRDragonEntity dragon)
    {
        if (dragon != null && dragon.hasRestriction())
            return TRANSLATE_PATH + "home.remove";
        return TRANSLATE_PATH + "home.set";
    }
}
