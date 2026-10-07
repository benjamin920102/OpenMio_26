package me.mioclient.util.player;

import me.mioclient.event.events.network.PacketSendPostEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.api.ClientAccessor;
import me.mioclient.network.PacketSender;

import me.mioclient.event.EventBus;

import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec3;

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
        if (minecraftClient.player.onGround() || this.flag) {
            this.num2 = 0;
            this.num++;
        } else {
            this.num = 0;
            this.num2++;
        }
    }

    @Listen
    public void onPacketSendPost(PacketSendPostEvent sendInternalEvent) {
        ServerboundMovePlayerPacket packet904 = (sendInternalEvent.getPacket()) instanceof ServerboundMovePlayerPacket ? (ServerboundMovePlayerPacket) (sendInternalEvent.getPacket()) : null;
        if (packet904 instanceof ServerboundMovePlayerPacket) {
            ServerboundMovePlayerPacket playerMoveC2SPacket = packet904;
            this.val = playerMoveC2SPacket.getX(minecraftClient.player.getX());
            this.val2 = playerMoveC2SPacket.getY(minecraftClient.player.getY());
            this.val3 = playerMoveC2SPacket.getZ(minecraftClient.player.getZ());
            this.flag = playerMoveC2SPacket.isOnGround();
            this.floatArr = new float[]{playerMoveC2SPacket.getYRot(minecraftClient.player.getYRot()), playerMoveC2SPacket.getXRot(minecraftClient.player.getXRot())};
        }
        ServerboundPlayerInputPacket packet9042 = (sendInternalEvent.getPacket()) instanceof ServerboundPlayerInputPacket ? (ServerboundPlayerInputPacket) (sendInternalEvent.getPacket()) : null;
        if (packet9042 instanceof ServerboundPlayerInputPacket) {
            ServerboundPlayerInputPacket playerInputC2SPacket = packet9042;
            this.flag4 = playerInputC2SPacket.input().shift();
        }
    }

    public void withSneakRelease(java.lang.Runnable runnable) {
        if (!ClientServices.antiPhaseHelper.isSneaking()) {
            runnable.run();
            return;
        }
        Input cur = minecraftClient.player.input.keyPresses;
        PacketSender.sendPacket(new ServerboundPlayerInputPacket(new Input(cur.forward(), cur.backward(), cur.left(), cur.right(), cur.jump(), false, cur.sprint())));
        runnable.run();
        Input cur2 = minecraftClient.player.input.keyPresses;
        PacketSender.sendPacket(new ServerboundPlayerInputPacket(new Input(cur2.forward(), cur2.backward(), cur2.left(), cur2.right(), cur2.jump(), true, cur2.sprint())));
    }

    public void withSneakPress(java.lang.Runnable runnable) {
        if (!(!ClientServices.antiPhaseHelper.isSneaking())) {
            runnable.run();
            return;
        }
        Input cur = minecraftClient.player.input.keyPresses;
        PacketSender.sendPacket(new ServerboundPlayerInputPacket(new Input(cur.forward(), cur.backward(), cur.left(), cur.right(), cur.jump(), true, cur.sprint())));
        runnable.run();
        Input cur2 = minecraftClient.player.input.keyPresses;
        PacketSender.sendPacket(new ServerboundPlayerInputPacket(new Input(cur2.forward(), cur2.backward(), cur2.left(), cur2.right(), cur2.jump(), false, cur2.sprint())));
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

    public Vec3 getPosition() {
        return new Vec3(this.val, this.val2, this.val3);
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
