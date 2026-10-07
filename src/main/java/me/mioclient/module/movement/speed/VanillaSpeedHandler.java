package me.mioclient.module.movement.speed;

import me.mioclient.util.player.MovementUtils;

import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.module.movement.Speed;

public final class VanillaSpeedHandler extends SpeedModeHandler {
    public VanillaSpeedHandler(Speed speed) {
        super(speed);
    }

    @Override // me.mioclient.module.movement.speed.SpeedModeHandler
    public void applyMovement(MoveEvent moveEvent) {
        if (this.speed.hasHeadCollision() || this.speed.isInLiquidBlocked() || minecraftClient.player.isFallFlying() || this.speed.is2824()) {
            return;
        }
        MovementUtils.applyVelocity(moveEvent, this.speed.speed.getValue().floatValue());
    }
}