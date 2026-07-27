package com.github.shannieann.wyrmroost.network;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.containers.BookContainer;
import com.github.shannieann.wyrmroost.entity.dragon.EntityOverworldDrake;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Sent when the player presses the inventory key while riding a tamed OWD. Opens the dragon book UI (same as tarragon tome) instead of player inventory. */
public class OpenRideableDragonInventoryPacket {

    public OpenRideableDragonInventoryPacket() {}

    public OpenRideableDragonInventoryPacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            Entity vehicle = player.getVehicle();
            if (vehicle instanceof EntityOverworldDrake drake && drake.isTame() && (drake.isVehicle() && drake.hasPassenger(player))) {
                System.out.println("[Wyrmroost OpenRideableDragonInventory] opening dragon book for player=" + player.getName().getString());
                BookContainer.open(player, drake, ItemStack.EMPTY);
            }
        });
        ctx.setPacketHandled(true);
        return true;
    }

    public static void send() {
        Wyrmroost.NETWORK.sendToServer(new OpenRideableDragonInventoryPacket());
    }
}
