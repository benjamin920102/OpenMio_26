package me.mioclient.event.events.network;

import net.minecraft.network.packet.Packet;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/SendImmediatelyEvent.class */
public final class PacketSendEvent extends PacketEvent {
    public PacketSendEvent(Packet<?> packet) {
        super(packet);
    }
}
