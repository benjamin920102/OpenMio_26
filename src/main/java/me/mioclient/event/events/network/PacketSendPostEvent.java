package me.mioclient.event.events.network;

import net.minecraft.network.packet.Packet;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/SendInternalEvent.class */
public final class PacketSendPostEvent extends PacketEvent {
    public PacketSendPostEvent(Packet<?> packet) {
        super(packet);
    }
}
