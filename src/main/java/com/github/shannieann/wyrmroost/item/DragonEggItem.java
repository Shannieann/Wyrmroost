package com.github.shannieann.wyrmroost.item;

import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.entity.dragonegg.DragonEggProperties;
import com.github.shannieann.wyrmroost.entity.dragonegg.WRDragonEggEntity;
import com.github.shannieann.wyrmroost.registry.WRItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DragonEggItem extends Item {
    public WRDragonEntity containedDragon;
    public int hatchTime;

    public DragonEggItem(Properties pProperties) {
        super(WRItems.builder().stacksTo(1));
    }

    //Used to create a DragonEgg, via breeding dragons
    public ItemStack getItemStack(WRDragonEntity dragonEntity, int hatchTime) {
        ItemStack stack = new ItemStack(WRItems.DRAGON_EGG.get());
        CompoundTag nbt = new CompoundTag();
        nbt.putString("contained_dragon", EntityType.getKey(dragonEntity.getType()).toString());
        this.containedDragon = dragonEntity;
        this.hatchTime = hatchTime;
        nbt.putInt("hatch_time", hatchTime);
        stack.setTag(nbt);
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()){
            return super.getName(stack);
        }

        Optional<EntityType<?>> type = EntityType.byString(tag.getString("contained_dragon"));

        if (type.isPresent()) {
            String dragonTranslation = type.get().getDescription().getString();
            return new TranslatableComponent(dragonTranslation + " ").append(new TranslatableComponent(getDescriptionId()));
        }
        return super.getName(stack);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!state.getCollisionShape(level, pos).isEmpty()) {
            pos = pos.relative(context.getClickedFace());
        }

        //Creates a WRDragonEggEntity with a previously defined containedDragon and hatchTime
        if (containedDragon != null) {
            WRDragonEggEntity dragonEggEntity = new WRDragonEggEntity(level, containedDragon,hatchTime);
            dragonEggEntity.absMoveTo(pos.getX(), pos.getY() + 0.5d, pos.getZ());

            if (!level.isClientSide) {
                level.addFreshEntity(dragonEggEntity);
            }

            if (!player.isCreative()) {
                player.getItemInHand(context.getHand()).shrink(1);
            }
            return InteractionResult.SUCCESS;

        }
        return InteractionResult.FAIL;
    }

    //Used to create a DragonEgg, in creative mode, from left-clicking a dragon
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
        nbt.putString("contained_dragon", EntityType.getKey(entity.getType()).toString());
        nbt.putInt("hatch_time", DragonEggProperties.get(entity.getType()).getHatchTime());
        stack.setTag(nbt);

        player.displayClientMessage(getName(stack), true);
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        CompoundTag tag = stack.getTag();

        if (tag != null && tag.contains("contained_dragon"))
            tooltip.add(new TranslatableComponent("item.wyrmroost.egg.tooltip", tag.getInt("Hatch Time") / 1200).withStyle(ChatFormatting.AQUA));
        Player player = ClientEvents.getPlayer();
        if (player != null && player.isCreative())
            tooltip.add(new TranslatableComponent("item.wyrmroost.egg.creativetooltip").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (this.allowdedIn(tab)) {
            // Add default item
            super.fillItemCategory(tab, items);
            // Add items with custom NBT data
            for (String nbtValue : getCustomNBTValues()) {
                ItemStack stack = new ItemStack(this);
                CompoundTag nbt = new CompoundTag();
                nbt.putString("contained_dragon", nbtValue);
                nbt.putInt("hatch_time", 500);
                stack.setTag(nbt);
                items.add(stack);
            }
        }
    }

    private List<String> getCustomNBTValues() {
        return Arrays.asList("wyrmroost:butterfly_leviathan", "wyrmroost:royal_red", "wyrmroost:canari_wyvern","wyrmroost:overworld_drake","wyrmroost:roost_stalker","wyrmroost:silver_glider","wyrmroost:alpine");
    }
}