package me.mioclient.event.events.interaction;

import me.mioclient.event.Event;

import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/InteractItemEvent_2.class */
public class InteractItemPacketEvent extends Event {
    public PlayerInteractItemC2SPacket playerInteractItemC2SPacket;

    public InteractItemPacketEvent(PlayerInteractItemC2SPacket playerInteractItemC2SPacket) {
        this.playerInteractItemC2SPacket = playerInteractItemC2SPacket;
    }

    public PlayerInteractItemC2SPacket getInteractPacket() {
        return this.playerInteractItemC2SPacket;
    }

    public void setInteractPacket(PlayerInteractItemC2SPacket playerInteractItemC2SPacket) {
        this.playerInteractItemC2SPacket = playerInteractItemC2SPacket;
    }

    public void setRotation(float[] fArr) {
        setInteractPacket(new PlayerInteractItemC2SPacket(this.playerInteractItemC2SPacket.getHand(), this.playerInteractItemC2SPacket.getSequence(), fArr[0], fArr[1]));
        cancel();
    }
}
