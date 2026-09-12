package me.mioclient.event.events.input;

import me.mioclient.event.Event;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/UpdateMouseEvent.class */
public class UpdateMouseEvent extends Event {
    public final double val;
    public final double val2;

    public UpdateMouseEvent(double d, double d2) {
        this.val = d;
        this.val2 = d2;
    }

    public double getDeltaX() {
        return this.val;
    }

    public double getDeltaY() {
        return this.val2;
    }
}
