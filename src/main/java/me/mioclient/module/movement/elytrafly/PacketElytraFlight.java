package me.mioclient.module.movement.elytrafly;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.input.InputTickEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.network.PacketSender;

import java.util.ArrayDeque;
import java.util.Queue;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.event.events.player.RotationEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.movement.ElytraFly;
import me.mioclient.module.movement.ObstaclePasser;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.world.inventory.ContainerInput;

public class PacketElytraFlight extends ElytraFlyModeBase {
    /* 26.2 port: resolved lazily — eager static init ran before the module registry was ready, leaving refs null (NPE). */
    private static ObstaclePasser obstaclePasser;

    public static ObstaclePasser getObstaclePasser() {
        if (obstaclePasser == null && ClientServices.moduleInjector != null) {
            try {
                obstaclePasser = (ObstaclePasser) ClientServices.moduleInjector.getModule(ObstaclePasser.class);
            } catch (Exception e) {
            }
        }
        return obstaclePasser;
    }
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
        minecraftClient.options.keyJump.setDown(false);
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
            minecraftClient.player.xBob = 0.0f;
            minecraftClient.player.yBob = 0.0f;
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
        if ((channelRead0Event.getPacket() instanceof ClientboundPlayerCombatKillPacket) || (channelRead0Event.getPacket() instanceof ClientboundTeleportEntityPacket)) {
            do604();
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (isObstaclePasserActive()) {
            return;
        }
        if (sendImmediatelyEvent.getPacket() instanceof net.minecraft.network.protocol.game.ServerboundMovePlayerPacket move
                && hasElytra() && this.elytraFly.silent.getValue().booleanValue() && this.elytraFly.pitchLock.getValue().booleanValue() && PlayerUtils.isReallyFallFlying() && !this.elytraFly.isUsingItemBlocked()) {
            // 26.2 MojMap: packets IMMUTABLE — cancel + resend with modified XRot
            sendImmediatelyEvent.cancel();
            float nx = this.elytraFly.pitch2.getValue().floatValue();
            boolean og = move.isOnGround();
            boolean hc = move.horizontalCollision();
            if (move.hasPosition() && move.hasRotation()) {
                PacketSender.sendPacket(new net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot(new net.minecraft.world.phys.Vec3(move.getX(0.0d), move.getY(0.0d), move.getZ(0.0d)), move.getYRot(0.0f), nx, og, hc));
            } else if (move.hasPosition()) {
                PacketSender.sendPacket(new net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos(new net.minecraft.world.phys.Vec3(move.getX(0.0d), move.getY(0.0d), move.getZ(0.0d)), og, hc));
            } else if (move.hasRotation()) {
                PacketSender.sendPacket(new net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Rot(move.getYRot(0.0f), nx, og, hc));
            } else {
                PacketSender.sendPacket(new net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly(og, hc));
            }
            return;
        }
        if (!this.elytraFly.compensate.getValue().booleanValue() || !minecraftClient.player.isFallFlying() || !hasElytra() || this.elytraFly.isUsingItemBlocked() || (sendImmediatelyEvent.getPacket() instanceof ServerboundPlayerCommandPacket) || (sendImmediatelyEvent.getPacket() instanceof ServerboundPongPacket)) {
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
            minecraftClient.gameMode.handleContainerInput(0, 6, 0, ContainerInput.PICKUP, minecraftClient.player);
            minecraftClient.gameMode.handleContainerInput(0, 6, 0, ContainerInput.PICKUP, minecraftClient.player);
            PacketSender.sendStartFallFlying();
        }
        if (!minecraftClient.player.horizontalCollision) {
            if (MovementUtils.is2181()) {
                minecraftClient.player.setSprinting(true);
                if (minecraftClient.player.isFallFlying()) {
                    minecraftClient.player.setShiftKeyDown(false);
                }
            }
            if (minecraftClient.player.onGround() && this.elytraFly.compensate.getValue().booleanValue()) {
                this.num = 3;
            }
        }
        if (!this.elytraFly.pitchLock.getValue().booleanValue() || this.elytraFly.silent.getValue().booleanValue()) {
            return;
        }
        minecraftClient.player.setXRot(this.elytraFly.pitch2.getValue().floatValue());
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
            { net.minecraft.client.player.ClientInput _ci = tickEvent_2.getInput(); net.minecraft.world.entity.player.Input _p = _ci.keyPresses; _ci.keyPresses = new net.minecraft.world.entity.player.Input(true, _p.backward(), _p.left(), _p.right(), true, _p.shift(), _p.sprint()); }
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
        ObstaclePasser obstaclePasser_lcl = getObstaclePasser();
        if (obstaclePasser_lcl == null) {
            return false;
        }
        return obstaclePasser_lcl.is929();
    }

    public boolean hasElytra() {
        return this.elytraFly.hasElytra() || this.flag;
    }
}