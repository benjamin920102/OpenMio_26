package me.mioclient.event.events.network;

import me.mioclient.event.Event;

public final class UpdateSelectedSlotEvent extends Event {
    public final int num;

    public UpdateSelectedSlotEvent(int i) {
        this.num = i;
    }

    public int getSlotIndex() {
        return this.num;
    }
}
