package com.github.shannieann.wyrmroost.item;

import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.entity.dragonegg.DragonEggEntity;
import com.github.shannieann.wyrmroost.entity.dragonegg.DragonEggProperties;
import com.github.shannieann.wyrmroost.registry.WRItems;
import com.github.shannieann.wyrmroost.util.WRModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class DragonEggItem extends Item
{
    //private static final BlockEntityWithoutLevelRenderer renderer = new DragonEggStackRenderer(null, null);
    public DragonEggItem() {

        super(WRItems.builder().stacksTo(1));
    }

    /*@Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderer;
            }
        });
    }*/

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.isCreative()) {
            return false;
        }
        if (!entity.isAlive()) {
            return false;
        }
        if (!(entity instanceof WRDragonEntity)) {
            return false;
        }

        CompoundTag nbt = new CompoundTag();
        nbt.putString("ContainedDragon", EntityType.getKey(entity.getType()).toString());
        nbt.putInt("HatchTime", DragonEggProperties.get(entity.getType()).getHatchTime());
        stack.setTag(nbt);

        player.displayClientMessage(getName(stack), true);
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {


        Player player = context.getPlayer();
        if (player.isShiftKeyDown()) return super.useOn(context);

        Level level = context.getLevel();
        CompoundTag tag = context.getItemInHand().getTag();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (!state.getCollisionShape(level, pos).isEmpty()) {
            pos = pos.relative(context.getClickedFace());
        }



        DragonEggEntity eggEntity = new DragonEggEntity(WRModUtils.getEntityTypeByKey(tag.getString("ContainedDragon")), tag.getInt("Hatch Time"), level);
        eggEntity.absMoveTo(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);

        if (!level.isClientSide) level.addFreshEntity(eggEntity);
        if (!player.isCreative()) player.setItemInHand(context.getHand(), ItemStack.EMPTY);
        return InteractionResult.SUCCESS;
    }
    
    @Override
    public Component getName(ItemStack stack)
    {
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()) return super.getName(stack);
        Optional<EntityType<?>> type = EntityType.byString(tag.getString("ContainedDragon"));
        
        if (type.isPresent())
        {
            String dragonTranslation = type.get().getDescription().getString();
            return new TranslatableComponent(dragonTranslation + " ").append(new TranslatableComponent(getDescriptionId()));
        }
        
        return super.getName(stack);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        CompoundTag tag = stack.getTag();

        if (tag != null && tag.contains("ContainedDragon"))
            tooltip.add(new TranslatableComponent("item.wyrmroost.egg.tooltip", tag.getInt("HatchTime") / 1200).withStyle(ChatFormatting.AQUA));
        Player player = ClientEvents.getPlayer();
        if (player != null && player.isCreative())
            tooltip.add(new TranslatableComponent("item.wyrmroost.egg.creativetooltip").withStyle(ChatFormatting.GRAY));
    }

    public static ItemStack getStack(EntityType<?> type)
    {
        return getStack(type, DragonEggProperties.get(type).getHatchTime());
    }

    public static ItemStack getStack(EntityType<?> type, int hatchTime)
    {
        ItemStack stack = new ItemStack(WRItems.DRAGON_EGG.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("ContainedDragon", EntityType.getKey(type).toString());
        tag.putInt("HatchTime", hatchTime);
        stack.setTag(tag);
        return stack;
    }
}
