package com.github.wolfshotz.wyrmroost.client.screen;

import com.github.wolfshotz.wyrmroost.client.ClientEvents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

public class TarragonTomeScreen extends Screen implements BookScreen
{
    public TarragonTomeScreen()
    {
        super(new TranslatableComponent("tarragonTome.title"));
    }


    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    public static void open()
    {
        ClientEvents.getClient().setScreen(new TarragonTomeScreen());
    }
}
