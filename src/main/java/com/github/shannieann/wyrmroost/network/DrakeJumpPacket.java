package com.github.shannieann.wyrmroost.network;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.entity.dragon.EntityOverworldDrake;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Sent when the player releases the jump key. */
/** This file is Temu vanilla horse code */
public class DrakeJumpPacket {

    private final int jumpPower;

    public DrakeJumpPacket(int jumpPower) {
        this.jumpPower = jumpPower;
    }

    public DrakeJumpPacket(FriendlyByteBuf buf) {
        this.jumpPower = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(jumpPower);
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            Entity vehicle = player.getVehicle();
            if (vehicle instanceof EntityOverworldDrake drake && drake.canJump()) {
                drake.onPlayerJump(jumpPower);
            }
        });
        ctx.setPacketHandled(true);
        return true;
    }

    public static void send(int jumpPower) {
        Wyrmroost.NETWORK.sendToServer(new DrakeJumpPacket(jumpPower));
    }
}
