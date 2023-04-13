package com.github.shannieann.wyrmroost.containers;

import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.registry.WRIO;
import com.github.shannieann.wyrmroost.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

import java.util.Collection;

// ngl I had no idea how to do containers or screens so I had to watch a tutorial
// But now I understand them so its fine

public class NewTarragonTomeContainer extends AbstractContainerMenu {
    private final ContainerLevelAccess containerAccess;
    public final WRDragonEntity dragon;
    //public final ContainerData data;

    // Client Constructor
    public NewTarragonTomeContainer(int id, Inventory playerInv, WRDragonEntity dragon) {
        this(id, playerInv, new ItemStackHandler(27), BlockPos.ZERO, dragon);
    }

    // Server Constructor
    public NewTarragonTomeContainer(int id, Inventory playerInv, IItemHandler slots, BlockPos pos, WRDragonEntity dragon) {
        super(WRIO.TARRAGON_TOME.get(), id);
        this.containerAccess = ContainerLevelAccess.create(playerInv.player.level, pos);
        //this.data = data;
        this.dragon = dragon;
        final int slotSizePlus2 = 18, startX = 8, startY = 83, hotbarY = 141;



        // TODO Add chest inventory


        for (int row = 0; row < 3; row++){
            for(int column = 0; column < 9; column++){
                addSlot(new Slot(playerInv, 9 + row * 9 + column, startX + column * slotSizePlus2,
                        startY + row * slotSizePlus2));
            }

        }
        for(int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv, column, startX + column * slotSizePlus2, hotbarY));
        }



    }


    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    //public static MenuConstructor getServerContainer(WRDragonEntity dragon, BlockPos pos) {
     //   return (id, playerInv, player) -> new NewTarragonTomeContainer(id, playerInv, dragon.getInventory(), pos, dragon);
    //}

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        var retStack = ItemStack.EMPTY;
        final Slot slot = this.getSlot(pIndex);
        if(slot.hasItem()){
            final ItemStack item = slot.getItem();
            retStack = item.copy();
            if(pIndex < 27) {
                if (!moveItemStackTo(item, 27, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            }else if (!moveItemStackTo(item, 0, 27, false))
                return ItemStack.EMPTY;

            if (item.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }

        return retStack;
    }

    public static NewTarragonTomeContainer factory(int id, Inventory playerInv, FriendlyByteBuf buf)
    {
        return new NewTarragonTomeContainer(id, playerInv, fromBytes(buf));
    }
    public static void open(ServerPlayer player, WRDragonEntity dragon)
    {
        NetworkHooks.openGui(player, dragon, b -> toBytes(dragon, b));
    }

    private static void toBytes(WRDragonEntity entity, FriendlyByteBuf buffer)
    {

        buffer.writeVarInt(entity.getId());

        Collection<MobEffectInstance> effects = entity.getActiveEffects();
        buffer.writeVarInt(effects.size());

        for (MobEffectInstance instance : effects)
        {
            buffer.writeByte(MobEffect.getId(instance.getEffect()) & 255);
            buffer.writeVarInt(Math.min(instance.getDuration(), 32767));
            buffer.writeByte(instance.getAmplifier() & 255);

            byte flags = 0;
            if (instance.isAmbient()) flags |= 1;
            if (instance.isVisible()) flags |= 2;
            if (instance.showIcon()) flags |= 4;

            buffer.writeByte(flags);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static WRDragonEntity fromBytes(FriendlyByteBuf buf)
    {
        WRDragonEntity dragon = (WRDragonEntity) ClientEvents.getLevel().getEntity(buf.readVarInt());
        dragon.getActiveEffectsMap().clear();

        int series = buf.readVarInt();
        for (int i = 0; i < series; i++)
        {
            byte flags;

            MobEffectInstance instance = new MobEffectInstance(MobEffect.byId(buf.readByte() & 0xFF),
                    buf.readVarInt(),
                    buf.readByte(),
                    ((flags = buf.readByte()) & 1) == 1,
                    (flags & 2) == 2,
                    (flags & 4) == 4);
            instance.setNoCounter(instance.getDuration() == 32767);
            dragon.forceAddEffect(instance, dragon);
        }

        return dragon;
    }


}
