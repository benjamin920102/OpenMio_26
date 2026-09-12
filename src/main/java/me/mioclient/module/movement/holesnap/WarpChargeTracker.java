package me.mioclient.module.movement.holesnap;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;

import me.mioclient.event.EventBus;

import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.movement.HoleSnap;
import me.mioclient.module.movement.Warp;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/WarpChargeTracker.class */
public final class WarpChargeTracker implements ClientAccessor {
    public static boolean flag;
    public static Warp warp = (Warp) ClientServices.moduleInjector.getModule(Warp.class);
    public final HoleSnap holeSnap;
    public int num;

    public WarpChargeTracker(HoleSnap holeSnap) {
        this.holeSnap = holeSnap;
        eventBus.register(this);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (is2181()) {
            return;
        }
        int intValue = warp.charge.getValue().intValue();
        int i = this.num + 1;
        this.num = i;
        this.num = Math.min(intValue, i);
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (!is2181() || this.num == 0) {
            return;
        }
        if (((sendImmediatelyEvent.getPacket() instanceof PlayerMoveC2SPacket) && flag) || warp.isToggled()) {
            int i = this.num - 1;
            this.num = i;
            this.num = Math.max(0, i);
        }
    }

    public boolean tryConsumeCharge() {
        if (this.num <= 0 || !is2181() || !this.holeSnap.shift.getValue().booleanValue() || !minecraftClient.player.isOnGround() || ClientServices.antiPhaseHelper.getGroundTicks() <= 1) {
            return false;
        }
        ClientServices.inner.requestTickRate(this.holeSnap, warp.boost.getValue().intValue());
        return true;
    }

    public boolean is2181() {
        return minecraftClient.player.getPos().squaredDistanceTo(minecraftClient.player.prevX, minecraftClient.player.prevY, minecraftClient.player.prevZ) > 0.0d;
    }
}
