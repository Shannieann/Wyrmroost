package com.github.shannieann.wyrmroost.network;

import com.github.shannieann.wyrmroost.item.book.TarragonTomeItem;
import com.github.shannieann.wyrmroost.item.book.action.BookAction;
import com.github.shannieann.wyrmroost.item.book.action.BookActions;
import com.github.shannieann.wyrmroost.registry.WRItems;
import com.github.shannieann.wyrmroost.util.WRModUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BookActionPacket
{
    public final BookAction action;

    public BookActionPacket(BookAction action)
    {
        this.action = action;
    }

    public BookActionPacket(FriendlyByteBuf buf)
    {
        action = BookActions.ACTIONS.get(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(BookActions.ACTIONS.indexOf(action));
    }

    public boolean handle(Supplier<NetworkEvent.Context> context)
    {
        ServerPlayer player = context.get().getSender();
        ItemStack stack = WRModUtils.getHeldStack(player, WRItems.TARRAGON_TOME.get());
        if (stack != null)
        {
            TarragonTomeItem.setAction(action, player, stack);
            return true;
        }
        return false;
    }
}
