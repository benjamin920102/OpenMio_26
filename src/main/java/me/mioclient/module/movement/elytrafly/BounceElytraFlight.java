package me.mioclient.module.movement.elytrafly;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.player.MovementUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.player.MovementUtils;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.event.events.player.RotationEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.mixin.ducks.DuckPlayerMoveC2SPacket;
import me.mioclient.module.movement.ElytraFly;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import me.mioclient.mode.combat.KeyPearlMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/BounceElytraFlight.class */
public class BounceElytraFlight extends ElytraFlyModeBase {
    public long num;
    public boolean flag;
    public boolean flag2;
    public boolean flag3;

    public BounceElytraFlight(ElytraFly elytraFly) {
        super(elytraFly);
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void onEnable() {
        if (minecraftClient.player.isFallFlying() && !minecraftClient.player.isOnGround()) {
            this.flag = true;
            minecraftClient.player.stopFallFlying();
        }
        this.flag2 = false;
        this.flag3 = false;
        this.num = System.currentTimeMillis();
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleTick(TickEvent tickEvent) {
        if (minecraftClient.player.isOnGround()) {
            this.flag = false;
        } else if (minecraftClient.player.isFallFlying()) {
            this.flag = true;
        }
        boolean z = minecraftClient.player.input.jumping;
        if (this.elytraFly.takeoff.getValue() == ElytraFly.ElytraFlyMode_2.STRICT) {
            z = !minecraftClient.player.isOnGround() && minecraftClient.player.getVelocity().getY() < Double.longBitsToDouble(4591870180066957722L);
        } else {
            ClientServices.holeSnapHelper.releaseTickRate(this.elytraFly);
        }
        if (z || this.flag2) {
            this.elytraFly.sendStartFallFlying();
            this.flag2 = !minecraftClient.player.isFallFlying();
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleMove(MoveEvent moveEvent) {
        if (!minecraftClient.player.isFallFlying() && this.flag && !minecraftClient.player.horizontalCollision && minecraftClient.player.getVelocity().getY() < 0.0d) {
            this.flag2 = true;
        }
        if (minecraftClient.player.isFallFlying()) {
            if (!MovementUtils.is2181()) {
                this.num = System.currentTimeMillis();
            }
            float floatValue = this.elytraFly.speed2.getValue().floatValue();
            double[] doubleArray2507 = MovementUtils.applyVelocity(moveEvent, MathHelper.lerp(MathHelper.clamp(((float) (System.currentTimeMillis() - this.num)) / Float.intBitsToFloat(1140457472), 0.0f, Float.intBitsToFloat(1065353216)), this.elytraFly.start.getValue().floatValue(), floatValue));
            minecraftClient.player.setVelocity(doubleArray2507[0], 0.0d, doubleArray2507[1]);
            moveEvent.setY(0.0d);
            minecraftClient.player.setVelocity(minecraftClient.player.getVelocity().withAxis(Direction.Axis.Y, 0.0d));
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handlePacketReceive(PacketReceiveEvent channelRead0Event) {
        if ((channelRead0Event.getPacket() instanceof PlayerPositionLookS2CPacket) && minecraftClient.player.isFallFlying() && !minecraftClient.player.isOnGround()) {
            this.num = System.currentTimeMillis();
            minecraftClient.player.stopFallFlying();
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
        DuckPlayerMoveC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof PlayerMoveC2SPacket ? (DuckPlayerMoveC2SPacket)(sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof PlayerMoveC2SPacket) {
            DuckPlayerMoveC2SPacket duckPlayerMoveC2SPacket = (DuckPlayerMoveC2SPacket)((PlayerMoveC2SPacket) packet904);
            if (!minecraftClient.player.isFallFlying() || minecraftClient.player.isOnGround() || minecraftClient.player.getVelocity().getY() >= Double.longBitsToDouble(4591870180066957722L) || !MovementUtils.is2181()) {
                return;
            }
            duckPlayerMoveC2SPacket.setPitch(Float.intBitsToFloat(-1063256064));
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleMotion(MotionEvent motionEvent) {
        if (motionEvent.getPhase() != KeyPearlMode.Pre) {
            return;
        }
        boolean z = false;
        if (this.elytraFly.hasElytra() && !motionEvent.isOnGround()) {
            if (!minecraftClient.player.isFallFlying()) {
                z = true;
                this.flag3 = false;
            } else if (!this.flag3) {
                if (MovementUtils.is2181()) {
                    this.flag3 = true;
                } else {
                    z = true;
                }
            }
        }
        if (z) {
            motionEvent.setYaw(motionEvent.getYaw() + (minecraftClient.player.age % 2 == 0 ? -2 : 2));
            motionEvent.setPitch(motionEvent.getPitch() + (minecraftClient.player.age % 2 == 0 ? -2 : 2));
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleTickPost(TickPostEvent tickPostEvent) {
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleRotation(RotationEvent event_3) {
    }
}
