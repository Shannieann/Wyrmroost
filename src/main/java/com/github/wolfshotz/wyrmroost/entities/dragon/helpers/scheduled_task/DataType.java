package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.scheduled_task;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record DataType<T>(ResourceLocation identifier) {

    /// @param value value to store
    /// @return new value holder
    public Holder<T> createHolder(T value) {
        return new Holder<>(value);
    }

    /// Used as value holder in data map in [BRState]
    /// @param value held value
    /// @param <T> value type
    public record Holder<T>(@Nullable T value) {
    }
}
