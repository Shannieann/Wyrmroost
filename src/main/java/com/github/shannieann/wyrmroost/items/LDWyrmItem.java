package com.github.shannieann.wyrmroost.items;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entities.dragon.LesserDesertwyrmEntity;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import com.github.shannieann.wyrmroost.registry.WRItems;
import com.github.shannieann.wyrmroost.util.ModUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class LDWyrmItem extends Item
{
    public static final String DATA_CONTENTS = "DesertWyrm";

    public LDWyrmItem()
    {
        super(WRItems.builder());

        if (ModUtils.isClient())
            ItemProperties.register(this, Wyrmroost.id("is_alive"), (stack, world, player, z) ->
            {
                if (stack.hasTag() && stack.getTag().contains(DATA_CONTENTS)) return 1f;
                return 0f;
            });
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        ItemStack stack = context.getItemInHand();
        if (stack.hasTag())
        {
            CompoundTag tag = stack.getTag();
            if (tag.contains(DATA_CONTENTS))
            {
                Level level = context.getLevel();
                if (!level.isClientSide)
                {
                    BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
                    CompoundTag contents = tag.getCompound(DATA_CONTENTS);
                    LesserDesertwyrmEntity entity = WREntityTypes.LESSER_DESERTWYRM.get().create(level);

                    entity.deserializeNBT(contents);
                    if (stack.hasCustomHoverName())
                        entity.setCustomName(stack.getHoverName()); // Item name takes priority
                    entity.absMoveTo(pos.getX(), pos.getY(), pos.getZ());
                    level.addFreshEntity(entity);
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
