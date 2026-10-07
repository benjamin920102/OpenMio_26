package me.mioclient.event.events.network;

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
