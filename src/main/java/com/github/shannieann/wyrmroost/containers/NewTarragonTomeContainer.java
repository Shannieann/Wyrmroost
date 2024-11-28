package com.github.shannieann.wyrmroost.containers;

import com.github.shannieann.wyrmroost.events.ClientEvents;
import com.github.shannieann.wyrmroost.containers.util.DynamicSlot;
import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;
import com.github.shannieann.wyrmroost.item.DragonArmorItem;
import com.github.shannieann.wyrmroost.registry.WRIO;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Predicate;

// ngl I had no idea how to do containers or screens so I had to watch a tutorial
// But now I understand them so its fine

public class NewTarragonTomeContainer extends AbstractContainerMenu {
    private final ContainerLevelAccess containerAccess;
    public final WRDragonEntity dragon;

    private final Inventory playerInv;
    // These need to be public so the screen can access them to render the slot backgrounds
    public int accessorySlotNumber = 0; // Current amount of accessory slots so there's no wasted space on the bookmark.

    private boolean isChestInventory;
    public Slot saddleSlot;
    public Slot armorSlot;
    public Slot chestSlot;
    public Slot extraSlot;

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
        this.playerInv = playerInv;
        final int slotSizePlus2 = 18, startX = 8, startY = 83, hotbarY = 141;


        // Create saddle/armor/chest slots so quick move goes to them first


        for (int row = 0; row < 3; row++){
            for(int column = 0; column < 9; column++){
                addSlot(new DynamicSlot(playerInv, 9 + row * 9 + column, startX + column * slotSizePlus2,
                        startY + row * slotSizePlus2));
            }
        }
        for(int column = 0; column < 9; column++) {
            addSlot(new DynamicSlot(playerInv, column, startX + column * slotSizePlus2, hotbarY));
        }
        dragon.applyTomeInfo(this);

        if (hasChestSlot()) setChestInventory(hasChestEquipped());



    }

    // =============================
    // Slot Stuff
    // =============================
    /*
    TODO I did this in a weird way where there can be only one of each slot...
    So it isn't even really a builder structure.
    Possibly change in the future if need be, but I don't think we will need a dragon
    to have more than 1 saddle or chest, so it should be fine.
     */

    // The addSlot part can't be put into another method b/c mayPlace needs access to where the new Slot is created.
    // These are used in the WRDragonEntity method applyTomeInfo() to add any slots the dragon may need.
    public NewTarragonTomeContainer addSaddleSlot(){
        this.saddleSlot = addSlot(new SlotItemHandler(dragon.getInventory(), accessorySlotNumber, 8, 6 + (17 * accessorySlotNumber)) {
            public boolean mayPlace(ItemStack item) {
                return item.getItem() instanceof SaddleItem;
            }
            public int getMaxStackSize() {return 1;}
            public int getMaxStackSize(ItemStack stack) {return 1;}
        });
        accessorySlotNumber++;
        return this;
    }
    public NewTarragonTomeContainer addArmorSlot(){
        this.armorSlot = addSlot(new SlotItemHandler(dragon.getInventory(), accessorySlotNumber, 8, 6 + (17 * accessorySlotNumber)) {
            public boolean mayPlace(ItemStack item) {
                return item.getItem() instanceof DragonArmorItem;
            }
            public int getMaxStackSize() {return 1;}
            public int getMaxStackSize(ItemStack stack) {return 1;}
        });
        accessorySlotNumber++;
        return this;
    }
    public NewTarragonTomeContainer addChestSlot(){
        this.chestSlot = addSlot(new SlotItemHandler(dragon.getInventory(), accessorySlotNumber, 8, 6 + (17 * accessorySlotNumber)){
            public boolean mayPlace(@NotNull ItemStack stack) {return stack.is(Tags.Items.CHESTS_WOODEN);}

            public int getMaxStackSize() {return 1;}
            public int getMaxStackSize(ItemStack stack) {return 1;}
            // Detect when a chest is equipped
            @Override
            public void setChanged() {
                super.setChanged();
                setChestInventory(hasChestEquipped());
            }
        });

        // Create the chest slots, which are only active if the dragon has a chest equipped.
        final int startX = 26, startY = 74, slotSizePlus2 = 18;
        for (int row = 0; row < 3; row++){
            for(int column = 0; column < 7; column++){
                addSlot(new SlotItemHandler(dragon.getInventory(), accessorySlotNumber + 1 + row * 7 + column, startX + column * slotSizePlus2,
                        startY + row * slotSizePlus2) {
                    public boolean isActive() {return hasChestEquipped();}
                });
            }
        }
        accessorySlotNumber++;
        return this;
    }
    private void setChestInventory(boolean settingToChest){
        if (isChestInventory == settingToChest) return; // If the slots are already in the correct place, don't move them again.
        isChestInventory = settingToChest;
        if (settingToChest){
            // Move the slots to where they should be.
            movePlayerInvSlots(true);

        } else {
            // Move the slots back to non-chested inventory
            movePlayerInvSlots(false);
            dragon.dropStorage(); // Drop the chest inventory
        }
    }

    private void movePlayerInvSlots(boolean down){
        // First we loop through all the slots
        for (Slot slot : slots){
            // If it's a player inventory slot, we move it to where it should be.
            if ((slot instanceof DynamicSlot dynamicSlot)){
                dynamicSlot.move(0, down? 57 : 0); // TODO the way wolf coded this is kinda strange... move() always moves based on initial location, not current location. Maybe fix it?
            }
        }
    }

    // Adds an extra slot. What the slot contains is determined in the dragon's getTomeInfo method, where it supplies a predicate.
    public NewTarragonTomeContainer addExtraSlot(Predicate<ItemStack> condition){
        this.extraSlot = addSlot(new SlotItemHandler(dragon.getInventory(), accessorySlotNumber, 8, 6 + (17 * accessorySlotNumber)) {
            public boolean mayPlace(ItemStack item) {
                return condition.test(item);
            }
            public int getMaxStackSize() {return 1;}
            public int getMaxStackSize(ItemStack stack) {return 1;}

        });
        accessorySlotNumber++;
        return this;
    }



    // Basically here just to make the screen code look nicer.
    public boolean hasSaddleSlot(){
        return saddleSlot != null;
    }
    public boolean hasArmorSlot(){
        return armorSlot != null;
    }
    public boolean hasChestSlot(){
        return chestSlot != null;
    }
    public boolean hasExtraSlot(){
        return extraSlot != null;
    }

    public boolean hasChestEquipped() { return chestSlot.hasItem();}

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




    //===================================
    // Opening/Initializing the gui (in WRIO.java)
    //===================================

    public static NewTarragonTomeContainer factory(int id, Inventory playerInv, FriendlyByteBuf buf)
    {
        return new NewTarragonTomeContainer(id, playerInv, fromBytes(buf));
    }
    public static void open(ServerPlayer player, WRDragonEntity dragon)
    {
        NetworkHooks.openGui(player, dragon, b -> toBytes(dragon, b));
    }


    // (Originally from Wolf's code)
    // These two methods store the dragon
    // So a reference can be obtained in this class even when the factory is passed into WRIO
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
