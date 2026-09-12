package me.mioclient.module.movement.velocity;

import me.mioclient.event.events.network.PacketReceiveEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.math.BoxMath;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.module.movement.velocity.VelocityModeHandler;
import me.mioclient.util.math.BoxMath;
import me.mioclient.util.player.PlayerUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.ExplosionVelocityEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.mixin.ducks.DuckEntityVelocityUpdateS2CPacket;
import me.mioclient.module.movement.FastWeb;
import me.mioclient.module.movement.Velocity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AntiCheatVelocityHandler.class */
public class AntiCheatVelocityHandler extends VelocityModeHandler {
    public static final FastWeb fastWeb = (FastWeb) ClientServices.moduleInjector.getModule(FastWeb.class);
    public final List<Long> list;
    public final Stopwatch stopwatch;
    public boolean flag;

    public AntiCheatVelocityHandler(Velocity velocity) {
        super(velocity);
        this.list = Collections.synchronizedList(new ArrayList());
        this.stopwatch = new Stopwatch();
    }

    @Override // me.mioclient.module.movement.velocity.VelocityModeHandler
    public void handlePacketReceive(PacketReceiveEvent channelRead0Event) {
        EntityVelocityUpdateS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof EntityVelocityUpdateS2CPacket ? (EntityVelocityUpdateS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof EntityVelocityUpdateS2CPacket) {
            EntityVelocityUpdateS2CPacket entityVelocityUpdateS2CPacket = packet904;
            if (entityVelocityUpdateS2CPacket.getEntityId() == minecraftClient.player.getId() && is951()) {
                boolean z = fastWeb.isToggled() && fastWeb.isGrimMode() && PlayerUtils.isInCobweb(minecraftClient.player);
                DuckEntityVelocityUpdateS2CPacket duckEntityVelocityUpdateS2CPacket = (DuckEntityVelocityUpdateS2CPacket) entityVelocityUpdateS2CPacket;
                duckEntityVelocityUpdateS2CPacket.setZ((int) (entityVelocityUpdateS2CPacket.getVelocityZ() * Double.longBitsToDouble(4665518107723300864L) * 0.0f));
                duckEntityVelocityUpdateS2CPacket.setX((int) (entityVelocityUpdateS2CPacket.getVelocityX() * Double.longBitsToDouble(4665518107723300864L) * 0.0f));
                if ((this.flag || z) && entityVelocityUpdateS2CPacket.getVelocityY() > 0.0d) {
                    duckEntityVelocityUpdateS2CPacket.setY((int) (entityVelocityUpdateS2CPacket.getVelocityY() * Double.longBitsToDouble(-4557853929131474944L)));
                }
            }
        }
        if (channelRead0Event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            this.list.add(Long.valueOf(System.currentTimeMillis()));
        }
    }

    @Override // me.mioclient.module.movement.velocity.VelocityModeHandler
    public void handleMotion(MotionEvent motionEvent) {
    }

    @Override // me.mioclient.module.movement.velocity.VelocityModeHandler
    public void handleExplosionVelocity(ExplosionVelocityEvent explosionVelocityEvent) {
        if (is951()) {
            explosionVelocityEvent.setPlayerVelocityX(0.0f);
            explosionVelocityEvent.setPlayerVelocityY(0.0f);
            explosionVelocityEvent.setPlayerVelocityZ(0.0f);
        }
    }

    @Override // me.mioclient.module.movement.velocity.VelocityModeHandler
    public void onUpdate() {
        this.flag = isNearGroundCollision();
        this.list.removeIf(l -> {
            return System.currentTimeMillis() > l.longValue() + 1000;
        });
        if (this.list.size() > 5) {
            this.stopwatch.reset();
        }
    }

    public boolean is951() {
        if (fastWeb.isToggled() && fastWeb.isGrimMode() && PlayerUtils.isInCobweb(minecraftClient.player)) {
            return true;
        }
        return this.stopwatch.hasElapsedMs(500L) && this.flag;
    }

    public static boolean isNearGroundCollision() {
        return is1886();
    }

    public static boolean isHorizontallyColliding() {
        HashSet<BlockPos> hashSet = new HashSet<>();
        hashSet.add(getBlockPosAtOffset(Float.intBitsToFloat(1065353216), 0.0f));
        hashSet.add(getBlockPosAtOffset(Float.intBitsToFloat(-1082130432), 0.0f));
        hashSet.add(getBlockPosAtOffset(0.0f, Float.intBitsToFloat(1065353216)));
        hashSet.add(getBlockPosAtOffset(0.0f, Float.intBitsToFloat(-1082130432)));
        return hashSet.stream().filter(AntiCheatVelocityHandler::hasCollisionShape).count() >= 2;
    }

    public static boolean is1886() {
        return BlockPos.stream(minecraftClient.player.getBoundingBox().shrink(BoxMath.val, 0.0d, BoxMath.val).withMaxY(minecraftClient.player.getY()).offset(0.0d, Double.longBitsToDouble(4591870180174331904L), 0.0d)).anyMatch(AntiCheatVelocityHandler::hasCollisionShape);
    }

    public static BlockPos getBlockPosAtOffset(float f, float f2) {
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        float lengthX = (float) (clientPlayerEntity.getBoundingBox().getLengthX() / Double.longBitsToDouble(4611686018427387904L));
        return BlockPos.ofFloored(clientPlayerEntity.getX() + (f * lengthX * Float.intBitsToFloat(1065353224)), Math.round(clientPlayerEntity.getY()), clientPlayerEntity.getZ() + (f2 * lengthX * Float.intBitsToFloat(1065353224)));
    }

    public static boolean hasCollisionShape(BlockPos blockPos) {
        return !minecraftClient.world.getBlockState(blockPos).getCollisionShape(minecraftClient.world, blockPos).isEmpty();
    }
}
