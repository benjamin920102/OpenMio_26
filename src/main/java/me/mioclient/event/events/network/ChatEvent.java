package me.mioclient.event.events.network;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/feature/Event.class */
public class ChatEvent extends me.mioclient.event.Event {
    public String string;

    public ChatEvent(String str) {
        this.string = str;
    }

    public String getMessage() {
        return this.string;
    }

    public void setMessage(String str) {
        this.string = str;
    }
}
