package me.mioclient.event.events.input;

import me.mioclient.event.Event;

import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;

/* 26.2 port: Yarn mutable Input -> MojMap ClientInput (keyPresses record). The old
   slow-down speed parameter is gone from vanilla tick(); kept as -1 (hasSpeed false)
   so consumers fall back to full speed. */
public class InputTickEvent extends Event {
    public final ClientInput input;
    public final float val;

    public InputTickEvent(ClientInput input) {
        this(input, -1.0f);
    }

    public InputTickEvent(ClientInput input, float f) {
        this.input = input;
        this.val = f;
    }

    public ClientInput getInput() {
        return this.input;
    }

    public float getSpeed() {
        return this.val;
    }

    public boolean hasSpeed() {
        return this.val != -1.0f;
    }

    public void reset() {
        this.input.keyPresses = Input.EMPTY;
    }
}
