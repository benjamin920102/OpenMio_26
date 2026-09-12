package me.mioclient.event.events.client;

import me.mioclient.event.Event;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/StringEvent.class */
public class CommandDispatchEvent extends Event {
    public final String string;

    public CommandDispatchEvent(String str) {
        this.string = str;
    }

    public String getCommand() {
        return this.string;
    }
}
