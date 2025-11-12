package com.github.shannieann.wyrmroost.network;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.EntitySilverGlider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SGGlidePacket
{
    private final boolean gliding;

    public SGGlidePacket(FriendlyByteBuf buffer)
    {
        this.gliding = buffer.readBoolean();
    }

    public SGGlidePacket(boolean gliding)
    {
        this.gliding = gliding;
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeBoolean(gliding);
    }
    public boolean handle(Supplier<NetworkEvent.Context> context)
    {
        ServerPlayer reciever = context.get().getSender();
        if (reciever != null && !reciever.getPassengers().isEmpty())
        {
            Entity entity = reciever.getPassengers().get(0);
            if (entity instanceof EntitySilverGlider)
            {
                //((EntitySilverGlider) entity).isGliding = gliding; Looks like this class is unused. Commenting out for now
                context.get().setPacketHandled(true);
                return true;
            }
        }
        context.get().setPacketHandled(true);
        return false;
    }

    public static void send(boolean gliding)
    {
        Wyrmroost.NETWORK.sendToServer(new SGGlidePacket(gliding));
    }
}
