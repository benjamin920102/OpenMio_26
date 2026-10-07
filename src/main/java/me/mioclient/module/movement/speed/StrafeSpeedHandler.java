package me.mioclient.module.movement.speed;

import me.mioclient.api.ClientServices;

import me.mioclient.util.math.Vec3dUtils;
import me.mioclient.util.player.MovementUtils;
import me.mioclient.util.player.PlayerUtils;

import me.mioclient.render.RenderUtil;

import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.module.movement.LongJump;
import me.mioclient.module.movement.Speed;
import net.minecraft.world.phys.Vec3;

public class StrafeSpeedHandler extends SpeedModeHandler {
    /* 26.2 port: resolved lazily — eager static init ran before the module registry was ready, leaving refs null (NPE). */
    private static LongJump longJump;

    public static LongJump getLongJump() {
        if (longJump == null && ClientServices.moduleInjector != null) {
            try {
                longJump = (LongJump) ClientServices.moduleInjector.getModule(LongJump.class);
            } catch (Exception e) {
            }
        }
        return longJump;
    }

    public StrafeSpeedHandler(Speed speed) {
        super(speed);
        this.num = 4;
    }

    @Override // me.mioclient.module.movement.speed.SpeedModeHandler
    public void applyMovement(MoveEvent moveEvent) {
        double d;
        double d2;
        LongJump longJump_lcl = getLongJump();
        if (this.speed.hasHeadCollision() || this.speed.isInLiquidBlocked() || PlayerUtils.isHungry() || minecraftClient.player.isFallFlying() || minecraftClient.player.isSpectator() || (longJump_lcl != null && longJump_lcl.isToggled())) {
            return;
        }
        double d3 = moveEvent.getY();
        if (this.speed.useTimer.getValue().booleanValue() && ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsedMs(250L)) {
            ClientServices.holeSnapHelper.requestTickRate(this.speed, Float.intBitsToFloat(1066098124));
        } else if (this.speed.useTimer.getValue().booleanValue()) {
            ClientServices.holeSnapHelper.releaseTickRate(this.speed);
        }
        float intBitsToFloat = (this.speed.stopwatch.hasElapsedMs(1000L) || !this.speed.boost.getValue().booleanValue()) ? Float.intBitsToFloat(1065353216) : Float.intBitsToFloat(1067030938);
        if (this.num == 1 && MovementUtils.is2181()) {
            this.val2 = (Double.longBitsToDouble(4608758678669597082L) * MovementUtils.computeMoveSpeed(false, (Double.longBitsToDouble(4598847156609680094L) * this.speed.speed.getValue().floatValue()) * intBitsToFloat)) - Double.longBitsToDouble(4576918229304087675L);
        } else if (this.num == 2 && MovementUtils.is2181() && minecraftClient.player.onGround()) {
            d3 = Double.longBitsToDouble(4601237667291888353L) + MovementUtils.getJumpBoostBonus();
            this.val2 *= Double.longBitsToDouble(4609591844600660623L);
        } else if (this.num == 3) {
            this.val2 = this.val3 - (Double.longBitsToDouble(4604119971289628672L) * (this.val3 - MovementUtils.computeMoveSpeed(true, (Double.longBitsToDouble(4598847156609680094L) * this.speed.speed.getValue().floatValue()) * intBitsToFloat)));
        } else {
            if ((hasVerticalCollision() || minecraftClient.player.verticalCollision) && this.num > 0) {
                this.num = MovementUtils.is2181() ? 1 : 0;
            }
            this.val2 = this.val3 - (this.val3 / Double.longBitsToDouble(4639798331726364672L));
        }
        this.val2 = Math.max(this.val2, MovementUtils.computeMoveSpeed(false, Double.longBitsToDouble(4598847156609680094L) * this.speed.speed.getValue().floatValue() * intBitsToFloat));
        if (MovementUtils.is2181()) {
            double[] doubleArray2508 = MovementUtils.computeVelocity(minecraftClient.player.getYRot(), minecraftClient.player.input, this.val2);
            d = doubleArray2508[0];
            d2 = doubleArray2508[1];
        } else {
            d = 0.0d;
            d2 = 0.0d;
        }
        moveEvent.setMovement(new Vec3(d, d3, d2));
        if (MovementUtils.is2181()) {
            this.num++;
        }
    }
}