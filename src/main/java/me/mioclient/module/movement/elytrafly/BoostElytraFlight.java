package me.mioclient.module.movement.elytrafly;

import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.network.PacketSendEvent;

import me.mioclient.event.events.input.InputTickEvent;

import me.mioclient.api.ClientServices;

import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.player.PlayerUtils;
import me.mioclient.util.time.Stopwatch;

import me.mioclient.api.ClientServices;
import me.mioclient.network.PacketSender;
import me.mioclient.util.player.EntityUtil;
import me.mioclient.util.player.PlayerUtils;

import java.util.concurrent.TimeUnit;
import me.mioclient.event.events.network.PacketReceiveEvent;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.network.PacketSendEvent;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.event.events.input.InputTickEvent;
import me.mioclient.event.events.client.TickPostEvent;
import me.mioclient.event.events.player.RotationEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.util.time.Stopwatch;
import me.mioclient.module.client.AntiCheat;
import me.mioclient.module.movement.ElytraFly;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import me.mioclient.manager.RotationManager;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/BoostElytraFlight.class */
public class BoostElytraFlight extends ElytraFlyModeBase {
    public static final AntiCheat antiCheat = (AntiCheat) ClientServices.moduleInjector.getModule(AntiCheat.class);
    public final Stopwatch stopwatch;
    public boolean flag;
    public boolean flag2;
    public float val;

    public BoostElytraFlight(ElytraFly elytraFly) {
        super(elytraFly);
        this.stopwatch = new Stopwatch();
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void onEnable() {
        this.flag = true;
        this.val = this.elytraFly.boostPitch.getValue().floatValue();
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleTick(TickEvent tickEvent) {
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleMove(MoveEvent moveEvent) {
        float floatValue = this.elytraFly.limit.getValue().floatValue();
        if (floatValue > Float.intBitsToFloat(1128792064) && isNcpBoostInitial()) {
            floatValue = Float.intBitsToFloat(1128792064);
        }
        if (!this.elytraFly.limit.isAtMaximum() || isNcpBoostInitial()) {
            Vec3d vec3d689 = moveEvent.getMovement();
            if (Math.hypot(vec3d689.getX(), vec3d689.getZ()) * Double.longBitsToDouble(4626322717216342016L) * Double.longBitsToDouble(4615288898129284301L) <= floatValue) {
                return;
            }
            Vec3d multiply = moveEvent.getMovement().normalize().multiply((floatValue / Double.longBitsToDouble(4615288898129284301L)) / Double.longBitsToDouble(4626322717216342016L));
            moveEvent.setHorizontal(multiply.getX(), multiply.getZ());
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handlePacketReceive(PacketReceiveEvent channelRead0Event) {
        HealthUpdateS2CPacket packet904 = (channelRead0Event.getPacket()) instanceof HealthUpdateS2CPacket ? (HealthUpdateS2CPacket) (channelRead0Event.getPacket()) : null;
        if (packet904 instanceof HealthUpdateS2CPacket) {
            HealthUpdateS2CPacket healthUpdateS2CPacket = packet904;
            if (healthUpdateS2CPacket.getHealth() + healthUpdateS2CPacket.getSaturation() < EntityUtil.getPlayerHealth()) {
                onDisable();
                onEnable();
            }
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handlePacketSend(PacketSendEvent sendImmediatelyEvent) {
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleMotion(MotionEvent motionEvent) {
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleTickPost(TickPostEvent tickPostEvent) {
        this.flag2 = false;
        if (PlayerUtils.isInWater(minecraftClient.player)) {
            return;
        }
        if (antiCheat.isF2b2t() && minecraftClient.player.isUsingItem()) {
            return;
        }
        boolean z = minecraftClient.player.input.jumping;
        if (this.elytraFly.takeoff.getValue() == ElytraFly.ElytraFlyMode_2.STRICT) {
            z = !minecraftClient.player.isOnGround() && minecraftClient.player.getVelocity().getY() < Double.longBitsToDouble(4591870180066957722L);
        } else {
            ClientServices.holeSnapHelper.releaseTickRate(this.elytraFly);
        }
        if (minecraftClient.player.isFallFlying()) {
            this.stopwatch.reset();
        }
        if ((z || !this.stopwatch.hasElapsedMs(1500L)) && !minecraftClient.player.isFallFlying() && !EntityUtil.isOnGround(minecraftClient.player) && this.elytraFly.hasElytra()) {
            PacketSender.sendStartFallFlying();
            minecraftClient.player.startFallFlying();
        }
        if (this.elytraFly.isReallyFallFlying()) {
            if (this.elytraFly.autoBoost.getValue().booleanValue()) {
                oscillateBoostPitch();
            }
            float radians = (float) Math.toRadians(minecraftClient.player.getYaw());
            float floatValue = this.elytraFly.minBoost.getValue().floatValue() * Float.intBitsToFloat(1036831949);
            if (this.elytraFly.minBoost.isAtMaximum() && antiCheat.isF2b2t()) {
                floatValue = minecraftClient.player.getPitch() > 0.0f ? Float.intBitsToFloat(1020054733) : Float.intBitsToFloat(1016296636) + (this.elytraFly.verticalBoost.getValue().intValue() * Float.intBitsToFloat(981668463));
                if (minecraftClient.player.getPitch() <= 0.0f) {
                    if (!ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsed(Double.longBitsToDouble(4621819117588971520L), TimeUnit.SECONDS)) {
                        floatValue = Float.intBitsToFloat(1006834287);
                    }
                }
            }
            if (isNcpBoostInitial()) {
                floatValue = Float.intBitsToFloat(1077936128);
            }
            checkNcpBoostTiming();
            if (this.flag2) {
                return;
            }
            if (this.elytraFly.inLava.getValue().booleanValue() && minecraftClient.player.isInLava()) {
                floatValue = Float.intBitsToFloat(1045220557) * this.elytraFly.speed.getValue().floatValue();
            }
            if (!this.elytraFly.factorize.getValue().booleanValue() ? !(minecraftClient.options.forwardKey.isPressed() || this.elytraFly.always.getValue().booleanValue()) : minecraftClient.player.getPitch() <= 0.0f) {
                minecraftClient.player.addVelocity(MathHelper.sin(radians) * (-floatValue), 0.0d, MathHelper.cos(radians) * floatValue);
            } else if (minecraftClient.options.backKey.isPressed()) {
                minecraftClient.player.addVelocity(MathHelper.sin(radians) * floatValue, 0.0d, MathHelper.cos(radians) * (-floatValue));
            }
        }
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleRotation(RotationEvent event_3) {
    }

    @Override // me.mioclient.module.movement.elytrafly.ElytraFlyModeBase
    public void handleInputTick(InputTickEvent tickEvent_2) {
        if (this.elytraFly.autoJump.getValue().booleanValue() && minecraftClient.player.isFallFlying()) {
            tickEvent_2.getInput().jumping = true;
        }
    }

    public void checkNcpBoostTiming() {
        if (minecraftClient.player.getPitch() >= 0.0f || !this.elytraFly.nCPBoost.getValue().booleanValue()) {
            return;
        }
        int fallFlyingTicks = minecraftClient.player.getFallFlyingTicks();
        if (fallFlyingTicks % 6 > 2 || (fallFlyingTicks >= 1 && fallFlyingTicks < 10)) {
            this.flag2 = true;
        } else {
            ClientServices.RotationManager.requestRotations(new float[]{minecraftClient.player.getYaw(), 0.0f}, 10000, true);
            ClientServices.RotationManager.markRotationForced();
        }
    }

    public boolean isNcpBoostInitial() {
        return this.elytraFly.nCPBoost.getValue().booleanValue() && minecraftClient.player.getFallFlyingTicks() <= 5;
    }

    public void oscillateBoostPitch() {
        float floatValue = this.elytraFly.boostPitch.getValue().floatValue();
        if (minecraftClient.player.getY() >= this.elytraFly.minY.getValue().intValue() + this.elytraFly.add.getValue().intValue()) {
            this.flag = true;
        }
        if (minecraftClient.player.getY() <= this.elytraFly.minY.getValue().intValue() && this.flag) {
            this.flag = false;
        }
        if (this.flag) {
            this.val += Float.intBitsToFloat(1082130432);
        } else {
            this.val -= Float.intBitsToFloat(1082130432);
        }
        this.val = MathHelper.clamp(this.val, -floatValue, Math.max(floatValue, minecraftClient.player.getPitch()));
    }

    public float getPitch() {
        return this.val;
    }
}
