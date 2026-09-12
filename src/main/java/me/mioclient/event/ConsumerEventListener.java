package me.mioclient.event;

import java.util.function.Consumer;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/ConsumerEventListener.class */
public class ConsumerEventListener<T> implements EventListener {
    public final Class<?> eventType;
    public final int priority;
    public final Consumer<T> handler;

    public ConsumerEventListener(Class<?> cls, int i, Consumer<T> handler) {
        this.eventType = cls;
        this.priority = i;
        this.handler = handler;
    }

    public ConsumerEventListener(Class<?> cls, Consumer<T> handler) {
        this(cls, 0, handler);
    }

    @Override // me.mioclient.event.EventListener
    public void accept(Object obj) {
        this.handler.accept((T) obj);
    }

    @Override // me.mioclient.event.EventListener
    public Class<?> getEventType() {
        return this.eventType;
    }

    @Override // me.mioclient.event.EventListener
    public int getPriority() {
        return this.priority;
    }

    @Override // me.mioclient.event.EventListener
    public boolean isStaticListener() {
        return false;
    }
}
