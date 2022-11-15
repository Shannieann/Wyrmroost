package com.github.wolfshotz.wyrmroost.items;

/*import com.github.wolfshotz.wyrmroost.Wyrmroost;
//import com.github.wolfshotz.wyrmroost.entities.dragon.LesserDesertwyrmEntity;
import com.github.wolfshotz.wyrmroost.registry.WREntities;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
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
    public static final String DATA_CONTENTS = "DesertWyrm"; // Should ALWAYS be a compound. If it throws a cast class exception SOMETHING fucked up.

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
                    LesserDesertwyrmEntity entity = WREntities.LESSER_DESERTWYRM.get().create(level);

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
}*/
