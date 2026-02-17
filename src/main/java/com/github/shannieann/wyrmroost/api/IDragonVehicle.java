package com.github.shannieann.wyrmroost.api;

import java.util.List;

import com.github.shannieann.wyrmroost.entity.dragon.WRDragonEntity;

/**
 * Interface for entities that can carry dragon passengers (e.g. Silver Glider on shoulders).
 * Implemented via mixin on LivingEntity.
 */
public interface IDragonVehicle {

    /**
     * Add a dragon as a passenger to this entity
     * @param dragon The dragon to add
     */
    void addDragonPassenger(WRDragonEntity dragon);

    /**
     * Remove a dragon passenger from this entity
     * @param dragon The dragon to remove
     */
    void removeDragonPassenger(WRDragonEntity dragon);

    /**
     * Check if the entity has any dragon passengers
     * @return True if there are any dragon passengers
     */
    boolean hasDragonPassengers();

    /**
     * Check if the entity has a specific dragon passenger
     * @param dragon The dragon to check for
     * @return True if this entity has that dragon as passenger
     */
    boolean hasDragonPassenger(WRDragonEntity dragon);

    /**
     * Get all dragon passengers
     * @return List of all dragon passengers
     */
    List<WRDragonEntity> getDragonPassengers();

    /**
     * Get a specific dragon passenger
     * @param position The position (0-2) to get the dragon from
     * @return The dragon at the specified position, or null if no dragon is at that position
     */
    WRDragonEntity getDragonPassenger(int position);
}
