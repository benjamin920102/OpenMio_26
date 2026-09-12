package me.mioclient.event.events.network;

import me.mioclient.event.Event;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/UpdateSelectedSlotEvent.class */
public final class UpdateSelectedSlotEvent extends Event {
    public final int num;

    public UpdateSelectedSlotEvent(int i) {
        this.num = i;
    }

    public int getSlotIndex() {
        return this.num;
    }
}
