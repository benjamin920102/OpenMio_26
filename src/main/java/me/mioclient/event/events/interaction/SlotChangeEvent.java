package me.mioclient.event.events.interaction;

import me.mioclient.event.Event;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/SpeedMineEvent.class */
public class SlotChangeEvent extends Event {
    public final int num;

    public SlotChangeEvent(int i) {
        this.num = i;
    }

    public int getSlotIndex() {
        return this.num;
    }
}
