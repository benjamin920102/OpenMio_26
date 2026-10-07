package me.mioclient.event.events.network;

import net.minecraft.network.protocol.Packet;

public final class PacketReceiveEvent extends PacketEvent {
    public PacketReceiveEvent(Packet<?> packet) {
        super(packet);
    }
}
