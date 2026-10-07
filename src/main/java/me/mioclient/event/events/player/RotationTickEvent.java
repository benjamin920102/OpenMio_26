package me.mioclient.event.events.player;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.Event;

public final class RotationTickEvent extends Event implements ClientAccessor {
    public float getYaw() {
        return minecraftClient.player.getYRot();
    }

    public float getPitch() {
        return minecraftClient.player.getXRot();
    }
}
