package com.github.wolfshotz.wyrmroost.items;

/*import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import com.github.wolfshotz.wyrmroost.client.render.DragonEggStackRenderer;
import com.github.wolfshotz.wyrmroost.entities.dragon.TameableDragonEntity;
import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggEntity;
import com.github.wolfshotz.wyrmroost.entities.dragonegg.DragonEggProperties;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class DragonEggItem extends Item
{
    private static final BlockEntityWithoutLevelRenderer renderer = new DragonEggStackRenderer();
    public DragonEggItem()
    {
        super(WRItems.builder().stacksTo(1));
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity)
    {
        if (!player.isCreative()) return false;
        if (!entity.isAlive()) return false;
        if (!(entity instanceof TameableDragonEntity)) return false;

        CompoundTag nbt = new CompoundTag();
        nbt.putString(DragonEggEntity.DATA_DRAGON_TYPE, EntityType.getKey(entity.getType()).toString());
        nbt.putInt(DragonEggEntity.DATA_HATCH_TIME, DragonEggProperties.get(entity.getType()).getHatchTime());
        stack.setTag(nbt);

        player.displayClientMessage(getName(stack), true);
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx)
    {
        Player player = ctx.getPlayer();
        if (player.isShiftKeyDown()) return super.useOn(ctx);

        Level level = ctx.getLevel();
        CompoundTag tag = ctx.getItemInHand().getTag();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (tag == null || !tag.contains(DragonEggEntity.DATA_DRAGON_TYPE)) return InteractionResult.PASS;
        if (!state.getCollisionShape(level, pos).isEmpty()) pos = pos.relative(ctx.getClickedFace());
        if (!level.getEntitiesOfClass(DragonEggEntity.class, new AABB(pos)).isEmpty())
            return InteractionResult.FAIL;

        DragonEggEntity eggEntity = new DragonEggEntity(ModUtils.getEntityTypeByKey(tag.getString(DragonEggEntity.DATA_DRAGON_TYPE)), tag.getInt(DragonEggEntity.DATA_HATCH_TIME), level);
        eggEntity.absMoveTo(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);

        if (!level.isClientSide) level.addFreshEntity(eggEntity);
        if (!player.isCreative()) player.setItemInHand(ctx.getHand(), ItemStack.EMPTY);

        return InteractionResult.SUCCESS;
    }
    
    @Override
    public Component getName(ItemStack stack)
    {
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()) return super.getName(stack);
        Optional<EntityType<?>> type = EntityType.byString(tag.getString(DragonEggEntity.DATA_DRAGON_TYPE));
        
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

        if (tag != null && tag.contains(DragonEggEntity.DATA_HATCH_TIME))
            tooltip.add(new TranslatableComponent("item.wyrmroost.egg.tooltip", tag.getInt(DragonEggEntity.DATA_HATCH_TIME) / 1200).withStyle(ChatFormatting.AQUA));
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
        tag.putString(DragonEggEntity.DATA_DRAGON_TYPE, EntityType.getKey(type).toString());
        tag.putInt(DragonEggEntity.DATA_HATCH_TIME, hatchTime);
        stack.setTag(tag);
        return stack;
    }
}
*/