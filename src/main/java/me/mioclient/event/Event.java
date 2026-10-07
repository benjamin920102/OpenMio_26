package me.mioclient.event;

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
