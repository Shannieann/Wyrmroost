package com.github.shannieann.wyrmroost.network.packets;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.ClientEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class AddPassengerPacket {
    public final int passengerID;
    public final int vehicleID;

    AddPassengerPacket(Entity passenger, Entity vehicle) {
        this.passengerID = passenger.getId();
        this.vehicleID = vehicle.getId();
    }

    public AddPassengerPacket(FriendlyByteBuf buf) {
        passengerID = buf.readInt();
        vehicleID = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(passengerID);
        buf.writeInt(vehicleID);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        Entity passenger = ClientEvents.getLevel().getEntity(passengerID);
        Entity vehicle = ClientEvents.getLevel().getEntity(vehicleID);
        if (passenger == null || vehicle == null || !passenger.startRiding(vehicle, true))
        {
            Wyrmroost.LOG.warn("Could not add passenger on client");
            return false;
        }
        return true;
    }

    public static void send(Entity passenger, Entity vehicle) {
        Wyrmroost.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> vehicle), new AddPassengerPacket(passenger, vehicle));
    }
}
