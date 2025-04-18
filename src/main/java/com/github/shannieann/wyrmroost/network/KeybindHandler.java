package com.github.shannieann.wyrmroost.network;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.events.ClientEvents;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KeybindHandler {
    public static final byte MOUNT_KEY = 1;
    public static final byte ALT_MOUNT_KEY = 2;
    public static final byte SWITCH_FLIGHT = 3;

    private final byte key;
    private final int mods;
    private final boolean pressed;

    public KeybindHandler(byte key, int mods, boolean pressed) {
        this.key = key;
        this.mods = mods;
        this.pressed = pressed;
    }

    public KeybindHandler(FriendlyByteBuf buf) {
        this.key = buf.readByte();
        this.mods = buf.readInt();
        this.pressed = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeByte(key);
        buf.writeInt(mods);
        buf.writeBoolean(pressed);
    }

    public boolean handle(Supplier<NetworkEvent.Context> context) {
        return process(context.get().getSender());
    }

    public boolean process(Player player) {
        switch (key) {
            case MOUNT_KEY:
            case ALT_MOUNT_KEY:
                handleMountKey(player);
                break;

            case SWITCH_FLIGHT:
                handleSwitchFlight();
                break;

            default:
                logInvalidKeybind();
                return false;
        }
        return true;
    }

    private void handleMountKey(Player player) {
        Entity vehicle = player.getVehicle();
        if (vehicle instanceof WRDragonEntity dragon) {
            if (dragon.isTame() && dragon.getControllingPlayer() == player) {
                dragon.receivePassengerKeybind(key, mods, pressed);
            }
        }
    }

    private void handleSwitchFlight() {
        if (pressed) return; // Only toggle flight state when the key is released

        ClientEvents.keybindFlight = !ClientEvents.keybindFlight;
        String flightState = ClientEvents.keybindFlight ? "controlled" : "free";
        String translationKey = "entity.wyrmroost.dragons.flight." + flightState;

        ClientEvents.getPlayer().displayClientMessage(new TranslatableComponent(translationKey), true);
    }

    private void logInvalidKeybind() {
        Wyrmroost.LOG.warn(String.format("Received invalid keybind code: %s", key));
    }
}