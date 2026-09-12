package me.mioclient.event;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/event/Event.class */
public class Event {
    public boolean flag;

    public void cancel() {
        this.flag = true;
    }

    public void resetCancelled() {
        this.flag = false;
    }

    public boolean isCancelled() {
        return this.flag;
    }
}
