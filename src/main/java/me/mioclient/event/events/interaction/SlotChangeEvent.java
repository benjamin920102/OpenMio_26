package me.mioclient.event.events.interaction;

import me.mioclient.event.Event;

public class SlotChangeEvent extends Event {
    public final int num;

    public SlotChangeEvent(int i) {
        this.num = i;
    }

    public int getSlotIndex() {
        return this.num;
    }
}
