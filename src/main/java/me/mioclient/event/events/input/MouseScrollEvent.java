package me.mioclient.event.events.input;

import me.mioclient.event.Event;

public class MouseScrollEvent extends Event {
    public final double val;
    public final double val2;

    public MouseScrollEvent(double d, double d2) {
        this.val = d;
        this.val2 = d2;
    }

    public double getHorizontal() {
        return this.val;
    }

    public double getVertical() {
        return this.val2;
    }
}
