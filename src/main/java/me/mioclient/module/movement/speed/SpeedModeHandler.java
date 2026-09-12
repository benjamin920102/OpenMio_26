package me.mioclient.module.movement.speed;

import me.mioclient.mode.combat.KeyPearlMode;
import me.mioclient.util.player.MovementUtils;

import me.mioclient.api.ClientAccessor;
import me.mioclient.util.player.MovementUtils;

import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.event.events.player.MotionEvent;
import me.mioclient.module.movement.Speed;
import me.mioclient.mode.combat.KeyPearlMode;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SpeedModeHandler.class */
public abstract class SpeedModeHandler implements ClientAccessor {
    public final Speed speed;
    public final double val = Double.longBitsToDouble(4598847156609680094L);
    public double val2;
    public double val3;
    public int num;

    public SpeedModeHandler(Speed speed) {
        this.speed = speed;
    }

    public abstract void applyMovement(MoveEvent moveEvent);

    public void applyAntiKickMotion(MotionEvent motionEvent) {
        if (motionEvent.getPhase() == KeyPearlMode.Post) {
            return;
        }
        if (!MovementUtils.is2181()) {
            this.num = 4;
            this.val2 = 0.0d;
        }
        double x = minecraftClient.player.getX() - minecraftClient.player.prevX;
        double z = minecraftClient.player.getZ() - minecraftClient.player.prevZ;
        this.val3 = Math.sqrt((x * x) + (z * z));
    }

    public void onSpeedJump() {
    }

    public void onEnable() {
    }

    public boolean hasVerticalCollision() {
        return !minecraftClient.world.isSpaceEmpty(minecraftClient.player.getBoundingBox().offset(0.0d, minecraftClient.player.getVelocity().y, 0.0d));
    }
}
