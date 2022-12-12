package com.github.shannieann.wyrmroost.containers;

import com.github.shannieann.wyrmroost.client.ClientEvents;
import com.github.shannieann.wyrmroost.client.screen.DragonControlScreen;
import com.github.shannieann.wyrmroost.client.screen.widgets.CollapsibleWidget;
import com.github.shannieann.wyrmroost.containers.util.DynamicSlot;
import com.github.shannieann.wyrmroost.containers.util.Slot3D;
import com.github.shannieann.wyrmroost.entities.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.entities.dragon.ai.DragonInventory;
import com.github.shannieann.wyrmroost.items.book.action.BookAction;
import com.github.shannieann.wyrmroost.registry.WRIO;
import com.github.shannieann.wyrmroost.util.ModUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.github.shannieann.wyrmroost.client.ClientEvents.getClient;

public class BookContainer extends AbstractContainerMenu
{
    public final WRDragonEntity dragon;
    public final Inventory playerInv;
    public final List<BookAction> actions = new ArrayList<>();
    public final List<Component> toolTips = new ArrayList<>();
    public final List<CollapsibleWidget> collapsibles = new ArrayList<>();

    public BookContainer(int id, Inventory playerInv, WRDragonEntity dragon)
    {
        super(WRIO.TARRAGON_TOME.get(), id);
        this.dragon = dragon;
        this.playerInv = playerInv;

        CollapsibleWidget playerView = collapsibleWidget(0, 0, 193, 97, CollapsibleWidget.BOTTOM);
        ModUtils.createPlayerContainerSlots(playerInv, 17, 12, DynamicSlot::new, playerView::addSlot);
        addCollapsible(playerView);

        dragon.applyStaffInfo(this);
    }


    @Override
    public boolean stillValid(Player player)
    {
        return dragon.isAlive();
    }
    //for public access
    @Override
    public Slot addSlot(Slot slot)
    {
        return super.addSlot(slot);
    }

    public BookContainer slot(Slot slot)
    {
        addSlot(slot);
        return this;
    }

    public BookContainer addAction(BookAction... actions)
    {
        if (dragon.level.isClientSide) Collections.addAll(this.actions, actions);
        return this;
    }

    public BookContainer addTooltip(Component text)
    {
        if (dragon.level.isClientSide) toolTips.add(text);
        return this;
    }

    public BookContainer addCollapsible(CollapsibleWidget widget)
    {
        widget.slots.forEach(this::addSlot);
        collapsibles.add(widget);
        return this;
    }

    public static Slot3D accessorySlot(DragonInventory i, int index, int x, int y, int z, @Nonnull Vec2 iconUV)
    {
        return (Slot3D) new Slot3D(i, index, x, y, z)
                .condition(() -> getClient().screen instanceof DragonControlScreen && ((DragonControlScreen) getClient().screen).showAccessories())
                .iconUV(iconUV);
    }

    public static CollapsibleWidget collapsibleWidget(int u0, int v0, int width, int height, byte direction)
    {
        return new CollapsibleWidget(u0, v0, width, height, direction, DragonControlScreen.SPRITES);
    }

    public static BookContainer factory(int id, Inventory playerInv, FriendlyByteBuf buf)
    {
        return new BookContainer(id, playerInv, fromBytes(buf));
    }

    public static void open(ServerPlayer player, WRDragonEntity dragon, ItemStack stack)
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
