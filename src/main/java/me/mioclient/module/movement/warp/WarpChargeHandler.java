package me.mioclient.module.movement.warp;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.EventBus;

import java.util.LinkedList;
import java.util.Queue;
import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.movement.Warp;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/WarpChargeHandler.class */
public class WarpChargeHandler implements ClientAccessor {
    public final Queue<Packet<?>> queue = new LinkedList();
    public final Warp warp;

    public WarpChargeHandler(Warp warp) {
        eventBus.register(this);
        this.warp = warp;
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (this.warp.is2181() || this.warp.mode.getValue() != Warp.WarpMode.PLAIN) {
            return;
        }
        this.warp.num = Math.min(this.warp.charge.getValue().intValue(), this.warp.num + this.warp.chargeSpeed.getValue().intValue());
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (isNotInGame()) {
            return;
        }
        if (this.warp.isToggled() && this.warp.mode.getValue() == Warp.WarpMode.ALTERNATIVE) {
            Packet<?> packet904 = sendImmediatelyEvent.getPacket();
            if (packet904 instanceof CommonPongC2SPacket) {
                Packet<?> packet = (CommonPongC2SPacket) packet904;
                if (!this.warp.isPlayerMoving() && this.queue.size() < this.warp.charge.getValue().intValue()) {
                    sendImmediatelyEvent.cancel();
                    this.queue.add(packet);
                }
                if (!this.queue.isEmpty()) {
                    sendImmediatelyEvent.cancel();
                }
            }
        }
        if (this.warp.mode.getValue() == Warp.WarpMode.PLAIN && (sendImmediatelyEvent.getPacket() instanceof PlayerMoveC2SPacket) && this.warp.is2181() && this.warp.num != 0) {
            if (this.warp.isToggled() || this.warp.recharge.getValue() != WarpHelperMode.HOLD) {
                this.warp.num = Math.max(0, this.warp.num - 1);
            }
        }
    }
}
