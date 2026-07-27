package com.github.shannieann.wyrmroost.containers;

import com.github.shannieann.wyrmroost.entity.dragon.WRRideableDragonEntity;
import com.github.shannieann.wyrmroost.events.ClientEvents;
import com.github.shannieann.wyrmroost.item.DragonArmorItem;
import com.github.shannieann.wyrmroost.registry.WRIO;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.MenuProvider;
import net.minecraftforge.network.NetworkHooks;

public class RideableDragonInventoryContainer extends AbstractContainerMenu {

    private final Container rideableDragonInv;
    private final WRRideableDragonEntity rideableDragon;
    /** Number of rideableDragon slots in this menu (2 or 17). Used for quickMoveStack. */
    private final int rideableDragonSlotCount;
    /** Dummy 1-slot container for the "carried" item slot (packet sends slots + 1; index 38/53 must exist). */
    private final SimpleContainer carriedSlotDummy = new SimpleContainer(1);

    /**
     * @param addChestSlots if true, add 15 chest slots (must match server when opening so client/server slot count matches).
     */
    public RideableDragonInventoryContainer(int id, Inventory playerInv, WRRideableDragonEntity rideableDragon, boolean addChestSlots) {
        super(WRIO.RIDEABLE_DRAGON_INVENTORY.get(), id);
        this.rideableDragon = rideableDragon;
        this.rideableDragonInv = rideableDragon.getRideableDragonInventory();

        int invCols = rideableDragon.getInventoryColumns();
        int slotSize = 18;
        int rideableDragonSlotsAdded = 0;

        // Saddle slot
        this.addSlot(new Slot(rideableDragonInv, WRRideableDragonEntity.SADDLE_SLOT, 8, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.SADDLE);
            }
            @Override
            public int getMaxStackSize(ItemStack stack) {
                return 1;
            }
        });
        rideableDragonSlotsAdded++;
        // Armor slot
        this.addSlot(new Slot(rideableDragonInv, WRRideableDragonEntity.ARMOR_SLOT, 8, 36) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof DragonArmorItem;
            }
            @Override
            public int getMaxStackSize(ItemStack stack) {
                return 1;
            }
        });
        rideableDragonSlotsAdded++;

        // Chest slots (only when addChestSlots - must match server so slot count is identical)
        if (addChestSlots) {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < invCols; col++) {
                    int slotIndex = WRRideableDragonEntity.INV_BASE_SIZE + row * invCols + col;
                    if (slotIndex < rideableDragonInv.getContainerSize()) {
                        this.addSlot(new Slot(rideableDragonInv, slotIndex, 80 + col * slotSize, 18 + row * slotSize));
                        rideableDragonSlotsAdded++;
                    }
                }
            }
        }

        this.rideableDragonSlotCount = rideableDragonSlotsAdded;

        // Player inventory: match vanilla HorseInventoryMenu positions so items align with horse.png texture
        // Vanilla: main at (8 + col*18, 84 + row*18), hotbar at (8 + col*18, 142)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, 9 + row * 9 + col, 8 + col * slotSize, 84 + row * slotSize));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * slotSize, 142));
        }

        // Vanilla sends container slots + 1 (carried item); client initializeContents does getSlot(i) for i in 0..size-1.
        this.addSlot(new Slot(carriedSlotDummy, 0, -10000, -10000));
    }

    /** Server-side and client-side when slot count is known from entity state. */
    public RideableDragonInventoryContainer(int id, Inventory playerInv, WRRideableDragonEntity rideableDragon) {
        this(id, playerInv, rideableDragon, rideableDragonInvSize(rideableDragon) > WRRideableDragonEntity.INV_BASE_SIZE);
    }

    private static int rideableDragonInvSize(WRRideableDragonEntity rideableDragon) {
        return rideableDragon.getRideableDragonInventory().getContainerSize();
    }

    public WRRideableDragonEntity getRideableDragon() {
        return rideableDragon;
    }

    public int getInventoryColumns() {
        return rideableDragon.getInventoryColumns();
    }

    @Override
    public boolean stillValid(Player player) {
        return rideableDragon.isAlive() && rideableDragon.distanceTo(player) < 8.0F;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < rideableDragonSlotCount) {
                if (!this.moveItemStackTo(stack, rideableDragonSlotCount, this.slots.size() - 1, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < this.slots.size() - 1 && !this.moveItemStackTo(stack, 0, rideableDragonSlotCount, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    /**
     * Client-side: read entityId and chested from buffer so we build the same slot count as the server.
     * Sync client rideableDragon's chested state from packet so getRideableDragonInventory() has correct size.
     */
    public static RideableDragonInventoryContainer fromNetwork(int id, Inventory playerInv, FriendlyByteBuf buf) {
        int entityId = buf.readVarInt();
        boolean serverChested = buf.readBoolean();
        WRRideableDragonEntity rideableDragon = (WRRideableDragonEntity) ClientEvents.getLevel().getEntity(entityId);
        if (rideableDragon == null) {
            throw new IllegalStateException("Rideable dragon entity " + entityId + " not found on client");
        }
        // Client can have CHESTED synced but inventory never resized (setChested not called on sync).
        // Force inventory to match server so we build 54 slots when chested.
        rideableDragon.ensureInventorySizeForMenu(serverChested);
        return new RideableDragonInventoryContainer(id, playerInv, rideableDragon, serverChested);
    }

    /**
     * Open using an explicit MenuProvider so the server always creates RideableDragonInventoryContainer
     * (and the client opens RideableDragonInventoryScreen). Passing the entity can cause the wrong
     * menu type to be used and show the tome screen instead.
     */
    public static void open(Player player, WRRideableDragonEntity rideableDragon) {
        if (rideableDragon.level.isClientSide) return;
        net.minecraft.server.level.ServerPlayer serverPlayer = (net.minecraft.server.level.ServerPlayer) player;
        boolean chested = rideableDragon.isChested();
        MenuProvider provider = new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return rideableDragon.getDisplayName();
            }
            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
                return new RideableDragonInventoryContainer(id, inv, rideableDragon, chested);
            }
        };
        NetworkHooks.openGui(serverPlayer, provider, buf -> {
            buf.writeVarInt(rideableDragon.getId());
            buf.writeBoolean(chested);
        });
    }
}
