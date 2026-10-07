package me.mioclient.event.events.entity;

import me.mioclient.event.Event;

public class RemoveEntityEvent extends Event {
    public final int id;

    public RemoveEntityEvent(int i) {
        this.id = i;
    }

    public int getId() {
        return this.id;
    }
}
