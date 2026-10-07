package me.mioclient.event.events.network;

import net.minecraft.network.protocol.Packet;

public final class PacketSendPostEvent extends PacketEvent {
    public PacketSendPostEvent(Packet<?> packet) {
        super(packet);
    }
}
