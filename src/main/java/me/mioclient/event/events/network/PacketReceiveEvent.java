package me.mioclient.event.events.network;

import net.minecraft.network.packet.Packet;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/ChannelRead0Event.class */
public final class PacketReceiveEvent extends PacketEvent {
    public PacketReceiveEvent(Packet<?> packet) {
        super(packet);
    }
}
