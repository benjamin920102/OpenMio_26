package me.mioclient.module.movement.elytrafly;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.input.InputTickEvent;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.event.events.player.RotationEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.movement.ElytraFly;

public abstract class ElytraFlyModeBase implements ClientAccessor {
    public final ElytraFly elytraFly;

    public ElytraFlyModeBase(ElytraFly elytraFly) {
        this.elytraFly = elytraFly;
    }

    public abstract void handleTick(TickEvent tickEvent);

    public abstract void handleMove(MoveEvent moveEvent);

    public abstract void handlePacketReceive(PacketReceiveEvent channelRead0Event);

    public abstract void handlePacketSend(PacketSendEvent sendImmediatelyEvent);

    public abstract void handleMotion(MotionEvent motionEvent);

    public abstract void handleTickPost(TickPostEvent tickPostEvent);

    public abstract void handleRotation(RotationEvent event_3);

    public void handleInputTick(InputTickEvent tickEvent_2) {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }
}