package com.github.shannieann.wyrmroost.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Reflection-based access to Entity's protected members used by the LivingEntity mixin for fall-flying.
 * Avoids needing @Shadow (which fails when members are on the parent class Entity) and avoids
 * depending on Access Transformer for the compile classpath.
 */
public final class EntityAccessHelper {

    private static final MethodHandle GET_SHARED_FLAG;
    private static final MethodHandle SET_SHARED_FLAG;
    private static final MethodHandle GET_ON_GROUND;
    private static final MethodHandle GET_LEVEL;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            Method getSharedFlag = Entity.class.getDeclaredMethod("getSharedFlag", int.class);
            Method setSharedFlag = Entity.class.getDeclaredMethod("setSharedFlag", int.class, boolean.class);
            getSharedFlag.setAccessible(true);
            setSharedFlag.setAccessible(true);
            GET_SHARED_FLAG = lookup.unreflect(getSharedFlag);
            SET_SHARED_FLAG = lookup.unreflect(setSharedFlag);

            Field onGround = Entity.class.getDeclaredField("onGround");
            Field level = Entity.class.getDeclaredField("level");
            onGround.setAccessible(true);
            level.setAccessible(true);
            GET_ON_GROUND = lookup.unreflectGetter(onGround);
            GET_LEVEL = lookup.unreflectGetter(level);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize EntityAccessHelper", e);
        }
    }

    public static boolean getSharedFlag(Entity entity, int flag) {
        try {
            return (boolean) GET_SHARED_FLAG.invoke(entity, flag);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static void setSharedFlag(Entity entity, int flag, boolean value) {
        try {
            SET_SHARED_FLAG.invoke(entity, flag, value);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static boolean isOnGround(Entity entity) {
        try {
            return (boolean) GET_ON_GROUND.invoke(entity);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static Level getLevel(Entity entity) {
        try {
            return (Level) GET_LEVEL.invoke(entity);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private EntityAccessHelper() {}
}
