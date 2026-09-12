package me.mioclient.module.movement.speed;

import me.mioclient.api.ClientServices;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.player.MovementUtils;

import me.mioclient.api.ClientServices;
import me.mioclient.render.RenderUtil;
import me.mioclient.util.player.MovementUtils;

import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.movement.Speed;
import net.minecraft.entity.Entity;
import me.mioclient.mode.combat.KeyPearlMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/OnGroundSpeedHandler.class */
public final class OnGroundSpeedHandler extends SpeedModeHandler {
    public OnGroundSpeedHandler(Speed speed) {
        super(speed);
        this.num = 2;
    }

    @Override // me.mioclient.module.movement.speed.SpeedModeHandler
    public void applyMovement(MoveEvent moveEvent) {
        if (is594()) {
            this.speed.reset();
            MovementUtils.applyVelocity(moveEvent, MovementUtils.getBaseMoveSpeed(true));
            return;
        }
        if (this.num == 4) {
            this.speed.reset();
        }
        if (minecraftClient.player.isOnGround() || this.num == 3) {
            if ((!minecraftClient.player.horizontalCollision && minecraftClient.player.forwardSpeed != 0.0f) || minecraftClient.player.sidewaysSpeed != 0.0f) {
                if (this.num == 2) {
                    this.val2 *= Double.longBitsToDouble(4612021536599627006L);
                    this.num = 3;
                } else if (this.num == 3) {
                    this.num = 2;
                    this.val2 = this.val3 - (Double.longBitsToDouble(4604119971053405471L) * (this.val3 - MovementUtils.getBaseMoveSpeed(true)));
                }
            }
            this.val2 = Math.min(this.val2, this.speed.speed.getValue().floatValue());
            this.val2 = Math.max(this.val2, MovementUtils.getBaseMoveSpeed(true));
            double[] doubleArray2508 = MovementUtils.computeVelocity(minecraftClient.player.getYaw(RenderUtil.getPartialTick()), minecraftClient.player.input, this.val2);
            if (!minecraftClient.world.isSpaceEmpty(minecraftClient.player.getBoundingBox().stretch(doubleArray2508[0], 0.0d, doubleArray2508[1]))) {
                this.speed.reset();
                this.val2 = MovementUtils.getBaseMoveSpeed(true);
            }
            MovementUtils.applyVelocity(moveEvent, this.val2);
        }
    }

    @Override // me.mioclient.module.movement.speed.SpeedModeHandler
    public void applyAntiKickMotion(MotionEvent motionEvent) {
        if (motionEvent.getPhase() == KeyPearlMode.Post || is594()) {
            return;
        }
        super.applyAntiKickMotion(motionEvent);
        if (this.num == 3) {
            motionEvent.setY(motionEvent.getY() + (hasVerticalCollision() ? Double.longBitsToDouble(4596373779694328218L) : Double.longBitsToDouble(4600877379321698714L)) + MovementUtils.getJumpBoostBonus());
        }
    }

    public boolean is594() {
        Entity entity = minecraftClient.player;
        double x = entity.getX() - entity.prevX;
        double y = entity.getY() - entity.prevY;
        double z = entity.getZ() - entity.prevZ;
        if (!minecraftClient.world.isSpaceEmpty(minecraftClient.player.getBoundingBox().offset(x, y, z))) {
            return true;
        }
        if (!minecraftClient.world.isSpaceEmpty(minecraftClient.player.getBoundingBox().stretch(0.0d, Double.longBitsToDouble(-4616189618054758400L), 0.0d)) && !minecraftClient.player.horizontalCollision && minecraftClient.player.getY() <= Math.floor(minecraftClient.player.getY()) && is1827()) {
            if (ClientServices.mainhandHelper.getPredictedBox(minecraftClient.player, 3).minY >= minecraftClient.player.getY()) {
                return false;
            }
        }
        return true;
    }

    public boolean is1827() {
        return ClientServices.holeSnapRenderHelper.getSetbackStopwatch().hasElapsedMs(500L);
    }
}
