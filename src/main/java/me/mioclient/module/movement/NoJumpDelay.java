package me.mioclient.module.movement;

import me.mioclient.api.Category;
import me.mioclient.event.Listen;
import me.mioclient.event.events.client.TickEvent;
import me.mioclient.module.Module;

public class NoJumpDelay extends Module {
    public NoJumpDelay() {
        super("NoJumpDelay", "Removes the jumping delay when sprint-jumping.", Category.MOVEMENT, new String[0]);
    }

    @Listen
    public void onTick(TickEvent tickEvent) {
        if (isNotInGame() || minecraftClient.player == null || minecraftClient.player.input == null) {
            return;
        }
        if (minecraftClient.player.onGround() && minecraftClient.player.input.keyPresses.jump()) {
            minecraftClient.player.jumpFromGround();
        }
    }
}