package me.mioclient.event.events.interaction;

import me.mioclient.event.Event;

import net.minecraft.network.protocol.game.ServerboundUseItemPacket;

/* 26.2 port: Yarn ServerboundUseItemPacket -> MojMap ServerboundUseItemPacket (same shape). */
public class InteractItemPacketEvent extends Event {
    public ServerboundUseItemPacket playerInteractItemC2SPacket;

    public InteractItemPacketEvent(ServerboundUseItemPacket playerInteractItemC2SPacket) {
        this.playerInteractItemC2SPacket = playerInteractItemC2SPacket;
    }

    public ServerboundUseItemPacket getInteractPacket() {
        return this.playerInteractItemC2SPacket;
    }

    public void setInteractPacket(ServerboundUseItemPacket playerInteractItemC2SPacket) {
        this.playerInteractItemC2SPacket = playerInteractItemC2SPacket;
    }

    public void setRotation(float[] fArr) {
        setInteractPacket(new ServerboundUseItemPacket(this.playerInteractItemC2SPacket.getHand(), this.playerInteractItemC2SPacket.getSequence(), fArr[0], fArr[1]));
        cancel();
    }
}
