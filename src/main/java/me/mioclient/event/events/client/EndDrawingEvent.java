package me.mioclient.event.events.client;

import me.mioclient.mode.combat.KeyPearlMode;

import me.mioclient.event.Event;

public class EndDrawingEvent extends Event {
    public final KeyPearlMode keyPearlMode;

    public EndDrawingEvent(KeyPearlMode keyPearlMode) {
        this.keyPearlMode = keyPearlMode;
    }

    public KeyPearlMode getPhase() {
        return this.keyPearlMode;
    }
}
