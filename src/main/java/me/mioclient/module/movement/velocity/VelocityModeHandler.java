package me.mioclient.module.movement.velocity;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.ExplosionVelocityEvent;
import me.mioclient.event.events.interaction.InteractBlockEvent;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.movement.Velocity;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/VelocityModeHandler.class */
public abstract class VelocityModeHandler implements ClientAccessor {
    public final Velocity velocity;

    public VelocityModeHandler(Velocity velocity) {
        this.velocity = velocity;
    }

    public abstract void handlePacketReceive(PacketReceiveEvent channelRead0Event);

    public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
    }

    public void onUpdate() {
    }

    public void handleExplosionVelocity(ExplosionVelocityEvent explosionVelocityEvent) {
    }

    public void handleMotion(MotionEvent motionEvent) {
    }

    public void onInteractBlock(InteractBlockEvent interactBlockEvent) {
    }

    public void onDisable() {
    }
}
