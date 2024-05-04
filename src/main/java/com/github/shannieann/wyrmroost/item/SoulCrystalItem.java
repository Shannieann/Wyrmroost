package com.github.shannieann.wyrmroost.item;

import com.github.shannieann.wyrmroost.entity.projectile.SoulCrystalEntity;
import com.github.shannieann.wyrmroost.registry.WRItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static com.github.shannieann.wyrmroost.entity.projectile.SoulCrystalEntity.*;


@SuppressWarnings("ConstantConditions")
public class SoulCrystalItem extends Item
{
    Random random = new Random();
    public SoulCrystalItem()
    {
        super(WRItems.builder().durability(10));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand)
    {
        boolean more = stack.getCount() > 1;
        ItemStack split = (more? stack.split(1) : stack);
        InteractionResult result = captureDragon(player, target.getCommandSenderWorld(), split, target);
        if (more && !player.getInventory().add(split)) player.drop(split, true);
        return result;
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        return releaseDragon(context.getLevel(), context.getPlayer(), context.getItemInHand(), context.getClickedPos(), context.getClickedFace());
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (random.nextFloat() * 0.4f + 0.8f));
        if (!level.isClientSide)
        {
            SoulCrystalEntity entity = new SoulCrystalEntity(stack.split(1), player, level);
            entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5f, 1f);
            level.addFreshEntity(entity);
        }
        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide), stack);
    }

    @Override
    public Component getName(ItemStack stack)
    {
        TranslatableComponent name = (TranslatableComponent) super.getName(stack);
        if (containsDragon(stack)) name.withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC);
        return name;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn)
    {
        if (containsDragon(stack))
        {
            CompoundTag tag = stack.getTag().getCompound(DATA_DRAGON);
            Component name;

            if (tag.contains("CustomName"))
                name = Component.Serializer.fromJson(tag.getString("CustomName"));
            else name = EntityType.byString(tag.getString("id")).orElse(null).getDescription();

            tooltip.add(name.copy().withStyle(ChatFormatting.BOLD));
            Tag nameData = tag.get("OwnerName");
            if (nameData != null)
            {
                tooltip.add(new TextComponent("Tamed by ")
                        .append(new TextComponent(nameData.getAsString()).withStyle(ChatFormatting.ITALIC)));
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack)
    {
        return super.isFoil(stack) || containsDragon(stack);
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return 16;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return enchantment == Enchantments.UNBREAKING;
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        return stack.getCount() == 1;
    }

    @Override
    public int getEnchantmentValue()
    {
        return 1;
    }
}
