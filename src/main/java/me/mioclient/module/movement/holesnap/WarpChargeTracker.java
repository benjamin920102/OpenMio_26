package me.mioclient.module.movement.holesnap;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;

import me.mioclient.event.EventBus;

import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.movement.HoleSnap;
import me.mioclient.module.movement.Warp;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public final class WarpChargeTracker implements ClientAccessor {
    public static boolean flag;
    /* 26.2 port: resolved lazily — eager static init ran before the module registry was ready, leaving refs null (NPE). */
    private static Warp warp;

    public static Warp getWarp() {
        if (warp == null && ClientServices.moduleInjector != null) {
            try {
                warp = (Warp) ClientServices.moduleInjector.getModule(Warp.class);
            } catch (Exception e) {
            }
        }
        return warp;
    }
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
        Warp warp_lcl = getWarp();
        if (warp_lcl == null) {
            return;
        }
        int intValue = warp_lcl.charge.getValue().intValue();
        int i = this.num + 1;
        this.num = i;
        this.num = Math.min(intValue, i);
    }

    @Listen
    public void onPacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (!is2181() || this.num == 0) {
            return;
        }
        Warp warp_lcl = getWarp();
        if (((sendImmediatelyEvent.getPacket() instanceof ServerboundMovePlayerPacket) && flag) || (warp_lcl != null && warp_lcl.isToggled())) {
            int i = this.num - 1;
            this.num = i;
            this.num = Math.max(0, i);
        }
    }

    public boolean tryConsumeCharge() {
        if (this.num <= 0 || !is2181() || !this.holeSnap.shift.getValue().booleanValue() || !minecraftClient.player.onGround() || ClientServices.antiPhaseHelper.getGroundTicks() <= 1) {
            return false;
        }
        Warp warp_lcl = getWarp();
        if (warp_lcl == null) {
            return false;
        }
        ClientServices.inner.requestTickRate(this.holeSnap, warp_lcl.boost.getValue().intValue());
        return true;
    }

    public boolean is2181() {
        if (minecraftClient.player == null) {
            return false;
        }
        return minecraftClient.player.position().distanceToSqr(minecraftClient.player.xo, minecraftClient.player.yo, minecraftClient.player.zo) > 0.0d;
    }
}