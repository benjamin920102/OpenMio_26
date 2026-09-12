package me.mioclient.module.client.anticheat;

import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.mode.combat.CrystalRotationMode;

import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.network.PacketSender;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.mixin.ducks.DuckPlayerMoveC2SPacket;
import me.mioclient.module.client.AntiCheat;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import me.mioclient.mode.combat.CrystalRotationMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/YawSpoofHandler.class */
public final class YawSpoofHandler implements ClientAccessor {
    public final AntiCheat antiCheat;
    public boolean flag;

    public YawSpoofHandler(AntiCheat antiCheat) {
        this.antiCheat = antiCheat;
    }

    public void onUpdate() {
        if (is328()) {
            if (this.antiCheat.rotations.getValue() == CrystalRotationMode.SILENT) {
                PacketSender.sendFullMove(minecraftClient.player.getX(), minecraftClient.player.getY(), minecraftClient.player.getZ(), minecraftClient.player.getYaw(), minecraftClient.player.getPitch(), minecraftClient.player.isOnGround());
            }
            setYawSpoofActive(true);
        }
    }

    public void spoofYawOnMovePacket(PacketSendEvent sendImmediatelyEvent) {
        DuckPlayerMoveC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof PlayerMoveC2SPacket ? (DuckPlayerMoveC2SPacket)(sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof PlayerMoveC2SPacket) {
            DuckPlayerMoveC2SPacket duckPlayerMoveC2SPacket = (DuckPlayerMoveC2SPacket)((PlayerMoveC2SPacket) packet904);
            if (shouldSpoofYaw((PlayerMoveC2SPacket) duckPlayerMoveC2SPacket)) {
                duckPlayerMoveC2SPacket.setYaw(((PlayerMoveC2SPacket) duckPlayerMoveC2SPacket).getYaw(0.0f) + Float.intBitsToFloat(1359354950));
            }
        }
    }

    public boolean is328() {
        return shouldSpoofYaw((PlayerMoveC2SPacket) null);
    }

    public boolean shouldSpoofYaw(PlayerMoveC2SPacket playerMoveC2SPacket) {
        if ((!MovementUtils.is2181() && ClientServices.feetPlaceHelper.getEffectiveSpeed() < Double.longBitsToDouble(4617315517961601024L)) || !this.antiCheat.f2b2t.getValue().booleanValue() || PlayerUtils.isReallyFallFlying()) {
            return false;
        }
        if (playerMoveC2SPacket != null) {
            boolean z = (minecraftClient.currentScreen instanceof HandledScreen) && minecraftClient.player.currentScreenHandler == minecraftClient.player.playerScreenHandler;
            if (!playerMoveC2SPacket.changesLook() && !z) {
                return false;
            }
        }
        return this.antiCheat.flag2 || playerMoveC2SPacket == null;
    }

    public boolean isYawSpoofActive() {
        return this.flag;
    }

    public void setYawSpoofActive(boolean z) {
        this.flag = z;
    }
}
