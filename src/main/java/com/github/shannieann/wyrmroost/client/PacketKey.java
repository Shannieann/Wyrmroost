package com.github.shannieann.wyrmroost.client;

import com.github.shannieann.wyrmroost.entities.dragon.helpers.ai.goals.TestClass;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketKey {
    public PacketKey() {}

    public PacketKey(FriendlyByteBuf buf) {
    }

    public static void handle(PacketKey packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection().getReceptionSide().isServer()) {
            context.get().enqueueWork(() -> {
                ServerPlayer player = context.get().getSender();
                if (player != null) {
                    TestClass test = new TestClass(player);
                }
            });
            context.get().setPacketHandled(true);
        }
    }

    public static PacketKey decode(FriendlyByteBuf buffer) {
        return new PacketKey();
    }

    public static void encode(PacketKey packet, FriendlyByteBuf buffer) {}
}

