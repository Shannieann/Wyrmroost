package com.github.shannieann.wyrmroost.containers;

import com.github.shannieann.wyrmroost.client.screen.DragonControlScreen;
import com.github.shannieann.wyrmroost.containers.util.DynamicSlot;
import com.github.shannieann.wyrmroost.containers.util.Slot3D;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.entity.dragon.WRRideableDragonEntity;
import com.github.shannieann.wyrmroost.events.ClientEvents;
import com.github.shannieann.wyrmroost.item.book.action.BookAction;
import com.github.shannieann.wyrmroost.registry.WRIO;
import com.github.shannieann.wyrmroost.util.WRModUtils;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import com.github.shannieann.wyrmroost.client.screen.widgets.CollapsibleWidget;

/**
 * Container for the "dragon book" UI — the same UI as the tarragon tome (dragon info, actions, inventory).
 *   Tarragon tome = the item that opens this UI (right-click on a tamed dragon). Not yet in the game??
 *   Dragon book = this UI (BookContainer + DragonControlScreen).
 * Opened when: right-click with tarragon tome on a dragon, or (2) inventory key while riding a tamed dragon (e.g. OWD).
 * Layout differs for chested vs non-chested (chest slots appear when the dragon has a chest).
 */
public class BookContainer extends AbstractContainerMenu
{
    public final WRDragonEntity dragon;
    public final Inventory playerInv;
    private final IItemHandler accessoryHandler;
    public final List<BookAction> actions = new ArrayList<>();
    public final List<Component> toolTips = new ArrayList<>();
    public final List<CollapsibleWidget> collapsibles = new ArrayList<>();

    public BookContainer(int id, Inventory playerInv, WRDragonEntity dragon)
    {
        super(WRIO.DRAGON_BOOK.get(), id);
        this.dragon = dragon;
        this.playerInv = playerInv;
        this.accessoryHandler = dragon instanceof WRRideableDragonEntity rideable
                ? new InvWrapper(rideable.getRideableDragonInventory())
                : dragon.getInventory();
        System.out.println("[Wyrmroost BookContainer] init id=" + id + " dragon=" + dragon + " rideable=" + (dragon instanceof WRRideableDragonEntity));

        CollapsibleWidget playerView = collapsibleWidget(0, 0, 193, 97, CollapsibleWidget.BOTTOM);
        WRModUtils.createPlayerContainerSlots(playerInv, 17, 12, DynamicSlot::new, playerView::addSlot);
        addCollapsible(playerView);

        dragon.applyStaffInfo(this);
    }

    public IItemHandler getAccessoryHandler() {
        return accessoryHandler;
    }

    public boolean canTakeFromChestSlot() {
        if (dragon instanceof WRRideableDragonEntity rideable) {
            net.minecraft.world.Container inv = rideable.getRideableDragonInventory();
            for (int i = WRRideableDragonEntity.CHEST_SLOT + 1; i < inv.getContainerSize(); i++) {
                if (!inv.getItem(i).isEmpty()) return false;
            }
            return true;
        }
        return dragon.getInventory().isEmptyAfter(WRRideableDragonEntity.CHEST_SLOT);
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

    public static Slot3D accessorySlot(IItemHandler i, int index, int x, int y, int z, @Nonnull Vec2 iconUV)
    {
        return (Slot3D) new Slot3D(i, index, x, y, z)
                .condition(() -> ClientEvents.getClient().screen instanceof DragonControlScreen && ((DragonControlScreen) ClientEvents.getClient().screen).showAccessories())
                .iconUV(iconUV);
    }

    public static CollapsibleWidget collapsibleWidget(int u0, int v0, int width, int height, byte direction)
    {
        return new CollapsibleWidget(u0, v0, width, height, direction, DragonControlScreen.SPRITES);
    }

    public static BookContainer factory(int id, Inventory playerInv, FriendlyByteBuf buf)
    {
        System.out.println("[Wyrmroost BookContainer] factory id=" + id + " (client building container from packet)");
        return new BookContainer(id, playerInv, fromBytes(buf));
    }

    public static void open(ServerPlayer player, WRDragonEntity dragon, ItemStack stack)
    {
        System.out.println("[Wyrmroost BookContainer] open player=" + player.getName().getString() + " dragon=" + dragon);
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
        int entityId = buf.readVarInt();
        WRDragonEntity dragon = (WRDragonEntity) ClientEvents.getLevel().getEntity(entityId);
        if (dragon == null) {
            System.out.println("[Wyrmroost BookContainer] fromBytes WARNING entity id=" + entityId + " not found in level (client may desync)");
            throw new IllegalStateException("Dragon entity " + entityId + " not found on client");
        }
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
