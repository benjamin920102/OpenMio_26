package me.mioclient.event.events.render;

import me.mioclient.event.Event;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/RenderEvent.class */
public class RenderEvent extends Event {
    public final boolean flag;

    public RenderEvent(boolean z) {
        this.flag = z;
    }

    public boolean isThirdPerson() {
        return this.flag;
    }
}
