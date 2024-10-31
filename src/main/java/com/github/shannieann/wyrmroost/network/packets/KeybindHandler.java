package com.github.shannieann.wyrmroost.network.packets;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.ClientEvents;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KeybindHandler
{
    public static final byte MOUNT_KEY = 1;
    public static final byte ALT_MOUNT_KEY = 2;
    public static final byte SWITCH_FLIGHT = 4;

    private final byte key;
    private final int mods;
    private final boolean pressed;

    public KeybindHandler(byte key, int mods, boolean pressed)
    {
        this.key = key;
        this.mods = mods;
        this.pressed = pressed;
    }

    public KeybindHandler(FriendlyByteBuf buf)
    {
        this.key = buf.readByte();
        this.mods = buf.readInt();
        this.pressed = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeByte(key);
        buf.writeInt(mods);
        buf.writeBoolean(pressed);
    }

    public boolean handle(Supplier<NetworkEvent.Context> context)
    {
        return process(context.get().getSender());
    }

    public boolean process(Player player)
    {

        switch (key)
        {
            case MOUNT_KEY:
            case ALT_MOUNT_KEY:
                Entity vehicle = player.getVehicle();
                if (vehicle instanceof WRDragonEntity)
                {
                    WRDragonEntity dragon = ((WRDragonEntity) vehicle);
                    if (dragon.isTame() && dragon.getControllingPlayer() == player)
                        dragon.recievePassengerKeybind(key, mods, pressed);
                }
                break;
            case SWITCH_FLIGHT:
                if (!pressed)
                {
                    boolean b = ClientEvents.keybindFlight = !ClientEvents.keybindFlight;
                    String translate = "entity.wyrmroost.dragons.flight." + (b? "controlled" : "free");
                    ClientEvents.getPlayer().displayClientMessage(new TranslatableComponent(translate), true);
                }
                break;
            default:
                Wyrmroost.LOG.warn(String.format("Recieved invalid keybind code: %s How tf did u break this", key));
                return false;
        }
        return true;
    }
}
