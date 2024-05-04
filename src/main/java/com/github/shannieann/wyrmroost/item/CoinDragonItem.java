package com.github.shannieann.wyrmroost.item;

/*import com.github.shannieann.wyrmroost.Wyrmroost;
//import com.github.shannieann.wyrmroost.entities.dragon.CoinDragonEntity;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import com.github.shannieann.wyrmroost.registry.WRItems;
import com.github.shannieann.wyrmroost.util.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class CoinDragonItem extends Item
{
    public static final String DATA_ENTITY = "CoinDragonData";
    public static final ResourceLocation VARIANT_OVERRIDE = Wyrmroost.id("variant");

    public CoinDragonItem()
    {
        super(WRItems.builder().stacksTo(1));
        if (ModUtils.isClient())
            ItemProperties.register(this, VARIANT_OVERRIDE, (s, w, p, z) -> s.getOrCreateTag().getCompound(DATA_ENTITY).getInt(CoinDragonEntity.DATA_VARIANT));
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public InteractionResult useOn(UseOnContext context)
    {
        Level level = context.getLevel();
        //CoinDragonEntity entity = WREntityTypes.COIN_DRAGON.get().create(level);
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if (!level.isClientSide && stack.hasTag()) // read data first!: setting position before reading will reset that position!
        {
            CompoundTag tag = stack.getTag();
            if (tag.contains(DATA_ENTITY)) entity.deserializeNBT(tag.getCompound(DATA_ENTITY));
            if (stack.hasCustomHoverName()) entity.setCustomName(stack.getHoverName()); // set entity name from stack name
        }

        entity.absMoveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        if (!level.noCollision(entity))
        {
            player.displayClientMessage(new TranslatableComponent("item.wyrmroost.soul_crystal.fail").withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }

        if (!player.isCreative() || stack.getOrCreateTag().contains(DATA_ENTITY))
            player.setItemInHand(context.getHand(), ItemStack.EMPTY);
        entity.setDeltaMovement(Vec3.ZERO);
        entity.setYRot(entity.yHeadRot = player.yHeadRot + 180);
        level.addFreshEntity(entity);
        return InteractionResult.SUCCESS;
    }

    public static LootPoolEntryContainer.Builder<?> getLootEntry()
    {
        CompoundTag parent = new CompoundTag();
        CompoundTag child = new CompoundTag(); // because the parent nbt gets merged with the stack, we need to nest a child within the one getting merged
        child.putInt(CoinDragonEntity.DATA_VARIANT, new Random().nextInt(5));
        parent.put(DATA_ENTITY, child);
        return LootItem.lootTableItem(WRItems.COIN_DRAGON.get()).apply(SetNbtFunction.setTag(parent));
    }
}
*/