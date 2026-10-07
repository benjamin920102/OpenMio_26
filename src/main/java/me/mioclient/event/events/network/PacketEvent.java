package me.mioclient.event.events.network;

import me.mioclient.event.Event;

import net.minecraft.network.protocol.Packet;

public abstract class PacketEvent extends Event {
    public final Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }
}
