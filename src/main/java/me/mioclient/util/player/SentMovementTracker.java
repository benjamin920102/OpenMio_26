package me.mioclient.util.player;

import me.mioclient.event.events.network.PacketSendPostEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;
import me.mioclient.api.ClientServices;
import me.mioclient.network.PacketSender;

import me.mioclient.event.EventBus;

import me.mioclient.event.Listen;
import me.mioclient.event.events.network.PacketSendPostEvent;
import me.mioclient.event.events.client.TickEvent;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SentMovementTracker.class */
public final class SentMovementTracker implements ClientAccessor {
    public double val;
    public double val2;
    public double val3;
    public boolean flag;
    public boolean flag2;
    public boolean flag3;
    public boolean flag4;
    public int num;
    public int num2;
    public float[] floatArr;

    public SentMovementTracker() {
        eventBus.register(this);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        this.flag3 = this.flag2;
        this.flag2 = minecraftClient.player.isFallFlying();
        if (minecraftClient.player.isOnGround() || this.flag) {
            this.num2 = 0;
            this.num++;
        } else {
            this.num = 0;
            this.num2++;
        }
    }

    @Listen
    public void onPacketSendPost(PacketSendPostEvent sendInternalEvent) {
        PlayerMoveC2SPacket packet904 = (sendInternalEvent.getPacket()) instanceof PlayerMoveC2SPacket ? (PlayerMoveC2SPacket) (sendInternalEvent.getPacket()) : null;
        if (packet904 instanceof PlayerMoveC2SPacket) {
            PlayerMoveC2SPacket playerMoveC2SPacket = packet904;
            this.val = playerMoveC2SPacket.getX(minecraftClient.player.getX());
            this.val2 = playerMoveC2SPacket.getY(minecraftClient.player.getY());
            this.val3 = playerMoveC2SPacket.getZ(minecraftClient.player.getZ());
            this.flag = playerMoveC2SPacket.isOnGround();
            this.floatArr = new float[]{playerMoveC2SPacket.getYaw(minecraftClient.player.getYaw()), playerMoveC2SPacket.getPitch(minecraftClient.player.getPitch())};
        }
        ClientCommandC2SPacket packet9042 = (sendInternalEvent.getPacket()) instanceof ClientCommandC2SPacket ? (ClientCommandC2SPacket) (sendInternalEvent.getPacket()) : null;
        if (packet9042 instanceof ClientCommandC2SPacket) {
            ClientCommandC2SPacket clientCommandC2SPacket = packet9042;
            if (clientCommandC2SPacket.getMode() == ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY) {
                this.flag4 = true;
            } else if (clientCommandC2SPacket.getMode() == ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY) {
                this.flag4 = false;
            }
        }
    }

    public void withSneakRelease(java.lang.Runnable runnable) {
        if (!ClientServices.antiPhaseHelper.isSneaking()) {
            runnable.run();
            return;
        }
        PacketSender.sendPacket(new ClientCommandC2SPacket(minecraftClient.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        runnable.run();
        PacketSender.sendPacket(new ClientCommandC2SPacket(minecraftClient.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
    }

    public void withSneakPress(java.lang.Runnable runnable) {
        if (!(!ClientServices.antiPhaseHelper.isSneaking())) {
            runnable.run();
            return;
        }
        PacketSender.sendPacket(new ClientCommandC2SPacket(minecraftClient.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        runnable.run();
        PacketSender.sendPacket(new ClientCommandC2SPacket(minecraftClient.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
    }

    public double getX() {
        return this.val;
    }

    public double getY() {
        return this.val2;
    }

    public double getZ() {
        return this.val3;
    }

    public Vec3d getPosition() {
        return new Vec3d(this.val, this.val2, this.val3);
    }

    public boolean isOnGround() {
        return this.flag;
    }

    public boolean wasFallFlying() {
        return this.flag3;
    }

    public boolean isSneaking() {
        return this.flag4;
    }

    public int getGroundTicks() {
        return this.num;
    }

    public int get2232() {
        return this.num2;
    }

    public float[] getRotation() {
        return this.floatArr;
    }
}
