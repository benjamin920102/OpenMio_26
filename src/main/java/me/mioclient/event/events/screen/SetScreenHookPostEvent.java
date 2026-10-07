package me.mioclient.event.events.screen;

import net.minecraft.client.gui.screens.Screen;

public final class SetScreenHookPostEvent extends ScreenEvent {
    public SetScreenHookPostEvent(Screen screen, Screen screen2) {
        super(screen, screen2);
    }
}
