package me.mioclient.event.events.network;

import net.minecraft.network.protocol.Packet;

public final class PacketSendEvent extends PacketEvent {
    public PacketSendEvent(Packet<?> packet) {
        super(packet);
    }
}
