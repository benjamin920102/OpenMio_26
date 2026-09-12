package me.mioclient.event.events.network;

import me.mioclient.event.Event;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/ConnectEvent.class */
public class ConnectEvent extends Event {
    public final String string;

    public ConnectEvent(String str) {
        this.string = str;
    }

    public String getServer() {
        return this.string;
    }
}
