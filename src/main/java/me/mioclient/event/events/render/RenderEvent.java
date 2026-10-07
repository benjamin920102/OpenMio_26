package me.mioclient.event.events.render;

import me.mioclient.event.Event;

public class RenderEvent extends Event {
    public final boolean flag;

    public RenderEvent(boolean z) {
        this.flag = z;
    }

    public boolean isThirdPerson() {
        return this.flag;
    }
}
