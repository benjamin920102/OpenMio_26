package me.mioclient.event.events.screen;

import net.minecraft.client.gui.screens.Screen;

public final class SetScreenHookPreEvent extends ScreenEvent {
    public SetScreenHookPreEvent(Screen screen, Screen screen2) {
        super(screen, screen2);
    }
}
