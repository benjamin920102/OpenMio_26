package me.mioclient.module.movement;

import me.mioclient.api.Category;
import me.mioclient.api.Setting;
import me.mioclient.event.Listen;
import me.mioclient.event.events.player.MoveEvent;
import me.mioclient.module.Module;
import me.mioclient.setting.NumberSetting;
import net.minecraft.core.Direction;

public class FastLadder extends Module {
    public Setting<Float> speed = add(new NumberSetting<>("Speed", 0.2f, 0.0f, 1.0f));

    public FastLadder() {
        super("FastLadder", "Makes you move faster on ladders.", Category.MOVEMENT, new String[0]);
    }

    @Listen
    public void onMove(MoveEvent moveEvent) {
        if (isNotInGame() || minecraftClient.player == null) {
            return;
        }
        if (!minecraftClient.player.onClimbable()) {
            return;
        }
        float climbSpeed = this.speed.getValue().floatValue();
        if (minecraftClient.player.input.keyPresses.jump()) {
            moveEvent.setY(climbSpeed);
            minecraftClient.player.setDeltaMovement(minecraftClient.player.getDeltaMovement().with(Direction.Axis.Y, climbSpeed));
        } else if (minecraftClient.player.input.keyPresses.shift()) {
            moveEvent.setY(-climbSpeed);
            minecraftClient.player.setDeltaMovement(minecraftClient.player.getDeltaMovement().with(Direction.Axis.Y, -climbSpeed));
        } else if (minecraftClient.player.input.keyPresses.forward() || minecraftClient.player.input.keyPresses.backward()) {
            moveEvent.setY(0.0d);
            minecraftClient.player.setDeltaMovement(minecraftClient.player.getDeltaMovement().with(Direction.Axis.Y, 0.0d));
        }
    }
}