package com.github.shannieann.wyrmroost.registry;

import com.github.shannieann.wyrmroost.Wyrmroost;
import com.github.shannieann.wyrmroost.events.ClientEvents;
import com.github.shannieann.wyrmroost.network.packets.KeybindHandler;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

/**
 * @see org.lwjgl.glfw.GLFW
 */
public class WRKeybind extends KeyMapping
{
    private static final String CATEGORY = "keyCategory.wyrmroost";

    public static final KeyMapping FLIGHT_DESCENT = new KeyMapping("key.flight_descent", GLFW.GLFW_KEY_LEFT_CONTROL, CATEGORY);

    private final byte behaviorId;
    private final boolean sendsPacket;
    private boolean prevIsPressed;

    public WRKeybind(String name, int keyCode, byte behaviorId, boolean sendsPacket)
    {
        super(name, KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM.getOrCreate(keyCode), CATEGORY);
        this.behaviorId = behaviorId;
        this.sendsPacket = sendsPacket;
    }

    public WRKeybind(String name, int keyCode, byte behaviorId)
    {
        this(name, keyCode, behaviorId, true);
    }

    @Override
    public void setDown(boolean pressed)
    {
        super.setDown(pressed);

        if (ClientEvents.getPlayer() != null && prevIsPressed != pressed)
        {
            byte mods = 0;
            if (Screen.hasAltDown()) mods |= GLFW.GLFW_MOD_ALT;
            if (Screen.hasControlDown()) mods |= GLFW.GLFW_MOD_CONTROL;
            if (Screen.hasShiftDown()) mods |= GLFW.GLFW_MOD_SHIFT;
            KeybindHandler handler = new KeybindHandler(behaviorId, mods, pressed);
            handler.process(ClientEvents.getPlayer());
            if (sendsPacket) Wyrmroost.NETWORK.sendToServer(handler);
        }
        prevIsPressed = pressed;
    }

    public static void registerKeys()
    {
        ClientRegistry.registerKeyBinding(new WRKeybind("key.mountKey1", GLFW.GLFW_KEY_V, KeybindHandler.MOUNT_KEY));
        ClientRegistry.registerKeyBinding(new WRKeybind("key.mountKey2", GLFW.GLFW_KEY_G, KeybindHandler.ALT_MOUNT_KEY));
        ClientRegistry.registerKeyBinding(new WRKeybind("key.switch_flight", GLFW.GLFW_KEY_PERIOD, KeybindHandler.SWITCH_FLIGHT, false));
        ClientRegistry.registerKeyBinding(FLIGHT_DESCENT);
    }
}
