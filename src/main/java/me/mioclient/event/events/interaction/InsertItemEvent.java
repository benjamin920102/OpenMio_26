package me.mioclient.event.events.interaction;

import me.mioclient.event.Event;

import net.minecraft.world.inventory.Slot;

public class InsertItemEvent extends Event {
    public final Slot slot;

    public InsertItemEvent(Slot slot) {
        this.slot = slot;
    }

    public Slot getSlot() {
        return this.slot;
    }
}
