package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.scheduled_task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ScheduledTask {
    private final int maxTickTime;
    private int currentTickTime = 0;
    private final Map<DataType<?>, DataType.Holder<?>> dataMap = new HashMap<>();
    private final Consumer<ScheduledTask> task;

    public ScheduledTask(int maxTickTime, Consumer<ScheduledTask> task) {
        this.maxTickTime = maxTickTime;
        this.task = task;
    }

    /**
     * @param dataType type of data stored
     * @param <T> value type
     * @return held value if present or null
     */
    @Nullable
    public  <T> T getData(@NotNull DataType<T> dataType) {
        DataType.Holder<T> holder = (DataType.Holder<T>) dataMap.get(dataType);
        return holder == null ? null : holder.value();
    }

    /**
     * @param dataType type of data stored
     * @param <T> value type
     * @param defaultValue default value if no data is stored or holder has null value
     * @return held value if present or default
     */
    public <T> T getData(@NotNull DataType<T> dataType, @NotNull T defaultValue) {
        DataType.Holder<T> holder = (DataType.Holder<T>) dataMap.get(dataType);
        if (holder == null) return defaultValue;
        return holder.value() != null ? holder.value() : defaultValue;
    }

    /**
     * @param dataType type of data to store
     * @param value value to store
     * @param <T> value type
     */
    public <T> void setData(@NotNull DataType<T> dataType, T value) {
        dataMap.put(dataType, new DataType.Holder<>(value));
    }

    public int getCurrentTickTime() {
        return currentTickTime;
    }

    public int getMaxTickTime() {
        return maxTickTime;
    }

    public boolean isFinished() {
        return currentTickTime >= maxTickTime;
    }

    public void tick() {
        if (!isFinished()) task.accept(this);
        currentTickTime++;
    }
}
