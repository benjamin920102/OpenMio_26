package me.mioclient.event.events.screen;

import me.mioclient.event.Event;

import net.minecraft.client.gui.screens.Screen;

public class ScreenEvent extends Event {
    public final Screen screen;
    public final Screen screen2;

    public ScreenEvent(Screen screen, Screen screen2) {
        this.screen = screen;
        this.screen2 = screen2;
    }

    public Screen getScreen() {
        return this.screen2;
    }

    public Screen getOldScreen() {
        return this.screen;
    }
}
