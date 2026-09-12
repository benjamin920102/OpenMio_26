package me.mioclient.event.events.player;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.Event;


/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/HoleSnapEvent.class */
public final class RotationTickEvent extends Event implements ClientAccessor {
    public float getYaw() {
        return minecraftClient.player.getYaw();
    }

    public float getPitch() {
        return minecraftClient.player.getPitch();
    }
}
