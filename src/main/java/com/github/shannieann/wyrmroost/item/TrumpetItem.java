package com.github.shannieann.wyrmroost.item;

import com.github.shannieann.wyrmroost.registry.WRItems;
import com.github.shannieann.wyrmroost.registry.WRSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class TrumpetItem extends Item {
    Random random = new Random();
    public TrumpetItem()  {
        super(WRItems.builder());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        SoundEvent sound = player.getRandom().nextBoolean()? WRSounds.ENTITY_BFLY_IDLE.get() : WRSounds.ENTITY_BFLY_ROAR.get();
        level.playSound(player, player.blockPosition(), sound, SoundSource.PLAYERS, 0.75f, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
        player.getCooldowns().addCooldown(this, 50);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(new TranslatableComponent("item.wyrmroost.trumpet.desc").withStyle(ChatFormatting.GRAY));
    }
}
