package com.github.shannieann.wyrmroost.util;

import com.github.shannieann.wyrmroost.Wyrmroost;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by WolfShotz 7/9/19 - 00:31
 */
public final class WRModUtils
{
    private WRModUtils() {} // NU CONSTRUCTOR

    public static final String TEXTURE_FOLDER = "textures/entity/dragon/";

    public static final Direction[] DIRECTIONS = Direction.values(); // cached directions cus vanilla didn't do it
    public static final Direction[] HORIZONTALS = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    public static final boolean DECK_THE_HALLS;

    static
    {
        Calendar calendar = Calendar.getInstance();
        DECK_THE_HALLS = calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH) >= 19;
    }

    /**
     * Just checks if the game is running on the client and not the server.
     * Generally, this should be used in favor of {@link net.minecraftforge.fml.DistExecutor} spam
     * as there's no lambdas to abuse and can have both performance and readability improvements.
     * Could <i>potentially</i> be more dangerous but I haven't ran into any issues yet...
     * @return whether the environment is on the client or not
     */
    public static boolean isClient()
    {
        return FMLEnvironment.dist == Dist.CLIENT;
    }

    /**
     * Majorly self-explanatory
     */
    @SafeVarargs
    public static <T> boolean contains(T comparator, T... contents)
    {
        for (T t : contents) if (comparator.equals(t)) return true;
        return false;
    }

    /**
     * @param registry the DeferredRegistry instance holding the objects
     * @param <T>      the type of registry
     * @return An Immutable Set that contains all entries of the provided DeferredRegister
     */
    public static <T extends IForgeRegistryEntry<T>> Set<T> getRegistryEntries(DeferredRegister<T> registry)
    {
        return registry.getEntries().stream().map(RegistryObject::get).collect(Collectors.toSet());
    }

    /**
     * Checks both hands of the passed player for an item.
     * If one hand has one, return that ItemStack, else return the main hands stack.
     *
     * @param player the player
     * @param item   the item were trying to get
     * @return An ItemStack if it conatains the specified item, null otherwise
     */
    @Nullable
    public static ItemStack getHeldStack(Player player, Item item)
    {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return item == main.getItem()? main : item == off.getItem()? off : null;
    }

    /**
     * Play a sound on the local client.
     *
     * @param level  :thinking:
     * @param pos    the pos to play it at (I haven't seen this change anything tho...)
     * @param sound  again, :thinking:
     * @param volume so help me god
     * @param pitch  the pitch of the sound. lower values = sulfur hexafloride, higher values = dying chipmunk
     */
    public static void playLocalSound(Level level, BlockPos pos, SoundEvent sound, float volume, float pitch)
    {
        playLocalSound(level, pos, sound, SoundSource.NEUTRAL, volume, pitch);
    }

    public static void playLocalSound(Level level, BlockPos pos, SoundEvent sound, SoundSource category, float volume, float pitch)
    {
        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), sound, category, volume, pitch, false);
    }

    /**
     * Get all (approximate) {@link BlockPos}'s in an {@link AABB}
     * <p>
     * Iterable Version - for statements ftw
     *
     * @param aabb please tell me your not asking what this is for
     */
    public static Iterable<BlockPos> eachPositionIn(AABB aabb)
    {
        return BlockPos.betweenClosed(
                Mth.floor(aabb.minX),
                Mth.floor(aabb.minY),
                Mth.floor(aabb.minZ),
                Mth.ceil(aabb.maxX),
                Mth.ceil(aabb.maxY),
                Mth.ceil(aabb.maxZ));
    }

    /**
     * Convenience method for creating container slots adjacent to eachother in perfect seperations
     * @param inventory the inventory. duh.
     * @param index The initial slot index. This number is increased by 1 per slot
     * @param initialX The x position to start the slots
     * @param initialY The y position to start the slots
     * @param columns The amount of slots, in columns
     * @param rows The amount of slots, in rows
     * @param slotFactory the thing do to the thing.
     */
    public static <T extends Slot> void createContainerSlots(IItemHandler inventory, int index, int initialX, int initialY, int columns, int rows, SlotFunction<T> slotFactory, Consumer<T> consumer)
    {
        for (int y = 0; y < rows; ++y)
        {
            for (int x = 0; x < columns; ++x)
            {
                if (inventory.getSlots() <= index)
                {
                    Wyrmroost.LOG.error("TOO MANY SLOTS! ABORTING THE REST!");
                    return;
                }
                consumer.accept(slotFactory.create(inventory, index++, initialX + x * 18, initialY + y * 18));
            }
        }
    }

    /**
     * Convenience method for creating an entire player inventory slot array.
     * @param playerInv the inventory. duh.
     * @param initialX the x starting position of the inventory
     * @param initialY the y starting position of the inventory
     * @param slotFactory the thing to do the thing.
     */
    public static <T extends Slot> void createPlayerContainerSlots(Inventory playerInv, int initialX, int initialY, SlotFunction<T> slotFactory, Consumer<T> consumer)
    {
        PlayerInvWrapper inv = new PlayerInvWrapper(playerInv);
        createContainerSlots(inv, 9, initialX, initialY, 9, 3, slotFactory, consumer); // Player inv
        createContainerSlots(inv, 0, initialX, initialY + 58, 9, 1, slotFactory, consumer); // Hotbar
    }

    @FunctionalInterface
    public interface SlotFunction<T extends Slot>
    {
        T create(IItemHandler inv, int index, int posX, int posY);
    }

    public static <F, T> T getCapability(Capability<F> cap, CapabilityProvider<?> item)
    {
        return item.getCapability(cap).<T>cast().orElseThrow(NoSuchElementException::new);
    }

    public static <T> void runAndClear(Collection<T> c, Consumer<T> run)
    {
        Iterator<T> itr = c.iterator();
        while (itr.hasNext())
        {
            run.accept(itr.next());
            itr.remove();
        }
    }

    //Get an entity type by a string key
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends Entity> EntityType<T> getEntityTypeByKey(@Nonnull String key)
    {
        return (EntityType<T>) EntityType.byString(key).orElse(null);
    }


}
