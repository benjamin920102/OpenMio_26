package me.mioclient.event.events.client;

import me.mioclient.event.Event;

public class CommandDispatchEvent extends Event {
    public final String string;

    public CommandDispatchEvent(String str) {
        this.string = str;
    }

    public String getCommand() {
        return this.string;
    }
}
