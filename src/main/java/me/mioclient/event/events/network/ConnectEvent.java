package me.mioclient.event.events.network;

import me.mioclient.event.Event;

public class ConnectEvent extends Event {
    public final String string;

    public ConnectEvent(String str) {
        this.string = str;
    }

    public String getServer() {
        return this.string;
    }
}
