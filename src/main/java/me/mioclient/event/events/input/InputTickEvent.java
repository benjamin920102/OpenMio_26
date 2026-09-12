package me.mioclient.event.events.input;

import me.mioclient.event.Event;

import net.minecraft.client.input.Input;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/TickEvent_2.class */
public class InputTickEvent extends Event {
    public final Input input;
    public final float val;

    public InputTickEvent(Input input, float f) {
        this.input = input;
        this.val = f;
    }

    public Input getInput() {
        return this.input;
    }

    public float getSpeed() {
        return this.val;
    }

    public boolean hasSpeed() {
        return this.val != -1.0f;
    }

    public void reset() {
        Input input = this.input;
        Input input2 = this.input;
        Input input3 = this.input;
        Input input4 = this.input;
        Input input5 = this.input;
        this.input.sneaking = false;
        input5.jumping = false;
        input4.pressingRight = false;
        input3.pressingLeft = false;
        input2.pressingBack = false;
        input.pressingForward = false;
        this.input.movementForward = 0.0f;
        this.input.movementSideways = 0.0f;
    }
}
