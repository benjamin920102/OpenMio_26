package me.mioclient.event.events.client;

import me.mioclient.mode.combat.KeyPearlMode;

import me.mioclient.event.Event;

import me.mioclient.mode.combat.KeyPearlMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/KeyPearlModeEvent.class */
public class InventorySyncEvent extends Event {
    public final KeyPearlMode keyPearlMode;

    public InventorySyncEvent(KeyPearlMode keyPearlMode) {
        this.keyPearlMode = keyPearlMode;
    }

    public KeyPearlMode getPhase() {
        return this.keyPearlMode;
    }
}
