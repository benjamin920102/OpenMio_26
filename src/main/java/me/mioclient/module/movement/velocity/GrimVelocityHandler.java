package me.mioclient.module.movement.velocity;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.network.PacketSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import me.mioclient.event.events.network.ExplosionVelocityEvent;
import me.mioclient.module.client.AntiCheat;
import me.mioclient.module.movement.Velocity;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;

public class GrimVelocityHandler extends VelocityModeHandler {
    /* 26.2 port: resolved lazily — eager static init ran before the module registry was ready, leaving refs null (NPE). */
    private static SpeedMine speedMine;
    private static AntiCheat antiCheat;

    public static SpeedMine getSpeedMine() {
        if (speedMine == null && ClientServices.moduleInjector != null) {
            try {
                speedMine = (SpeedMine) ClientServices.moduleInjector.getModule(SpeedMine.class);
            } catch (Exception e) {
            }
        }
        return speedMine;
    }

    public static AntiCheat getAntiCheat() {
        if (antiCheat == null && ClientServices.moduleInjector != null) {
            try {
                antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
            } catch (Exception e) {
            }
        }
        return antiCheat;
    }
    public final Stopwatch stopwatch;
    public final List<Long> list;
    public boolean flag;
    public float val;
    public float val2;

    public GrimVelocityHandler(Velocity velocity) {
        super(velocity);
        this.stopwatch = new Stopwatch();
        this.list = Collections.synchronizedList(new ArrayList());
    }

    @Override // me.mioclient.module.movement.velocity.VelocityModeHandler
    public void handlePacketReceive(PacketReceiveEvent channelRead0Event) {
        ClientboundSetEntityMotionPacket packet904 = (channelRead0Event.getPacket()) instanceof ClientboundSetEntityMotionPacket ? (ClientboundSetEntityMotionPacket) (channelRead0Event.getPacket()) : null;
        if ((packet904 instanceof ClientboundSetEntityMotionPacket) && packet904.id() == minecraftClient.player.getId() && !isGrimBypassDisabled()) {
            channelRead0Event.cancel();
        }
        if (channelRead0Event.getPacket() instanceof ClientboundTeleportEntityPacket) {
            this.list.add(Long.valueOf(System.currentTimeMillis()));
        }
    }

    @Override // me.mioclient.module.movement.velocity.VelocityModeHandler
    public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
        ServerboundMovePlayerPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof ServerboundMovePlayerPacket ? (ServerboundMovePlayerPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof ServerboundMovePlayerPacket) {
            ServerboundMovePlayerPacket playerMoveC2SPacket = packet904;
            if (playerMoveC2SPacket.hasRotation()) {
                this.val = playerMoveC2SPacket.getYRot(0.0f);
                this.val2 = playerMoveC2SPacket.getXRot(0.0f);
            }
        }
    }

    @Override // me.mioclient.module.movement.velocity.VelocityModeHandler
    public void handleExplosionVelocity(ExplosionVelocityEvent explosionVelocityEvent) {
        if (((explosionVelocityEvent.getPlayerVelocityX() == 0.0f && explosionVelocityEvent.getPlayerVelocityY() == 0.0f && explosionVelocityEvent.getPlayerVelocityZ() == 0.0f) ? false : true) && this.velocity.explosions.getValue().booleanValue() && !isGrimBypassDisabled()) {
            explosionVelocityEvent.setPlayerVelocityX(0.0f);
            explosionVelocityEvent.setPlayerVelocityY(0.0f);
            explosionVelocityEvent.setPlayerVelocityZ(0.0f);
        }
    }

    @Override // me.mioclient.module.movement.velocity.VelocityModeHandler
    public void onUpdate() {
        // 26.2: BlockPos.stream(AABB) -> betweenClosedStream(AABB), ClientLevel.isAir -> getBlockState().isAir()
        this.flag = BlockPos.betweenClosedStream(minecraftClient.player.getBoundingBox()).anyMatch(blockPos -> {
            return !minecraftClient.level.getBlockState(blockPos).isAir();
        });
        if (minecraftClient.player.tickCount <= 20 || isGrimBypassDisabled()) {
            return;
        }
        this.list.removeIf(l -> {
            return System.currentTimeMillis() > l.longValue() + 1000;
        });
        if (this.list.size() <= 2) {
            sendGrimBypassPacket();
        } else {
            this.stopwatch.reset();
            this.list.clear();
        }
    }

    public void sendGrimBypassPacket() {
        BlockPos blockPos713 = getMostOverlappingBlockPos();
        if (blockPos713 != null) {
            PacketSender.sendPacket(new ServerboundMovePlayerPacket.PosRot(minecraftClient.player.getX(), minecraftClient.player.getY(), minecraftClient.player.getZ(), this.val, this.val2, minecraftClient.player.onGround(), minecraftClient.player.horizontalCollision));
            PacketSender.sendPacket(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPos713, Direction.DOWN));
        }
    }

    public BlockPos getMostOverlappingBlockPos() {
        // 26.2: BlockPos.stream -> betweenClosedStream; BlockPos is immutable so copy directly
        return (BlockPos) BlockPos.betweenClosedStream(minecraftClient.player.getBoundingBox()).map((v0) -> {
            return new BlockPos(v0);
        }).filter(WorldUtils::isAir).max(Comparator.comparing(this::getIntersectionSize)).orElse(null);
    }

    public float getIntersectionSize(BlockPos blockPos) {
        // 26.2: AABB.intersection -> intersect, getAverageSideLength -> getSize
        return (float) minecraftClient.player.getBoundingBox().intersect(new AABB(blockPos)).getSize();
    }

    public boolean isGrimBypassDisabled() {
        return minecraftClient.player.isFallFlying() || PlayerUtils.isInCobweb(minecraftClient.player) || (getMostOverlappingBlockPos() == null && !this.flag) || !this.stopwatch.hasElapsedMs(200L);
    }
}