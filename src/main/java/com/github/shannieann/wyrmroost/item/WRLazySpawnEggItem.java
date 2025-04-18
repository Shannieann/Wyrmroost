package com.github.shannieann.wyrmroost.item;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.registry.WRItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class WRLazySpawnEggItem<T extends Entity> extends ForgeSpawnEggItem
{
    public static final Set<WRLazySpawnEggItem<?>> SPAWN_EGGS = new HashSet<>();

    public final Lazy<EntityType<T>> type;

    public WRLazySpawnEggItem(Supplier<EntityType<T>> type, int primaryColor, int secondaryColor)
    {
        super(null, primaryColor, secondaryColor, WRItems.builder());

        this.type = Lazy.of(type);
        SPAWN_EGGS.add(this);
    }

    @Override
    public Component getName(ItemStack stack)
    {
        ResourceLocation regName = type.get().getRegistryName();
        return new TranslatableComponent("entity." + regName.getNamespace() + "." + regName.getPath())
                .append(" ")
                .append(new TranslatableComponent("item.wyrmroost.spawn_egg"));
    }

    public EntityType<?> getType(@Nullable CompoundTag tag)
    {
        if (tag != null && tag.contains("EntityTag", 10))
        {
            CompoundTag childTag = tag.getCompound("EntityTag");
            if (childTag.contains("id", 8))
                return EntityType.byString(childTag.getString("id")).orElse(type.get());
        }

        return type.get();
    }

    public static void addEggsToMap()
    {
        try
        {
            Map<EntityType<?>, SpawnEggItem> eggMap = ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "f_43201_");
            for (WRLazySpawnEggItem<?> item : SPAWN_EGGS) eggMap.put(item.type.get(), item);
        }
        catch (Exception e)
        {
            Wyrmroost.LOG.fatal("Error on SpawnEgg map", e);
        }
    }
}
