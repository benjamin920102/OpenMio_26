package me.mioclient.module.movement.velocity;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.ClientServices;
import me.mioclient.util.world.WorldUtils;

import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.network.PacketSender;
import me.mioclient.util.player.PlayerUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.ExplosionVelocityEvent;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.client.AntiCheat;
import me.mioclient.module.movement.Velocity;
import me.mioclient.module.player.SpeedMine;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import me.mioclient.util.world.WorldUtils;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/GrimVelocityHandler.class */
public class GrimVelocityHandler extends VelocityModeHandler {
    public static final SpeedMine speedMine = (SpeedMine) ClientServices.moduleInjector.getModule(SpeedMine.class);
    public static final AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
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
        EntityVelocityUpdateS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof EntityVelocityUpdateS2CPacket ? (EntityVelocityUpdateS2CPacket) (channelRead0Event.getPacket()) : null;
        if ((packet904 instanceof EntityVelocityUpdateS2CPacket) && packet904.getEntityId() == minecraftClient.player.getId() && !isGrimBypassDisabled()) {
            channelRead0Event.cancel();
        }
        if (channelRead0Event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            this.list.add(Long.valueOf(System.currentTimeMillis()));
        }
    }

    @Override // me.mioclient.module.movement.velocity.VelocityModeHandler
    public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
        PlayerMoveC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof PlayerMoveC2SPacket ? (PlayerMoveC2SPacket) (sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof PlayerMoveC2SPacket) {
            PlayerMoveC2SPacket playerMoveC2SPacket = packet904;
            if (playerMoveC2SPacket.changesLook()) {
                this.val = playerMoveC2SPacket.getYaw(0.0f);
                this.val2 = playerMoveC2SPacket.getPitch(0.0f);
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
        this.flag = BlockPos.stream(minecraftClient.player.getBoundingBox()).anyMatch(blockPos -> {
            return !minecraftClient.world.isAir(blockPos);
        });
        if (minecraftClient.player.age <= 20 || isGrimBypassDisabled()) {
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
            PacketSender.sendPacket(new PlayerMoveC2SPacket.Full(minecraftClient.player.getX(), minecraftClient.player.getY(), minecraftClient.player.getZ(), this.val, this.val2, minecraftClient.player.isOnGround()));
            PacketSender.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos713, Direction.DOWN));
        }
    }

    public BlockPos getMostOverlappingBlockPos() {
        return (BlockPos) BlockPos.stream(minecraftClient.player.getBoundingBox()).map((v0) -> {
            return v0.toImmutable();
        }).filter(WorldUtils::isAir).max(Comparator.comparing(this::getIntersectionSize)).orElse(null);
    }

    public float getIntersectionSize(BlockPos blockPos) {
        return (float) minecraftClient.player.getBoundingBox().intersection(new Box(blockPos)).getAverageSideLength();
    }

    public boolean isGrimBypassDisabled() {
        return minecraftClient.player.isFallFlying() || PlayerUtils.isInCobweb(minecraftClient.player) || (getMostOverlappingBlockPos() == null && !this.flag) || !this.stopwatch.hasElapsedMs(200L);
    }
}
