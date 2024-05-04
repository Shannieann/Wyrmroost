package com.github.shannieann.wyrmroost.entity.projectile;

import com.github.shannieann.wyrmroost.item.GeodeTippedArrowItem;
import com.github.shannieann.wyrmroost.registry.WREntityTypes;
import com.github.shannieann.wyrmroost.registry.WRItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class GeodeTippedArrowEntity extends AbstractArrow implements IEntityAdditionalSpawnData
{
    private final GeodeTippedArrowItem item;

    public GeodeTippedArrowEntity(EntityType<? extends AbstractArrow> type, Level level)
    {
        super(type, level);
        this.item = (GeodeTippedArrowItem) WRItems.BLUE_GEODE_ARROW.get();
    }

    public GeodeTippedArrowEntity(Level level, Item item)
    {
        super(WREntityTypes.GEODE_TIPPED_ARROW.get(), level);
        this.item = (GeodeTippedArrowItem) item;
    }

    public GeodeTippedArrowEntity(PlayMessages.SpawnEntity packet, Level level)
    {
        super(WREntityTypes.GEODE_TIPPED_ARROW.get(), level);

        FriendlyByteBuf buf = packet.getAdditionalData();
        Entity shooter = level.getEntity(buf.readInt());
        if (shooter != null) setOwner(shooter);
        this.item = (GeodeTippedArrowItem) Item.byId(buf.readVarInt());
    }

    public GeodeTippedArrowItem getItem()
    {
        return item;
    }

    @Override
    protected ItemStack getPickupItem()
    {
        return new ItemStack(item);
    }

    @Override
    public Packet<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    @Override
    public void writeSpawnData(FriendlyByteBuf buf)
    {
        Entity shooter = getOwner();
        buf.writeInt(shooter == null? 0 : shooter.getId());
        buf.writeVarInt(Item.getId(item));
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData)
    {
    }
}
