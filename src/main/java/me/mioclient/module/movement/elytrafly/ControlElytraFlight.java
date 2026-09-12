package me.mioclient.module.movement.elytrafly;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.input.InputTickEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.math.MathUtils;
import me.mioclient.util.player.MovementUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.math.MathUtils;
import me.mioclient.util.player.MovementUtils;

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
import me.mioclient.module.movement.Fireworks;
import me.mioclient.module.movement.ObstaclePasser;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.manager.RotationManager;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ControlElytraFlight.class */
public class ControlElytraFlight extends ElytraFlyModeBase {
    public static final ObstaclePasser obstaclePasser = (ObstaclePasser) ClientServices.moduleInjector.getModule(ObstaclePasser.class);
    public static Fireworks fireworks = (Fireworks) ClientServices.moduleInjector.getModule(Fireworks.class);
    public Vec3d vec3d;
    public int num;
    public double val;
    public long num2;
    public long num3;

    public ControlElytraFlight(ElytraFly elytraFly) {
        super(elytraFly);
        this.vec3d = Vec3d.ZERO;
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void onEnable() {
        this.val = 0.0d;
        long currentTimeMillis = System.currentTimeMillis();
        this.num2 = currentTimeMillis;
        this.num3 = currentTimeMillis;
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleTick(TickEvent tickEvent) {
        if (!minecraftClient.player.isFallFlying()) {
            this.num3 = System.currentTimeMillis();
        }
        if (minecraftClient.player.input.jumping || minecraftClient.player.input.sneaking) {
            return;
        }
        this.num3 = System.currentTimeMillis();
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleMove(MoveEvent moveEvent) {
        if (isObstaclePasserActive()) {
            return;
        }
        if (!minecraftClient.player.isFallFlying() || this.elytraFly.isManualVerticalActive()) {
            this.num2 = System.currentTimeMillis();
            return;
        }
        if (isElytraBoostReady() && this.elytraFly.hasElytra()) {
            if (!MovementUtils.is2181()) {
                this.num2 = System.currentTimeMillis();
            }
            float floatValue = this.elytraFly.vSpeed.getValue().floatValue();
            if (this.elytraFly.accelerate.getValue().booleanValue()) {
                this.val = (float) MovementUtils.computeAcceleratedSpeed(this.elytraFly.speed3.getValue().floatValue(), this.elytraFly.accelMin.getValue().floatValue(), this.elytraFly.accelTime.getValue().floatValue(), this.num2);
                floatValue = (float) MovementUtils.computeAcceleratedSpeed(floatValue, this.elytraFly.verAccelMin.getValue().floatValue(), this.elytraFly.accelTime.getValue().floatValue(), this.num3);
            } else {
                this.val = this.elytraFly.speed3.getValue().floatValue();
            }
            this.val += fireworks.getBoostFactor(false);
            if (this.num == 0) {
                if (minecraftClient.player.input.jumping && this.elytraFly.vertical.getValue() == ElytraFly.ElytraFlyMode.STRICT && this.val >= Double.longBitsToDouble(4611686018427387904L)) {
                    this.val = Double.longBitsToDouble(4611686018427387904L);
                }
                double[] doubleArray2507 = MovementUtils.applyVelocity(moveEvent, this.val);
                minecraftClient.player.setVelocity(doubleArray2507[0], minecraftClient.player.getVelocity().y, doubleArray2507[1]);
                this.vec3d = minecraftClient.player.getVelocity();
                if (this.elytraFly.antiKick.getValue().booleanValue() && minecraftClient.player.age % 4 == 0) {
                    moveEvent.setY(moveEvent.getY() - Double.longBitsToDouble(4547007122018943789L));
                    minecraftClient.player.setVelocity(minecraftClient.player.getVelocity().add(0.0d, Double.longBitsToDouble(-4676364914835832019L), 0.0d));
                }
            }
            if (minecraftClient.player.input.jumping && minecraftClient.player.isFallFlying()) {
                if (this.elytraFly.vertical.getValue() == ElytraFly.ElytraFlyMode.STRICT) {
                    this.num++;
                }
                if (this.elytraFly.vertical.getValue() != ElytraFly.ElytraFlyMode.PLAIN) {
                    if (this.elytraFly.vertical.getValue() == ElytraFly.ElytraFlyMode.STRICT && MovementUtils.is2181()) {
                        do861();
                        return;
                    }
                    return;
                }
                moveEvent.setY(floatValue);
                minecraftClient.player.setVelocity(minecraftClient.player.getVelocity().withAxis(Direction.Axis.Y, floatValue));
                return;
            }
            if (minecraftClient.player.input.sneaking) {
                this.num = 0;
                moveEvent.setY(-floatValue);
                minecraftClient.player.setVelocity(minecraftClient.player.getVelocity().withAxis(Direction.Axis.Y, -floatValue));
                return;
            }
            this.num = 0;
            moveEvent.setY(0.0d);
            minecraftClient.player.setVelocity(minecraftClient.player.getVelocity().withAxis(Direction.Axis.Y, 0.0d));
            if (minecraftClient.player.verticalCollision || this.elytraFly.glide.getValue().floatValue() == 0.0f) {
                return;
            }
            minecraftClient.player.setVelocity(minecraftClient.player.getVelocity().add(0.0d, -this.elytraFly.glide.getValue().floatValue(), 0.0d));
            moveEvent.setY(minecraftClient.player.getVelocity().y);
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handlePacketReceive(PacketReceiveEvent channelRead0Event) {
        if (channelRead0Event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            this.num2 = System.currentTimeMillis();
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
        if (isObstaclePasserActive()) {
            return;
        }
        DuckPlayerMoveC2SPacket packet904 = (sendImmediatelyEvent.getPacket()) instanceof PlayerMoveC2SPacket ? (DuckPlayerMoveC2SPacket)(sendImmediatelyEvent.getPacket()) : null;
        if (packet904 instanceof PlayerMoveC2SPacket) {
            DuckPlayerMoveC2SPacket duckPlayerMoveC2SPacket = (DuckPlayerMoveC2SPacket)((PlayerMoveC2SPacket) packet904);
            if (minecraftClient.player.isFallFlying() && !this.elytraFly.isManualVerticalActive() && isElytraBoostReady() && this.elytraFly.hasElytra()) {
                if (isVerticalInput()) {
                    duckPlayerMoveC2SPacket.setPitch(getPitch());
                    return;
                }
                if (MovementUtils.is2181()) {
                    if (this.elytraFly.spoofPitch.getValue().booleanValue()) {
                        duckPlayerMoveC2SPacket.setPitch(this.elytraFly.pitch.getValue().floatValue());
                    }
                    if (this.num > 0) {
                        duckPlayerMoveC2SPacket.setPitch(-this.elytraFly.vPitch.getValue().floatValue());
                    }
                }
            }
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleMotion(MotionEvent motionEvent) {
        if (minecraftClient.player.isFallFlying() && !this.elytraFly.isManualVerticalActive() && isElytraBoostReady() && this.elytraFly.hasElytra() && motionEvent.getPhase() == KeyPearlMode.Pre && !isObstaclePasserActive()) {
            if (isVerticalInput()) {
                motionEvent.setPitch(getPitch());
                return;
            }
            if (MovementUtils.is2181()) {
                motionEvent.setYaw(getYaw());
                if (this.elytraFly.spoofPitch.getValue().booleanValue()) {
                    motionEvent.setPitch(this.elytraFly.pitch.getValue().floatValue());
                }
                if (this.num > 0) {
                    motionEvent.setPitch(-this.elytraFly.vPitch.getValue().floatValue());
                }
            }
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleTickPost(TickPostEvent tickPostEvent) {
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleRotation(RotationEvent event_3) {
        if (this.elytraFly.hasElytra() && minecraftClient.player.isFallFlying() && !this.elytraFly.isManualVerticalActive() && isElytraBoostReady() && !isObstaclePasserActive()) {
            if (isVerticalInput()) {
                event_3.setPitch(getPitch());
            }
            if (MovementUtils.is2181()) {
                event_3.setYaw(getYaw());
            }
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleInputTick(InputTickEvent tickEvent_2) {
        if (tickEvent_2.getInput() == null || minecraftClient.player.input == null || isObstaclePasserActive() || !this.elytraFly.isManualVerticalActive()) {
            return;
        }
        tickEvent_2.getInput().jumping = true;
        tickEvent_2.getInput().pressingForward = true;
        tickEvent_2.getInput().movementForward = tickEvent_2.hasSpeed() ? tickEvent_2.getSpeed() : Float.intBitsToFloat(1065353216);
    }

    public void do861() {
        double d = MathUtils.getRadians(-this.elytraFly.vPitch.getValue().floatValue());
        Vec3d vec3d = this.vec3d;
        Vec3d vec3d375 = MathUtils.getRotationVector(-this.elytraFly.vPitch.getValue().floatValue(), minecraftClient.player.getYaw());
        double horizontalLength = vec3d.horizontalLength();
        double horizontalLength2 = vec3d375.horizontalLength();
        double length = vec3d375.length();
        double cos = Math.cos(d);
        Vec3d add = vec3d.add(0.0d, Double.longBitsToDouble(4590429028186199163L) * (Double.longBitsToDouble(-4616189618054758400L) + (cos * cos * Math.min(Double.longBitsToDouble(4607182418800017408L), length / Double.longBitsToDouble(4600877379321698714L)) * Double.longBitsToDouble(4604930618986332160L))), 0.0d);
        if (d < 0.0d && horizontalLength2 > 0.0d) {
            double longBitsToDouble = horizontalLength * (-Math.sin(d)) * Double.longBitsToDouble(4585925428558828667L);
            add = add.add(((-vec3d375.x) * longBitsToDouble) / horizontalLength2, longBitsToDouble * Double.longBitsToDouble(4614388178203810202L), ((-vec3d375.z) * longBitsToDouble) / horizontalLength2);
        }
        minecraftClient.player.setVelocity(add.multiply(Double.longBitsToDouble(4607092346807469998L), Double.longBitsToDouble(4607002274814922588L), Double.longBitsToDouble(4607092346807469998L)));
        this.vec3d = minecraftClient.player.getVelocity();
    }

    public float getYaw() {
        double[] doubleArray2508 = MovementUtils.computeVelocity(minecraftClient.player.getYaw(), minecraftClient.player.input, Double.longBitsToDouble(4607182418800017408L));
        return (float) (Math.toDegrees(Math.atan2(doubleArray2508[1], doubleArray2508[0])) - MathConstants.num2);
    }

    public float getPitch() {
        float pitch = minecraftClient.player.getPitch();
        if (isVerticalInput()) {
            pitch = MathConstants.num2;
            if (MovementUtils.is2181()) {
                pitch = MathConstants.num;
            }
            if (minecraftClient.player.input.jumping) {
                pitch = -pitch;
            }
        }
        return pitch;
    }

    public boolean isVerticalInput() {
        return minecraftClient.player.input.jumping || (minecraftClient.player.input.sneaking && this.elytraFly.vertical.getValue() == ElytraFly.ElytraFlyMode.PLAIN && RotationManager.is724());
    }

    public boolean isElytraBoostReady() {
        if (minecraftClient.player.isFallFlying() && this.elytraFly.hasElytra() && fireworks.autoLaunch.getValue().booleanValue() && fireworks.isToggled() && fireworks.stopwatch.hasElapsedMs(500L)) {
            return fireworks.hasActiveFirework();
        }
        return true;
    }

    public boolean isObstaclePasserActive() {
        if (obstaclePasser == null) {
            return false;
        }
        return obstaclePasser.isPathing();
    }
}
