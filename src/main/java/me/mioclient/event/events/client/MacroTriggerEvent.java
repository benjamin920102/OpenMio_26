package me.mioclient.event.events.client;

import me.mioclient.event.Event;

import me.mioclient.command.KeybindFeature;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ClientEvent_2.class */
public final class MacroTriggerEvent extends Event {
    public final KeybindFeature keybindFeature;

    public MacroTriggerEvent(KeybindFeature keybindFeature) {
        this.keybindFeature = keybindFeature;
    }

    public KeybindFeature getMacro() {
        return this.keybindFeature;
    }
}
