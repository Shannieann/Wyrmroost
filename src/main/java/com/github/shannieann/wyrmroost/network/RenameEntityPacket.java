package com.github.shannieann.wyrmroost.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class RenameEntityPacket
{
    private final UUID entity;
    private final Component text;

    public RenameEntityPacket(Entity entity, Component text)
    {
        this.entity = entity.getUUID();
        this.text = text;
    }

    public RenameEntityPacket(FriendlyByteBuf buf)
    {
        this.entity = buf.readUUID();
        this.text = buf.readComponent();
    }
    
    public void encode(FriendlyByteBuf buf)
    {
        buf.writeUUID(entity);
        buf.writeComponent(text);
    }

    public boolean handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().getSender().getLevel().getEntity(entity).setCustomName(text);
        return true;
    }
}
