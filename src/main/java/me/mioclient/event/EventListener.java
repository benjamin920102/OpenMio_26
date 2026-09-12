package me.mioclient.event;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/EventListener.class */
public interface EventListener {
    void accept(Object obj);

    Class<?> getEventType();

    int getPriority();

    boolean isStaticListener();
}
