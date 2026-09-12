package me.mioclient.event.events.input;

import me.mioclient.event.Event;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/KeyEvent.class */
public final class KeyEvent extends Event {
    public final int num;
    public final boolean flag;

    public KeyEvent(int i, boolean z) {
        this.num = i;
        this.flag = z;
    }

    public int getKey() {
        return this.num;
    }

    public boolean isPressed() {
        return this.flag;
    }
}
