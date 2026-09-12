package me.mioclient.module.movement.speed;

import me.mioclient.api.ClientServices;

import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.manager.TickRateManager;
import me.mioclient.util.math.MathConstants;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.module.movement.LongJump;
import me.mioclient.module.movement.Speed;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/StrafeStrictHandler.class */
public final class StrafeStrictHandler extends SpeedModeHandler {
    public static LongJump longJump = (LongJump) ClientServices.moduleInjector.getModule(LongJump.class);

    public StrafeStrictHandler(Speed speed) {
        super(speed);
    }

    @Override // me.mioclient.module.movement.speed.SpeedModeHandler
    public void applyMovement(MoveEvent moveEvent) {
        if (shouldPause()) {
            return;
        }
        if (this.speed.useTimer.getValue().booleanValue() && ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsedMs(250L)) {
            TickRateManager holeSnapHelper = ClientServices.holeSnapHelper;
            holeSnapHelper.requestTickRate(this.speed, Float.intBitsToFloat(1066098124));
        } else {
            ClientServices.holeSnapHelper.releaseTickRate(this.speed);
        }
        if (!MovementUtils.is2181()) {
            moveEvent.setHorizontal(0.0d, 0.0d);
            return;
        }
        double d = MovementUtils.getBaseMoveSpeed(false);
        if (ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsedMs(500L)) {
            d = Math.max(d, Math.hypot(moveEvent.getX(), moveEvent.getZ()));
        }
        MovementUtils.applyVelocity(moveEvent, d);
    }

    @Override // me.mioclient.module.movement.speed.SpeedModeHandler
    public void onSpeedJump() {
        if (!shouldPause() && MovementUtils.is2181() && minecraftClient.player.groundCollision) {
            double[] doubleArray2508 = MovementUtils.computeVelocity(minecraftClient.player.getYaw(), minecraftClient.player.input, Double.longBitsToDouble(4607182418800017408L));
            float degrees = (float) (Math.toDegrees(Math.atan2(doubleArray2508[1], doubleArray2508[0])) - MathConstants.num2);
            Vec3d velocity = minecraftClient.player.getVelocity();
            float radians = (float) Math.toRadians(degrees);
            float intBitsToFloat = Float.intBitsToFloat(1045220557);
            minecraftClient.player.setVelocity(velocity.x, Double.longBitsToDouble(4600877379321698714L) + MovementUtils.getJumpBoostBonus(), velocity.z);
            minecraftClient.player.addVelocityInternal(new Vec3d((-MathHelper.sin(radians)) * intBitsToFloat, 0.0d, MathHelper.cos(radians) * intBitsToFloat));
            minecraftClient.player.setSprinting(true);
        }
    }

    public boolean shouldPause() {
        return this.speed.hasHeadCollision() || this.speed.isInLiquidBlocked() || PlayerUtils.isHungry() || minecraftClient.player.isFallFlying() || minecraftClient.player.isSpectator() || longJump.isToggled();
    }
}
