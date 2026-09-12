package me.mioclient.module.movement.elytrafly;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.input.InputTickEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.network.PacketSender;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import java.util.ArrayDeque;
import java.util.Queue;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.input.InputTickEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.event.events.player.RotationEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.mixin.ducks.DuckPlayerMoveC2SPacket;
import me.mioclient.module.movement.ElytraFly;
import me.mioclient.module.movement.ObstaclePasser;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.screen.slot.SlotActionType;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/PacketElytraFlight.class */
public class PacketElytraFlight extends ElytraFlyModeBase {
    public static final ObstaclePasser obstaclePasser = (ObstaclePasser) ClientServices.moduleInjector.getModule(ObstaclePasser.class);
    public final Queue<Packet<?>> queue;
    public int num;
    public boolean flag;

    public PacketElytraFlight(ElytraFly elytraFly) {
        super(elytraFly);
        this.queue = new ArrayDeque();
        this.num = 0;
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void onDisable() {
        minecraftClient.options.jumpKey.setPressed(false);
        minecraftClient.player.setSprinting(false);
        this.num = 0;
        do604();
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleTick(TickEvent tickEvent) {
        if (this.elytraFly.isUsingItemBlocked() || isObstaclePasserActive()) {
            return;
        }
        if (minecraftClient.player.isFallFlying()) {
            minecraftClient.player.horizontalSpeed = 0.0f;
        }
        if (ClientServices.antiPhaseHelper.get2232() >= 40) {
            do604();
        }
        if (PlayerUtils.isInLiquid(minecraftClient.player) || PlayerUtils.isInCobweb(minecraftClient.player)) {
            do604();
            return;
        }
        if (this.elytraFly.compensate.getValue().booleanValue()) {
            this.num--;
            if (this.num == 0) {
                do604();
                return;
            }
            return;
        }
        if (this.num != 0) {
            this.num = 0;
            do604();
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleMove(MoveEvent moveEvent) {
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handlePacketReceive(PacketReceiveEvent channelRead0Event) {
        if ((channelRead0Event.getPacket() instanceof DeathMessageS2CPacket) || (channelRead0Event.getPacket() instanceof PlayerPositionLookS2CPacket)) {
            do604();
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (isObstaclePasserActive()) {
            return;
        }
        DuckPlayerMoveC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof DuckPlayerMoveC2SPacket ? (DuckPlayerMoveC2SPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof DuckPlayerMoveC2SPacket) {
            DuckPlayerMoveC2SPacket duckPlayerMoveC2SPacket = packet904;
            if (hasElytra() && this.elytraFly.silent.getValue().booleanValue() && this.elytraFly.pitchLock.getValue().booleanValue() && PlayerUtils.isReallyFallFlying() && !this.elytraFly.isUsingItemBlocked()) {
                duckPlayerMoveC2SPacket.setPitch(this.elytraFly.pitch2.getValue().floatValue());
            }
        }
        if (!this.elytraFly.compensate.getValue().booleanValue() || !minecraftClient.player.isFallFlying() || !hasElytra() || this.elytraFly.isUsingItemBlocked() || (sendImmediatelyEvent.getPacket() instanceof ClientCommandC2SPacket) || (sendImmediatelyEvent.getPacket() instanceof CommonPongC2SPacket)) {
            return;
        }
        this.queue.add(sendImmediatelyEvent.getPacket());
        sendImmediatelyEvent.cancel();
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleMotion(MotionEvent motionEvent) {
        if (!this.elytraFly.isUsingItemBlocked() && hasElytra() && !isObstaclePasserActive() && this.elytraFly.pitchLock.getValue().booleanValue() && this.elytraFly.silent.getValue().booleanValue()) {
            motionEvent.setPitch(this.elytraFly.pitch2.getValue().floatValue());
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleTickPost(TickPostEvent tickPostEvent) {
        this.flag = this.elytraFly.shouldSwapElytra();
        if (minecraftClient.player.isFallFlying() && ClientServices.antiPhaseHelper.getGroundTicks() > 5) {
            minecraftClient.player.stopFallFlying();
            return;
        }
        boolean hasElytra = hasElytra();
        if (PlayerUtils.isInLiquid(minecraftClient.player) || PlayerUtils.isInCobweb(minecraftClient.player) || this.elytraFly.isUsingItemBlocked() || isObstaclePasserActive() || !hasElytra) {
            return;
        }
        if (!PlayerUtils.isReallyFallFlying() && ClientServices.antiPhaseHelper.getGroundTicks() < 3 && !this.flag) {
            minecraftClient.player.startFallFlying();
            PacketSender.sendStartFallFlying();
        }
        if (this.elytraFly.infDurability.getValue().booleanValue() && !this.flag) {
            minecraftClient.interactionManager.clickSlot(0, 6, 0, SlotActionType.PICKUP, minecraftClient.player);
            minecraftClient.interactionManager.clickSlot(0, 6, 0, SlotActionType.PICKUP, minecraftClient.player);
            PacketSender.sendStartFallFlying();
        }
        if (!minecraftClient.player.horizontalCollision) {
            if (MovementUtils.is2181()) {
                minecraftClient.player.setSprinting(true);
                if (minecraftClient.player.isFallFlying()) {
                    minecraftClient.player.setSneaking(false);
                }
            }
            if (minecraftClient.player.isOnGround() && this.elytraFly.compensate.getValue().booleanValue()) {
                this.num = 3;
            }
        }
        if (!this.elytraFly.pitchLock.getValue().booleanValue() || this.elytraFly.silent.getValue().booleanValue()) {
            return;
        }
        minecraftClient.player.setPitch(this.elytraFly.pitch2.getValue().floatValue());
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleRotation(RotationEvent event_3) {
        if (!hasElytra() || isObstaclePasserActive() || !this.elytraFly.pitchLock.getValue().booleanValue() || this.elytraFly.isUsingItemBlocked()) {
            return;
        }
        event_3.setPitch(this.elytraFly.pitch2.getValue().floatValue());
        event_3.cancel();
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleInputTick(InputTickEvent tickEvent_2) {
        if (!isObstaclePasserActive() && hasElytra() && PlayerUtils.isReallyFallFlying() && !this.elytraFly.isUsingItemBlocked()) {
            tickEvent_2.getInput().jumping = true;
            tickEvent_2.getInput().pressingForward = true;
            tickEvent_2.getInput().movementForward = tickEvent_2.hasSpeed() ? tickEvent_2.getSpeed() : Float.intBitsToFloat(1065353216);
            minecraftClient.player.setSprinting(true);
        }
    }

    public void do604() {
        Packet<?> poll;
        if (isNotInGame()) {
            this.queue.clear();
            return;
        }
        while (!this.queue.isEmpty() && (poll = this.queue.poll()) != null) {
            PacketSender.sendPacketDirect(poll);
        }
    }

    public boolean isObstaclePasserActive() {
        if (obstaclePasser == null) {
            return false;
        }
        return obstaclePasser.is929();
    }

    public boolean hasElytra() {
        return this.elytraFly.hasElytra() || this.flag;
    }
}
